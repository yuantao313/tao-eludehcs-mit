package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * @author:yhl
 * @create: 2022-08-04 14:52
 * @Description: 小阶段设备组需求信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class EntityStepEquipmentGroup extends EntityResourceGroup {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 设备组ID
     */
    private Integer equipmentGroupId;
    /**
     * BOM_NO
     */
    private String bomNo;
    /**
     * 小阶段ID
     */
    private Integer stepId;
    /**
     * 设备组名称
     */
    private String equipmentGroupName;
    /**
     * 设备数量
     */
    private Integer equipmentNum;
    /**
     * 需每个设备的容量
     */
    private Integer equipmentQty;
    /**
     * 工作时长
     */
    private BigDecimal workTime;
    /**
     * 是否固定设备
     */
    private String isFixingEquipment;
    /**
     * 是否作为排程约束
     */
    private String isConstraint;
    /**
     * 设备状态
     */
    private String equipmentStatus;
    /**
     * 是否按日工时 0: 不按天，1:按天
     */
    private String dailyMode;
    /**
     * 工作日数
     */
    private Integer dayNum;
    /**
     * 本次试验每天花费工时
     */
    private BigDecimal dailyWorkTime;
    /**
     * 设备使用的可扩展性
     */
    private String isExpandableString;
    /**
     * 是否与阶段时间一致 （0:不同步，1:同步）
     */
    private String isSyncStep;

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(String stepName, int stepOrder) {

        // 是否过滤
        Boolean isValid = true;

        // 小阶段需求的设备数量<=0
        if (equipmentNum == null || equipmentNum <= 0){
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 "+ stepOrder + "，ID "+ stepId
                            + "）的设备组"+ equipmentGroupName + " 要求的设备数量为空或小于等于0，忽略该设置，继续排程");
//            log.info("BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 "+ stepOrder + "，ID "+stepId
//                    + "）的设备组 " + equipmentGroupName + " 要求的设备数量小于等于0，忽略该设置，继续排程");
            isValid = false;
        }

        if (isExpandableString == null) {
            // 默认为不可以expandable
            isExpandableString = "0";
        }

        // 小阶段需求的设备容量<=0
        if (equipmentQty == null || equipmentQty <= 0){
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 " + stepOrder + "，ID " + stepId
                            + "）的设备组 " + equipmentGroupName + " 需要的设备容量为空或小于等于0，采用默认设置1，继续排程");
            equipmentQty = 1;
        }

        // 按天工作时，工作天数<=0
        if (dailyMode.equals(ConstantUtil.RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
            if (dayNum == null || dayNum <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 "+ stepOrder + "，ID "+stepId
                                + "）的设备组 " + equipmentGroupName +
                                " 按天工作，但工作天数为空或小于等于0，忽略该记录，继续排程");
                isValid = false;
            }
            // 按天工作时，每天工作时长<=0
            if (dailyWorkTime == null || dailyWorkTime.doubleValue() <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 "+ stepOrder + "，ID "+stepId
                                + "）的设备组 " + equipmentGroupName +
                               " 按天工作，但工作时长为空或小于等于0，忽略该记录，继续排程");
                isValid = false;
            }
        } else {
            // 不按天工作
            if (workTime == null && (!isSyncStep.equals(EQUIPMENT_IS_SYNC_STEP))) {
                //不按天工作，不是syn step，工作时长为空的情况
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 " + stepOrder + "，ID " + stepId
                                + "）的设备组 " + equipmentGroupName +
                                " 工作时长为空，而且没有设置为与阶段时间一致，忽略该记录，继续排程");
                isValid = false;
            }
        }
        return isValid;
    }
}
