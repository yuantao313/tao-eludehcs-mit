package com.oat.patac.dao;

import com.oat.patac.entity.EntityStaffPlanOutput;
import com.oat.patac.entity.EntityStepPlanOutput;
import com.oat.patac.entity.EntitySubTaskPlanOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface StepPlanOutputMapper {

    /**
     * 获取所有的输出小阶段计划对象
     * @return
     */
    ArrayList<EntityStepPlanOutput> getAllStepPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 批量将结果写入数据库
     * @param stepPlanOutputs
     * @return
     */
    int insertAllStepPlanOutputs(@Param("stepPlanOutputs") List<EntityStepPlanOutput> stepPlanOutputs);

    /**
     * 查出指定功能块的计划数据
     * @param functionId
     * @return
     */
    List<EntityStepPlanOutput> selectAllStepPlanOutputs(@Param("functionId") Integer functionId);

    /**
     * 删除指定 functionId 的 StepPlanOutputs
     * @param functionId
     */
    void deleteStepPlanOutputsByFunctionId(@Param("functionId") Integer functionId);
}
