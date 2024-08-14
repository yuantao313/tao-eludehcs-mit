package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.patac.entity.EntitySubTask;
import com.oat.patac.entity.EntitySubTaskPlan;
import ilog.concert.IloException;
import ilog.concert.IloIntervalVar;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 包含的约束有：
 * 非共享样件（整车）同一时间只能支持一个批次活动
 * cSampleANoOverlap[unique_sample]
 */
@Log4j2
public class ConstraintSampleANoOverlap extends PatacConstraint{

    public ConstraintSampleANoOverlap(SubModel model) {
        super(model);
        setConstraintName("SampleA No Overlap Constraint");
    }

    @Override
    public boolean apply() throws IloException {
        //得到不能冲突的样品所对应的需要使用该样品的批次的对应关系
        HashMap<String, ArrayList<EntitySubTask>> uniqueSampleSupportedSubtasksMap = subModelData.getUniqueSampleSupportedSubtasks();
        HashMap<String, ArrayList<EntitySubTaskPlan>> uniqueSampleSupportedSubtaskPlansMap = subModelData.getUniqueSampleSupportedSubtaskPlans();
        //获得子模型中需要考虑的任务单和下面的批次
        HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMap = subModelData.getToBePlannedSubTaskMap();
        // 获得subtask和小阶段活动的变量
        CPModel.Var2DArray ivSubtask = subModel.getIvSubTask();
        //获得其他需要的参数
        int granularity = subModelData.getPlanGranularity();
        Date subModelStart = subModelData.getMinPlanStartTime();
        Date subModelEnd = subModelData.getMaxPlanEndTime();

        //遍历样品与对应的批次
        for(Map.Entry<String, ArrayList<EntitySubTask>> entry : uniqueSampleSupportedSubtasksMap.entrySet()){
            //得到对应的样品号以及该样品对应使用的批次
            String uniqueSampleNo = entry.getKey();
            ArrayList<EntitySubTask> uniqueSampleSupportedSubtasks = entry.getValue();

            ArrayList<IloIntervalVar> ivSubtasksFixedForUniqueSample = new ArrayList<>();
            ArrayList<EntitySubTaskPlan> subTasksPlans = uniqueSampleSupportedSubtaskPlansMap.get(uniqueSampleNo) ;
            if (subTasksPlans != null) {
                //为不在本次排程范围的批次（非待排程任务单下的批次）建立新的变量
                for (EntitySubTaskPlan subTaskPlan : subTasksPlans) {

//                    if (toBePlannedSubTaskMap.containsKey(subTaskPlan.getTaskNo())) {
//                        continue;
//                    }
                    IloIntervalVar var = cp.intervalVar("ivSubtasksFixedForUniqueSample: uniqueSampleNo_" + uniqueSampleNo + "_subtask_" + subTaskPlan.getSubTaskNo());
                    // 设置固定批次变量的最早开始时间
                    if (subTaskPlan.getSubTaskStartInModel().compareTo(subModelEnd) > 0
                    || subTaskPlan.getSubTaskEndInModel().compareTo(subModelStart) <  0 ){
                        continue;
                    }
                    Date startMin = DateUtil.getMaxTime(subModelStart,subTaskPlan.getSubTaskStartInModel());
                    int startMinInt = DateUtil.getDistanceIntTime(startMin, subModelStart,
                                1000*60*granularity);
                    var.setStartMin(startMinInt);
                    Date endMax = DateUtil.getMinTime(subModelEnd,subTaskPlan.getSubTaskEndInModel());
                    int endMaxInt = DateUtil.getDistanceIntTime(endMax, subModelStart,
                            1000*60*granularity);
                    var.setEndMax(endMaxInt);
                    int varSize = endMaxInt-startMinInt;
                    var.setSizeMax(varSize);
                    var.setSizeMin(varSize);
                    ivSubtasksFixedForUniqueSample.add(var);
                }
            }

            //得到某个样品对应的批次个数
            int size1 = uniqueSampleSupportedSubtasks.size();
            int size2 = ivSubtasksFixedForUniqueSample.size();
            //建立以该
            // 批次数为大小的一维变量数组
            IloIntervalVar[] ivaSampleA = new IloIntervalVar[size1+size2];

            for(int i =0; i < size1; i++){
                EntitySubTask subTask = uniqueSampleSupportedSubtasks.get(i);
                String subTaskNo = subTask.getSubTaskNo();
                //获取该样品对应的批次的变量
                IloIntervalVar var = (IloIntervalVar) ivSubtask.getVariable(subTask.getTaskNo(), subTaskNo);
                //将对应的变量加入一维的变量数组中
                ivaSampleA[i] = var;
            }
            for (int i = 0; i< size2; i++){
                ivaSampleA[i+size1] = ivSubtasksFixedForUniqueSample.get(i);
            }

            if (engineProcessedData.getPlan().isEnabledConstraintSampleANoOverlap()) {
                if (ivaSampleA.length>=2) {
                    cp.add(cp.noOverlap(ivaSampleA));

                    log.info("ConstraintSampleANoOverlap:: " + " NO OVERLAP AMONG ");
                    Stream.of(ivaSampleA).forEach(v -> log.info("ConstraintSampleANoOverlap:: " + v.getName()));
                }
            }
        }




        return true;
    }





}
