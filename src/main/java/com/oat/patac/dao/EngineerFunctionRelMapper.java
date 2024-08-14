package com.oat.patac.dao;

import com.oat.patac.entity.EntityEngineerFunctionRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface EngineerFunctionRelMapper {

    /**
     * 获取所有的 EngineerFunctionRel 对象
     * @return
     * @param functionId
     */
    ArrayList<EntityEngineerFunctionRel> getAllEngineerFunctionRels(@Param("functionId") Integer functionId);
}
