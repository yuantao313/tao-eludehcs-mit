package com.oat.patac.entity;

import lombok.Data;

/**
 * @author:yhl
 * @create: 2022-10-08 13:48
 * @Description:
 */
@Data
public class EntityEngineerFunctionRel {

    /**
     * 员工 Id
     */
    private String staffId;
    /**
     * 功能块Id
     */
    private Integer functionId;
    /**
     * 最低工作负荷
     */
    private Integer workLoad;
    /**
     * 最高工作负荷
     */
    private  Integer maxWorkLoad;
}
