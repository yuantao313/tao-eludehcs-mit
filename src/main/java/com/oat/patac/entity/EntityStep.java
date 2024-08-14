package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * 这个实体类代表实验的大阶段下面包含的小阶段
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class EntityStep {

    /**
     *功能块的ID
     */
    private Integer functionId;
    /**
     * bom编号
     */
    private String bomNo;
    /**
     *实验的小阶段的ID, 具有唯一性
     */
    private Integer stepId;
    /**
     * 小阶段的名称
     */
    private String stepName;
    /**
     * 小阶段的阶段顺序
     */
    private Integer stepOrder;
    /**
     * 小阶段的时间长度(小时)
     */
    private BigDecimal timeLength;
    /**
     * 工作模式
     */
    private String isConstTime;

    /**
     * 与上一阶段最大的时间间隔
     */
    private BigDecimal maxStepGap;
    /**
     * 小阶段所属的大阶段：值为试验准备/执行/收尾/试验数据及报告
     */
    private String testPhase;
    /**
     * 是否试验实施阶段
     */
    private String isReportOff;
    /**
     * 拆并箱标志: 4个选项： pass , repeat, extent, expand
     */
    private String packingFlag;
    /**
     * 有效标志
     */
    private String isDisabled;
    /**
     * 是否设备故障
     */
    private String isPreemptive;
    /**
     * 工程师数量
     */
    private Integer engineerCount;
    /**
     * 工程师工作时间
     */
    private BigDecimal engineerWorktime;
    /**
     * 工程师日常模式（是否按天）
     * 工程师是否按天工作0: 不按天，1:按天
     */
    private String engineerDailyMode;
    /**
     * 工程师工作天数
     */
    private Integer engineerDayNum;
    /**
     * 工程师每天工时
     */
    private BigDecimal engineerDailyWorktime;
    /**
     * 工程师工时类型
     */
    private String engineerWorktimeType;
    /**
     * 是否排程约束（工程师）
     */
    private String isEngineerConstraint;

    /**
     * taskId
     */
    private Integer taskId;

    /**
     * 该小阶段的资源组名和实体映射关系
     * key是resource group name
     */
    private HashMap<String, EntityResourceGroup> resourceGroupHashMap = new HashMap<>();
    /**
     * 被授权的技师，不 考虑auth procedure, auth_bom, auth_step - 2022-12-15重构
     */
    private ArrayList<EntityStaff> authStepTechnicians = new ArrayList<>();

    /**
     * 被授权的工程师，不 考虑auth procedure, auth_bom, auth_step 后来又更新了该list - 2022-12-15重构
     */
    private ArrayList<EntityStaff> authStepEngineers = new ArrayList<>();

    /**
     * 考虑小阶段工作模式之后的时长
     */
    private double timeLengthConsideredWorkMode;
    /**
     * 小阶段是否是repeat模式
     */
    private boolean isRepeat = false;
    private boolean isExpand = false;
    /**
     * 该实体数据验证
     * 返回值：1为正常，0为忽略该记录，-1为相关任务单无法排程
     */
    public int singleCheckStep() {
        int result = RESULT_VALID;
        // 阶段名称不得为空
        CheckDataUtil.notBlank(this.stepName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "BOM "+ bomNo + " 的小阶段（ID " + stepId + "）没有对应的阶段名称，不影响排程");

        // 阶段顺序为空
        if (stepOrder == null) {
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " （ID "+stepId + "）的顺序号为空，导致相关任务单无法排程");
            result = RESULT_CAN_NOT_SCHEDULE;
        }

        // 时间长度<0
        if (timeLength == null || timeLength.doubleValue() < 0) {
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " （顺序号 "+ stepOrder
                            + "，ID "+stepId + "）对应的时间长度为空或者小于0，默认为0，继续排程");
            timeLength = new BigDecimal(0.0);
        }

        // 试验阶段名称不得为空
        if (this.testPhase == null) {
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " （顺序号 "+ stepOrder
                            + "，ID "+stepId + "）没有对应的试验阶段名称，默认不是报告阶段，继续排程");
            testPhase = "";
        }

        if (packingFlag !=null && packingFlag.toLowerCase().contains(REPEAT)){
            isRepeat = true;
        }
        if (packingFlag !=null && packingFlag.toLowerCase().contains(EXPAND)){
            isExpand = true;
        }
        return result;
    }
    public Boolean singleCheckStepEngineer(){
        Boolean isValid = true;
        // 小阶段需求的工程师数量<=0
        if (engineerCount == null || engineerCount <= 0){
//            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
//                    "BOM " + bomNo + " 的小阶段 " + stepId + " 的工程师数量小于等于0，忽略该设置，继续排程");
            log.info("BOM " + bomNo + " 的小阶段 " + stepId + " 的工程师数量小于等于0，忽略该设置，继续排程");
            // 没有工程师需求，不用做后面的检查了
            return false;
        }

        // 人员工时类型worktime type不合法，即不为0，1，或2
        if (engineerWorktimeType != null && !engineerWorktimeType.equals("0") &&
                !engineerWorktimeType.equals("1") && !engineerWorktimeType.equals("2")){
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " （顺序号 "+ stepOrder
                            + "，ID "+stepId + "）的工程师工时类型为 " +engineerWorktimeType + "，无效数据，采用默认值0，继续排程");
            engineerWorktimeType = "0";
        }

        if (engineerDailyMode == null){
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "BOM " + bomNo + " 的小阶段 " + stepName + " （顺序号 "+ stepOrder
                            + "，ID "+stepId + "）的工程师是否按天工作为空，忽略该小阶段的工程师需求，继续排程");
            return false;
        }

        if (engineerDailyMode.equals(ConstantUtil.RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
            // 按天工作时，工作天数<=0
            if (engineerDayNum == null || engineerDayNum <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 "  + stepName + " （顺序号 "+ stepOrder
                                + "，ID "+stepId + "）的工程师按天工作，但工作天数为空或小于等于0，忽略该小阶段的工程师需求，继续排程");
                isValid = false;
            }
            // 按天工作时，每天工作时长<=0
            if (engineerDailyWorktime == null || engineerDailyWorktime.doubleValue() <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + stepName + " （顺序号 "+ stepOrder
                                + "，ID "+stepId + "）的工程师按天工作，但每天工作时长为空或小于等于0，忽略该小阶段的工程师需求，继续排程");
                isValid = false;
            }
        } else {
            // 不按天工作
            if (engineerWorktime == null || engineerWorktime.doubleValue() <= 0){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                        "BOM " + bomNo + " 的小阶段 " + stepName + " （顺序号 "+ stepOrder
                                + "，ID "+stepId + "）的工程师不按天工作，但工作时长为空或小于等于0，忽略该小阶段的工程师需求，继续排程");
                isValid = false;
            }
        }

        return isValid;

     /*   // STEP_ID存在时，与上一阶段的最大时间间隔不得为空
        CheckDataUtil.notBlank(this.maxStepGap, ConstantUtil.MESSAGE_SEVERITY_ERROR,ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "小阶段" + stepId + "没有对应的与上一阶段的最大时间间隔，无法进行排程");*/

    }
    public void calculateStepHours() {
        timeLengthConsideredWorkMode = timeLength.doubleValue();
        // timeLength没有考虑repeat，pass等的情况
        // 小阶段考虑工作模式后的时间

        if (isConstTime.equals(STEP_WEEKDAYS_WORK)){
            double i1 = timeLength.doubleValue() % (5 * 24);
            if (i1 == 0){
                timeLengthConsideredWorkMode = Math.floor(timeLength.doubleValue() / (5*24))  * (7*24) - 2 * 24;
            }else {
                timeLengthConsideredWorkMode = Math.floor(timeLength.doubleValue() / (5*24))  * (7*24) + i1;
            }
        }

    }

}
