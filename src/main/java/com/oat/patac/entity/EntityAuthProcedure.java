package com.oat.patac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:yhl
 * @create: 2022-08-10 16:53
 * @Description: 规范授权
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityAuthProcedure{

    /**
     * 规范ID
     */
    private String  procedureNo;
    /**
     * 员工的ID
     */
    private String staffId;

}
