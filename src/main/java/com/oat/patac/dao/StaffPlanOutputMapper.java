package com.oat.patac.dao;

import com.oat.patac.entity.EntityStaffPlanOutput;
import com.oat.patac.entity.EntitySubTaskPlanOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface StaffPlanOutputMapper {

    /**
     * 获取所有的输出批次计划对象
     * @return
     */
    ArrayList<EntityStaffPlanOutput> getAllStaffPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 批量将结果写入数据库
     * @param staffPlanOutputs
     * @return
     */
    int insertAllStaffPlanOutputs(@Param("staffPlanOutputs") List<EntityStaffPlanOutput> staffPlanOutputs);

    /**
     * 查询所有指定功能块的计划数据
     * @param functionId
     * @return
     */
    List<EntityStaffPlanOutput> selectStaffPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 删除指定 functionId 的 StaffPlanOutputs
     * @param functionId
     */
    void deleteStaffPlanOutputsByFunctionId(@Param("functionId") Integer functionId);
}
