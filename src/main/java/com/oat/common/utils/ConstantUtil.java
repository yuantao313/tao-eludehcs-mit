package com.oat.common.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author:yhl
 * @create: 2022-08-19 16:50
 * @Description: 常量
 */
public class ConstantUtil {

    /**
     * 信息严重程度常量
     */
    public static final String MESSAGE_SEVERITY_WARNNING = "WARNNING";
    public static final String MESSAGE_SEVERITY_ERROR = "ERROR";
    public static final String MESSAGE_SEVERITY_INFO = "INFO";

    /**
     * 信息类型常量
     */
    public static final String MESSAGE_TYPE_FIELD_MISS = "属性缺失";
    public static final String MESSAGE_TYPE_FIELD_ILLEGAL = "属性值不合法";
    public static final String MESSAGE_TYPE_FIELD_ERROR_NOT_SCHEDULED = "必要属性错误，导致无法进行排程";
    public static final String MESSAGE_TYPE_MISSING_DATA = "数据缺失";
    public static final String MESSAGE_TYPE_REPEAT_DATA = "数据重复";
    public static final String MESSAGE_TYPE_TIME_CONFICT = "时间冲突";
    public static final String MESSAGE_TYPE_STAFF_NOT_ENOUGH = "人员条件不满足";
    public static final String MESSAGE_TYPE_EQUIPMENT_NOT_ENOUGH = "设备条件不满足";
    public static final String MESSAGE_TYPE_RESOURCES_NOT_ENOUGH = "资源不足";
    public static final String MESSAGE_TYPE_INVALID_DATA = "无效数据";
    public static final String MESSAGE_TYPE_SCHEDULED_BATCH_CONFLICT = "已排程批次冲突";
    public static final String MESSAGE_TYPE_POTENTIAL_SCHEDULING_CONFLICTS = "潜在排程冲突";
    public static final String MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT = "不满足批次组条件";

    public static final String MESSAGE_TYPE_TIME_IGNORE_REDUNDANT_RECORDS = "忽略多余记录，继续排程";
    public static final String MESSAGE_TYPE_NOT_ENOUGH_SCHEDULING_CONDITIONS = "不满足排程条件";
    public static final String MESSAGE_TYPE_NOT_SCHEDULED_RESOURCE_CONFLICT = "已排程资源冲突";
    public static final String MESSAGE_TYPE_STAFF_CONDITIONS_NOT_ENOUGH_NOT_SCHEDULED = "人员条件不满足，导致无法进行排程";
    public static final String MESSAGE_TYPE_STAFF_CONDITIONS_NOT_ENOUGH_CAN_SCHEDULED = "人员条件不满足，忽略该人员设置，继续排程";
    public static final String MESSAGE_TYPE_TIME_LOGIC_ERROR_CAN_SCHEDULED = "时间逻辑错误，但可以进行排程";
    public static final String MESSAGE_TYPE_INVALID_DATA_CAN_SCHEDULED = "无效数据，但可进行排程";
    public static final String MESSAGE_TYPE_MISSING_DATA_CAN_SCHEDULED = "缺失数据，但可进行排程";
    public static final String MESSAGE_TYPE_NO_SOLUTION_FOUND = "没有找到解决方案";


    /**
     * 最大每天工时
     */
    public static final Integer MAX_DAILY_WORK_TIME = 14;
    /**
     * 希望资源不要跨天的时长限制 （小时）
     */
    public static final Integer LENGTH_NEAR_TO_SIZE_LIMIT = 6;
    /**
     * 希望资源不要跨天的时长限制 （小时）时，length最多可以比size大的小时数
     */
    public static final Integer LENGTH_MAX_INCREASE_WHEN_NEAR_TO_SIZE_LIMIT = 2;
    /**
     * 抛异常信息
     */
    public static final String ERROR_MESSAGE = "ERROR！数据有错误无法排程！";

    /**
     * 计划模式, 1是自动基础排程模式, 2是手动触发排程模式
     */
    public static final int PLAN_MODE_UNKNOWN = 0;
    public static final int PLAN_MODE_AUTOMATIC = 1;
    public static final int PLAN_MODE_MANUAL_TRIGGERED = 2;
    /**
     * 求解模式，0按批次组内顺序，兼顾时间颗粒度分子模型
     * 1 时间颗粒度
     * 2 如果没有批次组，按任务单分子模型，组内按时间颗粒度再分子模型，前面子模型的结果保留
     * 3 如果没有批次组，按任务单分子模型，组内按时间颗粒度再分子模型，前面子模型的结果不保留
     * 4 按批次组一组一组分子模型，组内按顺序兼顾时间颗粒度分子模型，前面子模型的结果保留
     * 5 按批次组一组一组分子模型，组内按顺序兼顾时间颗粒度分子模型，前面子模型的结果不保留
     */
    public static final int SOLVE_MODE_DEFAULT = 0;
    public static final int SOLVE_MODE_BY_SUBTASK_GROUP_AND_GRANULARITY = 0;
    public static final int SOLVE_MODE_BY_GRANULARITY = 1;
    public static final int SOLVE_MODE_BY_TASK = 2;

    public static final int SOLVE_MODE_BY_TASK_NOT_SAVE = 3;
    public static final int SOLVE_MODE_BY_SUBTASK_GROUP = 4;
    public static final int SOLVE_MODE_BY_SUBTASK_GROUP_NOT_SAVE = 5;
    public static final int VARIABLE_TYPE_BINARY=0;
    public static final int VARIABLE_TYPE_INTEGER=1;
    public static final int VARIABLE_TYPE_INTERVAL = 3;
    // 其实不是variable了，不过借用了variable维度的结构
    public static final int VARIABLE_TYPE_STATE_FUNCTION = 4;

    public static final int VARIABLE_TYPE_CUMUL_FUNCTION_EXPRESSION = 5;

    public static final int VARIABLE_TYPE_EXPRESSION = 6;
    public static final String VARIABLE_NOT_DEFINED_ERROR = "VARIABLE_NOT_DEFINED_ERROR";


    // 提前排程量/最小准备时间：排程开始时间在触发后几小时。会从数据库读取。单位小时。
    public static final int DEFAULT_PLAN_PREPARE_TIME = 10;
    // 提前排程量/最小准备时间 和 委托天数 是否考虑周末和节假日。会从数据库读取。
    public static final boolean DEFAULT_PLAN_CONFIG_IS_WEEKEND_INCLUDE = false;
    public static final int DEFAULT_PLAN_GRANULARITY = 60;

    public static final int DEFAULT_PLAN_CALENDAR_DAYS = 30;

    public static final int DEFAULT_PLAN_TIME_LIMIT = 6000;

    public static final int DEFAULT_WORKERS = 4;
    public static final int DEFAULT_CONFLICT_REFINER_TIME_LIMIT = 180;

    /**
     * 排程参数模板中是否计入周末和节假日参数的 含义；0表示不计入；1表示计入
     */
    public static final String PLAN_CONFIG_IS_WEEKEND_INCLUDE = "1";

    /**
     * packingFlag
     */
    public static final String PASS = "pass";
    public static final String REPEAT = "repeat";
    public static final String EXTENT = "extent";
    public static final String EXPAND = "expand";
    public static final ArrayList<String> allPackingFlag = new ArrayList<>();
    static {
        allPackingFlag.add(PASS);
        allPackingFlag.add(REPEAT);
        allPackingFlag.add(EXTENT);
        allPackingFlag.add(EXPAND);
    }

    /**
     * 是否需要新夹具
     */
    public static final String IS_NEED_NEW_FIXTURE = String.valueOf('2');
    /**
     * staff的分类，0是工程师，1是技师
     */
    public static final String STAFF_CLASS_ENGINEER = "0";
    public static final String STAFF_CLASS_TECHNICIAN = "1";
    /**
     * 小阶段的工作模式：0：仅工作日工作，1：工作日和周末, 2:全年
     */
    public static final String STEP_WEEKDAYS_WORK = "0";
    public static final String STEP_WEEKDAYS_WEEKEND_WORK = "1";
    public static final String STEP_ALL_DAYS_WORK = "2";

    /**
     * Special Day的工作模式;1：周末工作日，2：非周末休息日,3:节假日周末
     */
    public static final String SPECIAL_DAY_WEEKEND_WORK = "1";
    public static final String SPECIAL_DAY_WORKDAY_REST = "2";
    public static final String SPECIAL_DAY_HOLIDAYS_WEEKEND = "3";

    /**
     * 人员状态是否工作：上班 or 休息
     */
    public static final Integer WORK = 1;
    public static final Integer REST = 0;
    /**
     * 单体验证返回值约定
     */
    public static final int RESULT_VALID = 1;
    public static final int RESULT_INVALID = 0;
    public static final int RESULT_CAN_NOT_SCHEDULE = -1;
    /**
     * 默认的资源容量值
     */
    public static final int DEFAULT_RESOURCE_CAPACITY = 1;
    /**
     * 不排它资源的默认容量要求
     */
    public static final double DEFAULT_RESOURCE_OCCUPATION = 0;//如果考虑排它为0.001;
    /**
     * 资源容量的放大系数，数据预处理中设置为double，约束中转为int时需要程的倍数
     */
    public static final double DEFAULT_AMPLIFICATION_FACTOR = 1; //如果考虑排它为DEFAULT_RESOURCE_CAPACITY/DEFAULT_RESOURCE_OCCUPATION;

    /**
     * 非约束的资源占用，在计算人员总体usage时的打折系数
     */
    public static final double ENGINEER_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO = 1.0;
    public static final double TECHNICIAN_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO = 1.0;
    /**
     * 人员不均衡，做惩罚的时候，不惩罚的平均区间的上下限
     */
    public static final double  RESOURCE_UNBALANCE_RATIO_UPPER_BOUND = 1.3;
    public static final double  RESOURCE_UNBALANCE_RATIO_LOWER_BOUND = 0.7;
    public static final double RESOURCE_UNBALANCE_POWER_FOR_SURPLUS = 1.1;
    public static final double RESOURCE_UNBALANCE_POWER_FOR_SLACK = 1.1;
    /**
     * 资源的类型名
     */
    public static final String RESOURCE_TYPE_ENGINEER = "engineer";
    public static final String RESOURCE_TYPE_TECHNICIAN = "technician";
    public static final String RESOURCE_TYPE_EQUIPMENT = "equipment";

    /**
     * 人员的类型名对应在message中的汉字
     */
    public static final String RESOURCE_TYPE_ENGINEER_IN_MESSAGE = "工程师 ";
    public static final String RESOURCE_TYPE_TECHNICIAN_IN_MESSAGE = "技师 ";
    /**
     * 工程师资源的默认资源组名
     */
    public static final String DEFAULT_ENGINEER_RESOURCE_GROUP_NAME = "engineer";
    //技师的资源组用skill set id命名，如果为空，用这个名字
    public static final String DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME = "technicianNoSkill";
    //设备的资源组用equipment group id命名
    //public static final String DEFAULT_EQUIPMENT_RESOURCE_GROUP_NAME = "equipment";
    /**
     * 资源组工作模式，是否按天,0不按天，1按天
     */
    public static final String RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY = "0";
    public static final String RESOURCE_GROUP_DAILY_MODE_BY_DAY = "1";
    /**
     * 人员资源工作模式，是否一同开始、结束，或没有要求
     */
    public static final String STAFF_NOT_LIMITED_START_END = "0";
    public static final String STAFF_SAME_START = "1";
    public static final String STAFF_SAME_END = "2";

    /**
     * 设备资源工作模式，是否与阶段时间一致 （0:不同步，1:同步）
     */
    public static final String EQUIPMENT_IS_SYNC_STEP = "1";
//    public static final String EQUIPMENT_SAME_START = "1";
//
//    public static final String EQUIPMENT_SAME_END = "2";
    /**
     * 资源组是否为约束，0不是，1是
     */
    public static final String RESOURCE_GROUP_IS_NOT_CONSTRAINT = "0";
    public static final String RESOURCE_GROUP_IS_CONSTRAINT = "1";
    /**
     * 资源组是否可扩展，0不可，1可
     */
    public static final String RESOURCE_GROUP_IS_NOT_EXPANDABLE = "0";
    public static final String RESOURCE_GROUP_IS_EXPANDABLE = "1";
    /**
     * 报告撰写的大阶段名字
     */
    public static final String REPORTING_PHASE_NAME = "试验数据及报告";
    /**
     * 整车对应的sample type
     */
    public static final String SAMPLE_TYPE_CAR = "整车";

    /**
     * 设备是否固定
     */
    public static final String EQUIPMENT_IS_FIXING = "1";
    public static final String EQUIPMENT_IS_NOT_FIXING = "0";
    /**
     * 设备是否available
     */
    public static final String EQUIPMENT_IS_AVAILABLE = "1";

    /**
     * workValue 不工作为0，工作为100.
     */
    public static final Double WORK_VALUE = 100.0;
    public static final Double NOT_WORK_VALUE = 0.0;

    /**
     * staff/equipment mode
     */
    public static final String MODE_SPECIAL_DAY_NOT_ROLL = "0";
    public static final String MODE_SPECIAL_DAY_ROLL = "1";

    /**
     * BOM的类型，1为标准，0为非标准
     */
    public static final String BOM_TYPE_STANDARD = "1";
    public static final String BOM_TYPE_NOT_STANDARD = "0";
    /**
     * calendarType
     */
    public static final String CALENDAR_TYPE_WORK = "0";
    public static final String CALENDAR_TYPE_NOT_WORK = "1";
    /**
     * 排程状态
     */
    public static final String PLAN_STATUS_COMPLETE = "完成";

    /**
     * 日历中每天的时间点
     */
    public static final String SHIFTSTART= "shiftStart";
    public static final String BREAKSTART= "breakStart";
    public static final String BREAKEND= "breakEnd";
    public static final String SUPPERBREAKSTART= "supperBreakStart";
    public static final String SUPPERBREAKEND= "supperBreakEnd";
    public static final String SHIFTEND= "shiftEnd";
    public static final String ZERO_POINT_TIME = "00:00";

    /**
     * calendar 类型 0 正常， 1 翻班
     */
    public static final String CALENDAR_CLASS_NORMAL = "0";
    public static final String CALENDAR_CLASS_ROLL = "1";

    public static final String ConstraintTaskSpanSubtask = "ConstraintTaskSpanSubtask";
    public static final String ConstraintTaskSubtaskPresence = "ConstraintTaskSubtaskPresence";
    public static final String ConstraintSubtaskGroupSequence = "ConstraintSubtaskGroupSequence";

    public static final String ConstraintStepAForbidStartEnd = "ConstraintStepAForbidStartEnd";
    public static final String ConstraintStepARelationSubtaskSpanStepA = "ConstraintStepARelationSubtaskSpanStepA";
    public static final String ConstraintStepARelationSubtaskStepAPresence = "ConstraintStepARelationSubtaskStepAPresence";
    public static final String ConstraintStepARelationStepASequence = "ConstraintStepARelationStepASequence";
    public static final String ConstraintStepARelationStepAMaxGap = "ConstraintStepARelationStepAMaxGap";

    public static final String ConstraintSampleANoOverlap = "ConstraintSampleANoOverlap";
    // the following is only for model3
    public static final String ConstraintResourceGroupANotByDayPresence = "ConstraintResourceGroupANotByDayPresence";
    public static final String ConstraintResourceGroupANotByDayInStepARange = "ConstraintResourceGroupANotByDayInStepARange";

    public static final String ConstraintResourceGroupADayRangePresence = "ConstraintResourceGroupADayRangePresence";
    public static final String ConstraintResourceGroupADayRangeSequence = "ConstraintResourceGroupADayRangeSequence";
    public static final String ConstraintResourceGroupADayRangeInStepARange = "ConstraintResourceGroupADayRangeInStepARange";
    public static final String ConstraintResourceGroupADayRangeForbidStartEnd = "ConstraintResourceGroupADayRangeForbidStartEnd";
    public static final String ConstraintResourceGroupAByDayPresence = "ConstraintResourceGroupAByDayPresence";
    public static final String ConstraintResourceGroupAByDayInResourceGroupADayRange = "ConstraintResourceGroupAByDayInResourceGroupADayRange";

    public static final String ConstraintResourceGroupAAlternativeResource = "ConstraintResourceGroupAAlternativeResource";

    public static final String ConstraintResourceAState = "ConstraintResourceAState";
    // the above is only for model3
    // the following is for replacement of alternative constraints, model4
    public static final String ConstraintResourceAInStepARange = "ConstraintResourceAInStepARange";
    public static final String ConstraintResourceABeAssignedQuantity = "ConstraintResourceABeAssignedQuantity";

    // the above is for replacement of alternative constraints, model4
    //the following is for both model2 and model3
    public static final String ConstraintResourceAForbidStartEnd = "ConstraintResourceAForbidStartEnd";
    public static final String ConstraintResourceAPresenceNotByDay = "ConstraintResourceAPresenceNotByDay";
    public static final String ConstraintResourceAPresenceByDay = "ConstraintResourceAPresenceByDay";
    // the above is for both model2 and model3
    // the following is only for model2
    public static final String ConstraintResourceARelationStepAAssignResGroupRes = "ConstraintResourceARelationStepAAssignResGroupRes";
    public static final String ConstraintResourceARelationStepAStartAtResANotByDay = "ConstraintResourceARelationStepAStartAtResANotByDay";
    public static final String ConstraintResourceARelationStepAStartBeforeResANotByDay = "ConstraintResourceARelationStepAStartBeforeResANotByDay";
    public static final String ConstraintResourceARelationStepAEndAtResANotByDay = "ConstraintResourceARelationStepAEndAtResANotByDay";
    public static final String ConstraintResourceARelationStepAEndAfterResANotByDay = "ConstraintResourceARelationStepAEndAfterResANotByDay";
    public static final String ConstraintResourceARelationResourceAStateNotByDay = "ConstraintResourceARelationResourceAStateNotByDay";


    public static final String ConstraintResourceARelationStepAStartBeforeResAByDay = "ConstraintResourceARelationStepAStartBeforeResAByDay";
    public static final String ConstraintResourceARelationStepAEndAfterResAByDay = "ConstraintResourceARelationStepAEndAfterResAByDay";
    public static final String ConstraintResourceARelationResASpanDays = "ConstraintResourceARelationResASpanDays";
    public static final String ConstraintResourceARelationStepAStartAtResAByDay = "ConstraintResourceARelationStepAStartAtResAByDay";
    public static final String ConstraintResourceARelationStepAEndAtResAByDay = "ConstraintResourceARelationStepAEndAtResAByDay";
    public static final String ConstraintResourceARelationResourceAStateByDay = "ConstraintResourceARelationResourceAStateByDay";
    // the above is only for model2
    public static final String ConstraintResourceCapacity = "ConstraintResourceCapacity";

    public static final String ConstraintResourceAFixedState = "ConstraintResourceAFixedState";

    public static final String ConstraintResourceAssignmentRelationSubtaskStepA = "ConstraintResourceAssignmentRelationSubtaskStepA";
    public static final String ConstraintResourceAssignmentRelationSubtaskAssignSameRes = "ConstraintResourceAssignmentRelationSubtaskAssignSameRes";

    public static final String ConstraintEngineerAssignmentForReportingPhase = "ConstraintEngineerAssignmentForReportingPhase";
    public static final String ConstraintEngineerAssignmentForTaskAndStepA = "ConstraintEngineerAssignmentForTaskAndStepA";
    public static final String ConstraintEngineerAssignmentForTask = "ConstraintEngineerAssignmentForTask";

    public static final String ObjectiveOptionalTasks = "ObjectiveOptionalTasks";
    public static final String ObjectiveLateSubTasks = "ObjectiveLateSubTasks";
    public static final String ObjectiveLateTasks = "ObjectiveLateTasks";
    public static final String ObjectiveAdditionalUsedResourceQty = "ObjectiveAdditionalUsedResourceQty";
    public static final String ObjectiveResourceUnbalance = "ObjectiveResourceUnbalance";
    public static final String ObjectiveSumOfStepASize = "ObjectiveSumOfStepASize";
    public static final String LengthMaxOfStepA = "LengthMaxOfStepA";
    public static final String LengthMaxOfResourceA = "LengthMaxOfResourceA";

    public static final int DEFAULT_WEIGHT_OPTIONAL_TASKS = 1;
    public static final int DEFAULT_WEIGHT_LATE_SUBTASKS = 1;
    public static final int DEFAULT_WEIGHT_LATE_TASKS = 1;
    public static final int DEFAULT_WEIGHT_ADDITIONAL_USED_RESOURCE_QTY = 100;
    public static final int DEFAULT_WEIGHT_RESOURCE_UNBALANCE = 1;

    public static final int DEFAULT_WEIGHT_SUM_OF_STEP_SIZE = 1;//1000;

    public static final String MODEL_VERSION_2 = "2";
    public static final String MODEL_VERSION_3 = "3";
    public static final String MODEL_VERSION_4 = "4";
    public static final String DEFAULT_MODEL_VERSION = "4";
    //todo: 设置property
    public static final String CSV_FILE_OUTPUT_FOLDER = "./csv";
    public static final String CSV_FILE_EXPECTED_RESULT_FOLDER = "./test/expected_results";

    public static final ArrayList<String> PLAN_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","planPeriodStartTime",
            "planPeriodEndTime","planIsCompleted","planStatus"));

    public static final ArrayList<String> MESSAGE_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","messageSeverity","messageType",
            "messageBody"));

    public static final ArrayList<String> TASK_PLAN_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","taskNo",
            "taskPlanStart", "taskPlanEnd","respEngineerId"));

    public static final ArrayList<String> SUB_TASK_PLAN_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","taskNo","subTaskNo",
            "subTaskPlanStart", "subTaskPlanEnd", "respEngineerId","taskGroup","taskSeqNo", "sampleGroupId", "sampleNo"));

    public static final ArrayList<String> STEP_PLAN_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","subTaskNo","stepId",
            "stepPlanStart", "stepPlanEnd", "bomNo","stepPlanId","reqProcTime","planDuration"));
    public static final ArrayList<String> STAFF_PLAN_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","stepPlanId","staffPlanId",
            "staffPlanStart", "staffPlanEnd", "staffId","isConstraint","reqWorkTime","planWorkTime"));
    public static final ArrayList<String> EQUIPMENT_PLAN_OUTPUT_PROPERTIES = new ArrayList<>(Arrays.asList(
            //"planId",
            "functionId","stepPlanId","equipmentPlanId",
            "equipmentPlanStart", "equipmentPlanEnd", "equipmentId","equipmentGroupId","equipmentQty","isConstraint",
            "isExpandable","equipmentStatus","reqWorkTime","planWorkTime"));
}
