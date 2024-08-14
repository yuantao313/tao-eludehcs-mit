package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author:yhl
 * @create: 2022-08-12 17:16
 * @Description: 员工活动计划
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntityStaffPlanInput extends EntityStaffPlan{

    /**
     * 员工实际开始时间
     */
    private Date staffActualStart ;
    /**
     * 员工实际结束时间
     */
    private Date staffActualEnd ;

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
//        if (ConstantUtil.RESOURCE_GROUP_IS_NOT_CONSTRAINT.equals(getIsConstraint())){
//            return false;
//        }
        // 是否过滤
        Boolean isValid = true;

        // 员工实际开始时间小于员工实际结束时间
        if (staffActualStart != null && staffActualEnd != null){
            isValid = CheckDataUtil.timeCompare(this.staffActualStart, this.staffActualEnd, ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    "员工计划" + getStaffPlanId() + "对应的员工" + getStaffId() + "的实际开始时间晚于员工实际结束时间，使用计划时间，继续排程");
        }
        // 员工计划开始时间小于员工计划结束时间
        if (getStaffPlanStart() != null && getStaffPlanEnd() != null){
            isValid = isValid && CheckDataUtil.timeCompare(this.getStaffPlanStart(), this.getStaffPlanEnd(), ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    "员工计划" + getStaffPlanId() + "对应的员工" + getStaffId() + "的计划开始时间晚于员工计划结束时间，忽略该记录，继续排程");
        }
        // IS_CONSTRAINT不能为空
        isValid = isValid && CheckDataUtil.notBlank(this.getIsConstraint(), ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "员工计划" + getStaffPlanId() + "对应的员工" + getStaffId() + "的IS_CONSTRAINT字段为空，忽略该记录，继续排程");

        return isValid;
    }

}
