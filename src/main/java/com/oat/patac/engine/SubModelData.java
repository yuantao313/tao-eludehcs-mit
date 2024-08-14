package com.oat.patac.engine;

import com.alibaba.druid.util.StringUtils;
import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.DateUtil;
import com.oat.patac.entity.*;
import ilog.concert.IloIntervalVar;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;

@Log4j2
@Data
public class SubModelData {
    /**
     * 子模型的名字，目前是用包含5位数字的字符串表示的。
     * 数字的含义是该子模型的时间颗粒度，单位为分钟。
     * 比如00060表示该子模型的时间颗粒度是60分钟
     */
    private String subModelName;
    private PatacSchedulingTask patacSchedulingTask;
    private EngineProcessedData engineProcessedData;

    /**
     * 排程时间颗粒度，单位是分钟
     */
    private int planGranularity;
    /**
     * 最长排程天数，是各个待排程批次的排程天数的最大值
     */
    private int maxCalendarDays;
    /**
     * 排程求解时间限制，单位是秒。是各个待排程批次的排程时间限制的最大值
     */
    private int maxTimeLimit;
    /**
     * 该子模型的最早的排程开始时间。是各个待排程批次的最早排程时间的最小值
     */
    private Date minPlanStartTime;
    /**
     * 该子模型的最晚的排程结束时间。是各个待排程批次的最早排程时间+排程天数得到的最晚时间
     */
    private Date maxPlanEndTime;
    /**
     * 该子模型的整数结束时间。根据最长排程天数和排程时间颗粒度计算得到
     */
    private int endIntTime;

    /**
     * 子模型中要排程的task和subtask
     * HashMap的key是taskId
     * HashMap的value是该taskId对应的task下面包含的该子模型需要排程的批次
     */
    private HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMap = new HashMap<>();
    /**
     * 子模型中要排程的task和其下完整的subtask
     * HashMap的key是taskId
     * HashMap的value是该taskId对应的task下面包含的该子模型所需要关心的所有的批次
     */
    private HashMap<String, ArrayList<EntitySubTask>> subTaskMap = new HashMap<>();
    /**
     * 子模型中要排程的sub task groups
     * HashMap的key是 批次组名称
     * HashMap的value是该批次组下包含的该子模型需要排程的批次
     */
    // todo, remove应该用不到这个 ，批次组
    //private HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskGroupMap = new HashMap<>();
    /**
     * 每个不能冲突的样品需要支持的批次，注意map的key不是sampleId，而是sampleNo
     * //todo: 为他们赋值
     */
    private HashMap<String, ArrayList<EntitySubTask>> uniqueSampleSupportedSubtasks = new HashMap<>();
    private HashMap<String, ArrayList<EntitySubTaskPlan>> uniqueSampleSupportedSubtaskPlans = new HashMap<>();
    /**
     * 子模型中 小阶段活动的step function需要的时间数组，针对仅工作日工作的模式0，和工作日和周末工作的模式1
     * 需要根据子模型的时间颗粒度，从main_model.[workdays_mode_time]计算得到。
     * 可能需要根据子模型的maxCalendarDays只截取main_model.[workdays_mode_time]的一部分使用.
     * 模型约束中可以使用main_model.[workdays_mode_value]来作为value的设置
     */
    //todo: 赋值时如果方便直接赋值为array，可以修改为array
    private HashMap<String, ArrayList<Double>> stepFunctionStepTime = new HashMap<>();
    private HashMap<String, ArrayList<Double>> stepFunctionStepValue = new HashMap<>();
    /**
     * 子模型中 人员和设备活动的step function需要的时间数组
     */
    private HashMap<String,ArrayList<Double>> stepFunctionStaffTime = new HashMap<>();
    private HashMap<String,ArrayList<Double>> stepFunctionStaffValue = new HashMap<>();
    private HashMap<String,ArrayList<Double>> stepFunctionEquipmentTime = new HashMap<>();
    private HashMap<String,ArrayList<Double>> stepFunctionEquipmentValue = new HashMap<>();
    /**
     * 子模型中 排程时间范围内的每天开始/结束时间段为100的step function的时间/值数组
     */
    private ArrayList<Double> stepFunctionDayStartTime = new ArrayList<>();
    private ArrayList<Double> stepFunctionDayStartValue = new ArrayList<>();
    private ArrayList<Double> stepFunctionDayEndTime = new ArrayList<>();
    private ArrayList<Double> stepFunctionDayEndValue = new ArrayList<>();
    /**
     * 子模型中所有可能用的人员列表
     */
//    private ArrayList<EntityStaff> possibleStaffs = new ArrayList<>();
    private HashSet<EntityStaff> possibleStaffs = new HashSet<>();
    /**
     * 子模型中所有可能用的设备列表
     */
//    private ArrayList<EntityEquipment> possibleEquipments = new ArrayList<>();
    private HashSet<EntityEquipment> possibleEquipments = new HashSet<>();
    /**
     * 子模型中所有可能用的并带状态的设备列表，和设备可能的状态，key是equipmentId，hashset内是状态, 这里只是小阶段需求的状态，不包括已排程资源的状态
     */
//    private ArrayList<EntityEquipment> equipmentsWithState = new ArrayList<>();
    private HashMap<String, HashSet<String>> equipmentsWithState = new HashMap<>();
    /**
     * 每种人员资源（工程师、技师）在每个子模型中平均需要多少工作时间
     * key是资源类型 engineer or technician
     */
    private HashMap<String, Double> averageExpectedUsagePerResourceType = new HashMap<>();

    private HashMap<String, Double> totalWorkTime = new HashMap<>();
    private HashMap<String, Double> totalRequestedWorkTime = new HashMap<>();
//    private HashMap<String, Double> totalRequestedConstraintWorkTime = new HashMap<>();
//    private HashMap<String, Double> totalRequestedNonConstraintWorkTime = new HashMap<>();
    HashMap<String, HashSet<EntityStaff>> totalPossibleResourceQuantity = new HashMap<>();
    /**
     * 每个人员在每个子模型中有多少已经固定的工作时间
     * key是staff id
     */
    private HashMap<String, Double> totalFixedTimePerStaff = new HashMap<>();
//    private HashMap<String, Double> totalFixedNonConstraintTimePerStaff = new HashMap<>();
//    private HashMap<String, Double> totalFixedConstraintTimePerStaff = new HashMap<>();

    // todo: 确定代码位置




    /**
     * 构造函数
     * @param subModelName
     * @param patacSchedulingTask
     */

    public SubModelData(String subModelName, PatacSchedulingTask patacSchedulingTask) {
        this.subModelName = subModelName;
        this.patacSchedulingTask = patacSchedulingTask;
        engineProcessedData = patacSchedulingTask.getEngineProcessedData();
    }

    public boolean prepareData() {
        calculateEndTime();

        prepareSubTaskList();
        initializeStepFunctionWorkdayMode();
        prepareResourceStepFunction();

        calculateAverageExpectedUsagePerResourceType();
        prepareStepFunctionDayTime();

        log.info("子模型 " + subModelName + " 的待排程任务单为： " + toBePlannedSubTaskMap.keySet());
        if (toBePlannedSubTaskMap.size() == 0) {
            log.info("子模型 " + subModelName + " 没有待排程的任务单");
            return false;
        }
        return true;
    }

    /**
     * 子模型中 排程时间范围内的每天开始/结束时间段为100的step function的时间/值数组
     */
    private void prepareStepFunctionDayTime(){
        Calendar startTime = DateUtil.dateToCalendar(minPlanStartTime);
        Calendar endTime = DateUtil.dateToCalendar(maxPlanEndTime);

        Double total = DateUtil.getDistanceIntTime(endTime, startTime, planGranularity);
        log.info("当前子模型的总排程时间范围的时长（total）为:"  + total);

        // 第一天单独考虑
        Double i = 0.0;

        stepFunctionDayStartTime.add(i);
        stepFunctionDayStartValue.add(WORK_VALUE);
        stepFunctionDayEndTime.add(i);
        stepFunctionDayEndValue.add(NOT_WORK_VALUE);
        i += 1;
        // 获取下一天开始
        Calendar cal = DateUtil.dateToCalendar(DateUtil.getNextDay(startTime.getTime()));
        Double i2 = Math.floor((double) (cal.getTime().getTime() - startTime.getTime().getTime())/(1000*60)/planGranularity);

        if (i < i2){
            stepFunctionDayStartTime.add(i);
            stepFunctionDayStartValue.add(NOT_WORK_VALUE);
        }
        if (i2 - 1 > i){
            stepFunctionDayEndTime.add(i2 - 1);
            stepFunctionDayEndValue.add(WORK_VALUE);
            stepFunctionDayEndTime.add(i2);
            stepFunctionDayEndValue.add(NOT_WORK_VALUE);
        }

        i = i2;

        int oneDay = 24 * 60 / planGranularity;

        for (; i < total;) {
            if (i + oneDay >= total){
                break;
            }
            stepFunctionDayStartTime.add(i);
            stepFunctionDayStartValue.add(WORK_VALUE);
            i += 1;
            stepFunctionDayStartTime.add(i);
            stepFunctionDayStartValue.add(NOT_WORK_VALUE);
            i += oneDay - 2;
            stepFunctionDayEndTime.add(i);
            stepFunctionDayEndValue.add(WORK_VALUE);
            i += 1;
            stepFunctionDayEndTime.add(i);
            stepFunctionDayEndValue.add(NOT_WORK_VALUE);
        }

        // 单独考虑最后一天不完整的情况
        stepFunctionDayStartTime.add(i);
        stepFunctionDayStartValue.add(WORK_VALUE);
        if (i + 1 < total) {
            stepFunctionDayStartTime.add(i + 1);
            stepFunctionDayStartValue.add(NOT_WORK_VALUE);
        }
        if (total - 1 > i){
            stepFunctionDayEndTime.add(total - 1);
            stepFunctionDayEndValue.add(WORK_VALUE);
        }

        stepFunctionDayStartTime.add(total);
        stepFunctionDayEndTime.add(total);
        log.info("stepFunctionDayStartTime的最后一个值：" + stepFunctionDayStartTime.get(stepFunctionDayStartTime.size() - 1));
        log.info("stepFunctionDayStartValue的最后一个值：" + stepFunctionDayStartValue.get(stepFunctionDayStartValue.size() - 1));
        log.info("stepFunctionDayEndTime的最后一个值：" + stepFunctionDayEndTime.get(stepFunctionDayEndTime.size() - 1));
        log.info("stepFunctionDayEndValue的最后一个值：" + stepFunctionDayEndValue.get(stepFunctionDayEndValue.size() - 1));


//        while (tempTime.after(endTime)){
//            stepFunctionDayStartTime.add(DateUtil.getDistanceIntTime(tempTime, startTime, planGranularity));
//            tempTime.add(Calendar.MINUTE, planGranularity);
//
//            stepFunctionDayStartTime.add(DateUtil.getDistanceIntTime(tempTime, startTime, planGranularity));
//
//            stepFunctionDayStartValue.add(NOT_WORK_VALUE);
//        }
    }

    /**
     * 为子模型会使用到的资源设置step function
     */
    private void prepareResourceStepFunction() {
        //todo added by Sophia: 下面写的不对，只是为了临时让模型可以跑通

        // 工程师资源
        // todo: 确定人员的范围
        for (EntityStaff possibleStaff : possibleStaffs) {
            String staffId = possibleStaff.getStaffId();

//            if (staffId.equals("152415")){
//                System.out.println();
//            }
            ArrayList<EntityStaffCalendar> staffCalendars = engineProcessedData.getStaffCalendarHashMap().get(staffId);

            // 用来存储每天时间节点的集合
            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> values = new ArrayList<>();
            // 遍历使用到的 staffCalendar 生成对应的 stepFunction
            int size = staffCalendars.size();

            // 员工日历只有 1 个
            if (size == 1){
                calculateResourceStepFunction(staffId, RESOURCE_TYPE_ENGINEER, staffCalendars.get(0), null,
                        minPlanStartTime, maxPlanEndTime, times, values);
            // 员工日历有多个
            }else {
                // 解决员工多个 calendar 的情况
                // 将 staffCalendar 按照开始时间的先后进行排序
                Collections.sort(staffCalendars, new Comparator<EntityStaffCalendar>() {
                    @Override
                    public int compare(EntityStaffCalendar o1, EntityStaffCalendar o2) {
                        Date startFrom1 = o1.getStartFrom();
                        Date startFrom2 = o2.getStartFrom();
                        return startFrom1.compareTo(startFrom2);
                    }
                });

                // 将该 subModel 使用到的 staffCalendar 收集起来
                ArrayList<EntityStaffCalendar> staffCalendars1 = new ArrayList<>();

                // 记录 minPlanStartTime 之前最近的时间
                EntityStaffCalendar  latestStaffCalendar = null;
                boolean flag = false;
                for (EntityStaffCalendar staffCalendar : staffCalendars) {
                    Date startFromSf = staffCalendar.getStartFrom();
                    if (startFromSf.before(minPlanStartTime)){
                        latestStaffCalendar = staffCalendar;
                        flag = true;
                    }else if (startFromSf.after(minPlanStartTime) && startFromSf.before(maxPlanEndTime)){
                        // 将最近的日历开始时间和在 minPlanStartTime 和 maxPlanEndTime 之间的日历加入到 staffCalendars1 中
                        if (flag){
                            staffCalendars1.add(latestStaffCalendar);
                            flag = false;
                        }
                        staffCalendars1.add(staffCalendar);
                    }
                }

                // 添加离 minPlanStartTime 最近的 calendar
                if (staffCalendars1.size() == 0){
                    staffCalendars1.add(latestStaffCalendar);
                }
                staffCalendars = staffCalendars1;

                // staff 在 submodel 中只能用到一个 calendar
                if (staffCalendars.size() == 1){
                    calculateResourceStepFunction(staffId, RESOURCE_TYPE_ENGINEER, staffCalendars.get(0), null,
                            minPlanStartTime, maxPlanEndTime, times, values);
                }else {
                    // submodel 时间段内使用了多个 calendar
                    for (int i = 0; i < staffCalendars.size(); i++) {
                        EntityStaffCalendar staffCalendar = staffCalendars.get(i);
                        // 计算员工使用该日历的时间段
                        Date calendarStartTime;
                        Date calendarEndTime ;
                        // 第一个日历
                        if (i == 0){
                            calendarStartTime = minPlanStartTime;
                            calendarEndTime = staffCalendars.get(i + 1).getStartFrom();
                        }else {
                            calendarStartTime = staffCalendars.get(i).getStartFrom();
                            // 最后一个日历
                            if (i == size - 1){
                                calendarEndTime = maxPlanEndTime;
                            }else {
                                calendarEndTime = staffCalendars.get(i + 1).getStartFrom();
                            }
                        }

                        calculateResourceStepFunction(staffId, RESOURCE_TYPE_ENGINEER, staffCalendar, null,
                                calendarStartTime, calendarEndTime, times, values);
                    }
                }
            }
        }

        // 设备资源
        for (EntityEquipment possibleEquipment : possibleEquipments) {
            String equipmentId = possibleEquipment.getEquipmentId();

//            if (equipmentId.equals("PEC0-03611")) {
//                log.info(equipmentId);
//            }
            // 用来存储每天时间节点的集合
            ArrayList<Double> times = new ArrayList<>();
            ArrayList<Double> values = new ArrayList<>();
            EntityEquipmentCalendar equipmentCalendar = engineProcessedData.getEquipmentCalendarHashMap().get(equipmentId);
            calculateResourceStepFunction(equipmentId, RESOURCE_TYPE_EQUIPMENT, null, equipmentCalendar,
                    minPlanStartTime, maxPlanEndTime, times, values);
        }


    }

    /**
     * 计算资源设置 Resource step function
     * @param resourceId
     */
    private void calculateResourceStepFunction(String resourceId,
                                               String resourceType,
                                               EntityStaffCalendar staffCalendar,
                                               EntityEquipmentCalendar equipmentCalendar,
                                               Date calendarStartTime,
                                               Date calendarEndTime,
                                               ArrayList<Double> times,
                                               ArrayList<Double> values){

//        if (resourceId.equals("10001")){
//            System.out.println();
//        }

        String resourceMode="";
        Integer calendarId=0;
        String calendarClass = "";
        Date startFrom=new Date();


        if (resourceType.equals(RESOURCE_TYPE_ENGINEER)){
            if (staffCalendar != null) {
                resourceMode = staffCalendar.getStaffMode();
                calendarId = staffCalendar.getCalendarId();
                calendarClass = staffCalendar.getCalendarClass();
                startFrom = staffCalendar.getStartFrom();
            }
        }else {
            if (equipmentCalendar != null) {
                resourceMode = equipmentCalendar.getEquipmentMode();
                calendarId = equipmentCalendar.getCalendarId();
                calendarClass = equipmentCalendar.getCalendarClass();
                startFrom = equipmentCalendar.getStartFrom();
            }
        }

        // 开始和结束时间
        Calendar startTime = DateUtil.dateToCalendar(minPlanStartTime);
        Calendar endTime = DateUtil.dateToCalendar(calendarEndTime);

        HashMap<Date, EntitySpecialDay> specialDayHashMap = engineProcessedData.getSpecialDayHashMap();

        // 确认一开始的工作状态
        Double flag = 0.0;
        // 遍历用的动态时间
        Calendar tempTime = DateUtil.dateToCalendar(calendarStartTime);
        // 记录第一天需要计算节点的数组
        HashMap<String, String[]> firstTimePointOfDayMap = new HashMap<>();
        // 第一天遍历时的临时数组
        Calendar firstDayTempTime = DateUtil.dateToCalendar(calendarStartTime);


        // 每天切换的工作时间点
        // 记录第一天在 tempTime 后面的时间点，key 为时间的名称，value 为该时间节点分小时和分钟的数组
        HashMap<String, String[]> timePointOfDayMap = new HashMap<>();
        char[] chars = null;


        // 翻班日历整体的 switchMap
        HashMap<Integer, HashMap<String, String[]>> allRollTimePointOfDayMap = new HashMap<>();
        int day = 0;
        int rollDaySize = 1;
        ArrayList<EntityRollCalendar> rollCalendars = null;

        // 常规日历
        if (calendarClass.equals(CALENDAR_CLASS_NORMAL)) {
            EntityCalendar calendar = engineProcessedData.getCalendarHashMap().get(calendarId);

            // staff 每周的工作日
            chars = calendar.getWeekWorkDay().toCharArray();

            // 计算 flag 初始值，以及对应的 Map 数据
            flag = calculateFlagAndSwitchMap(calendar, timePointOfDayMap,
                    firstTimePointOfDayMap, firstDayTempTime, tempTime, flag);

        // 翻班日历
        }else if (calendarClass.equals(CALENDAR_CLASS_ROLL)){

            ArrayList<EntityRollCalendar> allRollCalendars = engineProcessedData.getRollCalendarHashMap().get(calendarId);
            // 创建 rollCalendar 集合考虑日历的重复次数
            rollCalendars = calculateAllRollCalendars(allRollCalendars);

            rollDaySize = rollCalendars.size();

            // 计算余多少天
            // todo:确认逻辑
            if (startFrom.before(tempTime.getTime())){
                Calendar startFromCal = DateUtil.dateToCalendar(startFrom);
                startFromCal.set(Calendar.HOUR_OF_DAY, 0);
                startFromCal.set(Calendar.MINUTE, 0);
                startFromCal.set(Calendar.SECOND, 0);
                startFromCal.set(Calendar.MILLISECOND, 0);
                long l = tempTime.getTime().getTime() - startFromCal.getTime().getTime();

                // 节假日顺延模式时找出在 startFrom 和 tempTime 时间之内的 special day
                int specialDayCount = 0;
                if (MODE_SPECIAL_DAY_NOT_ROLL.equals(resourceMode)){
                    for (Map.Entry<Date, EntitySpecialDay> entry : specialDayHashMap.entrySet()) {
                        Date specialDay = entry.getKey();
                        EntitySpecialDay entitySpecialDay = entry.getValue();
                        // special day的模式为 1 时，不考虑
                        if (specialDay.after(startFrom) && specialDay.before(tempTime.getTime()) &&
                                !SPECIAL_DAY_WEEKEND_WORK.equals(entitySpecialDay.getWorkDayType())){
                            specialDayCount++;
                        }
                    }
                }

                // 减去中间的 special day 后算出的对应时间
                day = ((int)Math.floor((double)l / (1000 * 60 * 60 * 24)) - specialDayCount) % rollDaySize;

            }

            // 生成每天的 switchMap
            for (int i = 0; i < rollDaySize; i++) {
                EntityRollCalendar rollCalendar = rollCalendars.get(i);
                // 每天切换的工作时间点
                // 记录第一天在 tempTime 后面的时间点，key 为时间的名称，value 为该时间节点分小时和分钟的数组
                HashMap<String, String[]> rollTimePointOfDayMap = new HashMap<>();

                // 将开始时间对应翻班日历中的这一天记录到 first 的相关结构中
                if (day == i){
                    // 计算出循翻班中对应日期，已经第一天的需要计算的节点
                    flag = calculateFlagAndSwitchMap(rollCalendar, rollTimePointOfDayMap,
                            firstTimePointOfDayMap, firstDayTempTime, tempTime, flag);
                }else {
                    calculateFlagAndSwitchMap(rollCalendar, rollTimePointOfDayMap,
                            null, null, null, null);
                }

                // 将每天的 switchMap 添加到翻班日历整体中
                allRollTimePointOfDayMap.put(i, rollTimePointOfDayMap);
            }
        }
        if (calendarClass.equals(CALENDAR_CLASS_NORMAL) || calendarClass.equals(CALENDAR_CLASS_ROLL)) {

            // 添加开始时间
            if (times.size() == 0){
                times.add(0.0);
            }
            // 按照该天上班，先添加开始工作状态
            values.add(flag);

            // todo：确认是否存在跨天问题
            // 第一天单独设置
            // 创建临时日期，用于特殊日期的判断
            Calendar tempTime1 = DateUtil.dateToCalendar(startTime.getTime());
            tempTime1.set(Calendar.HOUR_OF_DAY, 0);
            tempTime1.set(Calendar.MINUTE, 0);
            tempTime1.set(Calendar.SECOND, 0);
            tempTime1.set(Calendar.MILLISECOND, 0);

            // 考虑特殊日期
            if (StringUtils.equals(resourceMode, MODE_SPECIAL_DAY_NOT_ROLL) && specialDayHashMap.containsKey(tempTime1)) {
                String workDayType = specialDayHashMap.get(tempTime1).getWorkDayType();
                if (SPECIAL_DAY_WEEKEND_WORK.equals(workDayType)) {
                    flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime, times,
                            values, firstTimePointOfDayMap, null, 0, 0);
                } else {
                    flag = NOT_WORK_VALUE;
                    // 如果这天不工作，则需要重新
                    values.remove(0);
                    values.add(flag);
                }
                // 不是特殊日期 或 不考虑特殊日期
            } else {
                // 常规
                if (calendarClass.equals(CALENDAR_CLASS_NORMAL)) {
                    char c = calculateStaffIsWork(tempTime, chars);
                    if (c == '1') {
                        flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime,
                                times, values, firstTimePointOfDayMap, null, 0, 0);
                    } else {
                        flag = NOT_WORK_VALUE;
                        values.remove(0);
                        values.add(flag);
                    }
                    // 翻班
                } else {
                    EntityRollCalendar rollCalendar = rollCalendars.get(day);
                    if (rollCalendar.getCalendarType().equals(CALENDAR_TYPE_WORK)) {
                        flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime,
                                times, values, firstTimePointOfDayMap, null, 0, 0);
                    } else {
                        flag = NOT_WORK_VALUE;
                        values.remove(0);
                        values.add(flag);
                    }
                }
            }

            // 将时间推进到下一天的 00:00
            tempTime.set(Calendar.HOUR_OF_DAY, 0);
            tempTime.set(Calendar.MINUTE, 0);
            tempTime.set(Calendar.SECOND, 0);
            tempTime.set(Calendar.MILLISECOND, 0);
            tempTime.add(Calendar.DATE, 1);

            // 翻班日历的第二天
            int i = (day + 1) % rollDaySize;
            // 记录当前是哪一天
            int nowDay;
            // 按天遍历时间段，找到状态切换的时间点
            while (tempTime.before(endTime)) {
                nowDay = tempTime.get(Calendar.DATE);
                // 考虑 specialDay 不工作
                if (StringUtils.equals(resourceMode, MODE_SPECIAL_DAY_NOT_ROLL) && specialDayHashMap.containsKey(tempTime.getTime())) {
                    EntitySpecialDay specialDay = specialDayHashMap.get(tempTime.getTime());
                    String workDayType = specialDay.getWorkDayType();
                    // 周末工作
                    if (SPECIAL_DAY_WEEKEND_WORK.equals(workDayType)) {
                        // 正常日历
                        if (calendarClass.equals(CALENDAR_CLASS_NORMAL)) {
                            flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime,
                                    times, values, timePointOfDayMap, null, 0, 0);
                            // 翻班日历
                        } else {
                            flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime,
                                    times, values, allRollTimePointOfDayMap.get(i), allRollTimePointOfDayMap, i, rollDaySize);
                            // 对应翻班日历集合的下标
                            i = (i + 1) % rollDaySize;
                        }
                    }
                    // 不工作的两种情况
                } else {
                    // 非 specialDay
                    // 正常日历
                    if (calendarClass.equals(CALENDAR_CLASS_NORMAL)) {
                        // 计算员工是否工作
                        char c = calculateStaffIsWork(tempTime, chars);
                        if (c == '1') {
                            flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime,
                                    times, values, timePointOfDayMap, null, 0, 0);
                        }
                        // 翻班日历
                    } else {
                        EntityRollCalendar rollCalendar = rollCalendars.get(i);
                        // 上班
                        if (rollCalendar.getCalendarType().equals(CALENDAR_TYPE_WORK)) {
                            flag = calculateCalendarDayTimesAndValues(flag, tempTime, startTime,
                                    times, values, allRollTimePointOfDayMap.get(i), allRollTimePointOfDayMap, i, rollDaySize);
                        }
                        // 对应翻班日历集合的下标
                        i = (i + 1) % rollDaySize;
                    }
                }

                // 将时间推进到下一天的 00:00
                // 判断是否跨天
                if (tempTime.get(Calendar.DATE) == nowDay) {
                    tempTime.set(Calendar.HOUR_OF_DAY, 0);
                    tempTime.set(Calendar.MINUTE, 0);
                    tempTime.set(Calendar.SECOND, 0);
                    tempTime.set(Calendar.MILLISECOND, 0);
                    tempTime.add(Calendar.DATE, 1);
                }
            }

            // 将结尾时间加到 times 中
            times.add(DateUtil.getDistanceIntTime(endTime, startTime, planGranularity));
        } else {
            // 没有设置日历
            times = new ArrayList<>(Arrays.asList(new Double[]{0.0, (double) endIntTime}));
            values = new ArrayList<>(Arrays.asList(new Double[]{100.0}));
        }

        if (resourceType.equals(RESOURCE_TYPE_ENGINEER)){

            

            stepFunctionStaffTime.put(resourceId, times);
            stepFunctionStaffValue.put(resourceId, values);
            log.info("人员 " + resourceId + " 的stepFunctionStaffTime的size：" + stepFunctionStaffTime.get(resourceId).size());

            //log.info("人员 " + resourceId + " 的stepFunctionStaffTime：" + stepFunctionStaffTime.get(resourceId));

            log.info("人员 " + resourceId + " 的stepFunctionStaffValue的size：" + stepFunctionStaffValue.get(resourceId).size());
            //log.info("人员 " + resourceId + " 的stepFunctionStaffValue：" + stepFunctionStaffValue.get(resourceId));

        }else {
            stepFunctionEquipmentTime.put(resourceId, times);
            stepFunctionEquipmentValue.put(resourceId, values);
            log.info("设备 " + resourceId + " 的stepFunctionEquipmentTime的size：" + stepFunctionEquipmentTime.get(resourceId).size());
//            log.info("设备 " + resourceId + " 的stepFunctionEquipmentTime：" + stepFunctionEquipmentTime);
            log.info("设备 " + resourceId + " 的stepFunctionEquipmentValue的size：" + stepFunctionEquipmentValue.get(resourceId).size());
//            log.info("设备 " + resourceId + " 的stepFunctionEquipmentValue：" + stepFunctionEquipmentValue);
        }
    }

    /**
     * 计算 flag 初始值，第一天需要考虑的时间段和每天切换时间的 Map
     * @param calendar
     * @param timePointOfDayMap
     * @param firstTimePointOfDayMap
     * @param firstDayTempTime
     * @param tempTime
     * @param flag
     * @return
     */
    private Double calculateFlagAndSwitchMap(EntitySuperCalendar calendar,
                       HashMap<String, String[]> timePointOfDayMap,
                       HashMap<String, String[]> firstTimePointOfDayMap,
                       Calendar firstDayTempTime,
                       Calendar tempTime,
                       Double flag){

        String shiftStartStr = calendar.getShiftStart();
        String breakStartStr = calendar.getBreakStart();
        String breakEndStr = calendar.getBreakEnd();
        String supperBreakStartStr = calendar.getSupperBreakStart();
        String supperBreakEndStr = calendar.getSupperBreakEnd();
        String shiftEndStr = calendar.getShiftEnd();

        // 记录 flag 是否改变
        Boolean changeFlag = false;
        if (shiftStartStr != null){
            // 去除前后空格
            shiftStartStr = shiftStartStr.trim();
            String[] shiftStart = shiftStartStr.split(":");
            timePointOfDayMap.put(SHIFTSTART, shiftStart);

            // 当需要计算第一天时执行以下代码
            if (firstTimePointOfDayMap != null){
                firstDayTempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(shiftStart[0]));
                firstDayTempTime.set(Calendar.MINUTE, Integer.parseInt(shiftStart[1]));
                // 将该天在 tempTime 后的时间节点，单独记录下来，便于做第一天的切换点的计算
                if (tempTime.before(firstDayTempTime) || tempTime.equals(firstDayTempTime)){
                    firstTimePointOfDayMap.put(SHIFTSTART, shiftStart);
                    // 确定开始时的工作状态
                    if (!changeFlag){
                        flag = NOT_WORK_VALUE;
                        changeFlag = true;
                    }
                }
            }

        }

        if (breakStartStr != null){
            breakStartStr = breakStartStr.trim();
            String[] breakStart = breakStartStr.split(":");
            timePointOfDayMap.put(BREAKSTART, breakStart);

            if (firstTimePointOfDayMap != null){
                firstDayTempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(breakStart[0]));
                firstDayTempTime.set(Calendar.MINUTE, Integer.parseInt(breakStart[1]));
                if (tempTime.before(firstDayTempTime)){
                    firstTimePointOfDayMap.put(BREAKSTART, breakStart);
                    if (!changeFlag){
                        flag = WORK_VALUE;
                        changeFlag = true;
                    }
                }
            }
        }
        if (breakEndStr != null){
            breakEndStr = breakEndStr.trim();
            String[] breakEnd = breakEndStr.split(":");
            timePointOfDayMap.put(BREAKEND, breakEnd);

            if (firstTimePointOfDayMap != null) {
                firstDayTempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(breakEnd[0]));
                firstDayTempTime.set(Calendar.MINUTE, Integer.parseInt(breakEnd[1]));
                if (tempTime.before(firstDayTempTime)) {
                    firstTimePointOfDayMap.put(BREAKEND, breakEnd);
                    if (!changeFlag) {
                        flag = NOT_WORK_VALUE;
                        changeFlag = true;
                    }
                }
            }
        }
        if (supperBreakStartStr != null && supperBreakStartStr.trim().compareTo(shiftEndStr.trim()) < 0){
            supperBreakStartStr = supperBreakStartStr.trim();
            String[] supperBreakStart = supperBreakStartStr.split(":");
            timePointOfDayMap.put(SUPPERBREAKSTART, supperBreakStart);

            if (firstTimePointOfDayMap != null) {
                firstDayTempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(supperBreakStart[0]));
                firstDayTempTime.set(Calendar.MINUTE, Integer.parseInt(supperBreakStart[1]));
                if (tempTime.before(firstDayTempTime)) {
                    firstTimePointOfDayMap.put(SUPPERBREAKSTART, supperBreakStart);
                    if (!changeFlag) {
                        flag = WORK_VALUE;
                        changeFlag = true;
                    }
                }
            }
        }

        if (supperBreakEndStr != null && supperBreakEndStr.trim().compareTo(shiftEndStr.trim()) < 0){
            supperBreakEndStr = supperBreakEndStr.trim();
            String[] supperBreakEnd = supperBreakEndStr.split(":");
            timePointOfDayMap.put(SUPPERBREAKEND, supperBreakEnd);

            if (firstTimePointOfDayMap != null) {
                firstDayTempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(supperBreakEnd[0]));
                firstDayTempTime.set(Calendar.MINUTE, Integer.parseInt(supperBreakEnd[1]));
                if (tempTime.before(firstDayTempTime)) {
                    firstTimePointOfDayMap.put(SUPPERBREAKEND, supperBreakEnd);
                    if (!changeFlag) {
                        flag = NOT_WORK_VALUE;
                        changeFlag = true;
                    }
                }
            }
        }


        if (shiftEndStr != null){
            shiftEndStr = shiftEndStr.trim();

            String[] shiftEnd = shiftEndStr.split(":");
            timePointOfDayMap.put(SHIFTEND, shiftEnd);
            if (firstTimePointOfDayMap != null) {
//                // 00:00 时，date 加 1
//                if (ZERO_POINT_TIME.equals(shiftEndStr)){
//                    firstDayTempTime.add(Calendar.DATE, 1);
//                }
                firstDayTempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(shiftEnd[0]));
                firstDayTempTime.set(Calendar.MINUTE, Integer.parseInt(shiftEnd[1]));
                if (tempTime.before(firstDayTempTime)) {
                    firstTimePointOfDayMap.put(SHIFTEND, shiftEnd);
                    if (!changeFlag) {
                        flag = WORK_VALUE;
                    }
                }
            }
        }

        return flag;
    }

    /**
     * 按照输入每天的字符串数组，算出每天日历工作的时间切换
     * @param flag
     * @param tempTime
     * @param startTime
     * @param times
     * @param values
     * @param timePointOfDayMap
     */
    public Double calculateCalendarDayTimesAndValues(Double flag,
                                                     Calendar tempTime,
                                                     Calendar startTime,
                                                     ArrayList<Double> times,
                                                     ArrayList<Double> values,
                                                     HashMap<String, String[]> timePointOfDayMap,
                                                     HashMap<Integer, HashMap<String, String[]>> allRollTimePointOfDayMap,
                                                     int i,
                                                     int rollDaySize){

        String[] shiftStart = timePointOfDayMap.get(SHIFTSTART);
        if (shiftStart != null){
            int minute = Integer.parseInt(shiftStart[0]);
            int second = Integer.parseInt(shiftStart[1]);
            // 常规日历
            if (allRollTimePointOfDayMap == null){
                flag = setTimeAndFlag(tempTime, startTime, minute, second, flag, times, values, WORK_VALUE);
            // 解决跨天的问题
            // 当日历为翻班时
            }else {
                if (minute == 0 && second == 0){
                    // 获取前一天
                    HashMap<String, String[]> timeMap = allRollTimePointOfDayMap.get((i - 1 + rollDaySize) % rollDaySize);
                    String[] shiftEndPreviousDay = timeMap.get(SHIFTEND);
                    // 如果前一天结束不是 00:00 则不需要考虑跨天问题
                    if (!Arrays.equals(shiftStart, shiftEndPreviousDay)){
                        flag = setTimeAndFlag(tempTime, startTime, minute, second, flag, times, values, WORK_VALUE);
                    }
                }else {
                    flag = setTimeAndFlag(tempTime, startTime, minute, second, flag, times, values, WORK_VALUE);
                }
            }
        }

        String[] breakStart = timePointOfDayMap.get(BREAKSTART);
        if (breakStart != null){
            flag = setTimeAndFlag(tempTime, startTime, Integer.parseInt(breakStart[0]),
                    Integer.parseInt(breakStart[1]), flag, times, values, NOT_WORK_VALUE);
        }

        String[] breakEnd = timePointOfDayMap.get(BREAKEND);
        if (breakEnd != null){
            flag = setTimeAndFlag(tempTime, startTime, Integer.parseInt(breakEnd[0]),
                    Integer.parseInt(breakEnd[1]), flag, times, values, WORK_VALUE);
        }

        String[] supperBreakStart = timePointOfDayMap.get(SUPPERBREAKSTART);
        if (supperBreakStart != null){
            flag = setTimeAndFlag(tempTime, startTime, Integer.parseInt(supperBreakStart[0]),
                    Integer.parseInt(supperBreakStart[1]), flag, times, values, NOT_WORK_VALUE);
        }

        String[] supperBreakEnd = timePointOfDayMap.get(SUPPERBREAKEND);
        if (supperBreakEnd != null){
            flag = setTimeAndFlag(tempTime, startTime, Integer.parseInt(supperBreakEnd[0]),
                    Integer.parseInt(supperBreakEnd[1]), flag, times, values, WORK_VALUE);
        }

        String[] shiftEnd = timePointOfDayMap.get(SHIFTEND);
        if (shiftEnd != null){
            int minute = Integer.parseInt(shiftEnd[0]);
            int second = Integer.parseInt(shiftEnd[1]);

            if (minute == 0 && second == 0){
                // date 加一天
                tempTime.set(Calendar.HOUR_OF_DAY, minute);
                tempTime.set(Calendar.MINUTE, second);
                tempTime.add(Calendar.DATE, 1);
                // 解决跨天的问题
                // 当日历为翻班时
                if (allRollTimePointOfDayMap != null){
                    // 获取下一天
                    HashMap<String, String[]> timeMap = allRollTimePointOfDayMap.get((i + 1) % rollDaySize);
                    String[] shiftStartNextDay = timeMap.get("shiftStart");
                    if (Arrays.equals(shiftEnd, shiftStartNextDay)){
                        return flag;
                    }
                }
            }

            flag = setTimeAndFlag(tempTime, startTime, minute, second, flag, times, values, NOT_WORK_VALUE);
        }

        return flag;
    }


    /**
     * calculateCalendarDayTimesAndValues（）方法中代码模块的抽取，设置时间和 flag
     * @param tempTime
     * @param startTime
     * @param minute
     * @param second
     * @param flag
     * @param times
     * @param values
     * @param workValue
     * @return
     */
    private Double setTimeAndFlag(Calendar tempTime,
                                  Calendar startTime,
                                  int minute,
                                  int second,
                                  Double flag,
                                  ArrayList<Double> times,
                                  ArrayList<Double> values,
                                  Double workValue){
        tempTime.set(Calendar.HOUR_OF_DAY, minute);
        tempTime.set(Calendar.MINUTE, second);
//        log.info(tempTime.getTime());
//        log.info(startTime.getTime());

        // 如果换算的时间一样，舍去第一个点
        if (Math.floor((double) (tempTime.getTime().getTime() - startTime.getTime().getTime())/(1000*60)/planGranularity) == 0.0){
            times.remove(0);
            values.remove(0);
        }

        // todo：检查逻辑
        int size = times.size();
        if (size > 1){
            Double time = times.get(size - 1);
            // 连着两个数是一样的，应该是说它那个break time它往前一一错
            if (Math.floor((double) (tempTime.getTime().getTime() - startTime.getTime().getTime())/(1000*60)/planGranularity) == time){
                times.remove(size - 1);
                times.add(time - 1);
            }
        }

        times.add(Math.floor((double) (tempTime.getTime().getTime() - startTime.getTime().getTime())/(1000*60)/planGranularity));

        values.add(workValue);
        flag = workValue;

        return flag;
    }

    /**
     * 计算员工是否工作
     * @param tempTime
     * @param chars
     * @return
     */
    private char calculateStaffIsWork(Calendar tempTime, char[] chars){
        // 获取星期几与 chars 对应的下标对应的值，即该天工不工作
        int dayOfWeek = tempTime.get(Calendar.DAY_OF_WEEK) - 2;
        char c ;
        if (dayOfWeek >= 0){
            c = chars[dayOfWeek];
        }else {
            // dayOfWeek 为星期日 -1 时，特殊考虑
            c = chars[6];
        }

        return c;
    }

    public void setMaxCalendarDays(int calendarDays){
        maxCalendarDays = Math.max(maxCalendarDays, calendarDays);
    }

    public void setMaxTimeLimit(int timeLimit){
        maxTimeLimit = Math.max(maxTimeLimit, timeLimit);
    }

    public void setMinPlanStartTime(Date minPlanStartTime) {
        if (this.minPlanStartTime == null) {
            this.minPlanStartTime = minPlanStartTime;
        } else if (this.minPlanStartTime.compareTo(minPlanStartTime) > 0 ){
            this.minPlanStartTime = minPlanStartTime;
        }
    }

    public void setMaxPlanEndTime(Date planEndTime) {
        this.maxPlanEndTime = DateUtil.getMaxTime(this.maxPlanEndTime, planEndTime);
    }

    /**
     * 准备子模型中，任务单下的完整批次列表。
     * 这个列表中包括该submodel排程涉及到的任务单下的所有已经排程和该submodel将要排程的批次。
     * 如果该任务单有后续submodel要排程的批次，这些批次暂时不放在这个list中，即本次排程中假设没有那些批次。
     * 初始化 stepA
     */
    private void prepareSubTaskList() {
        //todo: 如果前面排程中有把这次排程的一些task进行了排程，并且没有排进去（is_presence == false),则需要从这个list中去掉
        HashMap<Integer, String> sampleGroupUniqueSampleNoMap = engineProcessedData.getSampleGroupUniqueSampleNoMap();
//        for(Map.Entry<String, ArrayList<EntitySubTask>> entry: toBePlannedSubTaskMap.entrySet()){
        for (Iterator<Map.Entry<String, ArrayList<EntitySubTask>>> it = toBePlannedSubTaskMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ArrayList<EntitySubTask>> entry = it.next();

            String taskNo = entry.getKey();
            ArrayList<EntitySubTask> toBePlannedSubTasks = entry.getValue();
            EntityTask task = engineProcessedData.getTaskHashMap().get(taskNo);

            // 责任工程师不在授权允许的人员范围内
            EntityStaff taskAssignedRespEngineer = task.getAssignedRespEngineer();
            if (taskAssignedRespEngineer != null) {
                if (!task.getInitialPossibleRespEngineers().contains(taskAssignedRespEngineer)) {
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_CONDITIONS_NOT_ENOUGH_NOT_SCHEDULED,
                            "已部分排程任务单 " + taskNo + " 对应的责任工程师 " + taskAssignedRespEngineer.getStaffName()
                            + "（ID " + taskAssignedRespEngineer.getStaffId() + "）不在授权允许的人员范围内，忽略该设置，继续排程");
                    task.setAssignedRespEngineer(null);
                }
            }

            // 判断该任务单是否需要排程
            if (!task.getIsToBePlanned()) {
                it.remove();

                continue;
            }


//            // 用于储存每个任务单下所有的优先工程师
//            ArrayList<EntityStaff> authRespEngineersIntersection = new ArrayList<>();
            // 将已经排程并且没有排进去的 Task，从该子模型 plan.st 中去除
            if (!task.getIsPresence()) {
                // optional的没排上，不用更新
                if (!task.getIsOptional()) {
                    it.remove();
                    continue;
                }
            }
            ArrayList<EntitySubTask> allSubTasks = task.getSubTasks();
            ArrayList<EntitySubTask> subTasksTemp = new ArrayList<>();
            // 遍历该 task 下面所有的 subTask
            int size = allSubTasks.size();

            for (int i = 0; i < size; i++) {
                EntitySubTask subTask = allSubTasks.get(i);
                String subTaskNo = subTask.getSubTaskNo();

                // 试验批次的指定负责人不在授权允许的人员范围内
                // 无法进行该验证，因为不知道批次对应的BOM等信息
                String subAssignedRespEngineerId = subTask.getAssignedRespEngineerId();
                if (subAssignedRespEngineerId != null) {
//                    boolean flag = false;
//                    for (EntityStaff authRespEngineer : subTask.getAuthRespEngineers()) {
//                        if (authRespEngineer.getStaffId().equals(subAssignedRespEngineerId)) {
//                            flag = true;
//                            break;
//                        }
//                    }
                    EntityStaff subTaskStaff = engineProcessedData.getInitialStaffHashMap().get(subAssignedRespEngineerId);
//
//                    if (!flag) {
//                        if (subTaskStaff == null) {
//                            // 前面已经验证过
//                        } else {
//                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_CONDITIONS_NOT_ENOUGH_CAN_SCHEDULED,
//                                    "已排程批次 " + subTaskNo + " 对应的指定负责人 " + subTaskStaff.getStaffName() + " ID（"
//                                            + subAssignedRespEngineerId + ")不在授权允许的人员范围内，忽略该设置，继续排程");
//                            subTask.setAssignedRespEngineerId(null);
//                        }
//                    }
                    // 检查是否该任务单下的已排程批次负责人和任务单负责人是一个人
                    if (!StringUtils.equals(taskAssignedRespEngineer.getStaffId(), subAssignedRespEngineerId)) {
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_CONDITIONS_NOT_ENOUGH_CAN_SCHEDULED,
                                "已部分排程任务单 " + taskNo + " 对应的责任工程师 " + taskAssignedRespEngineer.getStaffName() +
                                        "（ID " + taskAssignedRespEngineer.getStaffId() + "）和批次 " + subTaskNo + " 对应的指定负责人 "
                                        + subTaskStaff.getStaffName() + "（ID " + subTaskStaff.getStaffId() + "）不是同一个人，忽略该设置，继续排程");
                        subTask.setAssignedRespEngineerId(null);
                    }
                }

                // 批次对应的BOM下没有小阶段
                if (toBePlannedSubTasks.contains(subTask)) {
                    // 已排程的不需要检查了，也不需要小阶段了
                    int stepCount = subTask.getBom().getSteps().size();
                    if (stepCount == 0) {
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                                "批次 " + subTaskNo + " 的BOM " + subTask.getBomNo() + " 没有定义任何小阶段，导致相关任务单无法进行排程");
                        // 将相关任务单移除排程
                        subTask.setIsToBePlanned(false);
                        task.setIsToBePlanned(false);
                        break;
                    }

                    // 初始化 uniqueSampleSupportedSubtasks 和 uniqueSampleSupportedSubtaskPlans
                    Integer sampleGroupId = subTask.getSampleGroupId();
                    // 该批次所要的样品为 “整车”
                    if (sampleGroupUniqueSampleNoMap.containsKey(sampleGroupId)) {
                        String sampleNO = sampleGroupUniqueSampleNoMap.get(sampleGroupId);
                        ArrayList<EntitySubTask> subTasks = uniqueSampleSupportedSubtasks.computeIfAbsent(sampleNO, k -> new ArrayList<>());
                        subTasks.add(subTask);
                        // 从主模型中获取 subTaskPlan，这里已经包括了plan input表来的和前面子模型的结果了。
                        ArrayList<EntitySubTaskPlan> subTaskPlans = engineProcessedData.getUniqueSampleSupportedSubtaskPlans().get(sampleNO);
                        if (subTaskPlans != null && subTaskPlans.size() != 0) {
                            uniqueSampleSupportedSubtaskPlans.computeIfAbsent(sampleNO, k -> subTaskPlans);
                        }

//                        log.info(uniqueSampleSupportedSubtasks.size());
//                        log.info(uniqueSampleSupportedSubtaskPlans.size());
                    }

                    // 小阶段 step 相关数据的初始化
                    // 顺便计算和设置了批次的长度
                    if (!initializeStepRelatedData(subTask)) {
//                        it.remove();

                        subTask.setIsToBePlanned(false);
                        task.setIsToBePlanned(false);
                        break;
                    } else {
                        if (subTask.getTaskGroup()!= null && !subTask.getTaskGroup().equals("")){
                            //如果有批次组，先不计算，应该在批次组的地方报错了
                        } else {
                            //不属于批次组
                            double taskLength = Math.max(subTask.getSubTaskLength(), task.getTaskLength());
                            task.setTaskLength(taskLength);
                        }

                    }

                }
                subTasksTemp.add(subTask);


//                    // 获取该 task 下所有批次指定工程师的交集
//                    ArrayList<EntityStaff> authRespEngineers = subTask.getAuthRespEngineers();
//                    if (i == 0){
//                        authRespEngineersIntersection.addAll(authRespEngineers);
//                    }
//                    authRespEngineersIntersection.retainAll(authRespEngineers);

            }

            // task的总长度的判断
            // 计算从排程开始到结束时间之内根据任务单下小阶段实施时间的要求是否根本不可能完成整个任务单
            Date taskEarliestTime = minPlanStartTime;
            if (IS_NEED_NEW_FIXTURE.equals(task.getIsNeedNewFixture())) {
                taskEarliestTime = DateUtil.getMaxTime(minPlanStartTime, task.getFixtureReadyTime());
            }
            int planLength = DateUtil.getDistanceIntTime(maxPlanEndTime, taskEarliestTime, 1000 * 60 * 60);

            if (task.getTaskLength() > planLength){
                DecimalFormat df = new DecimalFormat("#0.00");
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                        "任务单 " + task.getTaskNo() + " 可以开始的时间为 " + DateUtil.getDateString(taskEarliestTime) +
                                "，下属批次的工作时间为 " + task.getTaskLength() + " 小时（"
                                + df.format(task.getTaskLength()/24)
                                +" 天），排程结束时间为 " + DateUtil.getDateString(maxPlanEndTime) +
                                "，不可能完成该任务单，导致该任务单无法排程");

                task.setIsToBePlanned(false);
            }


            if (task.getIsToBePlanned()) {
                subTaskMap.put(taskNo, subTasksTemp);
            } else {
                it.remove();
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_INFO, MESSAGE_TYPE_NOT_ENOUGH_SCHEDULING_CONDITIONS,
                        "任务单 " + taskNo + " 的数据不满足排程条件，导致该任务单无法排程");
            }

//            // 待排程任务单执行工程师的可能的人选——不会被子模型更新的列表
//            taskInitialPossibleRespEngineers(task, authRespEngineersIntersection);
        }

        // 根据批次组再循环，去掉因为批次组前序批次有数据问题，导致后续不能排程的情况
        HashMap<String, ArrayList<EntitySubTask>> validSortedSubTaskGroupHashMap
                = engineProcessedData.getValidSortedSubTaskGroupHashMap(engineProcessedData.getSortedSubTaskGroupHashMap(),
                "由于数据问题无法排程");
        engineProcessedData.setSortedSubTaskGroupHashMap(validSortedSubTaskGroupHashMap);
        // 再次循环子模型的待排程批次去掉新不要的内容
        for (Iterator<Map.Entry<String, ArrayList<EntitySubTask>>> it = toBePlannedSubTaskMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ArrayList<EntitySubTask>> entry = it.next();
            EntityTask task = engineProcessedData.getTaskHashMap().get(entry.getKey());
            if (!task.getIsToBePlanned()) {
                it.remove();
                subTaskMap.remove(task.getTaskNo());
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_INFO, MESSAGE_TYPE_NOT_ENOUGH_SCHEDULING_CONDITIONS,
                        "任务单 " + task.getTaskNo() + " 的数据不满足排程条件，导致该任务单无法排程");
            }
        }

     }



    /**
     * 小阶段 step 相关数据的初始化
     * @param subTask
     */
    private boolean initializeStepRelatedData(EntitySubTask subTask){

        // 给 subTask 的 stepActivities 赋值
        ArrayList<EntityStepActivity> stepActivities = subTask.getStepActivities();
        EntityTask task = engineProcessedData.getTaskHashMap().get(subTask.getTaskNo());
        EntityBom bom = subTask.getBom();
        ArrayList<EntityStep> steps = bom.getSteps();
        double subTaskLength = 0;
//        // 用于存放当批次下没有“报告撰写”大阶段时，所有小阶段可选工程师的并集
//        HashSet<EntityStaff> subTaskRespEngineers = new HashSet<>();
        for (EntityStep step : steps) {

            // 创建 stepA
            EntityStepActivity stepActivity = new EntityStepActivity();
            stepActivity.setStep(step);
            
            // 计算小阶段活动长度
            double timeLength = step.getTimeLength().doubleValue();
            double stepASize = timeLength * 60 / planGranularity;

            if (step.getPackingFlag() != null){
                // 将拆并箱标志转换成 list

                String[] packingFlag = step.getPackingFlag().toLowerCase().split(",");
                List<String> packingFlagList = Arrays.asList(packingFlag);
                // 拆并箱标志为pass
                if (packingFlagList.contains(ConstantUtil.PASS)){
                    // 批次不是所属的批次组中的第一个，则不创建 stepA
                    if (subTask.getPrevious() != null){
                        continue;
                    }
                }

                if (packingFlagList.contains(ConstantUtil.REPEAT)
                        || packingFlagList.contains(ConstantUtil.EXPAND)) {
                    // 有效样件数量
                    int validSampleQuantity = 0;
                    ArrayList<EntitySample> samples = subTask.getSamples();

                    // 如果小阶段既含有标准标志，又有非标准的拆并箱标志
                    ArrayList<String> tempList1 = new ArrayList<>();
                    tempList1.addAll(packingFlagList);
                    tempList1.removeAll(ConstantUtil.allPackingFlag);
                    if (tempList1.size() != 0){
                        for (EntitySample sample : samples) {
                            if (sample.getPackingFlag() == null) {
                                continue;
                            }
                            String[] samplePackingFlag = sample.getPackingFlag().toLowerCase().split(",");
                            List<String> samplePackingFlagList = Arrays.asList(samplePackingFlag);

                            // 判断 step 的拆并箱标志和 sample 是否有交集
                            ArrayList<String> tempList2 = new ArrayList<>();
//                            tempList2.addAll(packingFlagList);
//                            if (tempList2.retainAll(samplePackingFlagList)){
//                                validSampleQuantity += sample.getSampleQuantity();
//                            }
                            tempList2.addAll(tempList1);
                            tempList2.retainAll(samplePackingFlagList);
                            if (tempList2.size()> 0){
                                validSampleQuantity += sample.getSampleQuantity();
                            }
                        }
                    }else {
                        // 一般情况，只含有标准标志
                        for (EntitySample sample : samples) {
                            Integer sampleQuantity = sample.getSampleQuantity();
                            validSampleQuantity += sampleQuantity;
                        }
                    }

                    // 拆并箱标志为repeat，需要根据批次样品组中的有效样件数量倍乘。
                    if (packingFlagList.contains(ConstantUtil.REPEAT)){
                        stepASize *= validSampleQuantity;
                    }

                    // 设置有效样件数量
                    stepActivity.setValidSampleQuantity(validSampleQuantity);

                    log.info("subTask_" + subTask.getTaskNo() + "_stepA_" + stepActivity.getStep().getStepId() + "有效样件数量是：" + validSampleQuantity);
                }
            }

            // 设置小阶段活动的时长（小时）
            stepActivity.setSize(stepASize);

            log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段 " + stepActivity.getStep().getStepId() + " 的工作时长(时间块)是：" + stepASize);

            stepActivities.add(stepActivity);
            if (step.isRepeat()) {
                subTaskLength = subTaskLength + step.getTimeLengthConsideredWorkMode() * stepActivity.getValidSampleQuantity();
            } else {
                subTaskLength = subTaskLength + step.getTimeLengthConsideredWorkMode();
            }
            /*// 添加 step 可选的工程师
            addStepPossibleEngineers(step, bom);
            // 添加 step 可选的技师
            addStepPossibleTechnicians(step);*/

//            // todo：确认代码位置
//            // 考虑各种授权后待排程 subTask 的可能的“实验批次负责人”
//            ArrayList<EntityStaff> stepAuthEngineers = step.getAuthEngineersConsideredBOM();
//            String testPhase = step.getTestPhase();
//            // 找出批次下“报告撰写”大阶段下的小阶段对应的step的engineer_group
//            if (StringUtils.equals(testPhase, REPORTING_PHASE_NAME)){
//                subTask.getAuthRespEngineers().addAll(stepAuthEngineers);
//            }else {
//                // 获取该 subTask 下所有小阶段的并集
//                subTaskRespEngineers.addAll(stepAuthEngineers);
//            }


            // 为 stepA 每个可能的resource group添加可能的资源

            preparePossibleResourcesForStepActivity(stepActivity, task, subTask);
            //log.info("subTask" + subTask.getSubTaskNo() + "_PossibleResources：" + subTask.getPossibleResources());

        }
        subTask.setSubTaskLength(subTaskLength);

        int planLength = DateUtil.getDistanceIntTime(subTask.getMaxPlanEndTime(), subTask.getMinPlanStartTime(), 1000 * 60 * 60);
        if (subTaskLength > planLength) {
            DecimalFormat df = new DecimalFormat("#0.00");
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                    "批次 " + subTask.getSubTaskNo() + " 可以开始的时间为 " + DateUtil.getDateString(subTask.getMinPlanStartTime())
                            + "，下属小阶段的工作时间（考虑工程师日历后）总和为 " + subTaskLength +
                            " 小时（" + df.format(subTaskLength / 24) + " 天），排程结束时间为 "
                            + DateUtil.getDateString(subTask.getMaxPlanEndTime()) + "，不可能完成该批次，导致相关任务单 "
                            + subTask.getTaskNo() + " 无法排程");
            task.setIsToBePlanned(false);
            subTask.setIsToBePlanned(false);
            return false;
        }

        // 将 maxRequestedResourceQuantity 中使用为 1 次的移除——不必了
        //calculateMaxRequestedResourceQuantity(subTask);
        log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段使用相同资源组的资源组名称和对应次数（ResourceGroupRequestedQuantity）为：" + subTask.getResourceGroupRequestedQuantity());
        log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段使用相同资源组的资源组名称和对应的最大需求资源的数量（MaxRequestedResourceQuantity）为：" + subTask.getMaxRequestedResourceQuantity());
        log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段使用相同资源组的资源组名称和对应的最小需求资源的数量（MinRequestedResourceQuantity）为：" + subTask.getMinRequestedResourceQuantity());
        log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段使用相同资源组的情况中，被要求使用固定资源的资源组名称（ResourceGroupsFixingResource）为：" + subTask.getResourceGroupsFixingResource());


//        // 如果批次下没有“报告撰写”大阶段，则取该批次下所有小阶段的[auth_engineer_considered_bom]的并集
//        if (subTask.getAuthRespEngineers().size() == 0){
//            subTask.setAuthRespEngineers(new ArrayList<>(subTaskRespEngineers));
//        }

        // 将 stepActivities 根据 stepA 中的 step 的 order小阶段顺序从小到大排序
        calculateAndSetPreviousStepActivity(stepActivities, subTask);
        return true;
    }

    /**
     * 给subTask里的小阶段活动的previous赋值
     * @param stepActivities
     * @param subTask
     */
    private void calculateAndSetPreviousStepActivity(ArrayList<EntityStepActivity> stepActivities,
                                                     EntitySubTask subTask) {
        Collections.sort(stepActivities, new Comparator<EntityStepActivity>() {
            @Override
            public int compare(EntityStepActivity o1, EntityStepActivity o2) {
                return o1.getStep().getStepOrder() - o2.getStep().getStepOrder();
            }
        });
        // 给 stepA 的 previous 赋值
        for (int j = 0; j < stepActivities.size(); j++) {
            if (j != 0){
                EntityStepActivity stepAPrevious = stepActivities.get(j - 1);
                stepActivities.get(j).setPrevious(stepAPrevious);
            }

            // todo: 测试代码注解掉
            // 测试代码
            if (stepActivities.get(j).getPrevious() == null){
                log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段 " + stepActivities.get(j).getStep().getStepId() +
                        " 没有前序小阶段");
            }else {
                log.info("批次 " + subTask.getSubTaskNo() + " 的小阶段 " + stepActivities.get(j).getStep().getStepId() +
                        " 的前序小阶段为 " + stepActivities.get(j).getPrevious().getStep().getStepId());
            }
        }
    }

    /**
     * 为小阶段活动添加需要的资源组，和每个资源内可能的资源
     * @param stepActivity
     */
    private void preparePossibleResourcesForStepActivity(EntityStepActivity stepActivity, EntityTask task, EntitySubTask subTask) {
        EntityStep step = stepActivity.getStep();
        String index = step.getBomNo() + step.getStepId() + "";

        HashMap<String, EntityResourceGroup> resourceGroupHashMap = step.getResourceGroupHashMap();
        HashMap<String, ArrayList<Object>> possibleResources = stepActivity.getPossibleResources();
        HashMap<String, Double> occupiedCapacity = stepActivity.getOccupiedCapacity();
        int validSampleQuantity = stepActivity.getValidSampleQuantity();

        // 遍历 stepA 中使用的每个资源组
        for (Map.Entry<String, EntityResourceGroup> entry: resourceGroupHashMap.entrySet()){

            String resourceGroupName = entry.getKey();
            EntityResourceGroup resourceGroup = entry.getValue();
            log.info("Possible resources for BOM " + step.getBomNo() + " step " + step.getStepId()
                    + " resource group " + resourceGroupName + " is："+ resourceGroup.getPossibleResources().size()
                    +". Required resources quantity is "+ resourceGroup.getRequestedResourceQuantity()
                    +". Required work time is "+ resourceGroup.getRequestedWorkTime()
                    + ". Details: ");

            String resourceType = resourceGroup.getResourceType();
            // 小阶段可能用到的一种类型的资源
            ArrayList<Object> stepPossibleResources = resourceGroup.getPossibleResources();
            if (stepPossibleResources != null && stepPossibleResources.size() != 0){
                // 工程师
                if (resourceType.equals(RESOURCE_TYPE_ENGINEER)){

                    // step 需要的工程师数
                    Integer engineerCount = step.getEngineerCount();
                    // 不考虑is contraint是false的需求 -> changed to consider is constraint = 0, on 2022.12.21
                    addRequestedWorkTime(totalWorkTime, totalRequestedWorkTime, RESOURCE_TYPE_ENGINEER,
                        resourceGroup.isConstraint(), step.isRepeat(),
                        resourceGroup.getRequestedResourceQuantity(), resourceGroup.getRequestedDailyMode(),
                        resourceGroup.getRequestedWorkTime(), resourceGroup.getRequestedDayQuantity(),
                        validSampleQuantity);

                    // 统计 engineer 资源组使用次数的相关信息
                    calculateSubtaskResourceGroupRelatedData(subTask, resourceType, resourceGroup,engineerCount,
                            null, resourceGroup.getPossibleResources());

                    // 遍历小阶段可能用到的资源，并计算每个资源对应的 occupiedCapacity
                    for (Object resource : stepPossibleResources) {

                        EntityStaff engineer = (EntityStaff) resource;
                        String staffId = engineer.getStaffId();
                        log.info ("engineer " + staffId);
                        double occupiedCapacityValue = engineProcessedData.calculateOccupiedCapacity(
                                resourceGroup.isConstraint(), resourceGroup.isExpandable(), (double)DEFAULT_RESOURCE_CAPACITY,
                                (double)resourceGroup.getRequestedResourceCapacity(), false, 1);
                        occupiedCapacity.put(resourceGroupName+"_"+engineer.getStaffId(), occupiedCapacityValue);

                        if (!possibleStaffs.contains(engineer)){
                            // 计算固定的时间
                            calculateTotalFixedTime(staffId, resourceType);
                            addFixedWorkTime(staffId, RESOURCE_TYPE_ENGINEER,
                                    totalWorkTime, totalFixedTimePerStaff);
                        }
                        HashSet<EntityStaff> engineerSet = totalPossibleResourceQuantity.computeIfAbsent(RESOURCE_TYPE_ENGINEER, k -> new HashSet<>());
                        engineerSet.add(engineer);
                        totalPossibleResourceQuantity.put(RESOURCE_TYPE_ENGINEER, engineerSet);

                        // 将可选 engineer 添加到所有可能的人员列表中
                        possibleStaffs.add(engineer);

                        // 获得人员日历信息
                        //calculateSwitchTimeAndSwitchValue(resource,resourceType);
                    }

                    // stepA 工程师资源的赋值
                    // 报告撰写可能需要多于1个工程师，这时不能只有任务单的可能工程师才行
//                    if (StringUtils.equals(step.getTestPhase(), ConstantUtil.REPORTING_PHASE_NAME)){
//                        // stepA 对应的是“报告撰写”大阶段
//                        possibleResources.put(resourceGroupName, new ArrayList<>(task.getPossibleRespEngineers()));
//                    }else {
                        // 普通 stepA
                        possibleResources.put(resourceGroupName, new ArrayList<>(stepPossibleResources));
                    //}
                // todo:加数据验证
                // 技师
                }else if(resourceType.equals(RESOURCE_TYPE_TECHNICIAN)){

                    HashMap<String, EntityStepSkill> stepSkillHashMap = engineProcessedData.getStepSkillHashMap();
                    String stepSkillKey = index;
                    if (!resourceGroupName.equals(DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME)){
                        stepSkillKey = index + resourceGroupName;
                    }
                    // step 需要的 technician 数
                    EntityStepSkill stepSkill = stepSkillHashMap.get(stepSkillKey);
                    Integer technicianCount = stepSkill.getTechnicianCount();

                    addRequestedWorkTime(totalWorkTime, totalRequestedWorkTime, RESOURCE_TYPE_TECHNICIAN,
                        resourceGroup.isConstraint(), step.isRepeat(),
                        resourceGroup.getRequestedResourceQuantity(), resourceGroup.getRequestedDailyMode(),
                        resourceGroup.getRequestedWorkTime(), resourceGroup.getRequestedDayQuantity(),
                        validSampleQuantity);

                    String skillIdSet = stepSkill.getSkillIdSet();
                    // 统计 skillSet 资源组使用次数的相关信息
                    calculateSubtaskResourceGroupRelatedData(subTask, skillIdSet, resourceGroup,
                            technicianCount, null, resourceGroup.getPossibleResources());

                    // 遍历可能用到 technician，给他们添加 calendar 等信息
                    for (Object resource : stepPossibleResources) {
                        EntityStaff technician = (EntityStaff) resource;
                        String staffId = technician.getStaffId();
                        log.info ("technician " + staffId);
                        double occupiedCapacityValue = engineProcessedData.calculateOccupiedCapacity(
                                resourceGroup.isConstraint(), resourceGroup.isExpandable(), (double)DEFAULT_RESOURCE_CAPACITY,
                                (double)resourceGroup.getRequestedResourceCapacity(), false, 1);
                        occupiedCapacity.put(resourceGroupName+"_"+technician.getStaffId(), occupiedCapacityValue);

                        if (!possibleStaffs.contains(technician)){
                            // 计算固定的时间
                            calculateTotalFixedTime(staffId, resourceType);
                            addFixedWorkTime(staffId, RESOURCE_TYPE_TECHNICIAN,
                                    totalWorkTime, totalFixedTimePerStaff);
                        }

                        HashSet<EntityStaff> technicianSet = totalPossibleResourceQuantity.computeIfAbsent(RESOURCE_TYPE_TECHNICIAN, k -> new HashSet<>());
                        technicianSet.add(technician);
                        totalPossibleResourceQuantity.put(RESOURCE_TYPE_TECHNICIAN, technicianSet);

                        // 将可选 technician 添加到所有可能的人员列表中
                        possibleStaffs.add(technician);



                        // 获得人员日历信息
                        //calculateSwitchTimeAndSwitchValue(resource,resourceType);
                    }
                    // 给 stepA 技师资源赋值
                    possibleResources.put(resourceGroupName, new ArrayList<>(stepPossibleResources));
                // 设备
                }else if (resourceType.equals(RESOURCE_TYPE_EQUIPMENT)){
                    HashMap<String, EntityStepEquipmentGroup> stepEquipmentGroupHashMap = engineProcessedData.getStepEquipmentGroupHashMap();
                    String stepEGIndex = index + resourceGroup.getResourceGroupName();
                    EntityStepEquipmentGroup stepEquipmentGroup = stepEquipmentGroupHashMap.get(stepEGIndex);
                    Integer equipmentNum = stepEquipmentGroup.getEquipmentNum();
                    String isFixingEquipment = stepEquipmentGroup.getIsFixingEquipment();

                    // 统计 equipment 资源组使用次数的相关信息
                    calculateSubtaskResourceGroupRelatedData(subTask, resourceGroupName, resourceGroup,
                            equipmentNum, isFixingEquipment, resourceGroup.getPossibleResources());

                    List<String> packingFlagList = null;
                    if (step.getPackingFlag() != null){
                        String[] packingFlag = step.getPackingFlag().split(",");
                        packingFlagList = Arrays.asList(packingFlag);
                    }

                    ArrayList<Object> needEquipments = new ArrayList<>();
                    // 遍历所有设备，过滤设备容量不够的设备
                    for (Object resource : stepPossibleResources) {
                        EntityEquipment equipment = (EntityEquipment) resource;
                        log.info ("equipment " + equipment.getEquipmentId());
                        // 计算出设备组的资源占用
                        double occupiedCapacityValue = engineProcessedData.calculateOccupiedCapacity(
                                resourceGroup.isConstraint(), resourceGroup.isExpandable(), (double)equipment.getCapacityInModel(),
                                (double)resourceGroup.getRequestedResourceCapacity(), step.isExpand(),validSampleQuantity);

                        occupiedCapacity.put(resourceGroupName + "_" + equipment.getEquipmentId(), occupiedCapacityValue);

                        int capacityInModel = equipment.getCapacityInModel();
                        int requestedResourceCapacity = resourceGroup.getRequestedResourceCapacity();

                        if (packingFlagList != null){
                            // 有拆并箱的expand标识时，容量要乘以它的有效样件
                            if (packingFlagList.contains(ConstantUtil.EXPAND)) {
                                requestedResourceCapacity *= stepActivity.getValidSampleQuantity();
                            }
                        }

                        // 去掉容量不够的设备
                        if (capacityInModel >= requestedResourceCapacity){
                            // 生成所有可能的设备的列表
                            possibleEquipments.add(equipment);

                            // 获得设备日历信息
                            ///calculateSwitchTimeAndSwitchValue(resource,resourceType);

                            if (resourceGroup.getState() != null && !resourceGroup.getState().equals("")
                                    && resourceGroup.isConstraint()){
                                // 获得所有带状态的设备的列表
                                HashSet<String> states = equipmentsWithState.computeIfAbsent(equipment.getEquipmentId(),
                                        k-> new HashSet<>());
                                states.add(resourceGroup.getState());
                            }
                            needEquipments.add(equipment);
                        }else {
                            // todo
                            // 小阶段需求的设备容量大于设备本身的容量，需要考虑每个批次乘以有效样件数量之后的容量
                        }
                    }
                    // 给 stepA 的设备资源赋值
                    possibleResources.put(resourceGroupName, needEquipments);

                }
            }
        }
    }

    /**
     * 在preparePossibleResourcesForStepActivity函数中调用
     * @param totalRequestedWorkTime
     * @param totalFixedTimePerStaff
     */
    private void addFixedWorkTime(String staffId, String resourceType,
                                  HashMap<String, Double> totalRequestedWorkTime, HashMap<String, Double> totalFixedTimePerStaff) {
        Double fixedTime = totalFixedTimePerStaff.get(staffId);
        if (fixedTime != null && fixedTime > 0){
            Double totalTime = totalRequestedWorkTime.computeIfAbsent(resourceType, k -> 0.0);
            totalRequestedWorkTime.put(resourceType, fixedTime + totalTime);
        }
    }

    /**
     * 在preparePossibleResourcesForStepActivity函数中用到
     * @param totalRequestedWorkTimeMap
     * @param resourceType
     * @param requestedCount
     * @param validSampleQuantity
     */
    private void addRequestedWorkTime(HashMap<String, Double> totalWorkTimeMap,
                                      HashMap<String, Double> totalRequestedWorkTimeMap,
                                      String resourceType,
                                      boolean isConstraint, boolean isRepeat,
                                      int requestedCount, String resourceGroupDailyMode,
                                      Double requestedWorkTime, Integer requestedDayNum, int validSampleQuantity) {
        // 计算 资源 在每个子模型中被要求了多少时间，在每个子模型中有多少个
        Double totalWorkTime = totalWorkTimeMap.computeIfAbsent(resourceType, k -> 0.0);
        Double totalRequestedWorkTime = totalRequestedWorkTimeMap.computeIfAbsent(resourceType, k -> 0.0);
        Double workTime = 0.0;
        // 不按天
        if (RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY.equals(resourceGroupDailyMode)){
            workTime = requestedCount * requestedWorkTime *  60 / planGranularity;
        }
        // 按天
        if (RESOURCE_GROUP_DAILY_MODE_BY_DAY.equals(resourceGroupDailyMode)){
            workTime = requestedCount * requestedDayNum
                    * requestedWorkTime * 60/ planGranularity;
        }
        if (isRepeat){
            workTime = workTime * validSampleQuantity;
        }
        if (!isConstraint) {
            if (RESOURCE_TYPE_ENGINEER.equals(resourceType)) {
                workTime = workTime * ENGINEER_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO;
            }
            if (RESOURCE_TYPE_TECHNICIAN.equals(resourceType)){
                workTime = workTime * TECHNICIAN_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO;
            }
        }
        totalWorkTime += workTime;
        totalRequestedWorkTime += workTime;
        totalWorkTimeMap.put(resourceType, totalWorkTime);
        totalRequestedWorkTimeMap.put(resourceType, totalRequestedWorkTime);
    }

    /**
     * 统计批次中各个资源组次数的相关信息
     * @param subTask
     * @param name resourceGroupRequestedQuantity 的 key 命名
     * @param count stepA 资源要求数量
     * @param resourceGroup 资源组
     * @param isFixingEquipment 设备组是否使用固定设备的资源组名列表
     * @param resourceList stepA 资源组中可能的资源
     */
    private void calculateSubtaskResourceGroupRelatedData(EntitySubTask subTask,
                                                          String name,
                                                          EntityResourceGroup resourceGroup,
                                                          Integer count,
                                                          String isFixingEquipment,
                                                          ArrayList<Object> resourceList){

        HashMap<String, Integer> resourceGroupRequestedQuantity = subTask.getResourceGroupRequestedQuantity();
        HashMap<String, Integer> maxRequestedResourceQuantity = subTask.getMaxRequestedResourceQuantity();
        HashMap<String, Integer> minRequestedResourceQuantity = subTask.getMinRequestedResourceQuantity();
        HashSet<String> resourceGroupsFixingResource = subTask.getResourceGroupsFixingResource();
        HashMap<String, HashSet<Object>> possibleResources = subTask.getPossibleResources();
        HashMap<String, String> resourcesGroupTypeHashMap = subTask.getResourcesGroupTypeHashMap();

        String resourceGroupName = resourceGroup.getResourceGroupName();
        String resourceType = resourceGroup.getResourceType();

        // 统计资源组使用次数
        Integer resourceGroupCount = resourceGroupRequestedQuantity.computeIfAbsent(name, k -> 0);
        resourceGroupCount++;
        resourceGroupRequestedQuantity.put(name, resourceGroupCount);

        // 记录资源组被要求数量的最大值
        Integer requestedQty = maxRequestedResourceQuantity.computeIfAbsent(resourceGroupName, k -> 0);
        if (count > requestedQty){
            maxRequestedResourceQuantity.put(resourceGroupName, count);
        }

        // 记录资源组被要求数量的最小值
        Integer minRequestedQty = minRequestedResourceQuantity.computeIfAbsent(resourceGroupName, k -> count);
        if (count < minRequestedQty){
            minRequestedResourceQuantity.put(resourceGroupName, count);
        }

        // 统计使用多于 1 次的资源组，并记录其被要求数量的最大值——修改为不需要多于1次，因为即使只有一次，但是如果需要做多天，还是需要这个约束
        //if (resourceGroupCount > 1){
            // 批次中被要求使用多于1次而且被要求使用固定设备
            if (isFixingEquipment != null){
                if (isFixingEquipment.equals(EQUIPMENT_IS_FIXING)){
                    resourceGroupsFixingResource.add(resourceGroupName);
                }
            }

            // 批次中的被要求使用多于1次的资源组对应的可能的资源的并集
            HashSet<Object> resources = possibleResources.computeIfAbsent(resourceGroupName, k -> new HashSet<>());
            resources.addAll(resourceList);

            // 批次中的被要求使用多于1次的资源组，和对应的资源类型
            resourcesGroupTypeHashMap.put(resourceGroupName, resourceType);
        //}

    }

    /**
     * 将 maxRequestedResourceQuantity 中使用为 1 次的移除
     * @param subTask
     */
    @Deprecated
    private void calculateMaxRequestedResourceQuantity(EntitySubTask subTask){
        HashMap<String, Integer> maxRequestedResourceQuantity = subTask.getMaxRequestedResourceQuantity();
        HashMap<String, Integer> minRequestedResourceQuantity = subTask.getMinRequestedResourceQuantity();
        HashMap<String, Integer> resourceGroupRequestedQuantity = subTask.getResourceGroupRequestedQuantity();
        for (Iterator<Map.Entry<String, Integer>> it = maxRequestedResourceQuantity.entrySet().iterator(); it.hasNext();) {
            String resourceGroupName = it.next().getKey();
            Integer count;
            if (DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME.equals(resourceGroupName)){
                 count = resourceGroupRequestedQuantity.get(null);
            }else {
                count = resourceGroupRequestedQuantity.get(resourceGroupName);
            }
            //不需要去掉 count为1的情况了
//            if (count < 2){
//                it.remove();
//                minRequestedResourceQuantity.remove(resourceGroupName);
//            }
        }
    }


    /**
     * 创建 rollCalendar 集合考虑日历的重复次数
     * @param allRollCalendars
     */
    private ArrayList<EntityRollCalendar> calculateAllRollCalendars(ArrayList<EntityRollCalendar> allRollCalendars){
        ArrayList<EntityRollCalendar> rollCalendars = new ArrayList<>();
        for (EntityRollCalendar rollCalendar : allRollCalendars) {
            Integer repeateNum = rollCalendar.getRepeateNum();
            // 算出翻班日历的天数
            if (repeateNum == 1){
                rollCalendars.add(rollCalendar);
            }else{
                for (int i = 1; i <= repeateNum; i++) {
                    rollCalendars.add(rollCalendar);
                }
            }
        }

        // 将 allRollCalendars 按照从小到大的顺序排序
        Collections.sort(rollCalendars, new Comparator<EntityRollCalendar>() {
            @Override
            public int compare(EntityRollCalendar o1, EntityRollCalendar o2) {
                return o1.getRollSeq() - o2.getRollSeq();
            }
        });

        return rollCalendars;
    }
    // 以下函数后面没有用到，所以暂时被注释
    // todo: 后面确认没用可以删掉
    /**
     * 计算资源人员和设备日历对应的每一天是否工作的切换时间
     * //@param resource
     */

    /*
    private void calculateSwitchTimeAndSwitchValue(Object resource, String resourceType){
        Integer calendarId = 0;
        String calendarClass = "";
        String mode = "";
        ArrayList<EntityStaffCalendar> staffCalendars = null;
        ArrayList<EntityEquipmentCalendar> equipmentCalendars = null;

        HashMap<Date, ArrayList<Date>> switchTimePerDay;
        HashMap<Date, ArrayList<Double>> switchValuePerDay;
        // 人员
        if (resourceType.equals(RESOURCE_TYPE_ENGINEER) || resourceType.equals(RESOURCE_TYPE_TECHNICIAN)){
            EntityStaff staff = (EntityStaff) resource;
            switchTimePerDay = staff.getSwitchTimePerDay();
            switchValuePerDay = staff.getSwitchValuePerDay();
            staffCalendars = staff.getStaffCalendar();
            if (staffCalendars.size() == 0){
                // todo: 待确认是否staff可用不设置calendar？
                log.info(staff.getStaffId()+" has no calendar");
            } else {
                EntityStaffCalendar staffCalendar = staffCalendars.get(0);
                mode = staffCalendar.getStaffMode();
                calendarId = staffCalendar.getCalendarId();
                calendarClass = staffCalendar.getCalendarClass();
            }
        // 设备
        }else{
            EntityEquipment equipment = (EntityEquipment) resource;

            switchTimePerDay = equipment.getSwitchTimePerDaySpecialDayNotWork();
            switchValuePerDay = equipment.getSwitchValuePerDaySpecialDayNotWork();
            equipmentCalendars = equipment.getEquipmentCalendar();

            if (equipmentCalendars.size() > 0 ){
                // 如果设置了日历
                EntityEquipmentCalendar equipmentCalendar = equipmentCalendars.get(0);
                mode = equipmentCalendar.getEquipmentMode();
                calendarId = equipmentCalendar.getCalendarId();
                calendarClass = equipmentCalendar.getCalendarClass();
            }
        }

        // 常规日历
        if (calendarClass.equals(CALENDAR_CLASS_NORMAL)) {
            EntityCalendar calendar = engineProcessedData.getCalendarHashMap().get(calendarId);
            if (mode.equals(MODE_SPECIAL_DAY_NOT_ROLL)){
                switchTimePerDay.putAll(calendar.getSwitchTimePerDaySpecialDayNotWork());
                switchValuePerDay.putAll(calendar.getSwitchValuePerDaySpecialDayNotWork());
            }else if (mode.equals(MODE_SPECIAL_DAY_ROLL)){
                switchTimePerDay.putAll(calendar.getSwitchTimePerDaySpecialDayWork());
                switchValuePerDay.putAll(calendar.getSwitchValuePerDaySpecialDayWork());
            }
        // 翻班日历
        }else if (calendarClass.equals(CALENDAR_CLASS_ROLL)){
            // 该 staff/equipment 用到的所有的翻班日历
            ArrayList<EntityRollCalendar> allRollCalendars = new ArrayList<>();
            if (resourceType.equals(RESOURCE_TYPE_EQUIPMENT)){
                for (EntityEquipmentCalendar equipmentCalendar : equipmentCalendars) {
                    ArrayList<EntityRollCalendar> rollCalendars = engineProcessedData.getRollCalendarHashMap().get(equipmentCalendar.getCalendarId());
                    allRollCalendars.addAll(rollCalendars);
                }
            }else {
                for (EntityStaffCalendar staffCalendar : staffCalendars) {
                    ArrayList<EntityRollCalendar> rollCalendars = engineProcessedData.getRollCalendarHashMap().get(staffCalendar.getCalendarId());
                    allRollCalendars.addAll(rollCalendars);
                }
            }
            // 创建 rollCalendar 集合考虑日历的重复次数
            ArrayList<EntityRollCalendar> rollCalendars = calculateAllRollCalendars(allRollCalendars);
            int rollDaySize = rollCalendars.size();

            Calendar tempTime = DateUtil.dateToCalendar(minPlanStartTime);
            HashMap<Date, EntitySpecialDay> specialDayHashMap = engineProcessedData.getSpecialDayHashMap();
            int i = 0;
            while (tempTime.before(maxPlanEndTime)){

                ArrayList<Date> times = new ArrayList<>();
                ArrayList<Double> values = new ArrayList<>();
                // 添加开始时间
                times.add(tempTime.getTime());
                values.add(NOT_WORK_VALUE);

                EntityRollCalendar rollCalendar = rollCalendars.get(i);

                // 每天切换的工作时间点
                String[] shiftStart = null;
                String shiftStartStr = rollCalendar.getShiftStart();
                if (shiftStartStr != null){
                    shiftStart = shiftStartStr.split(":");
                }

                String[] breakStart = null;
                String breakStartStr = rollCalendar.getBreakStart();
                if (breakStartStr != null){
                    breakStart = breakStartStr.split(":");
                }

                String[] breakEnd = null;
                String breakEndStr = rollCalendar.getBreakEnd();
                if (breakEndStr != null){
                    breakEnd = breakEndStr.split(":");
                }


                String[] supperBreakStart = null;
                String supperBreakStartStr = rollCalendar.getSupperBreakStart();
                if (supperBreakStartStr != null){
                    supperBreakStart = supperBreakStartStr.split(":");
                }

                String[] supperBreakEnd = null;
                String supperBreakEndStr = rollCalendar.getSupperBreakStart();
                if (supperBreakEndStr != null){
                    supperBreakEnd = supperBreakStartStr.split(":");
                }

                String[] shiftEnd = null;
                String shiftEndStr = rollCalendar.getShiftEnd();
                if (shiftEndStr != null){
                    shiftEnd = shiftEndStr.split(":");
                }


//                // 是节假日
//                if (specialDayHashMap.containsKey(tempTime.getTime())){
//                    String workDayType = specialDayHashMap.get(tempTime.getTime()).getWorkDayType();
//                    // 周末工作日
//                    if (workDayType.equals(SPECIAL_DAY_WEEKEND_WORK)){
//                        engineProcessedData.calculateCalendarDayTimesAndValues(tempTime,
//                                times, values,
//                                shiftStart, breakStart, breakEnd, supperBreakStart,supperBreakEnd, shiftEnd);
//                    }
//                    // 休息
//                    // 判断节假日是否翻班
//                    if (mode.equals(MODE_SPECIAL_DAY_ROLL)){
//                        // 对应翻班日历集合的下标，当天日历被覆盖
//                        i = (i + 1) % rollDaySize;
//                    }
//                // 不是节假日
//                }else {
//                    // 工作
//                    if (rollCalendar.getCalendarType().equals(CALENDAR_TYPE_WORK)){
//                        engineProcessedData.calculateCalendarDayTimesAndValues(tempTime,
//                                times, values,
//                                shiftStart, breakStart, breakEnd, supperBreakStart,supperBreakEnd, shiftEnd);
//                    }
//                    // 休息
//                    // 对应翻班日历集合的下标
//                    i = (i + 1) % rollDaySize;
//                }
                // 是节假日
                if (mode.equals(MODE_SPECIAL_DAY_NOT_ROLL) && specialDayHashMap.containsKey(tempTime.getTime())) {
                    String workDayType = specialDayHashMap.get(tempTime.getTime()).getWorkDayType();
                    // 周末工作日
                    if (workDayType.equals(SPECIAL_DAY_WEEKEND_WORK)) {
                        engineProcessedData.calculateCalendarDayTimesAndValues(tempTime,
                                times, values,
                                shiftStart, breakStart, breakEnd, supperBreakStart, supperBreakEnd, shiftEnd);
                    }
                }else {
                    // 工作
                    if (rollCalendar.getCalendarType().equals(CALENDAR_TYPE_WORK)){
                        engineProcessedData.calculateCalendarDayTimesAndValues(tempTime,
                                times, values,
                                shiftStart, breakStart, breakEnd, supperBreakStart,supperBreakEnd, shiftEnd);
                    }
                    // 休息
                    // 对应翻班日历集合的下标
                    i = (i + 1) % rollDaySize;
                }


                // 获取下一天的开始
                Date nextDay = DateUtil.getNextDay(tempTime.getTime());
                times.add(nextDay);

                tempTime.setTime(nextDay);

                switchTimePerDay.put(times.get(0), times);
                switchValuePerDay.put(times.get(0), values);

                log.info("switchTimePerDay：" + switchTimePerDay.get(times.get(0)).size());
                log.info("switchValuePerDay：" + switchValuePerDay.get(times.get(0)).size());
            }
        } else{
            // todo:这个函数好像其实没有用了
        }
        log.info("switchTimePerDay：" + switchTimePerDay);
        log.info("switchValuePerDay：" + switchValuePerDay);
    }
    */

     private void calculateEndTime() {
        //todo: 是否使用考虑了提前排程量之后的时间作为开始时间
        /*
        Date startTime = engineProcessedData.getPlan().getPlanPeriodStartTime();
        endTime = DateUtil.addDay(startTime, maxCalendarDays);
        endIntTime = (int) Math.ceil(maxCalendarDays * 24 * 60 /(double)planGranularity);
         */
        endIntTime = (int) Math.ceil(((double)(maxPlanEndTime.getTime() - minPlanStartTime.getTime())) / (1000*60) / planGranularity);
        log.info("Sub model name " + subModelName + ", start time is " + DateUtil.getDateString(minPlanStartTime)
                + ", end time is " + DateUtil.getDateString(maxPlanEndTime));
    }

    /**
     * 初始化 子模型的所有的工作和不工作的切换时刻
     * todo: 加工作日和周末工作模式
     */
    private void initializeStepFunctionWorkdayMode(){

        // 计算子模型的两种工作模式
        executeStepFunctionWorkdayMode(STEP_WEEKDAYS_WORK);
        executeStepFunctionWorkdayMode(STEP_WEEKDAYS_WEEKEND_WORK);
    }

    /**
     * 计算出子阶段中工作和不工作的切换时刻，和是否工作对应的数值
     * @param stepWorkMode
     */
    private void executeStepFunctionWorkdayMode(String stepWorkMode){
        ArrayList<Date> workdaysModeTime = engineProcessedData.getWorkdaysModeTimes().get(stepWorkMode);
        ArrayList<Integer> workdaysModeValue = engineProcessedData.getWorkdaysModeValues().get(stepWorkMode);
        //添加开始时间
        ArrayList<Double> stepFunctionWorkdayModeTime = new ArrayList<>();
        ArrayList<Double> stepFunctionWorkdayModeValue = new ArrayList<>();
        stepFunctionWorkdayModeTime.add((double) 0);
        // todo: 确认计算逻辑
        // 遍历主模型中的 workdaysModeTime ，向子模型中添加
        int i = 0;
        Date date = null;
        for (; i < workdaysModeTime.size(); i++) {
             date = workdaysModeTime.get(i);
            if (date.after(minPlanStartTime) && date.before(maxPlanEndTime)){
                // 计算出数字时间
//                double modeTime = (double)(date.getTime() - minPlanStartTime.getTime())/(1000*60)/planGranularity;
//                double modeTime = (date.getTime() - minPlanStartTime.getTime())/(1000*60)/planGranularity;

                double modeTime = Math.ceil((double) (date.getTime() - minPlanStartTime.getTime())/(1000*60)/planGranularity);
                stepFunctionWorkdayModeTime.add(modeTime);
                stepFunctionWorkdayModeValue.add((double)workdaysModeValue.get(i) * 100);
            }
        }

        // value 中最后一个数的添加
        if (maxPlanEndTime.equals(date)){
            i--;
        }
        stepFunctionWorkdayModeValue.add((double)workdaysModeValue.get(i) * 100);

        double maxEnd = (maxPlanEndTime.getTime() - minPlanStartTime.getTime())/(1000*60)/planGranularity;
        stepFunctionWorkdayModeTime.add(maxEnd);

        // 将计算出的结果写入到 map 中
        if (STEP_WEEKDAYS_WORK.equals(stepWorkMode)){
            stepFunctionStepTime.put(STEP_WEEKDAYS_WORK, stepFunctionWorkdayModeTime);
            stepFunctionStepValue.put(STEP_WEEKDAYS_WORK, stepFunctionWorkdayModeValue);
            log.info("仅工作日的stepFunctionWorkdayModeTime:" + stepFunctionWorkdayModeTime);
            log.info("仅工作日的stepFunctionWorkdayModeValue:" + stepFunctionWorkdayModeValue);
        }else if (STEP_WEEKDAYS_WEEKEND_WORK.equals(stepWorkMode)){
            stepFunctionStepTime.put(STEP_WEEKDAYS_WEEKEND_WORK, stepFunctionWorkdayModeTime);
            stepFunctionStepValue.put(STEP_WEEKDAYS_WEEKEND_WORK, stepFunctionWorkdayModeValue);
            log.info("工作日和周末工作的stepFunctionWorkdayModeTime:" + stepFunctionWorkdayModeTime);
            log.info("工作日和周末工作的stepFunctionWorkdayModeValue:" + stepFunctionWorkdayModeValue);
        }
    }


    /**
     * 计算每个人员在每个子模型中有多少已经固定的工作时间，
     */
    // todo: 找数据进行测试
    private void calculateTotalFixedTime(String staffId, String resourceType){
        //获得Staff_Input和Staff_Output表
        HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanInputHashMap = engineProcessedData.getStaffStaffPlanInputHashMap();
        HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanOutputHashMap = engineProcessedData.getStaffStaffPlanOutputHashMap();

        // 计算 fixed 人员的需求时间之和
        ArrayList<EntityStaffPlan> staffPlans = staffStaffPlanInputHashMap.get(staffId);
        ArrayList<EntityStaffPlan> staffPlans1 = staffStaffPlanOutputHashMap.get(staffId);
        ArrayList<EntityStaffPlan> allStaffPlans = new ArrayList<>();
        if (staffPlans != null){
            allStaffPlans.addAll(staffPlans);
        }
        if (staffPlans1 != null){
            allStaffPlans.addAll(staffPlans1);
        }

        // 将所有的 fixed 的时间相加求和
        Double totalConstraintTime = 0.0;
        Double totalNonConstraintTime = 0.0;
        for (EntityStaffPlan staffPlan : allStaffPlans) {
            if (staffPlan.getIsConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT)) {
                totalConstraintTime += calculateStaffPlanFixedWorkTime(staffPlan);
            } else {
                totalNonConstraintTime += calculateStaffPlanFixedWorkTime(staffPlan);
            }
        }
        if (totalConstraintTime > 0) {
            // original time should be 0, todo check
            Double originalTime = totalFixedTimePerStaff.computeIfAbsent(staffId, k -> 0.0);
            totalFixedTimePerStaff.put(staffId, originalTime + totalConstraintTime);
        }
        if (totalNonConstraintTime >0 ) {
            // original time should be 0, todo check
            if (resourceType.equals(RESOURCE_TYPE_ENGINEER)){
                totalNonConstraintTime = totalNonConstraintTime * ENGINEER_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO;
            } else {
                totalNonConstraintTime = totalNonConstraintTime * TECHNICIAN_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO;
            }
            Double originalTime = totalFixedTimePerStaff.computeIfAbsent(staffId, k -> 0.0);
            totalFixedTimePerStaff.put(staffId, originalTime + totalNonConstraintTime);
        }
    }

    /**
     * 在函数calculateTotalFixedTime中调用
     * @param staffPlan
     * @return
     */
    //todo: changed to read from plan work time field
    private Double calculateStaffPlanFixedWorkTime(EntityStaffPlan staffPlan) {
        double fixedWorkTime = 0;
        // 根据 subModel 的开始结束时间，对人员 fixed 的时间进行截取
        Date staffPlanStart = staffPlan.getStaffStartInModel();
        if (staffPlanStart.before(minPlanStartTime)){
            staffPlanStart = minPlanStartTime;
        }
        Date staffPlanEnd = staffPlan.getStaffEndInModel();
        if (staffPlanEnd.after(maxPlanEndTime)){
            staffPlanEnd = maxPlanEndTime;
        }

        if (staffPlanStart != null && staffPlanEnd != null){
            fixedWorkTime= Math.ceil((double) (staffPlanEnd.getTime() - staffPlanStart.getTime())
                    / (1000 * 60) / planGranularity);
        }
        BigDecimal planWorkTime = staffPlan.getPlanWorkTime();
        if (planWorkTime != null){
            if (fixedWorkTime > planWorkTime.doubleValue()){
                fixedWorkTime = planWorkTime.doubleValue();
            }
        }
        return fixedWorkTime;
    }

    /**
     * 每种人员资源（工程师、技师）在每个子模型中平均需要多少工作时间
     */
    private void calculateAverageExpectedUsagePerResourceType(){
        Double engineerTotalTime = totalWorkTime.get(RESOURCE_TYPE_ENGINEER);
        Double technicianTotalTime = totalWorkTime.get(RESOURCE_TYPE_TECHNICIAN);

        Integer engineerTotalCount = 0;
        if (totalPossibleResourceQuantity.get(RESOURCE_TYPE_ENGINEER) != null){
             engineerTotalCount = totalPossibleResourceQuantity.get(RESOURCE_TYPE_ENGINEER).size();
        }
        Integer technicianTotalCount = 0;
        if (totalPossibleResourceQuantity.get(RESOURCE_TYPE_TECHNICIAN) != null){
           technicianTotalCount = totalPossibleResourceQuantity.get(RESOURCE_TYPE_TECHNICIAN).size();
        }

        // 分别计算 engineer 和 technician 的平均值
        if (engineerTotalCount == null || engineerTotalCount == 0){
            averageExpectedUsagePerResourceType.put(RESOURCE_TYPE_ENGINEER, 0.0);
        }else {
            averageExpectedUsagePerResourceType.put(RESOURCE_TYPE_ENGINEER, engineerTotalTime / engineerTotalCount);
        }

        if (technicianTotalCount == null || technicianTotalCount == 0){
            averageExpectedUsagePerResourceType.put(RESOURCE_TYPE_TECHNICIAN, 0.0);
        }else {
            averageExpectedUsagePerResourceType.put(RESOURCE_TYPE_TECHNICIAN, technicianTotalTime/ technicianTotalCount);
        }

        log.info("每个人员的已固定的时长（totalFixedTimePerStaff）是：" + totalFixedTimePerStaff);
        log.info("每个人员的平均分配时长（averageExpectedUsagePerResourceType）为" + averageExpectedUsagePerResourceType);
    }
    /*
    //只以颗粒度来划分模型的话，暂时不需要这个函数了
    private void splitSubModelName(String subModelName) {

         //之前的生成方法：在EngineProcessedData中
         //         String.format("%05d", planGranularity)
         //                        +"_"+String.format("%03d", calendarDays)
         //                        +"_"+String.format("%04d", planTimeLimit);

        String[] nameSplit = subModelName.split("_");
        planGranularity = Integer.parseInt(nameSplit[0]);
        calendarDays = Integer.parseInt(nameSplit[1]);
        timeLimit = Integer.parseInt(nameSplit[2]);
        log.info("Sub model "+ subModelName);
        log.info("Plan granularity is " + planGranularity + "; calendar days is " + calendarDays
                + "; time limit is " + timeLimit);
    }
    */


     /*
    private void addStepPossibleTechnicians(EntityStep step){
        // 为小阶段添加技师相关资源
        EntityResourceGroup resourceGroup = step.getResourceGroupHashMap().get(RESOURCE_TYPE_TECHNICIAN);
        if (resourceGroup != null){
            String index = step.getBomNo() + step.getStepId();
            EntityStepSkill stepSkill = engineProcessedData.getStepSkillHashMap().get(index);
            // todo：确定step 没有对应的 stepSkill 情况
            if (stepSkill != null){
                // skillIdSet 为空时
                String skillIdSet = stepSkill.getSkillIdSet();
                if (skillIdSet == null || skillIdSet == ""){
                    Collection<EntityStaff> technicians = engineProcessedData.getTechnicianHashMap().values();
                    resourceGroup.getPossibleResources().addAll(technicians);
//                    // 将可选技师添加到所有可能的人员列表中
//                    possibleStaffs.addAll(technicians);
                // skillIdSet 不为空时
                }else {
                    String[] skillIds = skillIdSet.split(",");
                    // 获得掌握所有 skillIds 技能的技师 Id
                    ArrayList<String> reTechnicianIds = null;
                    for (int i = 0; i < skillIds.length; i++) {
                        ArrayList<EntityStaff> technicianIds = engineProcessedData.getSkillTechnicianHashMap().get(skillIds[i]);
                        if (i == 0){
                            reTechnicianIds = technicianIds;
                        }
                        reTechnicianIds.retainAll(technicianIds);
                    }
                    // 将该这些工程师添加到 step 技师的资源组中
                    for (String technicianId : reTechnicianIds) {
                        EntityStaff technician = engineProcessedData.getTechnicianHashMap().get(technicianId);
                        resourceGroup.getPossibleResources().add(technician);
//                        // 将可选技师添加到所有可能的人员列表中
//                        possibleStaffs.add(technician);
                    }
                }
            }
        }
    }
    private void addStepPossibleEngineers(EntityStep step, EntityBom bom){
        ArrayList<EntityStaff> authEngineersConsideredBOM = step.getAuthEngineersConsideredBOM();
        int stepSize = authEngineersConsideredBOM.size();
        // 考虑有BOM授权，没有小阶段授权和既没有 bom 也没有小阶段授权的情况
        if (stepSize == 0){
            int bomSize = bom.getAuthEngineersConsideredProcedure().size();
            if (bomSize > 0){
                authEngineersConsideredBOM = bom.getAuthEngineersConsideredProcedure();
            }else {
                EntityProcedure procedure = engineProcessedData.getProcedureHashMap().get(bom.getProcedureNo());
                authEngineersConsideredBOM = procedure.getAuthEngineers();
            }
        }
        // 向模型需要的资源中添加
        EntityResourceGroup resourceGroup = step.getResourceGroupHashMap().get(DEFAULT_ENGINEER_RESOURCE_GROUP_NAME);
        ArrayList<Object> possibleResources = resourceGroup.getPossibleResources();
        possibleResources.addAll(authEngineersConsideredBOM);

//        // 将可选工程师添加到所有可能的人员列表中
//        possibleStaffs.addAll(authEngineersConsideredBOM);
    }*/
    public void setIntervalVarMaxLength(int size, IloIntervalVar variable) {
        if (size <= LENGTH_NEAR_TO_SIZE_LIMIT * 60 / planGranularity) {
            variable.setLengthMax(size + LENGTH_MAX_INCREASE_WHEN_NEAR_TO_SIZE_LIMIT * 60 / planGranularity);
        } else {
            variable.setLengthMax(variable.getEndMax()- variable.getStartMin());
        }
    }

}
