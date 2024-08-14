package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 这个实体类用来表示日历父类
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityCalendar extends EntitySuperCalendar{

    /**
     * 每周的工作日
     */
    private String weekWorkDay;

    // 以下结构后面没有用到，所以暂时被注释
    // todo: 后面确认没用可以删掉

    /**
     * 普通日历对应的每一天是否工作的切换时间，针对staff/equipment mode为0，也就是节假日不上班的情况
     * 比如9.30日8：30上班，17点下班，11：30-12：00休息，则时间为[00:00, 8:30, 11:30,12:00, 17:00, 00:00]
     * 注意：需要对普通日历考虑星期几工作，和节假日；
     * key是每一天的日期（时间用0点）；value是每天中切换是否工作的时间点
     */
    //private HashMap<Date, ArrayList<Date>> switchTimePerDaySpecialDayNotWork = new HashMap<>();
    /**
     * 普通日历对应的每一天是否工作的切换时间，针对staff/equipment mode为1，也就是节假日上班的情况
     * 比如9.30日8：30上班，17点下班，11：30-12：00休息，则时间为[00:00, 8:30, 11:30,12:00, 17:00, 00:00]
     * 注意：需要对普通日历考虑星期几工作
     */
    //private HashMap<Date, ArrayList<Date>> switchTimePerDaySpecialDayWork = new HashMap<>();
    /**
     * 普通日历对应的每一天是否工作的切换值，针对staff/equipment mode为0，也就是节假日不上班的情况
     * 比如对应上例为[0,100,0,100,0]
     * key是每一天的日期（时间用0点）；value是每天中切换是否工作的时间点之间的值，不工作为0，工作为100.
     */
    //private HashMap<Date, ArrayList<Double>> switchValuePerDaySpecialDayNotWork = new HashMap<>();
    /**
     * 普通日历对应的每一天是否工作的切换值，针对staff/equipment mode为1，也就是节假日上班的情况
     */
    //private HashMap<Date, ArrayList<Double>> switchValuePerDaySpecialDayWork = new HashMap<>();

}
