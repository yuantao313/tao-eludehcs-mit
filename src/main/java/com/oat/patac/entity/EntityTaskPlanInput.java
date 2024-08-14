package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.DateUtil;
import lombok.*;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 16:36
 * @Description: 任务单计划
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntityTaskPlanInput extends EntityTaskPlan{

    /**
     *  任务单实际开始时间
     */
    private Date taskActualStart ;
    /**
     *  任务单实际结束时间
     */
    private Date taskActualEnd ;
    /**
     *  是否完成
     */
    private String isCompleted ;

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){

        Boolean isValid = true;

        if (taskActualStart != null && taskActualEnd != null){
            // 任务单实际开始时间小于任务单实际结束时间
            CheckDataUtil.timeCompare(this.taskActualStart, this.taskActualEnd, ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    "已部分排程任务单 " + getTaskNo() + " 的实际开始时间 "+ DateUtil.getDateString(taskActualStart)
                            + " 晚于实际结束时间 "+ DateUtil.getDateString(taskActualEnd) + "，忽略原有时间，使用计划时间，继续排程");
        }

        // 任务单计划开始时间小于任务单计划结束时间
        CheckDataUtil.timeCompare(this.getTaskPlanStart(), this.getTaskPlanEnd(), ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                "已部分排程任务单 " + getTaskNo() + " 的计划开始时间" + DateUtil.getDateString(getTaskPlanStart())
                        + " 晚于计划结束时间 "+ DateUtil.getDateString(getTaskPlanEnd())+"，忽略原有时间，根据批次时间重新计算，继续排程");


        // 责任工程师id为空
        CheckDataUtil.notBlank(this.getRespEngineerId(), ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "已部分排程任务单 " + getTaskNo() + " 对应的责任工程师ID为空，忽略该设置，继续排程");

        // 当任务单id存在时，IS_COMPLETED应该为完成
        // todo: 没有这个check要求啊
        //CheckDataUtil.isNotEquals(this.isCompleted, "完成", ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_NOT_SCHEDULED,
        //        "任务单" + this.getTaskNo() + "“IS_COMPLETED” 的字段为未完成，无法进行排程");

        return isValid;
    }
}
