package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface EquipmentMapper {

    /**
     * 获取所有的设备对象
     * @return
     */
    ArrayList<EntityEquipment> getAllEquipment(@Param("functionId") Integer functionId);
}
