package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


/**
 * 这个类的实体代表功能块
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityFunction{

    /**
     *功能块的ID, 具有唯一性
     */
    private Integer functionId;
    /**
     *功能块的名称
     */
    private String functionName;
//    /**
//     * 排程参数ID
//     * */
//    private Integer planConfigId ;
    /**
     * 实验室ID
     */
    private Integer labId;

    /**
     * 包含整车的样品组Id集合
     */
    // 应该不需要？
    //private ArrayList<Integer> vehicleGroupIds = new ArrayList<>();


    // 该功能块下的任务单子集
    /**
     * 需要排程的任务单的集合
     */
    private ArrayList<EntityTask> toBePlannedTasks = new ArrayList<>();
    /**
     * 需要排程的任务单中的最晚的审批时间
     */
    private Date maxApprovalDateOfTasks;

    /**
     * 以及被本次排程引擎排好程的任务单的集合，注意需要任务单下的所有批次都完成排程的才放到这里
     */
    //private ArrayList<EntityTask> plannedTasks = new ArrayList<>();
    /**
     * 下面有部分批次需要排程的任务单
     */
    //这个集合不确定是否需要，可以暂时不加
    //private ArrayList<Integer> partIsFixedTaskIds = new ArrayList<>();
    /**
     * 需要排程的而且允许不排入的任务单的集合
     */
    //这个集合不确定是否需要，可以暂时不加
    //private ArrayList<Integer> canBeAbsentTaskIds = new ArrayList<>();


    /**
     * function 单实体验证
     */
    public void singleCheck() {

        // 功能块id存在时，功能块名称不得为空
        CheckDataUtil.notBlank(this.functionName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "功能块" + functionId + "没有对应的功能块名称，但可以进行排程");

//        // 功能块id存在时，排程参数id不得为空
//        CheckDataUtil.notBlank(this.planConfigId, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_NOT_SCHEDULED,
//                "功能块" + functionId + "没有对应的排程参数");

        // 功能块id存在时，试验室id不得为空
        CheckDataUtil.notBlank(this.labId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "功能块" + functionId + "没有对应的试验室ID，但可以进行排程");

    }

}
