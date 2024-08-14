package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipmentPlanOutput;
import com.oat.patac.entity.EntityStepPlanOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface EquipmentPlanOutputMapper {

    /**
     * 获取所有的输出设备计划对象
     * @return
     */
    ArrayList<EntityEquipmentPlanOutput> getAllEquipmentPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 批量将结果写入数据库
     * @param equipmentPlanOutputs
     * @return
     */
    Integer insertAllEquipmentPlanOutputs(@Param("equipmentPlanOutputs") List<EntityEquipmentPlanOutput> equipmentPlanOutputs);

    /**
     * 查出指定功能块的计划数据
     */
    List<EntityEquipmentPlanOutput> selectAllByEquipmentPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 删除指定 functionId 的 EquipmentPlanOutputs
     * @param functionId
     */
    void deleteEquipmentPlanOutputsByFunctionId(@Param("functionId") Integer functionId);
}
