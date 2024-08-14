package com.oat.patac.entity;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:42
 * @Description: 设备计划结果
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EntityEquipmentPlanOutput extends EntityEquipmentPlan {

    /**
     * 排程任务ID
     */
    private String planId;
    /**
     * 要求工时（小时）
     */
    private BigDecimal reqWorkTime;
    /**
     * 计划工时长（小时）
     */
    private BigDecimal planWorkTime;
    /**
     * 设备组编号
     */
    private Integer equipmentGroupId;
}
