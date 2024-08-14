package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 16:51
 * @Description: 设备计划
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class EntityEquipmentPlanInput extends EntityEquipmentPlan{

    /**
     * 设备实际开始时间
     */
    private Date equipmentActualStart ;
    /**
     * 设备实际结束时间
     */
    private Date equipmentActualEnd ;


    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
        if (ConstantUtil.RESOURCE_GROUP_IS_NOT_CONSTRAINT.equals(getIsConstraint())){
            return false;
        }

        // 是否过滤
        Boolean isValid = true;

        // 设备实际开始时间小于设备实际结束时间
        if (equipmentActualStart != null && equipmentActualEnd != null){
            isValid = CheckDataUtil.timeCompare(this.equipmentActualStart, this.equipmentActualEnd, ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    "设备计划" + getEquipmentPlanId() + "对应的设备" + getEquipmentId() + "的实际开始时间晚于设备实际结束时间，使用计划时间，继续排程");
        }
        // 设备计划开始时间小于设备计划结束时间
        if (getEquipmentPlanStart() != null && getEquipmentPlanEnd() != null){
            isValid = isValid && CheckDataUtil.timeCompare(this.getEquipmentPlanStart(), this.getEquipmentPlanEnd(), ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    "设备计划" + getEquipmentPlanId() + "对应的设备" + getEquipmentId() + "的计划开始时间晚于设备计划结束时间，忽略该记录，继续排程");
        }
        // EQUIPMENT_QTY不能为空
        isValid = isValid && CheckDataUtil.notBlank(getEquipmentQty(), ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备计划" + getEquipmentPlanId() + "对应的设备" + getEquipmentId() + "的EQUIPMENT_QTY字段为空，忽略该记录，继续排程");
        // IS_EXPANDABLE不能为空
        isValid = isValid && CheckDataUtil.notBlank(getIsExpandable(), ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备计划" + getEquipmentPlanId() + "对应的设备" + getEquipmentId() + "的IS_EXPANDABLE字段为空，默认为不可扩展，继续排程");
        // IS_CONSTRAINT不能为空
        isValid = isValid && CheckDataUtil.notBlank(getIsConstraint(), ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备计划" + getEquipmentPlanId() + "对应的设备" + getEquipmentId() + "的IS_CONSTRAINT字段为空，忽略该记录，继续排程");

        /*// 设备计划id存在时，设备编号不为空
        CheckDataUtil.notBlank(this.getEquipmentId(), ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备计划" + this.getEquipmentPlanId() + "对应的设备编号为空，无法进行排程");*/

        return isValid;
    }
}
