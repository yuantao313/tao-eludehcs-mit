package com.oat.patac.entity;

import lombok.*;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:36
 * @Description: 批次计划结果
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EntitySubTaskPlanOutput extends EntitySubTaskPlan{

    /**
     * 排程任务ID
     */
    private String planId;

}
