package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipmentPlanInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Mapper
public interface EquipmentPlanInputMapper {

    /**
     * 获取所有的设备计划输入对象
     * @return
     */
    ArrayList<EntityEquipmentPlanInput> getAllEquipmentPlanInput(@Param("equipmentIdSet") Set<String> equipmentIdSet,
                                                                 @Param("planPeriodStartTime") Date planPeriodStartTime);
}
