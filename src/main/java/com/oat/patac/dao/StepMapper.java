package com.oat.patac.dao;

import com.oat.patac.entity.EntityStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface StepMapper {

    /**
     * 获取所有的小阶段对象
     * @return
     */
    ArrayList<EntityStep> getAllStep(@Param("functionId") Integer functionId);
}
