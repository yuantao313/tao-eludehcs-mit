package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-04 15:27
 * @Description: 年度节假日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntitySpecialDay{

    /**
     * 特殊日期 如:20220101
     */
    private Date specialDay;
    /**
     * 工作模式;1：周末工作日，2：非周末休息日,3:节假日周末
     */
    private String workDayType;

}
