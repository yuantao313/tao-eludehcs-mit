package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:yhl
 * @create: 2022-08-10 17:08
 * @Description: 设备和设备组的关系
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEquipmentGroupRel {

    /**
     *  设备组的ID
     */
    private Integer equipmentGroupId ;
    /**
     *  设备的ID
     */
    private String equipmentId ;


    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
        return true;
    }


}
