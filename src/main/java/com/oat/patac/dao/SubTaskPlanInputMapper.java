package com.oat.patac.dao;

import com.oat.patac.entity.EntitySubTaskPlanInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Set;

@Mapper
public interface SubTaskPlanInputMapper {

    /**
     * 获取所有的批次计划输入对象
     * @return
     */
    ArrayList<EntitySubTaskPlanInput> getAllSubTaskPlanInput(@Param("functionId") Integer functionId);
    /**
     * 获取待排程批次中的任务单（即本次待排程的任务单）中的已排程批次
     * @return
     */
    ArrayList<EntitySubTaskPlanInput> getSubTaskPlanInputOfSameTask (@Param("functionId") Integer functionId);
    /**
     * 获取和待排程批次在同一个批次组的已排程批次
     * @return
     */
    ArrayList<EntitySubTaskPlanInput> getSubTaskPlanInputOfSameSubTaskGroup (@Param("functionId") Integer functionId);

    /**
     * 获取和待排程批次使用相同的整车的已排程批次
     * @return
     */
    ArrayList<EntitySubTaskPlanInput> getSubTaskPlanInputOfSameUniqueSample (@Param("functionId") Integer functionId,
                                                                             @Param("uniqueSampleNoSet") Set<String> uniqueSampleNoSet);

}
