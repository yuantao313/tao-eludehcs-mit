package com.oat.patac.dao;

import com.oat.patac.entity.EntityTestArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Mapper
public interface TestAreaMapper {


    //    @MapKey("testAreaId")
    /**
     * 获取所有的业务区域对象
     * @return
     */
    ArrayList<EntityTestArea> getAllTestArea(@Param("functionId") Integer functionId);

    /**
     * 添加业务区域
     */
//    void addTestArea(EntityTestArea testArea);
}
