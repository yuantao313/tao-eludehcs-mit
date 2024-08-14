package com.oat.patac.dao;

import com.oat.patac.entity.EntityStepPlanInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface StepPlanInputMapper {

    /**
     * 获取所有的小阶段计划输入对象
     * @return
     */
    ArrayList<EntityStepPlanInput> getAllStepPlanInput(@Param("functionId") Integer functionId);
}
