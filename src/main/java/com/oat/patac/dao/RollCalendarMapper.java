package com.oat.patac.dao;

import com.oat.patac.entity.EntityRollCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Set;

@Mapper
public interface RollCalendarMapper {

    /**
     * 获取所有的翻班日历对象
     * @return
     */
    ArrayList<EntityRollCalendar> getAllRollCalendar(@Param("calendarIds") Set<Integer> calendarIds);
}
