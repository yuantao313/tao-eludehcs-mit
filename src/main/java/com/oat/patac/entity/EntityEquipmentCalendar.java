package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-09 17:34
 * @Description: 设备分配日历
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEquipmentCalendar {

    /**
     * 设备编号
     */
    private String equipmentId ;
    /**
     * 工作日历类型
     */
    private String calendarClass;
    /**
     * 工作日历编号
     */
    private Integer calendarId ;
    /**
     * 生效日期
     */
    private Date startFrom ;
    /**
     * 模式;0: 节假日不翻班（后续日历顺延）; 1: 节假日翻班（不顺延，当天日历被覆盖）
     */
    private String equipmentMode ;


    public boolean singleCheck(){

        // 是否过滤
        Boolean isValid;

        // 当员工id存在时，工作日历类型不得为空
        isValid = CheckDataUtil.notBlank(this.calendarClass, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_EQUIPMENT_NOT_ENOUGH,
                "设备" + equipmentId + "对应的工作日历类型为空，该设备无法排程，忽略该设备，继续排程");

        // 当员工id存在时，工作日历id不得为空
        isValid = isValid && CheckDataUtil.notBlank(this.calendarId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_EQUIPMENT_NOT_ENOUGH,
                "设备" + equipmentId + "对应的工作日历ID为空，该设备无法排程，忽略该设备，继续排程");

  /*      // 当员工id存在时，对应的生效日期不得晚于排程完成时间
        CheckDataUtil.timeCompare(this.startFrom, new Date(), ConstantUtil.MESSAGE_SEVERITY_ERROR,
                "设备" + equipmentId + "对应的工作日历开始时间晚于结束时间，无法进行排程");*/

        return isValid;
    }
}
