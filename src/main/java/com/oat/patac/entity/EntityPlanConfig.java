package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import static com.oat.common.utils.ConstantUtil.MESSAGE_SEVERITY_ERROR;
import static com.oat.common.utils.ConstantUtil.MESSAGE_TYPE_FIELD_MISS;

/**
 * 这个类的实体代表排程参数
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityPlanConfig {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 排程ID,具有唯一性
     * */
    private Integer planConfigId ;
    /**
     * 排程时间限制
     * */
    private Integer timeLimit;
    /**
     * 时间颗粒度
     * */
    private Integer planGranul;
//    /**
//     * 排程日历设置
//     * */
//    private String planCalendarSettings;

    /**
     * 生成日历天数
     * */
    private Integer calendarDays;
    /**
     * 委托天数
     * */
    private Integer entrustDays;
    /**
     * 排程提前量/最小准备时间，单位是小时
     * */
    private Integer leadFence;
    /**
     * 排程提前量/最小准备时间 和委托天数 是否计入周末和节假日
     * */
    private String isWeekendINC;

    /**
     * 根据委托天数和是否计入周末和节假日计算的最晚能接受的批次开始时间，
     * 用于结合批次样件提供的时间，判断批次是否在scope以内。
     * */
    private Date maxAcceptedSubTaskStartTime;

    /**
     * 计算得到的排程提前量/最小准备时间，考虑了是否考虑周末。单位小时
     * */
    private Date minPlanStartTime;
    /**
     * 计算得到的排程结束时间，考虑最小准备时间和排程天数
     * */
    private Date planEndTime;


    public  Boolean singleCheck(){
        return true;
    }
    /**
     * 该实体数据验证相关 task 是否排程
     */
    public Boolean singleCheckTask(){
        // 是否过滤
        Boolean isValid = true;

        // 颗粒度为空
        isValid = isValid && CheckDataUtil.notBlank(planGranul, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                    "排程参数模板 " + planConfigId + " 对应的排程颗粒度为空，导致相关任务单无法排程");
        // 排程时间限制为空
        isValid = isValid && CheckDataUtil.notBlank(timeLimit, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                "排程参数模板 " + planConfigId + " 对应的排程时间限制为空，导致相关任务单无法排程");
        // 生成日历天数为空
        isValid = isValid && CheckDataUtil.notBlank(calendarDays, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                "排程参数模板 " + planConfigId + " 对应的生成日历天数为空，导致相关任务单无法排程");
        // 委托天数
        isValid = isValid && CheckDataUtil.notBlank(entrustDays, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                "排程参数模板 " + planConfigId + " 对应的委托天数为空，导致相关任务单无法排程");
        // 排程提前量/最小准备时间为空
        isValid = isValid && CheckDataUtil.notBlank(leadFence,MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                "排程参数模板 " + planConfigId + " 对应的排程提前量/最小准备时间为空，导致相关任务单无法排程");
        // 排程提前量及委托天数是否计入周末及节假日时间
        isValid = isValid && CheckDataUtil.notBlank(isWeekendINC, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                "排程参数模板 " + planConfigId + " 对应的isWeekendINC为空，导致相关任务单无法排程");

        return isValid;
    }

    /**
     * 根据排程触发时间，计算实际排程提前量/最小准备时间，考虑是否考虑周末和节假日。
     * @param planPrepareTime:  排程提前量/最小准备时间，单位小时
     * @param isWeekendInclude: 是否计入周末及节假日时间：
     *                        指以上的排程提前量和委托天数是否需考虑周末的时间。
     *                        0表示周末及节假日作为休息时间、不计入排程提前量和委托天数，
     *                        例如某次排程触发是周五晚上24：00， 而排程提前量设为24小时。
     *                        此时如果此参数为0，周末不计入提前量，那么引擎在排程时，可以安排实验开始的最早时间为下个周一的晚上24：00。
     *                        如果此参数为1，周末计入提前量，那么引擎在排程时，可以安排实验开始的最早时间为本周6晚上24：00。
     *                        0 要加上周末和节假日时间，1不加周末和节假日时间
     * */
    // haolei todo: 除了考虑周末，还要同时考虑special day
    public static Date calculateMinPlanStartTime(Date planTriggeredTime, int planPrepareTime, boolean isWeekendInclude,
                                                 HashMap<Date, EntitySpecialDay> specialDayHashMap)  {
        // 获取整数小时数，原程序没有，暂时不加这个了
        //Date planTriggeredHour = DateUtil.getLatestHourDate(planTriggeredTime);
        Date planTriggeredHour = planTriggeredTime;
        // 不计入周末和节假日
        if (!isWeekendInclude){
            // 返回排程开始时间
            return calculateTime(planTriggeredHour, planPrepareTime, specialDayHashMap);
        }
        // 计入周末和节假日
        return DateUtil.addHour(planTriggeredHour, planPrepareTime);

    }


    /**
     * 根据排程触发时间，委托天数和是否计入周末和节假日计算的最晚能接受的批次开始时间，
     *             用于结合批次样件提供的时间，判断批次是否在scope以内。
     * @param isWeekendInclude: 是否计入周末及节假日时间：
     *                        指以上的排程提前量和委托天数是否需考虑周末的时间。
     *                        0表示周末及节假日作为休息时间、不计入排程提前量和委托天数，
     *                        例如某次排程触发是周五晚上24：00， 而排程提前量设为24小时。
     *                        此时如果此参数为0，周末不计入提前量，那么引擎在排程时，可以安排实验开始的最早时间为下个周一的晚上24：00。
     *                        如果此参数为1，周末计入提前量，那么引擎在排程时，可以安排实验开始的最早时间为本周6晚上24：00。
     *                        0 要加上周末和节假日时间，1不加周末和节假日时间
     * */
    public static Date calculateMaxAcceptedSubTaskStartTime(Date planTriggeredTime, int entrustDays, boolean isWeekendInclude,
                                                 HashMap<Date, EntitySpecialDay> specialDayHashMap)  {
        // 原程序没有考虑变为最近的小时，这里暂时不考虑
        //Date planTriggeredHour = DateUtil.getLatestHourDate(planTriggeredTime);
        Date planTriggeredHour = planTriggeredTime;
        // 不计入周末和节假日
        if (!isWeekendInclude){
            int hours = entrustDays * 24;
            return calculateTime(planTriggeredHour, hours, specialDayHashMap);
        }
        // 计入周末和节假日
        return DateUtil.addDay(planTriggeredHour, entrustDays);
    }


    /**
     * 计算出 tempTime 经过 hours 小时后的时间（不含周末和节假日）
     * @param tempTime 开始时间
     * @param hours 小时数
     * @param specialDayHashMap
     * @return
     */
    private static Date calculateTime(Date tempTime, int hours, HashMap<Date, EntitySpecialDay> specialDayHashMap){
        // 抵达目标时间
        while (hours !=  0){
            // ①特殊日期 1：周末工作日 ② 非特殊日期 工作日
            if ((specialDayHashMap.containsKey(tempTime) && specialDayHashMap.get(tempTime).getWorkDayType().equals(ConstantUtil.SPECIAL_DAY_WEEKEND_WORK)) ||
                    (!specialDayHashMap.containsKey(tempTime) && !DateUtil.isWeekend(tempTime))) {
                Date nextDay = DateUtil.addDay(tempTime, 1);
                int hourNum = (int) ((nextDay.getTime() - tempTime.getTime()) / (1000 * 3600));
                if (hours > hourNum){
                    hours -= hourNum;
                    tempTime = DateUtil.addHour(tempTime, hourNum);
                }else {
                    tempTime = DateUtil.addHour(tempTime, hours);
                    hours = 0;
                }
                // ③特殊日期 2：非周末休息日,3:节假日周末 ④ 非特殊日期 周末
            }else {
                tempTime = DateUtil.addDay(tempTime, 1);
            }
        }
        return tempTime;
    }
}
