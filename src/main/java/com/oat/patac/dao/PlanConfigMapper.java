package com.oat.patac.dao;

import com.oat.patac.entity.EntityPlanConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface PlanConfigMapper {

    /**
     * 获取所有的排程参数对象
     * @return
     */
    ArrayList<EntityPlanConfig> getAllPlanConfigs(@Param("functionId") Integer functionId);

    /**
     * 获取sub task表内存在的BOM对应的排程参数模板对象
     * @return
     */
    ArrayList<EntityPlanConfig> getPlanConfigBySubTaskBOM(@Param("functionId") Integer functionId);
}
