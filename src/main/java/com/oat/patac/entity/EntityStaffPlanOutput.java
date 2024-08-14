package com.oat.patac.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author:yhl
 * @create: 2022-08-12 17:45
 * @Description: 员工活动计划结果
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EntityStaffPlanOutput extends EntityStaffPlan{

    /**
     * 要求工时（小时）
     */
    private String planId;
    /**
     * 要求工时（小时）
     */
    private BigDecimal reqWorkTime;
    /**
     * 计划工时长（小时）
     */
    //private BigDecimal planWorkTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EntityStaffPlanOutput that = (EntityStaffPlanOutput) o;
        return Objects.equals(planId, that.planId)
                && Objects.equals(reqWorkTime, that.reqWorkTime)
                //&& Objects.equals(planWorkTime, that.planWorkTime)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), planId, reqWorkTime
                //, planWorkTime
        );
    }
}
