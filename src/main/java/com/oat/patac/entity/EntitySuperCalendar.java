package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author:yhl
 * @create: 2022-08-10 14:55
 * @Description: 日历类的父类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntitySuperCalendar {

    /**
     *日历的ID, 具有唯一性
     */
    private Integer calendarId;
    /**
     * 工作开始
     */
    private String shiftStart;
    /**
     * 工作结束
     */
    private String shiftEnd;
    /**
     * 午餐开始
     */
    private String breakStart;
    /**
     * 午餐结束
     */
    private String breakEnd;
    /**
     * 晚餐开始
     */
    private String supperBreakStart;
    /**
     * 晚餐结束
     */
    private String supperBreakEnd;

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(String calendarClass){

        // 是否过滤
        Boolean isValid = true;

      /*  // 开始时间晚于结束时间
        String[] shiftStartStr = shiftStart.trim().split(":");
        String[] shiftEndStr = shiftEnd.trim().split(":");
        Integer start = 0;
        start += Integer.parseInt(shiftStartStr[0]) * 60;
        start += Integer.parseInt(shiftStartStr[1]);
        Integer end = 0;
        end += Integer.parseInt(shiftEndStr[0]) * 60;
        end += Integer.parseInt(shiftEndStr[1]) * 60;*/

        // 开始时间晚于结束时间
        if (shiftStart != null && shiftEnd != null && !ConstantUtil.ZERO_POINT_TIME.equals(shiftEnd.trim())){
            if (shiftStart.trim().compareTo(shiftEnd.trim()) > 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_TIME_CONFICT,
                        calendarClass + calendarId + "对应的shiftStart开始时间晚于结束时间，忽略该记录，继续排程");
                isValid = false;
            }
        }
        if (breakStart != null && breakEnd != null && !ConstantUtil.ZERO_POINT_TIME.equals(breakEnd.trim())){
            if (breakStart.trim().compareTo(breakEnd.trim()) > 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_TIME_CONFICT,
                        calendarClass + calendarId + "对应的breakStart开始时间晚于结束时间，忽略该记录，继续排程");
                isValid = false;
            }
        }
        if (supperBreakStart != null && supperBreakEnd != null && !ConstantUtil.ZERO_POINT_TIME.equals(supperBreakEnd.trim())){
            if (supperBreakStart.trim().compareTo(supperBreakEnd.trim()) > 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_TIME_CONFICT,
                        calendarClass + calendarId + "对应的supperBreakStart开始时间晚于结束时间，忽略该记录，继续排程");
                isValid = false;
            }
        }


        return isValid;
    }

}
