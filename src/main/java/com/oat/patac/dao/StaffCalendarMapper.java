package com.oat.patac.dao;

import com.oat.patac.entity.EntityStaffCalendar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Set;

@Mapper
public interface StaffCalendarMapper {

    /**
     * 获取所有的 StaffCalendar 对象
     * @param allStaffIds 所有人员的 Id
     * @return
     */
    ArrayList<EntityStaffCalendar> getAllStaffCalendars(@Param("allStaffIds") Set<String> allStaffIds);

}
