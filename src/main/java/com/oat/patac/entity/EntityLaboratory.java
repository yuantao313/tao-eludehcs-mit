package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这个类的实体代表实验室
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityLaboratory {

    /**
     *实验室的ID, 具有唯一性
     */
    private Integer labId;
    /**
     *实验室的名称
     */
    private String labName;
    /**
     *实验室的类型
     */
    private String labType;
    /**
     * 业务区ID
     */
    private Integer testAreaId;

    /**
     * 单实体类检验
     */
    public void singleCheck() {

        // 试验室id存在时，对应的试验室名称不得为空
        CheckDataUtil.notBlank(this.labName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "试验室" + labId + "没有对应的试验室名称，但可以进行排程");
        // 试验室id存在时，对应的试验室类型不得为空
        CheckDataUtil.notBlank(this.labType, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "试验室" + labId + "没有对应的试验室类型，但可以进行排程");
        // 试验室id存在时，所属的业务区域id不得为空
        CheckDataUtil.notBlank(this.testAreaId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "试验室" + labId + "没有对应的业务区域，但可以进行排程");

    }
}
