package com.oat.patac.dao;

import com.oat.patac.entity.EntityPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface PlanMapper {

    int insertPlan(EntityPlan plan);

    void deletePlanByFunctionId(@Param("functionId") Integer functionId);
}
