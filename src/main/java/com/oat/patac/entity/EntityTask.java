package com.oat.patac.entity;


import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


/**
 * 这个实体类代表任务单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class EntityTask {

    /**
     *功能块的ID
     */
    private Integer functionId;
//    /**
//     * 委托单号，具有唯一性
//     */
//    private Integer taskId;

    /**
     * 前端显示的任务单号
     */
    private String taskNo;
    /**
     * 任务单的名称
     */
    private String taskName;
    /**
     * 任务单状态;任务单的当前状态：草稿；功能块负责人： 待审核；等等
     */
    private String taskStatus ;
    /**
     * 非“suspend”的状态
     */
    private String status;
    /**
     * 实验类型： 认证试验；设备or人员支持开发
     */
    private String testType;
    /**
     * 审批完成时间
     */
    private Date approveCompleteDate;
    /**
     * 夹具计划验收完成时间
     */
    private Date fixtureReadyTime;
    /**
     * 优先指定工程师
     */
    private String respUserId;
    /**
     * 任务单是否被删除
     */
    private String isDeleted;
    /**
     * 是否委外;是否委外(2:委外； 0，1，null：不委外)
     */
    private String isOutsourcing ;
    /**
     * 是否需要夹具
     */
    private String isNeedNewFixture;

    /**
     * 是否允许不排入（还没确定），默认设置为必须排入
     */
    private Boolean isOptional = Boolean.FALSE;

    /**
     * 本次排程是否排这个任务单（包括下面所有的sub task，或者是一部分sub task）
     */
    private Boolean isToBePlanned = Boolean.FALSE;

    /**
     * 该 Task 所包含所有的 subTasks
     */
    private ArrayList<EntitySubTask> subTasks = new ArrayList<>();

    /**
     * 该 Task 所包含需要进行排程的 subTasks
     */
    private ArrayList<EntitySubTask> toBePlannedSubTasks = new ArrayList<>();

    /**
     * 该 Task 是否没有被排入。默认为排入，如果子模型没有排入该task，需要设置该值为false
     */
    private Boolean isPresence = true;

    /**
     * 将 sub task 按照批次组Id分组
     */
    // todo: remove 批次组
    //HashMap<String, ArrayList<EntitySubTask>> subTaskGroupMap = new HashMap<>();

    /**
     * 已被分配的责任工程师。
     * 有可能是之前有部分批次已经排程（sub_task_plan_input），所以任务单的责任工程师已经分配；
     * 也有可能是sub model中分配的
     */
    private EntityStaff assignedRespEngineer;

    /**
     *可以作为该 Task 的责任工程师的可能的工程师列表，是子模型运行之前的列表
     */
    private ArrayList<EntityStaff> initialPossibleRespEngineers = new ArrayList<>();
    /**
     * 可以作为该 Task 的责任工程师的可能的工程师列表，是会被子模型不断更新的列表
     */
    private ArrayList<EntityStaff> possibleRespEngineers = new ArrayList<>();
    /**
     * task 下所有 step 所要时间总和
     */
    private double taskLength;

    /**
     * 单实体类检验
     * @return true 不过滤，false 过滤
     */
    // 根据plan mode做不同的逻辑
    public Boolean singleCheck() {
        // 是否过滤
        Boolean isValid;

        // （过滤条件）当任务单id存在时，任务单状态（STATUS）应为非“suspend”的状态
        isValid = !CheckDataUtil.isEquals(status, "suspend", ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " “STATUS”显示为" + status + "，该字段应该是XXX，为无效数据");

        // 当任务单id存在时，审批完成时间不得为空
        isValid = isValid && CheckDataUtil.notBlank(this.approveCompleteDate, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " 没有对应的审批完成时间，为无效数据");

        // 当任务单id存在时，需要新夹具IS_NEED_NEW_FIXTURE为“2”的情况下，夹具计划验收完成时间PLAN_CHECK_TIME不为空
        if (StringUtils.equals(isNeedNewFixture, "2")){
            isValid = isValid && CheckDataUtil.notBlank(this.fixtureReadyTime, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "任务单 " + taskNo + " 没有对应的夹具计划验收完成时间，为无效数据");
        }

        // （过滤条件）当任务单id存在时，IS_DELETED应该为N
        isValid = isValid && CheckDataUtil.isNotEquals(this.isDeleted, "N", ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " “IS_DELETED”显示为" + isDeleted + "，该字段应该是N，，为无效数据");

        // （过滤条件）当任务单id存在时，IS_OUTSOURCING不能为2
        isValid = isValid && !CheckDataUtil.isEquals(isOutsourcing, "2", ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " “IS_OUTSOURCING”显示为2，该字段应该是XXX，为无效数据");

        // 无效数据不进行数据验证
        if (!isValid){
            return isValid;
        }

        // 数据验证
        // 当任务单id存在时，任务单单号不得为空
        CheckDataUtil.notBlank(this.taskNo, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " 没有对应的任务单单号，但可以进行排程");

        // 当任务单id存在时，任务单名称不得为空
        CheckDataUtil.notBlank(this.taskName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " 没有对应的任务单名称，但可以进行排程");

        // 当任务单id存在时，试验类型不得为空
        CheckDataUtil.notBlank(this.testType, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " 没有对应的试验类型，但可以进行排程");

        // 当任务单id存在时，功能块id不得为空
        CheckDataUtil.notBlank(this.functionId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "任务单 " + taskNo + " 没有对应的功能块ID，但可以进行排程");

        return isValid;

    }

}
