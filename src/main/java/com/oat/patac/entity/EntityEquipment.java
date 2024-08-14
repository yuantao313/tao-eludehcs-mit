package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.oat.common.utils.ConstantUtil.EQUIPMENT_IS_AVAILABLE;

/**
 * 这个类的实体代表设备信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityEquipment{

    /**
     *设备编号, 具有唯一性
     */
    private String equipmentId;
    /**
     * 设备的名称
     */
    private String equipmentName;
//    /**
//     * 设备类型
//     */
//    private String equipmentType;
    /**
     * 该设备是否可用
     */
    private String isAvailable;
    /**
     * 设备的数量
     */
    private Integer equipmentQty;
    /**
     * 设备的容纳数量
     */
    private Integer equipmentCap;
    /**
     * 模型中使用的设备的容量
     */
    private Integer capacityInModel;
    /**
     * 是否带状态
     */
    private String isStateful;
    /**
     * 设备的日历信息
     */
    private ArrayList<EntityEquipmentCalendar> equipmentCalendar = new ArrayList<>();

    // 以下结构后面没有用到，所以暂时被注释
    // todo: 后面确认没用可以删掉

    /**
     * 翻班日历对应的每一天是否工作的切换时间，针对equipment mode为0，也就是节假日不上班的情况
     * 比如9.30日8：30上班，17点下班，11：30-12：00休息，则时间为[00:00, 8:30, 11:30,12:00, 17:00, 00:00]
     * 注意：对翻班日历需要考虑从生效日期开始的循环，每个是时间区间的工作类型（是否工作），和节假日， 还需要注意对于翻班跨天的情况，需要把两截连起来
     * key是每一天的日期（时间用0点）；value是每天中切换是否工作的时间点
     */
    //private HashMap<Date, ArrayList<Date>> switchTimePerDaySpecialDayNotWork = new HashMap<>();
    /**
     * 翻班日历对应的每一天是否工作的切换时间，针对equipment mode为1，也就是节假日上班的情况
     * 比如9.30日8：30上班，17点下班，11：30-12：00休息，则时间为[00:00, 8:30, 11:30,12:00, 17:00, 00:00]
     * 注意：对翻班日历需要考虑从生效日期开始的循环，每个是时间区间的工作类型（是否工作），和节假日， 还需要注意对于翻班跨天的情况，需要把两截连起来
     */
    //private HashMap<Date, ArrayList<Date>> switchTimePerDaySpecialDayWork = new HashMap<>();
    /**
     * 普通日历对应的每一天是否工作的切换值，针对equipment mode为0，也就是节假日不上班的情况
     * 比如对应上例为[0,100,0,100,0]
     * key是每一天的日期（时间用0点）；value是每天中切换是否工作的时间点之间的值，不工作为0，工作为100.
     */
    //private HashMap<Date, ArrayList<Double>> switchValuePerDaySpecialDayNotWork = new HashMap<>();
    /**
     * 普通日历对应的每一天是否工作的切换值，针对equipment mode为1，也就是节假日上班的情况
     */
    //private HashMap<Date, ArrayList<Double>> switchValuePerDaySpecialDayWork = new HashMap<>();

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){
        if (!isAvailable.equals(EQUIPMENT_IS_AVAILABLE)){
            return false;
        }

        boolean valid = true;
        // 当设备id存在时，设备的名称不能为空
        CheckDataUtil.notBlank(this.equipmentName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "设备 " + equipmentId + " 对应的设备名称为空，不影响排程，继续排程");

//        // 当设备id存在时，设备的类型不能为空
//        CheckDataUtil.notBlank(this.equipmentType, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_CAN_SCHEDULED,
//                "设备" + equipmentId + "对应的设备类型为空，但可以进行排程");

        if (equipmentCap == null || equipmentCap <= 0) {
            CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                    "设备 " + equipmentName + " （ID " + equipmentId + "）的设备容量为空或小于等于0，忽略该设备，继续排程");
            valid = false;
        }

       if (equipmentQty == null || equipmentQty <= 0) {
           CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                   "设备 " + equipmentName + " （ID "+ equipmentId+ "）的设备数量为空或小于等于0，忽略该设备，继续排程");
       }

        return valid;
    }


}
