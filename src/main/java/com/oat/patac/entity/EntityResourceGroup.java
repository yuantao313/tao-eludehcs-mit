package com.oat.patac.entity;

import lombok.Data;

import java.util.ArrayList;

import static com.oat.common.utils.ConstantUtil.DEFAULT_RESOURCE_CAPACITY;

@Data
public class EntityResourceGroup {

    /**
     * 资源的类型，见常量定义
     */
    private String resourceType;
    /**
     * 资源组的名字，不同的step内的资源组，可用同名，
     * 工程师的都叫engineer，
     * 技工的以skillset集合命名（如果为空则指定特殊默认名字，比如“empty”），
     * 设备组的以设备组id为名字
     */
    private String resourceGroupName;
    /**
     * 资源组中可能使用的资源
     * 对象可能是EntityStaff，也可能是EntityEquipment
     */
    private ArrayList<Object> possibleResources = new ArrayList<>();
    /**
     * 需要资源的个数
     */
    private int requestedResourceQuantity;
    /**
     * 需要资源的工作时间，如果是不按天工作的，是总时间；按天工作的是每天的工作时间
     */
    private double requestedWorkTime;
    /**
     * 需要的资源的工作模式，按天或者不按天
     */
    private String requestedDailyMode;
    /**
     * 需要资源工作的天数
     */
    private int requestedDayQuantity;
    /**
     * 是否需要资源的活动和小阶段活动同时开始
     */
    private boolean isSameStart = false;
    /**
     * 是否需要资源的活动和小阶段活动同时结束
     */
    private boolean isSameEnd = false;
    /**
     * 是否需要资源的活动和小阶段活动同时开始和结束
     */
    private boolean isSynchronized = false;
    /**
     * 是否需要资源的活动是否作为排程约束，即是否占用资源的容量
     */
    private boolean isConstraint = false;
    /**
     * 需要资源的容量
     */
    private int requestedResourceCapacity = DEFAULT_RESOURCE_CAPACITY;
    /**
     * 该资源组的资源的活动是否可和其他活动共享容量
     */
    private boolean isExpandable = true;
    /**
     * 设备的状态
     */
    private String state;

}
