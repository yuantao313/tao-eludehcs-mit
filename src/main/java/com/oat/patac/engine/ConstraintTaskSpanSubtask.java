package com.oat.patac.engine;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.cp.ConstraintViolationPlanResult;
import com.oat.patac.entity.EntityFunction;
import com.oat.patac.entity.EntitySubTask;
import com.oat.patac.entity.EntityTask;

import java.util.*;
import java.util.stream.Stream;

import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntervalVar;
import ilog.concert.IloNumExpr;
import lombok.extern.log4j.Log4j2;

import static com.oat.common.utils.ConstantUtil.IS_NEED_NEW_FIXTURE;
import static com.oat.common.utils.ConstantUtil.MESSAGE_SEVERITY_ERROR;

/**
 * 任务单 span 批次的约束
 * 同时初始化了ivTask，和ivSubTask
 */
@Log4j2
public class ConstraintTaskSpanSubtask extends PatacConstraint {


    public ConstraintTaskSpanSubtask(SubModel model) {
        super(model);
        setConstraintName("Task Span SubTask Constraint");
    }

    @Override
    public boolean apply() throws IloException {
        //获得子模型中需要考虑的任务单和下面的批次
        HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMap = subModelData.getToBePlannedSubTaskMap();
        //获得task的id和对象的map
        HashMap<String, EntityTask> taskHashMap = engineProcessedData.getTaskHashMap();
        //获得在sub model中的变量定义
        CPModel.Var1DArray ivTask = subModel.getIvTask();
        CPModel.Var2DArray ivSubTask = subModel.getIvSubTask();
        //获得其他需要的参数
        EntityFunction function = engineProcessedData.getFunctionHashMap().get(engineProcessedData.getFunctionId());
        int granularity = subModelData.getPlanGranularity();
        Date subModelStart = subModelData.getMinPlanStartTime();
        Date subModelEnd = subModelData.getMaxPlanEndTime();
        int subModelEndInt = subModelData.getEndIntTime();
        // 获得exprLateSubtasks
        IloIntExpr exprLateSubtasks = subModel.getExprLateSubtasks();
        IloIntExpr exprLateSubtask;
        // 获得exprLateTasks
        IloNumExpr exprLateTasks = subModel.getExprLateTasks();
        IloNumExpr exprLateTask;

        boolean errorFlag = false;

        //循环每个Task和Task内的待排程SubTask，加约束
        for (Map.Entry<String ,ArrayList<EntitySubTask>> entry: toBePlannedSubTaskMap.entrySet()) {
            String taskNo = entry.getKey();
            EntityTask task = taskHashMap.get(taskNo);
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            //创建task 的interval variable
            IloIntervalVar taskVar = cp.intervalVar("ivTask: task_"+taskNo);
            // 设置变量的optional属性
            if (task.getIsOptional()) {
                taskVar.setOptional();
            }
            // 设置任务单变量的最早开始时间
            Date minTaskStart = subModelStart;
            if (task.getIsNeedNewFixture().equals(IS_NEED_NEW_FIXTURE)
                && (task.getFixtureReadyTime().compareTo(minTaskStart) > 0 )){
                minTaskStart = task.getFixtureReadyTime();
                int minTaskStartInt = DateUtil.getDistanceIntTime(minTaskStart, subModelStart,
                        1000*60*granularity);
                taskVar.setStartMin(minTaskStartInt);
            }
            // 设置任务单变量的最晚结束时间
            Date maxTaskEnd = subModelEnd;
            int maxTaskEndInt = subModelEndInt;
            taskVar.setEndMax(maxTaskEndInt);


            ivTask.setVariable(taskNo, taskVar);

            int size = subTasks.size();
            //创建subtask 的interval变量的数组
            IloIntervalVar[] ivArraySubtask = new IloIntervalVar[size];

            for (int i = 0; i < size; i++) {
                EntitySubTask subTask = subTasks.get(i);
                String subTaskNo = subTask.getSubTaskNo();
                //创建subtask 的interval variable
                IloIntervalVar subtaskVar = cp.intervalVar("ivSubtask: subtask_"+ subTaskNo);

//                if (subTask.isFixed() || subTask.isPlanned()){
//                    // 设置固定批次的开始和结束时间
//                    Date startTime = subTask.getStartTime();
//                    if (startTime.compareTo(subModelEnd) > 0){
//                        // 固定批次 在子模型排程结束时间后才开始
//                        log.info("固定的Sub task " + subTaskNo + " 在该子模型排程结束后才开始，所以不予考虑。");
//                        break;
//                    }
//                    Date endTime = subTask.getEndTime();
//                    if (endTime.compareTo(subModelStart) < 0) {
//                        // 固定批次 在子模型排程开始时间前已经结束
//                        log.info("固定的Sub task " + subTaskNo + " 在该子模型排程开始前已经完成，所以不予考虑。");
//                        break;
//                    }
//                    if (startTime.compareTo(minTaskStart) < 0 ) {
//                        // 固定批次 开始时间晚于任务单变量最早开始时间
//                        startTime = minTaskStart;
//                    }
//                    int starTimeInt = DateUtil.getDistanceIntTime(startTime, subModelStart,
//                            1000*60*granularity);
//                    subtaskVar.setStartMin(starTimeInt);
//                    subtaskVar.setStartMax(starTimeInt);
//
//                    if (endTime.compareTo(maxTaskEnd) > 0 ) {
//                        // 在任务单变量最晚结束时间之后结束，设置为任务单变量最晚结束时间
//                        subtaskVar.setEndMin(maxTaskEndInt);
//                        subtaskVar.setEndMax(maxTaskEndInt);
//                    } else {
//                        // 在子模型排程结束时间之前结束，设置为批次结束时间
//                        int endTimeInt = DateUtil.getDistanceIntTime(endTime, subModelStart,
//                                1000*60*granularity);
//                        subtaskVar.setEndMin(endTimeInt);
//                        subtaskVar.setEndMax(endTimeInt);
//                    }
//                } else {
                    // 设置待排程批次
                    // 设置变量的optional属性
                    if (task.getIsOptional()) {
                        subtaskVar.setOptional();
                    }
                    // 设置批次变量的最早开始时间

                    if (!CheckDataUtil.timeCompare(subTask.getProvideDate(), subModelEnd, MESSAGE_SEVERITY_ERROR,
                            "待排程的Sub task " + subTaskNo + "的样件提供时间"+subTask.getProvideDate()+"晚于该子模型的排程结束时间"+subModelEnd+"，所以无法对该批次排程。")){
                        // 样品组提供时间晚于模型结束时间
                        log.error("待排程的Sub task " + subTaskNo + "的样件提供时间"+subTask.getProvideDate()+"晚于该子模型的排程结束时间"+subModelEnd+"，所以无法对该批次排程。");
                        //todo: 如果预处理正确，不会出现这种情况
                        errorFlag = true;
                        break;
                    }
                    // 取批次最早时间和样件提供时间的最晚时间
                    Date minSubTaskStart = DateUtil.getMaxTime(subTask.getMinPlanStartTime(), subTask.getProvideDate());
                    // 进一步取和任务单变量最早开始时间比较后的最晚时间
                    minSubTaskStart = DateUtil.getMaxTime(minTaskStart, minSubTaskStart);
                    int minSubTaskStartInt = DateUtil.getDistanceIntTime(minSubTaskStart, subModelStart,
                            1000 * 60 * granularity);
                    subtaskVar.setStartMin(minSubTaskStartInt);
                    // 设置批次变量的最晚结束时间
                    // 取批次最晚结束时间和任务单最晚结束时间中的最早时间
                    Date maxSubTaskEnd = DateUtil.getMinTime(subTask.getMaxPlanEndTime(), maxTaskEnd);
                    int maxSubTaskEndInt = DateUtil.getDistanceIntTime(maxSubTaskEnd, subModelStart,
                            1000 * 60 * granularity);
                    subtaskVar.setEndMax(maxSubTaskEndInt);
                //}

                ivSubTask.setVariable(taskNo, subTaskNo, subtaskVar);
                ivArraySubtask[i] = subtaskVar;

                //得到批次对应的试验期望完成时间
                if (subTask.getIsToBePlanned()) {
                    int taskReturnDate = DateUtil.getDistanceIntTime(subTask.getReturnDate(), subModelStart,
                            1000 * 60 * granularity);

                    exprLateSubtask = cp.max(cp.diff(cp.endOf((IloIntervalVar) ivSubTask.getVariable(taskNo, subTask.getSubTaskNo()), taskReturnDate), taskReturnDate),0);
                    if (exprLateSubtasks == null) {
                        exprLateSubtasks = exprLateSubtask;
                    } else {
                        exprLateSubtasks = cp.sum(exprLateSubtasks, exprLateSubtask);
                    }
                }
            }
            if (!errorFlag) {
                if (engineProcessedData.getPlan().isEnabledConstraintTaskSpanSubtask()) {
                    //创建task span subtask的约束
                    cp.add(cp.span(taskVar, ivArraySubtask));

                    log.info("ConstraintTaskSpanSubtask:: " + taskVar.getName() + " SPAN ");
                    Stream.of(ivArraySubtask).forEach(v->log.info("ConstraintTaskSpanSubtask:: " +  v.getName()));
                }
            }
            //得到任务单的审批完成时间
            int taskApproveCompleteDate = DateUtil.getDistanceIntTime(task.getApproveCompleteDate(),subModelStart,
                    1000 * 60 * granularity);
            Date maxApprovalDateOfTasks = function.getMaxApprovalDateOfTasks();
            int maxApprovalInt = DateUtil.getDistanceIntTime(maxApprovalDateOfTasks,subModelStart,
                    1000 * 60 * granularity);
            double gap = ((double)(maxApprovalInt-taskApproveCompleteDate))/24/10 +1;

            exprLateTask = cp.prod(cp.max(cp.diff(cp.endOf(taskVar,taskApproveCompleteDate),taskApproveCompleteDate),0), gap);
            //exprLateTask = cp.max(cp.diff(cp.endOf(taskVar,taskApproveCompleteDate),taskApproveCompleteDate),0);
            if(exprLateTasks == null){
                exprLateTasks = exprLateTask;
            }else{
                exprLateTasks = cp.sum(exprLateTasks,exprLateTask);
            }
        }
        if (errorFlag) {
            return false;
        }
        //设置表达式：尽量在批次“试验期望完成时间”之前完成

        subModel.setExprLateSubtasks(exprLateSubtasks);
        //设置表达式：尽量减少计划完成时间和任务单“审批完成时间”的差
        subModel.setExprLateTasks(exprLateTasks);
        return true;
    }

    @Override
    public Vector<ConstraintViolationPlanResult> check() {
        return null;
    }
}
