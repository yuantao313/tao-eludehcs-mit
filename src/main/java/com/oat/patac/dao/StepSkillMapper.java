package com.oat.patac.dao;

import com.oat.patac.entity.EntityStepSkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface StepSkillMapper {

    /**
     * 获取所有的 stepSkill 对象
     * @return
     */
    ArrayList<EntityStepSkill> getAllStepSkills(@Param("functionId") Integer functionId);
}
