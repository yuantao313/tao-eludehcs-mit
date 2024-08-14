package com.oat.patac.service.impl;

import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.DateUtil;
import com.oat.common.utils.CsvFileUtil;
import com.oat.patac.dao.*;
import com.oat.patac.dataAccess.DataContainer;
import com.oat.patac.dataAccess.LoadData;
import com.oat.patac.engine.EngineProcessedData;
import com.oat.patac.engine.PatacSchedulingTask;
import com.oat.patac.entity.*;
import com.oat.patac.service.PlanService;
import ilog.concert.IloException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;

@Service
@Log4j2
public class PlanServiceImpl implements PlanService {

    @Resource
    private LoadData loadData;
    @Resource
    private TaskPlanOutputMapper taskPlanOutputMapper;
    @Resource
    private SubTaskPlanOutputMapper subTaskPlanOutputMapper;
    @Resource
    private StepPlanOutputMapper stepPlanOutputMapper;
    @Resource
    private StaffPlanOutputMapper staffPlanOutputMapper;
    @Resource
    private EquipmentPlanOutputMapper equipmentPlanOutputMapper;
    @Resource
    private PlanMapper planMapper;
    @Resource
    private MessageMapper messageMapper;
    private PatacSchedulingTask schedulingTask;
    public PatacSchedulingTask getSchedulingTask() {
        return schedulingTask;
    }
    /**
     * 老DB和老模式调用的函数
     * @param functionId
     * @param planMode
     * @param toBePlannedSubTaskIds
     * @return
     * @throws IloException
     * @throws ParseException
     * @deprecated
     */
    @Override
    public boolean createPlan(Integer functionId, int planMode, ArrayList<String> toBePlannedSubTaskIds) throws IloException, ParseException {
        //TODO: - by Sophia
        log.info("开始准备排程...");
        //从数据库获得数据
        DataContainer dataContainer = loadData.getDataContainer(functionId, planMode);
        //设置约束是否起作用
        EntityPlan plan = dataContainer.getPlan();

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData,dataContainer, planMode, toBePlannedSubTaskIds);
        //生成模型需要的数据，并同时检查数据
        // 根据是否有无法进行排程的问题，决定是否调用下面的solve（）
//        if (schedulingTask.processAndCheckData()){
//            //建立模型并求解
//            schedulingTask.solve();
//        }
        schedulingTask.processAndCheckData();
        schedulingTask.solve(MODEL_VERSION_2);

        EntityPlanResult planResult = schedulingTask.getPlanResult();
        boolean planSucceeded = !planResult.isPlanFailed();
        log.info("Planning result is: " + planSucceeded);
        if (planSucceeded) {
            savePlanResult(schedulingTask, planSucceeded,false);
        }

        return planSucceeded;
    }

    /**
     * 新DB和新模式调用的函数
     * @return
     * @throws IloException
     * @throws ParseException
     */
    @Override
    public boolean createPlan(Integer functionId, String planId) {
            return createPlanLocal(functionId, planId, null, null,
                    null, DEFAULT_MODEL_VERSION, true, null, SOLVE_MODE_DEFAULT);
    }

    // 用于测试约束，并可以给定排程触发时间
    @Override
    public boolean createPlanForTest(Integer functionId, String planId,
                                     HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                                     Date planTriggeredTime, String modelVersion)  {
        return createPlanLocal(functionId, planId, constraintSelections, constraintDefaultValue,
                planTriggeredTime, modelVersion, false, null,SOLVE_MODE_DEFAULT);
    }

    // 用于测试，并可以设置是否保存数据
    @Override
    public boolean createPlanForTest(Integer functionId, String planId,
                                     HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                                     Date planTriggeredTime, String modelVersion, boolean saveDataFlag)  {
        return createPlanLocal(functionId, planId, constraintSelections, constraintDefaultValue,
                planTriggeredTime, modelVersion, saveDataFlag, null, SOLVE_MODE_DEFAULT);
    }
    // 用于测试，并可以给定待排程的任务单
    @Override
    public boolean createPlanForTest(Integer functionId, String planId,
                                     HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                                     Date planTriggeredTime, String modelVersion, boolean saveDataFlag,
                                     ArrayList<String> taskNos, int solveMode) {
        return createPlanLocal(functionId, planId, constraintSelections, constraintDefaultValue,
                planTriggeredTime, modelVersion, saveDataFlag, taskNos, solveMode);
    }


    private boolean createPlanLocal(Integer functionId, String planId,
                                    HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                                    Date planTriggeredTime, String modelVersion, boolean saveDataFlag,
                                    ArrayList<String> taskNos, int solveMode) {
        try {
            Date startSchedulingTime = new Date();
            log.info(DateUtil.getCurrentTimeString() + " 开始准备排程...");
            //从数据库获得数据
            DataContainer dataContainer = loadData.initializeDataContainer(functionId, planId);

            //设置约束是否起作用
            EntityPlan plan = dataContainer.getPlan();
            if (planTriggeredTime != null) {
                plan.initialize(planTriggeredTime);
            }
            if (constraintSelections != null) {
                setConstraints(constraintSelections, plan, constraintDefaultValue);
            }

            //创建排程任务
            schedulingTask = new PatacSchedulingTask(loadData, dataContainer, taskNos, solveMode);
            boolean planSuccessFlag;
            //生成模型需要的数据，并同时检查数据
            // 根据是否有无法进行排程的问题，决定是否调用下面的solve（）
            if (!schedulingTask.loadAndProcessData()) {
                log.info("加载和处理数据出现错误或没有有效任务单需要排程，引擎退出");
                planSuccessFlag = false;
            } else {
                //建立模型并求解
                if (!schedulingTask.solve(modelVersion)) {
                    // 没有排程任何一个任务单
                    log.info("模型求解过程中出现错误，引擎退出");
                    planSuccessFlag = false;
                } else {
                    // 至少排出一个任务单
                    log.info("模型部分或完全求解成功，输出结果");
                    planSuccessFlag = true;
                }

            }
            log.info("Planning result is: " + planSuccessFlag);
            savePlanResult(schedulingTask, planSuccessFlag, saveDataFlag);
            Date endSchedulingTime = new Date();
            log.info(DateUtil.getCurrentTimeString() + " 排程结束，共耗时 " + (endSchedulingTime.getTime() - startSchedulingTime.getTime()) / 1000 + " 秒");

            return planSuccessFlag;
        }catch (RuntimeException e){
            log.error("Runtime Exception, please contact service. ");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置每个约束为模型原始设置，或者全部enable，全部disable，然后按照HashMap中的要求来设置。
     * 在生产模式下，constraintSelection可以设置为null，即用程序默认设置
     * 在测试模式下，可以默认所有约束为disable，在constraintSelection中将仅被测试的约束为true
     * @param constraintSelections: 每个约束对应是否enable
     * @param plan：本次排程对应的plan实体
     * @param defaultValue: 指默认每个约束是enabled，还是disabled；null为使用程序默认；true为默认为enabled；false为默认为disabled。
     */
    private void setConstraints(HashMap<String, Boolean> constraintSelections, EntityPlan plan, Boolean defaultValue) {
        if (defaultValue != null){
            setAllConstraints(defaultValue, plan);
        }
        for (Map.Entry<String, Boolean> constraint: constraintSelections.entrySet()) {
            switch(constraint.getKey()) {
                case ConstantUtil.ConstraintTaskSpanSubtask: plan.setEnabledConstraintTaskSpanSubtask(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintTaskSubtaskPresence: plan.setEnabledConstraintTaskSubtaskPresence (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintSubtaskGroupSequence: plan.setEnabledConstraintSubtaskGroupSequence (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintStepAForbidStartEnd: plan.setEnabledConstraintStepAForbidStartEnd(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintStepARelationSubtaskSpanStepA: plan.setEnabledConstraintStepARelationSubtaskSpanStepA (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintStepARelationSubtaskStepAPresence: plan.setEnabledConstraintStepARelationSubtaskStepAPresence (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintStepARelationStepASequence: plan.setEnabledConstraintStepARelationStepASequence (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintStepARelationStepAMaxGap: plan.setEnabledConstraintStepARelationStepAMaxGap (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintSampleANoOverlap: plan.setEnabledConstraintSampleANoOverlap (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintResourceARelationStepAAssignResGroupRes: plan.setEnabledConstraintResourceARelationStepAAssignResGroupRes (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceAForbidStartEnd: plan.setEnabledConstraintResourceAForbidStartEnd(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceAPresenceNotByDay: plan.setEnabledConstraintResourceAPresenceNotByDay(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAStartAtResANotByDay: plan.setEnabledConstraintResourceARelationStepAStartAtResANotByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAStartBeforeResANotByDay: plan.setEnabledConstraintResourceARelationStepAStartBeforeResANotByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAEndAtResANotByDay: plan.setEnabledConstraintResourceARelationStepAEndAtResANotByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAEndAfterResANotByDay: plan.setEnabledConstraintResourceARelationStepAEndAfterResANotByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationResourceAStateNotByDay: plan.setEnabledConstraintResourceARelationResourceAStateNotByDay (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintResourceAPresenceByDay: plan.setEnabledConstraintResourceAPresenceByDay(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAStartBeforeResAByDay: plan.setEnabledConstraintResourceARelationStepAStartBeforeResAByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAEndAfterResAByDay: plan.setEnabledConstraintResourceARelationStepAEndAfterResAByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationResASpanDays: plan.setEnabledConstraintResourceARelationResASpanDays (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAStartAtResAByDay: plan.setEnabledConstraintResourceARelationStepAStartAtResAByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationStepAEndAtResAByDay: plan.setEnabledConstraintResourceARelationStepAEndAtResAByDay (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceARelationResourceAStateByDay: plan.setEnabledConstraintResourceARelationResourceAStateByDay (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintResourceCapacity: plan.setEnabledConstraintResourceCapacity (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintResourceAFixedState: plan.setEnabledConstraintResourceAFixedState (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA: plan.setEnabledConstraintResourceAssignmentRelationSubtaskStepA (constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes: plan.setEnabledConstraintResourceAssignmentRelationSubtaskAssignSameRes (constraint.getValue());
                    continue;

                case ConstantUtil.ConstraintEngineerAssignmentForReportingPhase: plan.setEnabledConstraintEngineerAssignmentForReportingPhase(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA: plan.setEnabledConstraintEngineerAssignmentForTaskAndStepA(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintEngineerAssignmentForTask: plan.setEnabledConstraintEngineerAssignmentForTask(constraint.getValue());
                    continue;
                // the following is added from model3
                case ConstantUtil.ConstraintResourceGroupANotByDayPresence: plan.setEnabledConstraintResourceGroupANotByDayPresence(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupANotByDayInStepARange: plan.setEnabledConstraintResourceGroupANotByDayInStepARange(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupADayRangePresence: plan.setEnabledConstraintResourceGroupADayRangePresence(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupADayRangeSequence: plan.setEnabledConstraintResourceGroupADayRangeSequence(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupADayRangeInStepARange: plan.setEnabledConstraintResourceGroupADayRangeInStepARange(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupAByDayPresence: plan.setEnabledConstraintResourceGroupAByDayPresence(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange: plan.setEnabledConstraintResourceGroupAByDayInResourceGroupADayRange(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupAAlternativeResource: plan.setEnabledConstraintResourceGroupAAlternativeResource(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceAState: plan.setEnabledConstraintResourceAState(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd: plan.setEnabledConstraintResourceGroupADayRangeForbidStartEnd(constraint.getValue());
                    continue;

                // the following is for objective:
                case ConstantUtil.ObjectiveOptionalTasks: plan.setEnabledObjectiveOptionalTasks(constraint.getValue());
                    continue;
                case ConstantUtil.ObjectiveLateSubTasks: plan.setEnabledObjectiveLateSubTasks(constraint.getValue());
                    continue;
                case ConstantUtil.ObjectiveLateTasks: plan.setEnabledObjectiveLateTasks(constraint.getValue());
                    continue;
                case ConstantUtil.ObjectiveAdditionalUsedResourceQty: plan.setEnabledObjectiveAdditionalUsedResourceQty(constraint.getValue());
                    continue;
                case ConstantUtil.ObjectiveResourceUnbalance: plan.setEnabledObjectiveResourceUnbalance(constraint.getValue());
                    continue;
                case ConstantUtil.ObjectiveSumOfStepASize: plan.setEnabledObjectiveSumOfStepASize(constraint.getValue());
                    continue;
                case ConstantUtil.LengthMaxOfStepA: plan.setEnabledLengthMaxOfStepA(constraint.getValue());
                    continue;
                case ConstantUtil.LengthMaxOfResourceA: plan.setEnabledLengthMaxOfResourceA(constraint.getValue());
                    continue;
                // the following is for replacement of alternative constraints
                case ConstantUtil.ConstraintResourceAInStepARange: plan.setEnabledConstraintResourceAInStepARange(constraint.getValue());
                    continue;
                case ConstantUtil.ConstraintResourceABeAssignedQuantity: plan.setEnabledConstraintResourceABeAssignedQuantity(constraint.getValue());
                    continue;

                // the above is for replacement of alternative constraints
                default:
                    throw new IllegalStateException("Unexpected value: " + constraint);
            }
        }
    }

    private void setAllConstraints(Boolean defaultValue, EntityPlan plan) {
        plan.setEnabledConstraintTaskSpanSubtask(defaultValue);
        plan.setEnabledConstraintTaskSubtaskPresence(defaultValue);
        plan.setEnabledConstraintSubtaskGroupSequence(defaultValue);

        plan.setEnabledConstraintStepAForbidStartEnd(defaultValue);
        plan.setEnabledConstraintStepARelationSubtaskSpanStepA(defaultValue);
        plan.setEnabledConstraintStepARelationSubtaskStepAPresence(defaultValue);
        plan.setEnabledConstraintStepARelationStepASequence(defaultValue);
        plan.setEnabledConstraintStepARelationStepAMaxGap(defaultValue);

        plan.setEnabledConstraintSampleANoOverlap(defaultValue);

        plan.setEnabledConstraintResourceARelationStepAAssignResGroupRes(defaultValue);

        plan.setEnabledConstraintResourceAForbidStartEnd(defaultValue);
        plan.setEnabledConstraintResourceAPresenceNotByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAStartAtResANotByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAStartBeforeResANotByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAEndAtResANotByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAEndAfterResANotByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationResourceAStateNotByDay(defaultValue);

        plan.setEnabledConstraintResourceAPresenceByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAStartBeforeResAByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAEndAfterResAByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationResASpanDays(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAStartAtResAByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationStepAEndAtResAByDay(defaultValue);
        plan.setEnabledConstraintResourceARelationResourceAStateByDay(defaultValue);

        plan.setEnabledConstraintResourceCapacity(defaultValue);

        plan.setEnabledConstraintResourceAFixedState(defaultValue);

        plan.setEnabledConstraintResourceAssignmentRelationSubtaskStepA(defaultValue);
        plan.setEnabledConstraintResourceAssignmentRelationSubtaskAssignSameRes(defaultValue);

        plan.setEnabledConstraintEngineerAssignmentForReportingPhase(defaultValue);
        plan.setEnabledConstraintEngineerAssignmentForTaskAndStepA(defaultValue);
        plan.setEnabledConstraintEngineerAssignmentForTask(defaultValue);
        // the following is added from model3
        plan.setEnabledConstraintResourceGroupANotByDayPresence(defaultValue);
        plan.setEnabledConstraintResourceGroupANotByDayInStepARange(defaultValue);
        plan.setEnabledConstraintResourceGroupADayRangePresence(defaultValue);
        plan.setEnabledConstraintResourceGroupADayRangeSequence(defaultValue);
        plan.setEnabledConstraintResourceGroupADayRangeInStepARange(defaultValue);
        plan.setEnabledConstraintResourceGroupAByDayPresence(defaultValue);
        plan.setEnabledConstraintResourceGroupAByDayInResourceGroupADayRange(defaultValue);
        plan.setEnabledConstraintResourceGroupAAlternativeResource(defaultValue);
        plan.setEnabledConstraintResourceAState(defaultValue);
        plan.setEnabledConstraintResourceGroupADayRangeForbidStartEnd(defaultValue);

        plan.setEnabledObjectiveOptionalTasks(defaultValue);
        plan.setEnabledObjectiveLateSubTasks(defaultValue);
        plan.setEnabledObjectiveLateTasks(defaultValue);
        plan.setEnabledObjectiveAdditionalUsedResourceQty(defaultValue);
        plan.setEnabledObjectiveResourceUnbalance(defaultValue);
        plan.setEnabledObjectiveSumOfStepASize(defaultValue);

        plan.setEnabledLengthMaxOfStepA(defaultValue);
        plan.setEnabledLengthMaxOfResourceA(defaultValue);
        // the following is for replacement of alternative constraints
        plan.setEnabledConstraintResourceAInStepARange(defaultValue);
        plan.setEnabledConstraintResourceABeAssignedQuantity(defaultValue);

        // the above is for replacement of alternative constraints
    }

    /**
     * 将排程结果写到_OUTPUT表中
     */
    private void savePlanResult(PatacSchedulingTask schedulingTask, boolean planSuccessFlag, boolean saveDataFlag) {

        // 输出 plan 表
        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        EntityPlan plan = engineProcessedData.getPlan();
        plan.setPlanCompleteTime(new Date());
        plan.setPlanStatus(ConstantUtil.PLAN_STATUS_COMPLETE);

        //log.info("plan: " + plan);
        log.info("messages: " );
        ArrayList<EntityMessage> messages = EngineProcessedData.messages;
        messages.forEach(v->v.printMessage());
        schedulingTask.addInfoToMessages();

        if (saveDataFlag) {
            log.info("正在保存数据：");
            planMapper.insertPlan(plan);
            if(messages.size() != 0){
                messageMapper.insertAllMessage(messages);
            }
        }


        if (!planSuccessFlag){
            return;
        }

        plan.setPlanIsCompleted(true);

        // 获得输出
        ArrayList<EntityTaskPlanOutput> taskPlanOutputs = new ArrayList<>(engineProcessedData.getTaskPlanOutputHashMap().values()) ;
        ArrayList<EntitySubTaskPlanOutput> subTaskPlanOutputs = new ArrayList<>(engineProcessedData.getSubTaskPlanOutputHashMap().values());
        ArrayList<EntityStepPlanOutput> stepPlanOutputs = new ArrayList<>(engineProcessedData.getStepPlanOutputHashMap().values());
        for (EntityStepPlanOutput stepPlanOutput : stepPlanOutputs) {
            stepPlanOutput.setPlanDuration(stepPlanOutput.getPlanDuration().setScale(6, RoundingMode.HALF_UP));
            stepPlanOutput.setReqProcTime(stepPlanOutput.getReqProcTime().setScale(6, RoundingMode.HALF_UP));
            stepPlanOutput.setPlanProcTime(stepPlanOutput.getPlanProcTime().setScale(6, RoundingMode.HALF_UP));
        }

        ArrayList<EntityStaffPlanOutput> staffPlanOutputs = new ArrayList<>();
        for (ArrayList<EntityStaffPlan> staffPlans : engineProcessedData.getStaffStaffPlanOutputHashMap().values()) {
            for (EntityStaffPlan staffPlan : staffPlans) {
                EntityStaffPlanOutput staffPlanOutput = (EntityStaffPlanOutput) staffPlan;
                staffPlanOutput.setPlanWorkTime(staffPlanOutput.getPlanWorkTime().setScale(6, RoundingMode.HALF_UP));
                staffPlanOutput.setReqWorkTime(staffPlanOutput.getReqWorkTime().setScale(6, RoundingMode.HALF_UP));
                staffPlanOutputs.add((EntityStaffPlanOutput) staffPlan);
            }
        }
        ArrayList<EntityEquipmentPlanOutput> equipmentPlanOutputs = new ArrayList<>();
        for (ArrayList<EntityEquipmentPlan> equipmentPlans : engineProcessedData.getEquipmentEquipmentPlanOutputHashMap().values()) {
            for (EntityEquipmentPlan equipmentPlan : equipmentPlans) {
                EntityEquipmentPlanOutput equipmentPlanOutput = (EntityEquipmentPlanOutput) equipmentPlan;
                equipmentPlanOutput.setPlanWorkTime(equipmentPlanOutput.getPlanWorkTime().setScale(6, RoundingMode.HALF_UP));
                equipmentPlanOutput.setReqWorkTime(equipmentPlanOutput.getReqWorkTime().setScale(6, RoundingMode.HALF_UP));
                equipmentPlanOutputs.add((EntityEquipmentPlanOutput) equipmentPlan);
            }
        }

        log.info("Size of taskPlanOutputs: " + taskPlanOutputs.size());
        log.info("Size of subTaskPlanOutputs: " + subTaskPlanOutputs.size());
        log.info("Size of stepPlanOutputs: " +  stepPlanOutputs.size());
        log.info("Size of staffPlanOutputs: " + staffPlanOutputs.size());
        //log.info("staffPlanOutputs: " + staffPlanOutputs);
        log.info("Size of equipmentPlanOutputs: " + equipmentPlanOutputs.size());

        // 将模型结果写出到数据库
        if (saveDataFlag) {
            if (taskPlanOutputs.size() != 0) {
                taskPlanOutputMapper.insertAllTaskPlanOutputs(taskPlanOutputs);
            }
            if (subTaskPlanOutputs.size() != 0) {
                subTaskPlanOutputMapper.insertAllSubTaskPlanOutputs(subTaskPlanOutputs);
            }
            if (stepPlanOutputs.size() != 0) {
                stepPlanOutputMapper.insertAllStepPlanOutputs(stepPlanOutputs);
            }
            if (staffPlanOutputs.size() != 0) {
                staffPlanOutputMapper.insertAllStaffPlanOutputs(staffPlanOutputs);
            }
            if (equipmentPlanOutputs.size() != 0) {
                equipmentPlanOutputMapper.insertAllEquipmentPlanOutputs(equipmentPlanOutputs);
            }
        }

    }


}
