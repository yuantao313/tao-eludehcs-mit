package com.oat.patac.entity;

import lombok.*;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:33
 * @Description: 任务单计划结果
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class EntityTaskPlanOutput extends EntityTaskPlan{

    /**
     * 是否可调整
     */
//    private String isAdjustable;
    /**
     * 排程任务ID
     */
    private String planId;



}
