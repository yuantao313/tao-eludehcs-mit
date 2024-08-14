package com.oat.patac.dao;

import com.oat.patac.entity.EntityProcedure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface ProcedureMapper {

    /**
     * 获取所有的规范对象
     * @return
     */
    ArrayList<EntityProcedure> getAllProcedure(@Param("functionId") Integer functionId);

    /**
     * 获取sub task表内存在的规范对象
     * @return
     */
    ArrayList<EntityProcedure> getProcedureBySubTask(@Param("functionId") Integer functionId);
}
