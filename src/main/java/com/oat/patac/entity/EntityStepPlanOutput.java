package com.oat.patac.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author:yhl
 * @create: 2022-08-12 17:39
 * @Description: 小阶段计划结果
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EntityStepPlanOutput extends EntityStepPlan{

    /**
     * 排程任务ID
     */
    private String planId;
    /**
     * 请求工时（小时）
     */
    private BigDecimal reqProcTime;
    /**
     * 计划跨时（小时）
     */
    private BigDecimal planDuration;
    /**
     * 考虑资源组请求工时之后的最大工时（size）
     */
    @EqualsAndHashCode.Exclude
    private BigDecimal planProcTime;


}
