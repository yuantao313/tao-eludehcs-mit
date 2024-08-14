package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-03 16:42
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntitySkill {

    /**
     * 技能Id
     */
    private String skillId;
    /**
     * 技能名称
     */
    private String skillName;
//    /**
//     * 技能是否可用 （0：可用； 1：不可用）
//     */
//    private String isAvailable;
//    /**
//     * 技能可用时间
//     */
//    private Date availableTime;


    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){

        // 当技能id存在时，技能名称不得为空
        CheckDataUtil.notBlank(this.skillName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "技能" + skillId + "对应的技能名称为空，但可以进行排程");

        // IS_AVAILABLE不为0或者null
//        if (StringUtils.equals(isAvailable, "0") || StringUtils.equals(isAvailable, null)){
//            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_CAN_SCHEDULED,
//                    "技能" + skillId + "“IS_AVAILABLE”，的状态不符合，但可以进行排程" );
//        }

        return true;
    }


}
