package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

import static com.oat.common.utils.ConstantUtil.DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME;

/**
 * @author:yhl
 * @create: 2022-08-26 17:09
 * @Description: 小阶段技能的需求信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class EntityStepSkill extends EntityResourceGroup {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 主键Id
     */
    private Integer stepSkillId;
    /**
     * 技能ID
     */
    private String skillIdSet;
    /**
     * BOM_NO
     */
    private String bomNo;
    /**
     * 小阶段ID
     */
    private Integer stepId ;
    /**
     * 技师个数
     */
    private Integer technicianCount;
    /**
     * 技师工作时间
     */
    private BigDecimal technicianWorktime;
    /**
     * 技师日常模式
     */
    private String technicianDailyMode;
    /**
     * 技师工作天数
     */
    private Integer technicianDayNum;
    /**
     * 技师每天工时
     */
    private BigDecimal technicianDailyWorktime;
    /**
     * 技师工时类型
     */
    private String technicianWorktimeType;
    /**
     * 是否排程约束（技师）
     */
    private String isTechnicianConstraint;


    public Boolean singleCheck(EntityStep step){
        // 是否过滤
        Boolean isValid = true;

        // 小阶段需求的技师数量<=0
        if (technicianCount == null || technicianCount <= 0){
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "BOM " + bomNo + " 的小阶段 " + step.getStepName() + " （阶段顺序号 "
                            + step.getStepOrder() +"，ID "+ stepId + "）的技师技能组 " +  skillIdSet + " 要求技师数量为空或小于等于0，忽略该设置，继续排程");
            //log.info("BOM " + bomNo + " 的小阶段 " + stepId + " 的技师技能组 " +  skillIdSet + " 要求技师数量小于等于0，忽略该设置，继续排程");
            return false;
        }

        if (technicianDailyMode.equals(ConstantUtil.RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
            // 按天工作时，工作天数<=0
            if (technicianDayNum == null){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                        "BOM " + bomNo + " 的小阶段 "+ step.getStepName() + " （阶段顺序号 "
                                + step.getStepOrder() +"，ID "+ stepId + "）的技师技能组 " + skillIdSet +
                                " 是按天工作模式，但是工作天数为空，忽略该记录，继续排程");
                isValid = false;
            } else if (technicianDayNum <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + step.getStepName() + " （阶段顺序号 "
                                + step.getStepOrder() +"，ID "+ stepId + "）的技师技能组 " + skillIdSet +
                                " 是按天工作模式，但是工作天数过短，为 " +technicianDayNum+ "，忽略该记录，继续排程");
                isValid = false;
            }
            // 按天工作时，每天工作时长<=0
            if (technicianDailyWorktime == null){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                        "BOM " + bomNo + " 的小阶段 " + step.getStepName() + " （阶段顺序号 "
                                + step.getStepOrder() +"，ID "+ stepId + "）的技师技能组 " + skillIdSet +
                                " 是按天工作模式，但是每天工作时长为空，忽略该记录，继续排程");
                isValid = false;
            } else if (technicianDailyWorktime.doubleValue() <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + step.getStepName() + " （阶段顺序号 "
                                + step.getStepOrder() +"，ID "+ stepId + "）的技师技能组 " + skillIdSet +
                                " 是按天工作模式，但是每天工作时长过短，为 " +technicianDailyWorktime+ "，忽略该记录，继续排程");
                isValid = false;
            }
        }


        return isValid;
    }

}
