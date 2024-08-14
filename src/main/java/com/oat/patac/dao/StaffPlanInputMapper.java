package com.oat.patac.dao;

import com.oat.patac.entity.EntityStaffPlanInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Mapper
public interface StaffPlanInputMapper {

    /**
     * 获取所有的员工计划输入对象
     * @return
     */
    ArrayList<EntityStaffPlanInput> getAllStaffPlanInput(@Param("allStaffIds") Set<String> allStaffIds,
                                                         @Param("planPeriodStartTime") Date planPeriodStartTime);
}
