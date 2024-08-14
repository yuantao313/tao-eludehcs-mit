package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:yhl
 * @create: 2022-08-10 16:58
 * @Description: 小阶段授权
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityAuthStep{

    /**
     * BOM的ID
     */
    private String bomNo;
    /**
     * 小阶段ID
     */
    private Integer stepId;
    /**
     * 员工的ID
     */
    private String staffId;

}
