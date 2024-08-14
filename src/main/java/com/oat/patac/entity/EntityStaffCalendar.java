package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-15 15:44
 * @Description: 人员分配日历
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityStaffCalendar {

    /**
     * 员工的ID
     */
    private String staffId ;
    /**
     * 工作日历编号
     */
    private Integer calendarId ;
    /**
     * 工作日历类型
     */
    private String calendarClass;
    /**
     *  生效日期
     */
    private Date startFrom ;
    /**
     * 模式;0: 节假日不翻班（后续日历顺延）; 1: 节假日翻班（不顺延，当天日历被覆盖）
     */
    private String staffMode ;

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
        // 是否过滤
        Boolean isValid;

        // 当员工id存在时，工作日历类型不得为空
        isValid = CheckDataUtil.notBlank(this.calendarClass, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                "人员" + staffId + "对应的工作日历类型为空，该人员无法排程，忽略该人员，继续排程");

        // 当员工id存在时，工作日历id不得为空
        isValid = isValid && CheckDataUtil.notBlank(this.calendarId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                "人员" + staffId + "对应的工作日历ID为空，该人员无法排程，忽略该人员，继续排程");

        /*// 当员工id存在时，对应的生效日期不得晚于排程完成时间
        CheckDataUtil.timeCompare(this.startFrom, new Date(), ConstantUtil.MESSAGE_SEVERITY_ERROR,
                "员工" + staffId + "对应的工作日历开始时间晚于结束时间，无法进行排程");*/

        return isValid;
    }
}
