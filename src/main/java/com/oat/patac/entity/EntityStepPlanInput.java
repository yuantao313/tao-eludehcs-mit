package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.*;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 16:48
 * @Description: 小阶段计划
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EntityStepPlanInput extends EntityStepPlan{

    /**
     * 小阶段实际开始时间
     */
    private Date stepActualStart ;
    /**
     * 小阶段实际结束时间
     */
    private Date stepActualEnd ;


    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){

        // 小阶段计划开始时间小于小阶段计划结束时间
        CheckDataUtil.timeCompare(this.getStepPlanStart(), this.getStepPlanEnd(), ConstantUtil.MESSAGE_SEVERITY_ERROR,
                "小阶段计划" + this.getStepPlanId() + "对应的小阶段计划开始时间晚于小阶段计划结束时间，无法进行排程");

        // 小阶段实际开始时间小于小阶段实际结束时间
        CheckDataUtil.timeCompare(this.stepActualStart, this.stepActualEnd, ConstantUtil.MESSAGE_SEVERITY_ERROR,
                "小阶段计划" + this.getStepPlanId() + "对应的小阶段实际开始时间晚于小阶段实际结束时间，无法进行排程");

        return true;
    }

}
