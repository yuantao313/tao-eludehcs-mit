package com.oat.patac.dao;

import com.oat.patac.entity.EntitySkill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashSet;

@Mapper
public interface SkillMapper {

    /**
     * 获取所有技能的基础数据
     * @return
     */
    ArrayList<EntitySkill> getAllSkill(@Param("skills") HashSet<String> skills);
}
