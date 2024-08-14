package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 这个类的实体代表业务区域
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityTestArea{

    /**
     * 业务区域的ID，具有唯一性
     */
    private Integer testAreaId;

    /**
     * 业务区域的名称
     */
    private String testAreaName;

    /**
     * 单实体类检验
     */
    public void singleCheck() {

        // 业务区域id存在时，对应业务区域名称不得为空
        CheckDataUtil.notBlank(testAreaName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "业务区域" + testAreaId + "没有对应的业务区域名称，但可以进行排程");

    }
}
