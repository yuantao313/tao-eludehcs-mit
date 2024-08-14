package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这个实体类代表设备组，包含多个设备
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEquipmentGroup {

    /**
     *设备组的ID, 具有唯一性
     */
    private Integer equipmentGroupId;
    /**
     * 设备组的名称
     */
    private String equipmentGroupName;
    /**
     * 实验室的ID
     */
    private Integer labId;
    /**
     * 设备故障处理 0故障不影响试验 1受故障影响试验需要重排
     */
    private String equipmentFaliureType;


    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){

        // 当设备组id存在时，设备组名称不能为空

        // 当设备组id存在时，设备组labId不能为空
        CheckDataUtil.notBlank(this.labId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备组" + equipmentGroupId + "对应的lab_id为空，但可以进行排程");

        return true;
    }

}
