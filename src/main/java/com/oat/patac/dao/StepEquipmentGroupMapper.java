package com.oat.patac.dao;

import com.oat.patac.entity.EntityStepEquipmentGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface StepEquipmentGroupMapper {

    /**
     * 获取所有的小阶段设备组对象
     * @return
     */
    ArrayList<EntityStepEquipmentGroup> getAllStepEquipment(@Param("functionId") Integer functionId);
}
