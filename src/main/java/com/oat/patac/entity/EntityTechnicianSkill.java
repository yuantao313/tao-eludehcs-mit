package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:yhl
 * @create: 2022-08-10 17:03
 * @Description: 技师具备技能
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityTechnicianSkill {

    /**
     * 员工ID
     */
    private String staffId;
    /**
     * 技能ID
     */
    private String skillId;

    /**
     * 该实体数据验证
     */
    public void singleCheck(){

    }

}
