package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:42
 * @Description: 设备计划父类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEquipmentPlan {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 设备计划ID
     */
    private String equipmentPlanId ;
    /**
     * 设备编号
     */
    private String equipmentId ;
    /**
     * 小阶段计划ID
     */
    private String stepPlanId ;
    /**
     * 设备排程开始时间
     */
    private Date equipmentPlanStart ;
    /**
     * 设备排程结束时间
     */
    private Date equipmentPlanEnd ;

    /**
     * 模型中会使用的设备活动开始时间
     * 默认为plan start，如果有actual start，改为用actual start
     */
    private Date equipmentStartInModel ;
    /**
     * 模型中会使用的设备活动结束时间
     * 默认为plan end，如果有actual end，改为用actual end
     */
    private Date equipmentEndInModel ;


    /**
     * 设备活动是否为排程约束
     */
    private String isConstraint ;
    /**
     * 设备活动是否为可扩展
     */
    private String isExpandable ;
    /**
     * 设备活动占用的设备容量
     */
    private Integer equipmentQty ;
    /**
     * 设备活动时设备的状态
     */
    private String equipmentStatus ;

    /**
     * 设备活动占用的资源容量
     */
    private double occupiedCapacity ;



}
