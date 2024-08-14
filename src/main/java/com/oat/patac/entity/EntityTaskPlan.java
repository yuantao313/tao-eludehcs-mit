package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:33
 * @Description: 任务单计划父类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityTaskPlan {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 委托单号
     */
    private String taskNo ;
    /**
     * 任务单排程开始时间
     */
    private Date taskPlanStart ;
    /**
     * 任务单排程结束时间
     */
    private Date taskPlanEnd ;
    /**
     * 责任工程师ID
     */
    private String respEngineerId;




}
