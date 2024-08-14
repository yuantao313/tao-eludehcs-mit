package com.oat.patac.dao;

import com.oat.patac.entity.EntityAuthStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface AuthStepMapper {

    /**
     * 获取所有的小阶段授权对象
     * @return
     */
    ArrayList<EntityAuthStep> getAllAuthStep(@Param("functionId") Integer functionId);
}