package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * 这个类的实体代表了排程中的一个"Bom"，BOM中定义了该BOM所需要做的具体试验阶段
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityBom {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * 前端的bom号
     */
    private String bomNo;
    /**
     * 实验规范号
     */
    private String procedureNo;
    /**
     * 排程参数Id
     */
    private Integer planConfigId;
    /**
     * BOM类型
     */
    private String bomType;
    /**
     * BOM中是否有“报告撰写”大阶段
     */
    private boolean hasReportingStep = false;

    /**
     * 排程参数模板
     */
    private EntityPlanConfig planConfig;
    /**
     * 包含所有小阶段
     */
    private ArrayList<EntityStep> steps = new ArrayList<>();
    /**
     * 该 bom 下所有小阶段的总时长
     */
    private double bomLength;
    /**
     * 该 bom 下是否需要技师
     */
    private boolean needTechnician = false;
    /**
     * authorized engineers NOT considering auth_procedure - 2022-12-15重构
     */
    private ArrayList<EntityStaff> authBomEngineers = new ArrayList<>();
    /**
     * authorized technicians NOT considering auth_procedure - 2022-12-15重构
     */
    private ArrayList<EntityStaff> authBomTechnicians = new ArrayList<>();
    /**
     * authorized engineers NOT considering auth_procedure - 2022-12-15重构
     */
    private ArrayList<EntityStaff> authBomAndProcedureEngineers = new ArrayList<>();
    /**
     * authorized technicians after considering auth_procedure - 2022-12-15重构
     */
    private ArrayList<EntityStaff> authBomAndProcedureTechnicians = new ArrayList<>();
    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
        // 是否有效
        Boolean isValid = true;

        // BOM类型为空
        if (this.bomType == null){
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "BOM " + bomNo + " 对应的BOM类型为空，设置为非标准，继续排程");
            this.bomType = ConstantUtil.BOM_TYPE_NOT_STANDARD;
        }


        // BOM的ID存在时，前端的bom号不得为空
        CheckDataUtil.notBlank(this.bomNo, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "BOM " + bomNo + " 没有对应的前端BOM号，但可以进行排程");

        // todo：确认逻辑
        // BOM的ID存在时，实验规范号不得为空
        CheckDataUtil.notBlank(this.procedureNo, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "BOM " + bomNo + " 没有对应的试验规范号，无法进行排程");



        // BOM的ID存在时，功能块的ID不得为空
        CheckDataUtil.notBlank(this.functionId, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "BOM " + bomNo + " 没有对应的功能块ID，无法进行排程");

        return isValid;
    }
}
