package com.oat.patac.dao;

import com.oat.patac.entity.EntityEquipmentMaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface EquipmentMaintMapper {

    /**
     * 获取所有的的设备维护信息对象
     * @return
     */
    ArrayList<EntityEquipmentMaint> getAllEquipmentMaint(@Param("functionId") Integer functionId);
}
