package com.oat.patac.dao;

import com.oat.patac.entity.EntityTaskPlanOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface TaskPlanOutputMapper {

    /**
     * 获取所有的输出任务计划对象
     * @return
     */
    ArrayList<EntityTaskPlanOutput> getAllTaskPlanOutput(@Param("functionId") Integer functionId);

    /**
     * 批量写入
     * @param taskPlanOutputs
     * @return
     */
    Integer insertAllTaskPlanOutputs(@Param("taskPlanOutputs") List<EntityTaskPlanOutput> taskPlanOutputs);

    /**
     * 删除指定 functionId 的 taskPlanOutput
     * @param functionId
     */
    void deleteTaskPlanOutputsByFunctionId(@Param("functionId") Integer functionId);
}
