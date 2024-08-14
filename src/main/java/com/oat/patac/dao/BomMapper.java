package com.oat.patac.dao;

import com.oat.patac.entity.EntityBom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface BomMapper {

    /**
     * 获取所有的bom对象
     * @return
     */
    ArrayList<EntityBom> getAllBom(@Param("functionId") Integer functionId);

    /**
     * 获取本次待排程批次对应的bom对象
     * @return
     */
    ArrayList<EntityBom> getBomBySubTask(@Param("functionId") Integer functionId);

}
