package com.oat.patac.dao;


import com.oat.patac.entity.EntityAuthBom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface AuthBomMapper {

    /**
     * 根据批次的需要，获取所有的bom授权对象
     * @return
     */
    ArrayList<EntityAuthBom> getAllAuthBom(@Param("functionId") Integer functionId);
}