package com.oat.patac.dao;

import com.oat.patac.entity.EntityTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface TaskMapper {

    /**
     * 获取所有的任务单对象
     * @return
     */
    ArrayList<EntityTask> getAllTaskByFunctionId(@Param("functionId") Integer functionId);

    /**
     * 获取所有的任务单对象
     * @return
     */
    ArrayList<EntityTask> getAllTask();
    /**
     * 获取sub task表内存在的任务单对象
     * @return
     */
    ArrayList<EntityTask> getTaskBySubTask(@Param("functionId") Integer functionId);

}
