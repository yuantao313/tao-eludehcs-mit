package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

/**
 * @author:yhl
 * @create: 2022-08-12 17:39
 * @Description: 小阶段计划父类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityStepPlan {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 小阶段计划ID
     */
    private String stepPlanId ;
    /**
     * BOM编号
     */
    private String bomNo ;
    /**
     * 批次ID
     */
    private String subTaskNo ;
    /**
     * 小阶段ID
     */
    private Integer stepId ;
    /**
     * 小阶段排程开始时间
     */
    private Date stepPlanStart ;
    /**
     * 小阶段排程结束时间
     */
    private Date stepPlanEnd ;

}
