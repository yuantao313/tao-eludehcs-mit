package com.oat.patac.dao;

import com.oat.patac.entity.EntitySpecialDay;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;

@Mapper
public interface SpecialDayMapper {

    /**
     * 获取所有的特殊日历信息
     * @return
     */
    ArrayList<EntitySpecialDay> getAllSpecialDay();

    /**
     * 获取某个时间之后的特殊日历信息
     * @return
     */
//    ArrayList<EntitySpecialDay> getSpecialDayAfterSpecificTime(@Param("time") Date time);
    ArrayList<EntitySpecialDay> getSpecialDayAfterSpecificTime(@Param("time") Date time);

}
