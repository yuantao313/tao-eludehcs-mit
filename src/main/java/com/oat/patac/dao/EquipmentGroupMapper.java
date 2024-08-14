package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipmentGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface EquipmentGroupMapper {

    /**
     * 获取排程需要所有的设备组对象
     * @return
     */
    ArrayList<EntityEquipmentGroup> getAllEquipmentGroup(@Param("functionId") Integer functionId);
}
