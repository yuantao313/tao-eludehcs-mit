package com.oat.patac.dao;

import com.oat.patac.entity.EntitySample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface SampleMapper {

    /**
     * 获取所有的样品对象
     * @return
     */
    ArrayList<EntitySample> getAllSample(@Param("functionId") Integer functionId);
    /**
     * 获取本次待排程批次和待排程批次所在任务单中的已完成批次使用的样品实体
     * @return
     */
    ArrayList<EntitySample> getSampleBySubTask(@Param("functionId") Integer functionId);
}
