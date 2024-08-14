package com.oat.patac.dao;

import com.oat.patac.entity.EntityTechnicianSkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashSet;

@Mapper
public interface TechnicianSkillMapper {

    /**
     * 获取所有的技师具备技能对象
     * @return
     */
    ArrayList<EntityTechnicianSkill> getAllTechnicianSkill(@Param("skills") HashSet<String> skills);

}