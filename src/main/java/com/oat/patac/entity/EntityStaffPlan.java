package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author:yhl
 * @create: 2022-08-12 17:45
 * @Description: 员工活动计划父类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityStaffPlan {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 员工计划ID
     */
    private String staffPlanId ;
    /**
     * 员工ID
     */
    private String staffId ;
    /**
     * 小阶段计划ID
     */
    private String stepPlanId ;
    /**
     * 员工排程开始时间
     */
    private Date staffPlanStart ;
    /**
     * 员工排程结束时间
     */
    private Date staffPlanEnd ;

    /**
     * 员工活动是否为排程约束
     */
    private String isConstraint ;
    /**
     * 模型中会使用的员工开始时间
     * 默认为plan start，如果有actual start，改为用actual start
     */
    private Date staffStartInModel ;
    /**
     * 模型中会使用的员工结束时间
     * 默认为plan end，如果有actual end，改为用actual end
     */
    private Date staffEndInModel ;

    /**
     * 员工活动占用的资源容量，是经过计算再赋值的
     */
    private double occupiedCapacity ;
    /**
     * 技能组合ID
     */
    private String skillIdSet;
    /**
     * 计划工时长（小时）
     */
    private BigDecimal planWorkTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityStaffPlan that = (EntityStaffPlan) o;
        return Objects.equals(functionId, that.functionId)
                && Objects.equals(staffPlanId, that.staffPlanId)
                && Objects.equals(staffId, that.staffId)
                && Objects.equals(stepPlanId, that.stepPlanId)
                && Objects.equals(staffPlanStart, that.staffPlanStart)
                && Objects.equals(staffPlanEnd, that.staffPlanEnd)
                && Objects.equals(isConstraint, that.isConstraint)
                && Objects.equals(skillIdSet, that.skillIdSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionId, staffPlanId, staffId, stepPlanId, staffPlanStart, staffPlanEnd, isConstraint, skillIdSet);
    }
}
