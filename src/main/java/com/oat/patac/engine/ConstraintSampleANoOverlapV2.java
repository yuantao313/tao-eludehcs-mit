package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.patac.entity.EntitySubTask;
import com.oat.patac.entity.EntitySubTaskPlan;
import ilog.concert.IloException;
import ilog.concert.IloIntervalVar;
import ilog.concert.IloNumToNumStepFunction;
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
public class ConstraintSampleANoOverlapV2 extends PatacConstraint{

    public ConstraintSampleANoOverlapV2(SubModel model) {
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

            ArrayList<EntitySubTaskPlan> subTasksPlans = uniqueSampleSupportedSubtaskPlansMap.get(uniqueSampleNo) ;
            if (subTasksPlans != null) {
                //为不在本次排程范围的批次（非待排程任务单下的批次）建立intensity function
                for (EntitySubTaskPlan subTaskPlan : subTasksPlans) {

                    // 设置固定批次变量的最早开始时间
                    if (subTaskPlan.getSubTaskStartInModel().after(subModelEnd)
                            || subTaskPlan.getSubTaskEndInModel().before(subModelStart) ){
                        continue;
                    }
                    IloNumToNumStepFunction stepFunction = cp.numToNumStepFunction();
                    ArrayList<Double> stepFunctionTime = new ArrayList<>();
                    ArrayList<Double> stepFunctionValue = new ArrayList<>();

                    Date startMin = DateUtil.getMaxTime(subModelStart,subTaskPlan.getSubTaskStartInModel());
                    int startMinInt = DateUtil.getDistanceIntTime(startMin, subModelStart,
                            1000*60*granularity);
                    stepFunctionTime.add(0.0);
                    if (startMinInt == 0){
                        stepFunctionValue.add(0.0);
                    } else {
                        stepFunctionTime.add((double)startMinInt);
                        stepFunctionValue.add(100.0);
                        stepFunctionValue.add(0.0);
                    }
                    Date endMax = DateUtil.getMinTime(subModelEnd,subTaskPlan.getSubTaskEndInModel());
                    int endMaxInt = DateUtil.getDistanceIntTime(endMax, subModelStart,
                            1000*60*granularity);
                    stepFunctionTime.add((double)endMaxInt);
                    if (endMaxInt < subModelData.getEndIntTime()) {
                        stepFunctionTime.add((double)subModelData.getEndIntTime());
                        stepFunctionValue.add(100.0);
                    }

                    int sizeTime = stepFunctionTime.size();
                    for (int j = 0; j < sizeTime - 1; j++) {
                        stepFunction.setValue(stepFunctionTime.get(j), stepFunctionTime.get(j + 1), stepFunctionValue.get(j));
                    }

                    //得到某个样品对应的批次个数
                    int size = uniqueSampleSupportedSubtasks.size();
                    // 为每一个sub task加在step function上的forbid extent

                    for(int i =0; i < size; i++){
                        EntitySubTask subTask = uniqueSampleSupportedSubtasks.get(i);
                        String subTaskNo = subTask.getSubTaskNo();
                        //获取该样品对应的批次的变量
                        IloIntervalVar var = (IloIntervalVar) ivSubtask.getVariable(subTask.getTaskNo(), subTaskNo);
                        if (engineProcessedData.getPlan().isEnabledConstraintSampleANoOverlap()) {
                            cp.add(cp.forbidExtent(var, stepFunction));
                            log.info("ConstraintSampleANoOverlap:: " + var.getName() + " FORBID EXTENT ON STEP FUNCTION OF TIME "
                            + stepFunctionTime + " VALUE " + stepFunctionValue);
                        }
                    }

                }
            }
            int size = uniqueSampleSupportedSubtasks.size();
            //建立以该批次数为大小的一维变量数组
            IloIntervalVar[] ivaSampleA = new IloIntervalVar[size];
            for(int i =0; i < size; i++) {
                EntitySubTask subTask = uniqueSampleSupportedSubtasks.get(i);
                String subTaskNo = subTask.getSubTaskNo();
                //获取该样品对应的批次的变量
                IloIntervalVar var = (IloIntervalVar) ivSubtask.getVariable(subTask.getTaskNo(), subTaskNo);
                ivaSampleA[i] = var;
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


