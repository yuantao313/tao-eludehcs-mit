package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipmentCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface EquipmentCalendarMapper {

    /**
     * 获取所有设备分配的日历对象
     * @return
     */
    ArrayList<EntityEquipmentCalendar> getAllEquipmentCalendar(@Param("functionId") Integer functionId);
}
