package com.oat.patac.dao;

import com.oat.patac.entity.EntityStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.HashSet;

@Mapper
public interface StaffMapper {

    /**
     * 获取所有的工程师对象
     * @return
     */
    ArrayList<EntityStaff> getAllEngineers(@Param("functionId") Integer functionId);

    /**
     * 小阶段需要技能不为空时
     * 先联表查询 step_skill 和 technician_skill 通过技能Id找出对应的 staffId
     * 再联表 staff表 查询到对应的技师
     * 获取所有的技师对象
     * @return
     */
    ArrayList<EntityStaff> getAllTechniciansByStepSkill(@Param("skills") HashSet<String> skills);

    /**
     * 小阶段需要技能为空时
     * 查询所有的 Technicians 技师
     * @return
     */
    ArrayList<EntityStaff> getAllTechnicians();

    /**
     * 查询所有的 人员
     * @return
     */
    ArrayList<EntityStaff> getAllStaffs();
}
