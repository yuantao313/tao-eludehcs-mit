package com.oat.patac.dao;

import com.oat.patac.entity.EntitySubTaskPlanOutput;
import com.oat.patac.entity.EntityTaskPlanOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface SubTaskPlanOutputMapper {

    /**
     * 获取所有的输出批次计划对象
     * @return
     */
    ArrayList<EntitySubTaskPlanOutput> getAllSubTaskPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 批量将结果写入数据库
     * @param subTaskPlanOutputs
     * @return
     */
    int insertAllSubTaskPlanOutputs(@Param("subTaskPlanOutputs") List<EntitySubTaskPlanOutput> subTaskPlanOutputs);

    /**
     * 查出指定功能块的计划数据
     * @param functionId
     * @return
     */
    List<EntitySubTaskPlanOutput> selectAllSubTaskPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 删除指定 functionId 的 subTaskPlanOutput
     * @param functionId
     */
    void deleteSubTaskPlanOutputsByFunctionId(@Param("functionId") Integer functionId);
}
