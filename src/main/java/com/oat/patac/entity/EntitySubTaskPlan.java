package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:36
 * @Description: 批次计划父类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EntitySubTaskPlan {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     *  批次编号
     */
    private String subTaskNo ;
    /**
     * 批次所在的任务单的单号
     */
    private String taskNo;
    /**
     * 批次所在的批次组
     */
    private String taskGroup;
    /**
     * 批次所在的批次组内顺序号
     */
    private Integer taskSeqNo;
    /**
     * 批次需要的样件组id
     */
    private Integer sampleGroupId;
    /**
     * 样品编号
     */
    private String sampleNo;
    /**
     * 试验负责人ID
     */
    private String respEngineerId ;
    /**
     * 批次排程开始时间
     */
    private Date subTaskPlanStart ;
    /**
     * 批次排程结束时间
     */
    private Date subTaskPlanEnd ;
    /**
     * 排程任务ID
     */
    //private String planId;

    /**
     * 模型中会使用的设备活动开始时间
     * 默认为plan start，如果有actual start，改为用actual start
     * todo: 赋值
     */
    @EqualsAndHashCode.Exclude
    private Date subTaskStartInModel ;
    /**
     * 模型中会使用的设备活动结束时间
     * 默认为plan end，如果有actual end，改为用actual end
     */
    @EqualsAndHashCode.Exclude
    private Date subTaskEndInModel ;


}
