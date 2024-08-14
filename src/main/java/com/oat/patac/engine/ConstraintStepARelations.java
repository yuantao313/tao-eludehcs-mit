package com.oat.patac.engine;

import com.oat.cp.CPModel;
import com.oat.cp.ConstraintViolationPlanResult;
import com.oat.patac.entity.EntityStep;
import com.oat.patac.entity.EntityStepActivity;
import com.oat.patac.entity.EntitySubTask;

import com.oat.patac.entity.EntityTask;
import ilog.concert.*;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Stream;

import static com.oat.common.utils.ConstantUtil.*;

/**
 *  小阶段活动和批次活动的关系，以及小阶段活动的顺序要求
 *  包含的约束有：
 *  - 批次活动span小阶段活动的约束
 *    cSubtaskSpanStepA[plan_t][plan_st]
 *    同时初始化了ivSubTask和ivStepA
 *  - 对optional的task，批次和其下属的小阶段活动的被排入状态应该一致
 *    cSubtaskStepAPresence[plan_t][plan_st][stepA]
 *  - 小阶段活动被安排的顺序要满足bom中定义的顺序
 *    cStepASequence[plan_t][plan_st][stepA]
 *  - 与上一个小阶段的最大间隔小时
 *    cStepAMaxGap[plan_t][plan_st][stepA]
 */
@Log4j2
public class ConstraintStepARelations extends PatacConstraint{
    public ConstraintStepARelations(SubModel model){
        super(model);
        setConstraintName("StepA Relations Constraint");
    }


    @Override
    public boolean apply() throws IloException {
        //获得批次和下面的小阶段活动
        //获得子模型中需要考虑的任务单和下面待排程的批次 - by Sophia
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getToBePlannedSubTaskMap();
        //获得task的id和对象的map
        HashMap<String, EntityTask> taskHashMap = engineProcessedData.getTaskHashMap();
        // 获得subtask和小阶段活动的变量
        CPModel.Var2DArray ivSubtask = subModel.getIvSubTask();
        CPModel.Var3DArray ivStepA = subModel.getIvStepA();

        //生成intensity function，为两种模式，仅工作日，和工作日及周末
        HashMap<String, IloNumToNumStepFunction> stepFunctionHashMap = new HashMap<>();
        HashMap<String, ArrayList<Double>> stepFunctionStepTime = subModelData.getStepFunctionStepTime();
        HashMap<String, ArrayList<Double>> stepFunctionStepValue = subModelData.getStepFunctionStepValue();

        //IloIntExpr exprSumOfStepASize = subModel.getExprSumOfStepASize();

        for (Map.Entry<String, ArrayList<Double>> entry: stepFunctionStepTime.entrySet()) {
            IloNumToNumStepFunction stepFStepCalendar = cp.numToNumStepFunction();
            String workMode = entry.getKey();
            ArrayList<Double> workdayModeTime = entry.getValue();
            ArrayList<Double> workdayModeValue = stepFunctionStepValue.get(workMode);
            int sizeTime = workdayModeTime.size();
            for (int j = 0; j < sizeTime - 1; j++) {
                stepFStepCalendar.setValue(workdayModeTime.get(j), workdayModeTime.get(j + 1), workdayModeValue.get(j));
            }
            stepFunctionHashMap.put(workMode,stepFStepCalendar);
        }

        //循环每个Task下的SubTask和StepActivities,加约束
        for (Map.Entry<String, ArrayList<EntitySubTask>> entry : subTaskMap.entrySet()) {
            // 得到待排程的任务单ID和对应的批次数组
            String taskNo = entry.getKey();
            EntityTask task = taskHashMap.get(taskNo);

            ArrayList<EntitySubTask> subTasks = entry.getValue();

            //遍历批次数组，得到对应的批次ID和对应的小阶段数组
            for (EntitySubTask subTask : subTasks) {
                String subTaskNo = subTask.getSubTaskNo();

                //获得小阶段对应的批次的颗粒度
                int granularity = subTask.getPlanGranul();

                ArrayList<EntityStepActivity> stepActivities = subTask.getStepActivities();

                IloIntervalVar subTaskVar = (IloIntervalVar) ivSubtask.getVariable(taskNo, subTaskNo);
                //创建小阶段活动的Interval变量的数组
                int size = stepActivities.size();
                IloIntervalVar[] ivArrayStepActivities = new IloIntervalVar[size];
                //遍历小阶段活动
                for(int i = 0; i < size; i++) {
                    EntityStepActivity stepActivity = stepActivities.get(i);
                    EntityStep step = stepActivity.getStep();
                    Integer stepId = step.getStepId();
                    int stepASize = (int) Math.ceil(stepActivity.getSize());
                    //int stepASize = (int) Math.ceil(stepActivity.getSize() * 60 /granularity);
                    // 创建小阶段活动的变量
                    IloIntervalVar stepAVar = cp.intervalVar("ivStepA: subTask_" + subTaskNo + "_step_" + stepId);
                    // 设置小阶段的size
                    //todo: 根据待定需求最终确定是否设置sizeMax
                    //stepAVar.setSizeMax(stepASize);
                    stepAVar.setSizeMin(stepASize);
                    //先初始化该值为小阶段的值，在做资源的变量时再确定最大值
                    stepActivity.setMaxSizeConsideredResources(stepASize);
                    if (engineProcessedData.getPlan().isEnabledLengthMaxOfStepA()) {
                        subModelData.setIntervalVarMaxLength(stepASize, stepAVar);
                    }

                    // 设置小阶段的optional属性，为任务单的optional属性
                    if (task.getIsOptional()) {
                        stepAVar.setOptional();
                    }

                    // 设置小阶段活动的开始结束时间，为批次的开始和结束时间
                    stepAVar.setStartMin(subTaskVar.getStartMin());
                    stepAVar.setEndMax(subTaskVar.getEndMax());

//                    //生成stefStepCalendar
//                    //创建stepFunction的t的数组
//                    ilog.concert.cppimpl.IloNumArray t = new ilog.concert.cppimpl.IloNumArray();
//                    for (Double workDayModeTime : subModelData.getStepFunctionWorkdayModeTime()) {
//                        t.add(workDayModeTime);
//                    }
//
//                    //创建stepFunction的v的数组
//                    ilog.concert.cppimpl.IloNumArray v = new ilog.concert.cppimpl.IloNumArray();
//                    for (Double workDayModeValue : subModelData.getStepFunctionWorkdayModeValue()) {
//                        v.add(workDayModeValue*100);
//                    }
//                    IloNumToNumStepFunction stepFStepCalendar = cp.numToNumStepFunction();
//                    stepFStepCalendar.setSteps(t, v);
//                    stepAVar.setIntensity(stepFStepCalendar);

                    //小阶段工作模式为0或者1时，即仅工作日工作，或工作日及周末；也就是不为3，全年工作时，加step Function
                    String workMode = stepActivity.getStep().getIsConstTime();
                    if (!workMode.equals(STEP_ALL_DAYS_WORK)) {
                        IloNumToNumStepFunction stepFunction = stepFunctionHashMap.get(workMode);
                        stepAVar.setIntensity(stepFunction);
                        if (engineProcessedData.getPlan().isEnabledConstraintStepAForbidStartEnd()) {
                            cp.add(cp.forbidStart(stepAVar, stepFunction));
                            cp.add(cp.forbidEnd(stepAVar, stepFunction));

                            log.info("ConstraintStepAForbidStartEnd:: " + stepAVar.getName() + " FORBID START END stepFunction");

                        }
                    }


                    ivStepA.setVariable(taskNo, subTaskNo, stepId, stepAVar);
                    ivArrayStepActivities[i] = stepAVar;
//                    if (exprSumOfStepASize == null){
//                        exprSumOfStepASize = cp.diff(cp.sizeOf(stepAVar),stepASize);
//                    } else{
//                        exprSumOfStepASize = cp.sum(exprSumOfStepASize, cp.diff(cp.sizeOf(stepAVar),stepASize));
//                    }

                    //如果该任务单是Optional的时候，则需要加约束：批次和小阶段活动需要同时被排上
                    if(task.getIsOptional()){
                        //获得批次的变量
                        //IloIntervalVar subTaskVar = (IloIntervalVar) ivSubtask.getVariable(taskNo, subTaskNo);
                        //获得小阶段活动的变量
                        //IloIntervalVar stepAVariable = (IloIntervalVar) ivStepA.getVariable(taskNo, subTaskNo, stepActivities.get(i).getStep().getStepId());
                        if (engineProcessedData.getPlan().isEnabledConstraintStepARelationSubtaskStepAPresence()) {
                            cp.add(cp.eq(cp.presenceOf(subTaskVar), cp.presenceOf(stepAVar)));

                            log.info("ConstraintStepARelationSubtaskStepAPresence:: " + subTaskVar.getName() + " SAME PRESENCE WITH " + stepAVar.getName());

                        }
                    }

                    // 如果有前序小阶段活动，添加活动顺序相关约束
                    EntityStepActivity previousStepActivity = stepActivity.getPrevious();
                    if(previousStepActivity != null){
                        IloIntervalVar previousVar = (IloIntervalVar) ivStepA.getVariable(taskNo, subTaskNo, previousStepActivity.getStep().getStepId());
                        if(engineProcessedData.getPlan().isEnabledConstraintStepARelationStepASequence()) {
                            // 添加顺序约束
                            cp.add(cp.endBeforeStart(previousVar, stepAVar));

                            log.info("ConstraintStepARelationStepASequence:: " + previousVar.getName() + " END BEFORE START " + stepAVar.getName());
                        }

                        // 与上一个小阶段的最大间隔小时约束
                        // 当MaxStepGap为空值时，无需该约束条件
                        if(stepActivity.getStep().getMaxStepGap() == null){
                            continue;
                        }
                        // 需要考虑颗粒度,精确的到毫秒，并且向下取整
                        int maxStepGap = (int) Math.floor(step.getMaxStepGap().doubleValue() *60 / granularity);
                        // 添加最大间隔小时约束
                        if (engineProcessedData.getPlan().isEnabledConstraintStepARelationStepAMaxGap()) {
                            cp.add(cp.startBeforeEnd(stepAVar, previousVar, -maxStepGap));

                            log.info("ConstraintStepARelationStepAMaxGap:: " + stepAVar.getName() + " START AFTER " + previousVar.getName() + " END WITHIN " + maxStepGap + " slots");
                        }
                    }
                }
                // 批次活动span小阶段活动的约束
                if (engineProcessedData.getPlan().isEnabledConstraintStepARelationSubtaskSpanStepA()) {
                    cp.add(cp.span(subTaskVar, ivArrayStepActivities));

                    log.info("ConstraintStepARelationSubtaskSpanStepA:: " + subTaskVar.getName() + " SPAN ");
                    Stream.of(ivArrayStepActivities).forEach(v->log.info("ConstraintStepARelationSubtaskSpanStepA:: " +  v.getName()));
                }
            }
        }
        //subModel.setExprSumOfStepASize(exprSumOfStepASize);
        return true;
    }

    @Override
    public Vector<ConstraintViolationPlanResult> check() {
        return null;
    }

}
