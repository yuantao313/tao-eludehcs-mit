package com.oat.patac.entity;

import com.alibaba.druid.util.StringUtils;
import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import com.oat.patac.service.PlanService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 这个类的实体代表了排程中的一个“批次”（或“任务批次”）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntitySubTask {

    /**
     * 批次的ID
     */
    private String subTaskNo ;
    /**
     * 委托单号
     */
    private String taskNo ;
    /**
     * bomNo
     */
    private String bomNo;
    /**
     * 试验规范编号
     */
    private String procedureNo ;
    /**
     *  样品组ID
     */
    private Integer sampleGroupId ;
    /**
     *  样品组提供时间
     */
    private Date provideDate ;
    /**
     *  试验期望完成时间
     */
    private Date returnDate ;
    /**
     * 适用于该批次的排程时间颗粒度（分钟）
     */
    private Integer planGranul;
    /**
     * 适用于该批次的求解时间限制（秒）
     */
    private Integer planTimeLimit;
    /**
     * 试验批次组（设定组名称，前端设定任务组维护）
     */
    private String taskGroup ;
    /**
     *  试验批次顺序号;设定组顺序，前端设定任务组维护
     */
    private Integer taskSeqNo ;
    /**
     *  是否删除;逻辑删除标志（Y/N）
     */
    private String isDeleted ;
    /**
     *  是否紧急排程;根据此值判定是0-正常排程，还是1-紧急资源申请，为1则不导入排程
     */
    private String businessFlag ;
    /**
     *  批次的状态;试验批次所处执行状态(null.等待实施, 1. 执行中, 2.完成, 3.试验报告完成, 4.样品状态待确认, 5.终止)
     */
    private String testStatus ;
    /**
     *功能块的ID
     */
    private Integer functionId;

    /**
     * 是否参于本次排程
     */
    private Boolean isToBePlanned = false;

    /**
     * 是否在本次排程中已被固定
     */
    private boolean isFixed = false;

    /**
     * 是否在本次排程中会被忽略，如果在需要排程的批次中，和已经fix的批次中都不能找到某个批次，
     * 有可能是在排程时间之前已经完成的批次，则本次排程不进行考虑。
     * 但需要注意这种情况下task的开始时间可能并不是根据本次排程中的批次所决定的，所以将保留原来的值。
     */
    private boolean isIgnored = false;

    /**
     * 是否在本次排程中已被排程
     */
    private boolean isPlanned = false;

    /**
     * 被哪一个子模型排程
     */
    private String plannedSubModelName;

    /**
     * 该批次的开始时间，如果是开始排程前已经被固定的批次，则该值从数据库中获得；如果是本次排程的批次，则该值由子模型后处理程序赋值。
     */
    private Date startTime;

    /**
     * 该批次的结束时间，如果是开始排程前已经被固定的批次，则该值从数据库中获得；如果是本次排程的批次，则该值由子模型后处理程序赋值。
     */
    private Date endTime;

    /**
     * 通过procedure和bom的关系，对应的plan_config
     */
    private EntityPlanConfig planConfig;

    /**
     * 生成日历天数
     */
    private int calendarDays;
    /**
     * 根据排程提前量/最小准备时间，考虑了是否考虑周末，和排程触发时间，计算得到的最早批次开始时间
     */
    private Date minPlanStartTime;
    /**
     * 批次的最晚结束时间，考虑排程天数
     */
    private Date maxPlanEndTime;
     /**
     * 子模型的名字
     */
    private String subModelName;

    /**
     * 对应的BOM
     */
    private EntityBom bom;

    /**
     * 批次可能的最小时长
     */
    private double subTaskLength;

    /**
     * 该批次的前一个批次。在定义有批次组时需要。第一个批次的值为null。包括已排程批次。
     */
    private EntitySubTask previous;

    /**
     * 需要使用的所有的sample
     */
    private ArrayList<EntitySample> samples = new ArrayList<>();
    /**
    * 需要保证活动不冲突的样件编号。注意，不是sample实体，因为可能多个实体是同一个编号
    */
    private String uniqueSampleNo;
    /**
     * 包含的所有 step activities，即小阶段活动
     */
    private ArrayList<EntityStepActivity> stepActivities = new ArrayList<>();
    /**
     * 是否含有“报告撰写“大阶段
     */
    private boolean hasReportingStep = false;
    /**
     * authorized engineers after considering auth_procedure, auth_bom, auth_step
     */
    private HashSet<EntityStaff> authRespEngineers = new HashSet<>();
    /**
     * assigned responsible engineer id, from sub_task_plan_input or result of sub models
     */
    private String assignedRespEngineerId;
    /**
     * 批次中的被要求使用的资源组，和对应被该批次下的小阶段要求该资源组的次数
     * key是资源组的名字（不同的step内的资源组，可用同名，
     * 工程师的都叫engineer，
     * 技工的以skillset集合命名（如果为空则为空字符集），
     * 设备组的以设备组id为名）
     * value是被该批次下小阶段活动需要的次数
     */
    private HashMap<String, Integer> resourceGroupRequestedQuantity = new HashMap<>();
    /**
     * 批次中的被要求使用多于1次的资源组，和对应被要求的数量的最大值
     * key是资源组的名字，仅包括被使用多余1次的资源组即可
     * value是该批次下小阶段中该资源组被要求的数量的最大值
     */
    private HashMap<String, Integer> maxRequestedResourceQuantity = new HashMap<>();
    /**
     * 批次中的被要求使用多于1次的资源组，和对应被要求的数量的最小值
     * key是资源组的名字，仅包括被使用多余1次的资源组即可
     * value是该批次下小阶段中该资源组被要求的数量的最大值
     */
    private HashMap<String, Integer> minRequestedResourceQuantity = new HashMap<>();
    /**
     * 批次中被要求使用多余1次而且被要求使用固定设备的资源组名列表
     */
//    private ArrayList<String> resourceGroupsFixingResource = new ArrayList<>();
    private HashSet<String> resourceGroupsFixingResource = new HashSet<>();
    /**
     * 批次中的被要求使用多于1次的资源组，和对应的可能的资源的并集
     * key是资源组的名字，仅包括被使用多余1次的资源组即可
     * value是该批次下小阶段中该资源组中可能使用的资源的并集
     */
    private HashMap<String, HashSet<Object>> possibleResources = new HashMap<>();
    /**
     * 批次中的被要求使用多于1次的资源组，和对应的资源类型
     * key是资源组的名字，仅包括被使用多余1次的资源组即可
     * value是该资源组的资源类型
     */
    private HashMap<String, String> resourcesGroupTypeHashMap = new HashMap<>();


    /**
     * 判断相关 task 无法排程
     * @return
     */
    public Boolean singleCheckTask(){
        Boolean isValid = true;

        // BOM_NO为空
        isValid = isValid && CheckDataUtil.notBlank(bomNo, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "批次 " + subTaskNo + " 没有对应的BOM_NO，导致相关任务单 " + taskNo + " 无法排程");

        // 样品组提供时间为空
        isValid = isValid && CheckDataUtil.notBlank(this.provideDate, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "批次 " + subTaskNo + " 没有对应的样件提供时间，导致相关任务单 " + taskNo + " 无法排程");

        // 试验期望完成时间为空
        isValid = isValid && CheckDataUtil.notBlank(this.returnDate, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "批次 " + subTaskNo + " 没有对应的试验期望完成时间，导致相关任务单 " + taskNo + " 无法排程");

        // 当批次id存在时，试验批次顺序号不得为空
        if (!StringUtils.isEmpty(this.getTaskGroup())){
            isValid = isValid && CheckDataUtil.notBlank(taskSeqNo, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "批次 " + subTaskNo + " 设置了批次组 " + taskGroup + "，但没有对应的试验顺序批次号，导致相关任务单 " + taskNo + " 无法排程");
        }

        return isValid;
    }

    /**
     * 该实体数据验证 判断该实体本身是否参与排程
     * @return true 不过滤，false 过滤
     */
    public Boolean singleCheck(){

        // 是否过滤
        Boolean isValid = true;

        // subTask 的过滤逻辑
        // 当批次id存在时，IS_DELETED字段是“N”
        isValid = isValid && CheckDataUtil.isNotEquals(isDeleted,"N", ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_INVALID_DATA,
                "批次" + subTaskNo + "“IS_DELETED”的字段为Y，不会参与排程");

        // 当批次id存在时，是否紧急排程BUSINESS_FLAG为"0"
        isValid = isValid && CheckDataUtil.isNotEquals(businessFlag, "0", ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_INVALID_DATA,
                "批次" + subTaskNo + "“BUSINESS_FLAG”，的字段为1，不会参与排程");

        // 样品组id为空
        CheckDataUtil.notBlank(this.sampleGroupId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "批次" + subTaskNo + "没有对应的样品组id，忽略样品组相关要求，继续排程");


        return isValid;


     /*   if (!isValid){
            return isValid;
        }

        //  当批次id存在时，任务单id不得为空
        CheckDataUtil.notBlank(this.taskNo, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "批次" + subTaskNo + "没有对应的任务单id，无法进行排程");

        // 当批次id存在时，试验规范id不得为空
        CheckDataUtil.notBlank(this.procedureNo, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "批次" + subTaskNo + "没有对应的规范id，无法进行排程");*/



//         当批次id存在时，批次组id不得为空
//        CheckDataUtil.notBlank(taskGroup, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_CAN_SCHEDULED,
//                "批次" + subTaskNo + "没有对应的批次组id，但是可以进行排程");



//        // 当批次id存在时，TEST_STATUS可能是：null.等待实施, 1. 执行中, 4.样品状态待确
//        // todo:确认状态
//        if (testStatus != null){
//            ArrayList<String> testStatuses= new ArrayList<>();
//            CheckDataUtil.isNotInList(testStatuses, testStatus, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_NOT_SCHEDULED,
//                    "批次" + subTaskId + "的状态不正确！");
//        }


    }

}
