package com.oat.patac.entity;

import com.oat.common.utils.DateUtil;
import com.oat.common.utils.IdUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * 排程任务
 */
@Log4j2
@Data
public class EntityPlan {

    //计划的ID
    private String planId="DEFAULT"; //=IdUtil.getTimeBasedId();


    //计划的模式：自动基础，手动触发
    //PLAN_MODE_AUTOMATIC or PLAN_MODE_MANUAL_TRIGGERED or PLAN_MODE_UNKNOWN
    private int planMode = PLAN_MODE_UNKNOWN;

    private Integer functionId;

    /**
     * 排程周期开始的时间
     */
    private Date planPeriodStartTime;
    /**
     * 排程周期结束的时间
     */
    private Date planPeriodEndTime;
    /**
     * 排程触发的时间
     */
    private Date planTriggeredTime;
    /**
     * 排程完成的时间
     */
    private Date planCompleteTime;
    /**
     * 排程的状态
     */
    private String planStatus;

    /**
     * 排程是否成功
     */
    private boolean planIsCompleted = false;

    private String modelVersion = MODEL_VERSION_3;

    private boolean isEnabledConstraintTaskSpanSubtask = true;
    private boolean isEnabledConstraintTaskSubtaskPresence = true;
    private boolean isEnabledConstraintSubtaskGroupSequence = true;

    private boolean isEnabledConstraintStepAForbidStartEnd = true;
    private boolean isEnabledConstraintStepARelationSubtaskSpanStepA = true;
    private boolean isEnabledConstraintStepARelationSubtaskStepAPresence = true;
    private boolean isEnabledConstraintStepARelationStepASequence = true;
    private boolean isEnabledConstraintStepARelationStepAMaxGap = true;

    private boolean isEnabledConstraintSampleANoOverlap = true;
    // the following is only for model v3
    private boolean isEnabledConstraintResourceGroupANotByDayPresence = true;
    private boolean isEnabledConstraintResourceGroupANotByDayInStepARange = true;

    private boolean isEnabledConstraintResourceGroupADayRangePresence = true;
    private boolean isEnabledConstraintResourceGroupADayRangeSequence = true;
    private boolean isEnabledConstraintResourceGroupADayRangeInStepARange = true;
    private boolean isEnabledConstraintResourceGroupADayRangeForbidStartEnd = true;

    private boolean isEnabledConstraintResourceGroupAByDayPresence = true;
    private boolean isEnabledConstraintResourceGroupAByDayInResourceGroupADayRange = true;

    private boolean isEnabledConstraintResourceGroupAAlternativeResource = true;
    private boolean isEnabledConstraintResourceAState = true;
    // the above is only for model3
    // the following is for both model2 and model3
    private boolean isEnabledConstraintResourceAForbidStartEnd = true;
    private boolean isEnabledConstraintResourceAPresenceNotByDay = true;
    private boolean isEnabledConstraintResourceAPresenceByDay = true;
    // the above is for both model2 and model3

    // the following is for replacement of alternative constraints
    private boolean isEnabledConstraintResourceAInStepARange = true;
    private boolean isEnabledConstraintResourceABeAssignedQuantity = true;
    // the above is for replacement of alternative constraints
    // the following is only for model2
    private boolean isEnabledConstraintResourceARelationStepAAssignResGroupRes = true;

    private boolean isEnabledConstraintResourceARelationStepAStartAtResANotByDay = true;
    private boolean isEnabledConstraintResourceARelationStepAStartBeforeResANotByDay = true;
    private boolean isEnabledConstraintResourceARelationStepAEndAtResANotByDay = true;
    private boolean isEnabledConstraintResourceARelationStepAEndAfterResANotByDay = true;
    private boolean isEnabledConstraintResourceARelationResourceAStateNotByDay = true;

    private boolean isEnabledConstraintResourceARelationStepAStartBeforeResAByDay = true;
    private boolean isEnabledConstraintResourceARelationStepAEndAfterResAByDay = true;
    private boolean isEnabledConstraintResourceARelationResASpanDays = true;
    private boolean isEnabledConstraintResourceARelationStepAStartAtResAByDay = true;
    private boolean isEnabledConstraintResourceARelationStepAEndAtResAByDay = true;
    private boolean isEnabledConstraintResourceARelationResourceAStateByDay = true;
    // the above is only for model2
    private boolean isEnabledConstraintResourceCapacity = true;

    private boolean isEnabledConstraintResourceAFixedState = true;

    private boolean isEnabledConstraintResourceAssignmentRelationSubtaskStepA = true;
    private boolean isEnabledConstraintResourceAssignmentRelationSubtaskAssignSameRes = true;

    private boolean isEnabledConstraintEngineerAssignmentForReportingPhase = true;
    private boolean isEnabledConstraintEngineerAssignmentForTaskAndStepA = true;
    private boolean isEnabledConstraintEngineerAssignmentForTask = true;

    private int weightOptionalTask = DEFAULT_WEIGHT_OPTIONAL_TASKS;
    private int weightLateSubtasks = DEFAULT_WEIGHT_LATE_SUBTASKS;
    private int weightLateTasks = DEFAULT_WEIGHT_LATE_TASKS;
    private int weightAdditionalUsedResourceQty = DEFAULT_WEIGHT_ADDITIONAL_USED_RESOURCE_QTY;
    private int weightResourceUnbalance = DEFAULT_WEIGHT_RESOURCE_UNBALANCE;

    private boolean isEnabledObjectiveOptionalTasks = true;
    private boolean isEnabledObjectiveLateSubTasks = true;
    private boolean isEnabledObjectiveLateTasks = true;
    private boolean isEnabledObjectiveAdditionalUsedResourceQty = true;
    private boolean isEnabledObjectiveResourceUnbalance = true;
    private boolean isEnabledObjectiveSumOfStepASize = true;

    private boolean isEnabledLengthMaxOfStepA = true;
    private boolean isEnabledLengthMaxOfResourceA = true;

    public EntityPlan(Integer functionId, String planId, int planMode) {
        this.functionId = functionId;
        if (planId != null) {
            this.planId = planId;
        }
        this.planMode = planMode;
     }

    /**
     * 初始化开始时间
     */
    public void initialize(Date planTriggeredTime) {
        log.info("排程plan id：" + planId);
        log.info("排程触发时间：" + DateUtil.toString(planTriggeredTime));
        // todo: 确认 planTriggeredTime
        this.planTriggeredTime = planTriggeredTime;
        planPeriodStartTime = calculatePlanPeriodStartTime(planTriggeredTime);
        log.info("排程计划开始时间：" + DateUtil.toString(planPeriodStartTime));
    }

    /**
     * 根据排程触发时间，和默认规则，生成排程计划开始时间
     */
    private Date calculatePlanPeriodStartTime(Date planTriggeredTime) {
        //因为不同的批次的最小准备时间可能不同，所以这里设置成触发时间（按小时取整）
        // 原程序没有考虑变为最近的小时，这里暂时不考虑
        //return DateUtil.getLatestHourDate(planTriggeredTime);
        return DateUtil.getLatestHalfHourDate(planTriggeredTime);
        //return planTriggeredTime;
    }

    public void setMaxPlanPeriodEndTime(Date planPeriodEndTime) {
        this.planPeriodEndTime = DateUtil.getMaxTime(this.planPeriodEndTime, planPeriodEndTime);
    }



}
