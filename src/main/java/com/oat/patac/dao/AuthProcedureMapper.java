package com.oat.patac.dao;


import com.oat.patac.entity.EntityAuthProcedure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface AuthProcedureMapper {

    /**
     * 获取所有的规范授权对象
     * @return
     */
    ArrayList<EntityAuthProcedure> getAllAuthProcedure(@Param("functionId") Integer functionId);

}