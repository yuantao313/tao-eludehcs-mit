package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * 这个实体类代表的是规范
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityProcedure {

    /**
     *功能块的ID
     */
    private Integer functionId;

    /**
     * 规范号
     */
    private String procedureNo;

    /**
     * bom
     */
    //private Integer bomId;
    private EntityBom bom;

    /**
     * authorized engineers
     */
    private ArrayList<EntityStaff> authEngineers = new ArrayList<>();
    /**
     * authorized technicians
     */
    private ArrayList<EntityStaff> authTechnicians = new ArrayList<>();

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
        // 是否有效
        Boolean isValid = true;

        // 当规范id存在时，规范号不得为空
        CheckDataUtil.notBlank(this.procedureNo, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "规范" + procedureNo + "没有对应的规范号，但可以进行排程");

        // 排程参数ID不得为空
//        isValid = CheckDataUtil.notBlank(this.planConfigId, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_NOT_SCHEDULED,
//                "规范" + procedureNo + "没有对应的排程参数id，无法进行排程");


        return isValid;
    }

    /**
     * 设置bom值的时候需要检查是否只有一个有效BOM
     */
    public Boolean setBom(EntityBom bom){
        //todo - haolei
        this.bom = bom;
        return true;
    }
}
