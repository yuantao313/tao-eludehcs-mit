package com.oat.patac.dao;

import com.oat.patac.entity.EntityCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Set;

@Mapper
public interface CalendarMapper {

    /**
     * 获取所有的calendar对象
     * @return
     */
    ArrayList<EntityCalendar> getAllCalendar(@Param("calendarIds") Set<Integer> calendarIds);
}
