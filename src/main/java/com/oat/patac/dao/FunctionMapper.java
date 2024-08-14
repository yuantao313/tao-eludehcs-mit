package com.oat.patac.dao;

import com.oat.patac.entity.EntityFunction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface FunctionMapper {

    /**
     * 获取所有的功能块对象
     * @return
     */
    ArrayList<EntityFunction> getFunction(@Param("functionId") Integer functionId);
}
