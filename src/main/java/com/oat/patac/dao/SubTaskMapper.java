package com.oat.patac.dao;

import com.oat.patac.entity.EntitySubTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface SubTaskMapper {

    /**
     * 老DB老模式
     * 获取按functionId过滤的批次信息
     */
    ArrayList<EntitySubTask> getSubTaskByFunctionId(@Param("functionId") Integer functionId);

    /**
     * 新DB新模式
     * 获取按functionId过滤的批次信息
     */
    ArrayList<EntitySubTask> getSubTask();;
}
