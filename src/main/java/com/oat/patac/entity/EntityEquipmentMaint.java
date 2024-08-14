package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-08 16:26
 * @Description: 设备维护信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEquipmentMaint {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 设备故障ID
     */
    private String equipmentFaultId ;
    /**
     * 设备编号
     */
    private String equipmentId ;
    /**
     * 是否影响试验
     */
    private String impactTest ;
    /**
     * 试验室ID
     */
    private Integer labId ;
    /**
     * 故障发生时间
     */
    private Date faultTime ;
    /**
     * 排故完成时间
     */
    private Date faultCompleteTime ;
    /**
     * 故障解决时间
     */
    private Date faultResolutionTime ;


    /**
     * 该实体数据验证
     */
    public void singleCheck(){

        // 设备维护编号id存在时，设备编号为空
        CheckDataUtil.notBlank(equipmentId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备故障" + equipmentFaultId + "对应的设备保养工单号为空，但可以进行排程");

        // 设备维护开始时间要晚于结束时间


    }
}
