package com.oat.patac.entity;

import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * 这个类的实体代表员工信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityStaff {

    /**
     *员工的ID, 具有唯一性
     */
    private String staffId;
    /**
     * 员工的姓名
     */
    private  String staffName;
    /**
     * 员工的类型
     * STAFF_CLASS_ID=0 engineer，1 technician
     */
    private String staffClassId;

    /**
     * 最低工作负荷
     */
    private BigDecimal workLoad ;
    /**
     * 最高工作负荷
     */
    private BigDecimal maxWorkLoad ;
    /**
     * 实验室的ID
     */
    private Integer labId;
    /**
     * 作为资源的resource type
     */
    private String resourceType;
    /**
     * 人员的默认资源容量
     */
    private double capacity = ConstantUtil.DEFAULT_RESOURCE_CAPACITY;

    /**
     * 对于技师来说，具有的skill set
     */
//    private String skillSet;

    /**
     * 人员的日历信息
     */
    private ArrayList<EntityStaffCalendar> staffCalendar = new ArrayList<>();

    /**
     * 日历对应的每一天是否工作的切换时间
     * 比如9.30日8：30上班，17点下班，11：30-12：00休息，则时间为[00:00, 8:30, 11:30,12:00, 17:00, 00:00]
     * 注意：需要合并常规日历和翻班日历, 对翻班日历需要考虑从生效日期开始的循环，每个是时间区间的工作类型（是否工作），和工作模式（节假日是否工作）， 还需要注意对于翻班跨天的情况，需要把两截连起来
     * key是每一天的日期（时间用0点）；value是每天中切换是否工作的时间点
     */
    private HashMap<Date, ArrayList<Date>> switchTimePerDay = new HashMap<>();
    /**
     * 日历对应的每一天是否工作的切换值
     * 比如对应上例为[0,100,0,100,0]
     * key是每一天的日期（时间用0点）；value是每天中切换是否工作的时间点之间的值，不工作为0，工作为100.
     */
    private HashMap<Date, ArrayList<Double>> switchValuePerDay = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityStaff staff = (EntityStaff) o;
        return Objects.equals(staffId, staff.staffId) && Objects.equals(staffName, staff.staffName) && Objects.equals(staffClassId, staff.staffClassId) && Objects.equals(labId, staff.labId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId, staffName, staffClassId, labId);
    }

    /**
     * 该实体数据验证
     */
    public Boolean singleCheck(){

        // 是否过滤
        Boolean isValid;

        // 员工名称不得为空
        isValid = CheckDataUtil.notBlank(this.staffName, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_INVALID_DATA,
                "人员" + staffName + "(" + staffId + ")" + "的姓名为空，无效数据，排程中不考虑该人员");

        // 人员类型不得为空
        isValid = isValid &&  CheckDataUtil.notBlank(this.staffClassId, MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_FIELD_MISS,
                "人员" + staffName + "(" + staffId + ")" + "对应的人员类型为空，排程中不考虑该人员");



/*        // 当STAFF_ID存在时，员工类型不得为空
        CheckDataUtil.notBlank(this.staffClassId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "员工" + staffId + "对应的员工类型为空，但可以进行排程");


        // 当STAFF_ID存在时，试验室的ID不得为空
        CheckDataUtil.notBlank(this.labId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                "员工" + staffId + "对应的试验室ID为空，但可以进行排程");*/


        return isValid;
    }
    /**
     * 给resource type 属性赋值
     */
    public void assignResourceType(){
        if(staffClassId != null){
            resourceType = staffClassId.equals(STAFF_CLASS_ENGINEER)? RESOURCE_TYPE_ENGINEER: RESOURCE_TYPE_TECHNICIAN;
        }

    }
    /**
     * 返回resource type相应的message中的汉字
     */
    public String resourceTypeMessage(){
        if (staffClassId == null){
            return "人员";
        }
        return staffClassId.equals(STAFF_CLASS_ENGINEER)? RESOURCE_TYPE_ENGINEER_IN_MESSAGE:RESOURCE_TYPE_TECHNICIAN_IN_MESSAGE;
    }
}
