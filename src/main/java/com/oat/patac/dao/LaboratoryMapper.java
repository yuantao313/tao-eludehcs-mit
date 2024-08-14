package com.oat.patac.dao;

import com.oat.patac.entity.EntityLaboratory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface LaboratoryMapper {

    /**
     * 获取所有的实验室对象
     * @return
     */
    ArrayList<EntityLaboratory> getAllLaboratory(@Param("functionId") Integer functionId);
}
