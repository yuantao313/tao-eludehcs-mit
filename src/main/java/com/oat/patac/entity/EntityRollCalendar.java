package com.oat.patac.entity;

import lombok.*;

/**
 * @author:yhl
 * @create: 2022-08-09 17:14
 * @Description: 翻班日历
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityRollCalendar extends EntitySuperCalendar{

    /**
     * 翻班序号
     */
    private Integer rollSeq ;
    /**
     *  重复次数
     * */
    private Integer repeateNum ;
    /**
     * 日历类型
     */
    private String calendarType ;

}
