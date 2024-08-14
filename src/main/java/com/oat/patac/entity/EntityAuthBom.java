package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:yhl
 * @create: 2022-08-10 16:47
 * @Description: bom授权
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityAuthBom{

    /**
     *BOM的ID
     */
    private String bomNo;
    /**
     * 员工的ID
     */
    private String staffId;


}
