package com.oat.patac.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class EntityStepActivity {
    /**
     * 对应的小阶段
     */
    private EntityStep step;
    /**
     * 前序小阶段，没有前序小阶段取值为null
     */
    private EntityStepActivity previous;
    /**
     * 对于该小阶段活动而言的有效样机的数量
     */
    private int validSampleQuantity = 1;
    /**
     * 该小阶段活动的时长，已经按照对应子模型的时间颗粒度进行了转换
     */
    private double size;
    /**
     * 该小阶段活动，在包含了资源组后的最长时长，已经按照对应子模型的时间颗粒度进行了转换
     */
    private double maxSizeConsideredResources;

    /**
     * 该小阶段活动的资源组和资源组内可能用的资源列表
     * key是resource group name
     */
    private HashMap<String, ArrayList<Object>> possibleResources = new HashMap<>();
    /**
     * 该小阶段活动在资源组和资源组内可能用的资源上，对应的占用容量
     * key是resource group name +”_“+ resource id
     */
    private HashMap<String, Double> occupiedCapacity = new HashMap<>();

    /**
     * 该小阶段活动的资源组和资源组内被分配的资源（模型的输出）
     * key是resource group name
     */
    private HashMap<String, ArrayList<Object>> assignedResources = new HashMap<>();
    /**
     * 该小阶段活动的设备和设备活动的列表（模型的输出）
     * key是equipment id
     */
//    private HashMap<String, ArrayList<EntityEquipmentPlanInput>> equipmentEquipmentPlanOutputHashMap = new HashMap<>();
    private HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = new HashMap<>();
    /**
     * 该小阶段活动的人员和人员活动的列表（模型的输出）
     * key是staff id
     */
//    private HashMap<String, ArrayList<EntityStaffPlanInput>> staffStaffPlanOutputHashMap = new HashMap<>();
    private HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanOutputHashMap = new HashMap<>();
    /**
     * 小阶段活动时长按天切分得到的数即（res_day 变量总数）
     */
    private Integer resDaysSize;

}
