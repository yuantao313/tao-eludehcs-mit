package com.oat.patac.entity;

import com.alibaba.druid.util.StringUtils;
import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.oat.common.utils.ConstantUtil.MESSAGE_TYPE_FIELD_MISS;

/**
 * 这个类的实体代表样品
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntitySample {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     *样品的ID, 具有唯一性
     */
    private Integer sampleId;
    /**
     * 样品数量
     */
    private Integer sampleQuantity ;
    /**
     * 样品组的ID
     */
    private Integer sampleGroupId;
    /**
     * 样品组的名称
     */
    private String sampleGroupName;
    /**
     *样品的类型，比如整车，或者零件
     */
    private String sampleType;
    /**
     * 拆并箱标志
     */
    private String packingFlag;
    /**
     * 试验任务单号
     */
    private String taskNo;
    /**
     * 样品编号
     */
    private String sampleNo;
    /**
     * 是否有效
     */
    private boolean isValid = true;


    /**
     * 该实体数据验证，
     * @return
     */
    public Boolean singleCheck() {
        // 当样品id存在时，样品数量为空
        if (this.sampleQuantity == null){
            CheckDataUtil.notBlank(this.sampleQuantity, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "样品" + sampleId + "对应的样品数量为空，采用默认设置1，继续排程");
            this.sampleQuantity = 1;
            //  样品数量<=0
        }else if (this.sampleQuantity <= 0){
            CheckDataUtil.notBlank(this.sampleQuantity, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "样品" + sampleId +" 对应的样品数量小于等于0，采用默认值1，继续排程");
            this.sampleQuantity = 1;
        }

        return  true;
    }
    /**
     * 判断相关的 task 是否排程
     */
    public Boolean singleCheckTask(){

        boolean isValid = true;

        // 当样品id存在时，样品组的名称为空
        CheckDataUtil.notBlank(this.sampleGroupName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "样品 " + sampleId + " 对应的样品组名称为空，不影响排程");

        // 当样品id存在时，样品的类型为空
        isValid = isValid && CheckDataUtil.notBlank(this.sampleType, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_MISSING_DATA,
                "样品 " + sampleId + " 对应的样品的类型为空，导致相关任务单无法排程");



        if (StringUtils.equals(sampleType, ConstantUtil.SAMPLE_TYPE_CAR)){
            // 样件类型是整车，但是sampleNo为空
            if (sampleNo == null || sampleNo == ""){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_ERROR,ConstantUtil.MESSAGE_TYPE_FIELD_ERROR_NOT_SCHEDULED,
                        "样件 " + sampleId + " 类型是整车，但是SAMPLE_NO为空，导致相关任务单无法排程");
                isValid = false;
            }
            // 整车的样品数量不为 1
            if (sampleQuantity != 1){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                        "样品 " + sampleId + " 对应为整车时候样品数量不为1，导致相关任务单无法排程");
                isValid = false;
            }
        }

        return isValid;
/*
        // 当样品id存在时，样品组id为空
        CheckDataUtil.notBlank(this.sampleGroupId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "样品" + sampleId + "对应的样品组id为空，但是可以进行排程");

        // 当样品id存在时，样品的编号为空
        CheckDataUtil.notBlank(this.sampleNo, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "样品" + sampleId + "对应的样品的编号为空，但是可以进行排程");*/



    }

}
