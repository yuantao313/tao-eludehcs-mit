package com.oat.patac.dao;

import com.oat.patac.entity.EntityTaskPlanInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface TaskPlanInputMapper {

    /**
     * 获取所有的输入计划对象
     * @return
     */
    ArrayList<EntityTaskPlanInput> getAllTaskPlanInput(@Param("functionId") Integer functionId);

}
