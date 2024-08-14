package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.*;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 16:43
 * @Description: 批次计划
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EntitySubTaskPlanInput extends EntitySubTaskPlan{

//    /**
//     * 是否冻结
//     */
//    private String isFrozen ;
    /**
     * 批次实际开始时间
     */
    private Date subTaskActualStart ;
    /**
     * 批次实际结束时间
     */
    private Date subTaskActualEnd ;
    /**
     * 是否完成
     */
    private String isCompleted;

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(Boolean checkRespEngineerFlag){

        Boolean isValid = true;

        // 批次实际开始时间小于批次实际结束时间
        if (subTaskActualStart != null && subTaskActualEnd != null){
            isValid = isValid && CheckDataUtil.timeCompare(this.subTaskActualStart, this.subTaskActualEnd, ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    "已排程批次 " + getSubTaskNo() + " 对应的批次实际开始时间 " + subTaskActualStart
                    +"，晚于批次实际结束时间 "+ subTaskActualEnd + "，忽略该条记录，继续排程");
        }

        // 批次计划开始时间小于批次计划结束时间
        isValid = isValid && CheckDataUtil.timeCompare(this.getSubTaskPlanStart(), this.getSubTaskPlanEnd(), ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                "已排程批次 " + getSubTaskNo() + " 对应的批次计划开始时间 "+ this.getSubTaskPlanStart() +
                        "，晚于批次计划结束时间 "+ this.getSubTaskPlanEnd() + "，忽略该条记录，继续排程");

        // 试验批次的指定负责人为空
        if (checkRespEngineerFlag) {
            CheckDataUtil.notBlank(getRespEngineerId(), ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "已排程批次 " + getSubTaskNo() + " 对应的指定负责人为空，忽略该设置，继续排程");

        }

        return isValid;
    }

    /**
     * 根据是否存在actual开始和结束时间，来更新subTaskStartInModel 和subTaskEndInModel的值
     */
    public void calculateSubTaskStartEndInModel() {
        if (subTaskActualStart != null){
            super.setSubTaskStartInModel(subTaskActualStart) ;
        } else {
            super.setSubTaskStartInModel(super.getSubTaskPlanStart());
        }
        if (subTaskActualEnd != null){
            super.setSubTaskEndInModel(subTaskActualEnd);
        } else {
            super.setSubTaskEndInModel(super.getSubTaskPlanEnd());
        }
    }
}
