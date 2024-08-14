package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipmentGroupRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface EquipmentGroupRelMapper {

    /**
     * 获取所有的设备和设备组对象
     * @return
     */
    ArrayList<EntityEquipmentGroupRel> getAllEquipmentGroupRel(@Param("functionId") Integer functionId);
}