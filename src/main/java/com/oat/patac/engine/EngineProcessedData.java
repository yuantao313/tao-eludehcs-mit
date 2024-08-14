package com.oat.patac.engine;

import com.alibaba.druid.util.StringUtils;
import com.oat.common.exception.PatacException;
import com.oat.common.utils.CheckDataUtil;
import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.DateUtil;
import com.oat.patac.dataAccess.DataContainer;
import com.oat.patac.dataAccess.LoadData;
import com.oat.patac.entity.*;
import lombok.Data;

import lombok.extern.log4j.Log4j2;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;
import static java.util.stream.Collectors.toList;

/**
 * @author:yhl
 * @create: 2022-08-17 09:03
 *  @Description: 数据处理类， 进行数据验证后，将数据存储到 HashMap 中
 * 数据验证：
 * （1） 单实体的数据验证
 * （2） 跨实体的数据验证
 */
@Log4j2
@Data
public class EngineProcessedData {

    PatacSchedulingTask schedulingTask;

    /**
     * 是否进行排程的标志
     */
    private Boolean schedulingFlag = true;

    private LoadData loadData;
    /**
     * 存放所有数据的容器
     */
    // private final DataContainer dataContainer;
    private DataContainer dataContainer;
    private EntityPlan plan;
    private Integer functionId;

    // 需要排程的批次。
    // 老模式：对于自动基础排程模式，为待排程任务单下的所有有效批次；对于手动触发排程模式，为排程调用的时候传入的数据
    // 新模式：sub task表中的有效批次即为有效批次，所以这个不需要了
    private ArrayList<String> toBePlannedSubTaskNos;

    private HashMap<String, SubModel> subModelHashMap = new HashMap<>();

    // 包含整车的样品组，以及样品组内的整车编号
    private HashMap<Integer, String> sampleGroupUniqueSampleNoMap = new HashMap<>();
    // 每个不能冲突的样品需要支持的批次，注意map的key不是sampleId，而是sampleNo
    private HashMap<String, ArrayList<EntitySubTask>> uniqueSampleSupportedSubtasks = new HashMap<>();
    private HashMap<String, ArrayList<EntitySubTaskPlan>> uniqueSampleSupportedSubtaskPlans = new HashMap<>();

    // 存储所有的 message 信息
    public static ArrayList<EntityMessage> messages = new ArrayList<>();
    /*
    // 解决 list 写入时的线程安全问题
    public static List<EntityMessage> messages = Collections.synchronizedList(new ArrayList<>());
     */

    private HashMap<Integer, EntityTestArea> testAreaHashMap = new HashMap<>();
    private HashMap<Integer, EntityLaboratory> laboratoryHashMap = new HashMap<>();
    private HashMap<Integer, EntityFunction> functionHashMap = new HashMap<>();
    // 任务单集合
    private HashMap<String, EntityTask> taskHashMap = new HashMap<>();
    private HashMap<String, EntitySubTask> subTaskHashMap = new HashMap<>();
    private HashMap<String, EntityProcedure> procedureHashMap = new HashMap<>();
    /**
     * 规范授权 (procedureId, list(人员Id))
     */
    private HashMap<String, ArrayList<String>> authProcedureHashMap = new HashMap<>();
    private HashMap<String, EntityBom> bomHashMap = new HashMap<>();
    /**
     * bom 授权 (bomId, list(人员Id))
     */
    private HashMap<String, ArrayList<String>> authBomHashMap = new HashMap<>();
    private HashMap<String , EntityStep> stepHashMap = new HashMap<>();
    /**
     * 小阶段授权 (stepId, list(人员Id))
     */
    private HashMap<String, ArrayList<String>> authStepHashMap = new HashMap<>();
    /**
     * 小阶段技能的需求信息(bomId + stepId + skillIdSet, stepSkill)
     * 如果skillIdSet是null，则用空字符串代替skillIdSet
     */
    private HashMap<String, EntityStepSkill> stepSkillHashMap = new HashMap<>();
    private HashMap<String, EntityStepEquipmentGroup> stepEquipmentGroupHashMap = new HashMap<>();
    private HashMap<String, EntityStaff> staffHashMap = new HashMap<>();
    private HashMap<String, EntityStaff> initialStaffHashMap = new HashMap<>();

    private HashMap<String, EntityStaff> engineerHashMap = new HashMap<>();
    private HashSet<String> noCalendarStaffSet = new HashSet<>();
    private HashMap<String, EntityStaff> technicianHashMap = new HashMap<>();
//    private HashMap<String, EntityStaffCalendar> staffCalendarHashMap = new HashMap<>();
    /**
    key是staffID
     */
    private HashMap<String, ArrayList<EntityStaffCalendar>> staffCalendarHashMap = new HashMap<>();
    private HashMap<String, EntitySkill> skillHashMap = new HashMap<>();
    /**
     * 技师具备技能(staffId, ArrayList(skillId))
     */
    private HashMap<String, ArrayList<String>> technicianSkillHashMap = new HashMap<>();
    /**
     * 技师具备技能(skillId, ArrayList(staffId))
     */
    private HashMap<String, ArrayList<EntityStaff>> skillTechnicianHashMap = new HashMap<>();
    /**
     * 所有有可能使用的staff
     */
    private HashSet<String> allPossibleStaffIds = new HashSet<>();
    private HashSet<EntityStaff> allPossibleStaffs = new HashSet<>();

    private HashMap<Integer, EntityCalendar> calendarHashMap = new HashMap<>();
    private HashMap<Integer, ArrayList<EntityRollCalendar>> rollCalendarHashMap = new HashMap<>();

    // 特殊日历
    private HashMap<Date, EntitySpecialDay> specialDayHashMap = new HashMap<>();

    private HashMap<Integer, EntityEquipmentGroup> equipmentGroupHashMap = new HashMap<>();
    private HashMap<String, EntityEquipment> equipmentHashMap = new HashMap<>();
    /**
     * 设备组和设备的关系(equipmentGroupId, list(equipmentId))
     */
    private HashMap<Integer, ArrayList<String>> equipmentGroupRelHashMap = new HashMap<>();
    private HashMap<String, EntityEquipmentGroupRel> equipmentGroupRelByEquipmentIdHashMap = new HashMap<>();
    /**
     *  将设备按设备组的 id 进行分组
     */
    private HashMap<String, ArrayList<EntityEquipment>> equipmentByEquipmentGroupIdHashMap = new HashMap<>();
    private HashMap<String, EntityEquipmentCalendar> equipmentCalendarHashMap= new HashMap<>();
    //private HashMap<Integer, EntitySample> sampleHashMap = new HashMap<>();
    /**
     * 样品组和组内样件实例的列表
     */
    private HashMap<Integer, ArrayList<EntitySample>> sampleGroupSamplesHashMap = new HashMap<>();
    private HashMap<Integer, EntityPlanConfig> planConfigHashMap = new HashMap<>();
    private HashMap<String, EntityTaskPlanInput> taskPlanInputHashMap = new HashMap<>();
    private HashMap<String, EntitySubTaskPlanInput> subTaskPlanInputHashMap = new HashMap<>();
    private HashMap<String, EntitySubTaskPlanInput> subTaskPlanInputForSubTaskGroupHashMap = new HashMap<>();
    private HashMap<String, EntitySubTaskPlanInput>  subTaskPlanInputForSampleHashMap = new HashMap<>();
    /**
     * 批次组的hashmap，key是批次组的名字，array list是批次组内的批次，包括待排程的和fixed
     */
    private HashMap<String , ArrayList<EntitySubTask>> subTaskGroupHashMap = new HashMap<>();

    private HashMap<String, EntityStepPlanInput> stepPlanInputHashMap = new HashMap<>();
    //haolei todo: 如果有staffStaffPlanInputHashMap，不用staffPlanInputHashMap？
    private HashMap<String, EntityEquipmentPlanInput> equipmentPlanInputHashMap = new HashMap<>();
    // key是equipment_ID, list是equipment_plan_input或output
    private HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanInputHashMap = new HashMap<>();
    private HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = new HashMap<>();
    //haolei todo: 如果有staffStaffPlanInputHashMap，不用staffPlanInputHashMap？
    private HashMap<String, EntityStaffPlanInput> staffPlanInputHashMap = new HashMap<>();
    // key是staff_ID, list是staff_plan_input或output
    private HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanInputHashMap = new HashMap<>();
    private HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanOutputHashMap = new HashMap<>();
    // 所有设备状态的字符串集合
    private HashSet<String> equipmentStates = new HashSet<>();
    // 设备状态从字符串到整数的映射关系
    private HashMap<String, Integer> equipmentStateStringToIntMap = new HashMap<>();
    /**
     * 整个计划周期内的所有的工作和不工作的切换时刻。
     * 是为在子模型中设置小阶段的intensity function准备的
     * 比如9.15 00：00开始排程至10.15 00：00结束。则数组中的内容是：
     * 9.17 00：00， 9.19 00：00，
     * 9.24 00：00，9：26 00：00，
     * 10.1 00:00, 10.8 00:00,
     * 10.15 00：00
     * 注意，周末和特殊日期中的非周末休息日是不工作的，非周末和特殊日期中的周末工作日是工作的。
     */
//    private ArrayList<Date> workdaysModeTime = new ArrayList<>();
    private HashMap<String, ArrayList<Date>> workdaysModeTimes = new HashMap<>();
    /**
     * 和“整个计划周期内的所有的工作和不工作的切换时刻”，对应的是否工作的数值
     * 是为在子模型中设置小阶段的intensity function准备的
     * 比如9.15 00：00开始排程至10.15 00：00结束。则数组中的内容是：
     * 1，0，1，
     * 0，1，
     * 0，1，
     * 0
     * 注意：该数组中的长度是workdays_mode_time的长度+1
     */
//    private ArrayList<Integer> workdaysModeValue = new ArrayList<>();
    private HashMap<String, ArrayList<Integer>> workdaysModeValues = new HashMap<>();

    /**
     * 输出数据
     */
    private HashMap<String, EntityTaskPlanOutput> taskPlanOutputHashMap = new HashMap<>();
    private HashMap<String, EntitySubTaskPlanOutput> subTaskPlanOutputHashMap = new HashMap<>();
    private HashMap<String, EntityStepPlanOutput> stepPlanOutputHashMap = new HashMap<>();

    private HashMap<String, EntityEquipmentPlanOutput> equipmentPlanOutputHashMap = new HashMap<>();
    private HashMap<String, EntityStaffPlanOutput> staffPlanOutputHashMap = new HashMap<>();

    /**
     * 工程师和技术总数
     */
    private Integer engineerTotal;
    private Integer technicianTotal;
//    private HashSet<String> skillTypeSet;

    // 获取所有小阶段需要的技能集合
    private HashSet<String> allSkillSet = new HashSet<>();
    // 所有需要人员 Id 集合
    private HashSet<String> allStaffIdSet = new HashSet<>();
    // 所有人员和设备所需要的正常日历和翻班日历 Id 集合
    private HashSet<Integer> allCalendarIdSet = new HashSet<>();
    private HashSet<Integer>  allRollCalendarIdSet = new HashSet<>();

    // 设备状态的 map
    public static final HashMap<String, Integer> stateMap = new HashMap<>();

    // BOM 对应的所有的 task
    public static final HashMap<String, HashSet<EntityTask>> bomSameTaskMap = new HashMap<>();
    // 无效BOM的集合
    private HashSet<String> invalidBom = new HashSet<>();

    private ArrayList<String> taskNos;

    int solveMode;
    /**
     * 内部批次已经按批次组顺序排序的批次组hash map，key是批次组名字；
     * list里面只有要排程的subtask，并随着数据验证不断remove掉不能排程的subtask
     */
    HashMap<String, ArrayList<EntitySubTask>> sortedSubTaskGroupHashMap = new HashMap<>();
    /**
     * 排程任务中包含的granularity，key是granularity，list是用这个granularity的subtasks
     */
     HashMap<Integer, ArrayList<EntitySubTask>> granularitySubTaskHashMap = new HashMap<>();
    /**
     * 批次组模式下的子模型列表
     */
     //HashMap<String, SubModel> subModelHashMapInSubTaskGroupMode = new HashMap<>();

    // 构造方法
    public EngineProcessedData(LoadData loadData,PatacSchedulingTask task, ArrayList<String> taskNos, int solveMode){

        this.loadData = loadData;

        this.schedulingTask = task;

        this.dataContainer = task.getDataContainer();

        functionId = dataContainer.getPlan().getFunctionId();
        plan = dataContainer.getPlan();

        this.taskNos = taskNos;
        this.solveMode = solveMode;

    }

    /**
     * EngineProcessedData 的初始化
     */
    @Deprecated
    public boolean initialize() throws PatacException, ParseException {
        // 单实体的验证
        initializeMasterData();
        // 跨实体的验证
        checkMasterData();

        // 初始化工作和不工作的切换时刻
        initializeWorkdaysMode();

        // 用于记录数据是是否正确
        boolean flag = true;

        /* Change to get functionId from constructor - by Sophia
        // 获取统一的 functionId
        Set<Integer> keySet = functionHashMap.keySet();
        Integer functionId = null;
        if (keySet.size() != 0){
            functionId = keySet.iterator().next();
        }
        */

        // 遍历所有 message，给所有的 message 添加 functionId
        if (messages.size() != 0){
            for (EntityMessage msg : messages){
                msg.setPlanId(plan.getPlanId());
                msg.setFunctionId(functionId);
                // 判断 message 的信息严重程度，是否有 ERROR 类型
                if (StringUtils.equals(msg.getMessageSeverity(), ConstantUtil.MESSAGE_SEVERITY_ERROR)){
                    flag = false;
                }
            }

            // （系统运行时）
            // 将所有的 message 写入数据库
//            messageMapper.insertAllMessage(messages);
            /*
            int i = messageMapper.insertAllMessage(EngineProcessedData.messages);
            if (i > 0){
                EngineProcessedData.messages.clear();
            }
             */
//            log.info(messages);

        }

        // 判断数据是否正确，错误抛出异常
//        if (!flag){
//            log.error(ConstantUtil.ERROR_MESSAGE);
//            throw new PatacException(ConstantUtil.ERROR_MESSAGE);
//        }

        return flag;
    }

    /**
     * 单实体的数据验证
     * 将数据从 DataContainer 的 ArrayList 中取出，
     * 进行单个实体数据验证，验证成功后存储到 HashMap 中
     */
    @Deprecated
    private void initializeMasterData() throws ParseException {

        // 验证所有 TestArea， 并将数据存放到 hashmap 中
        Iterator<EntityTestArea> testAreaIterator = dataContainer.getTestAreaIterator();
        while (testAreaIterator.hasNext()){
            EntityTestArea testArea = testAreaIterator.next();
            // 单实体验证
            testArea.singleCheck();
            testAreaHashMap.put(testArea.getTestAreaId(), testArea);
        }

        //  验证所有 lab， 并将数据存放到 hashmap 中
        Iterator<EntityLaboratory> laboratoryIterator = dataContainer.getLaboratoryIterator();
        while (laboratoryIterator.hasNext()){
            EntityLaboratory laboratory = laboratoryIterator.next();
            // 单实体验证
            laboratory.singleCheck();
            laboratoryHashMap.put(laboratory.getLabId(), laboratory);
        }

        // 验证所有 Function， 并将数据存放到 hashmap 中
        Iterator<EntityFunction> functionIterator = dataContainer.getFunctionIterator();
        while (functionIterator.hasNext()){
            EntityFunction function = functionIterator.next();
            // 单实体验证
            function.singleCheck();
            functionHashMap.put(function.getFunctionId(), function);
        }

        EntityFunction function = functionHashMap.get(functionId);
        /*
        //todo: 等有数据了去掉注释
        // 验证所有 planConfig ， 并将数据存放到 hashmap 中
        Iterator<EntityPlanConfig> planConfigIterator = dataContainer.getPlanConfigIterator();
        while (planConfigIterator.hasNext()){
            EntityPlanConfig planConfig = planConfigIterator.next();
            // 单实体验证
            if (planConfig.singleCheck()) {
                planConfigHashMap.put(planConfig.getPlanConfigId(), planConfig);
            }
        }
         */

        // 验证所有 Procedure， 并将数据存放到 hashmap 中
        Iterator<EntityProcedure> procedureIterator = dataContainer.getProcedureIterator();
        while (procedureIterator.hasNext()){
            EntityProcedure procedure = procedureIterator.next();
            // 单实体验证
            if (procedure.singleCheck()) {
                procedureHashMap.put(procedure.getProcedureNo(), procedure);
            }
        }

        // 验证所有 bom ， 并将数据存放到 hashmap 中
        Iterator<EntityBom> bomIterator = dataContainer.getBOMIterator();
        while (bomIterator.hasNext()){
            EntityBom bom = bomIterator.next();
            // 单实体验证
            if (bom.singleCheck()) {
                // 在规范中添加 bomId
                EntityProcedure procedure = procedureHashMap.get(bom.getProcedureNo());
                // procedure里面设置bom的时候需要检查是否只有一个bom
                if (procedure.getBom() != null){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ERROR_NOT_SCHEDULED,
                            "procedure_" + procedure.getProcedureNo() + "中含有多个BOM，分别为BOM" +
                                    procedure.getBom().getBomNo() + "和BOM" + bom.getBomNo());
                }else {
                    procedure.setBom(bom);
                }

                bomHashMap.put(bom.getBomNo(), bom);
            }
        }

        // 验证所有 sample ， 并将数据存放到 sample group和包含的sample list的hashmap 中
        Iterator<EntitySample> sampleIterator = dataContainer.getSampleIterator();
        while (sampleIterator.hasNext()){
            EntitySample sample = sampleIterator.next();
            // 单实体验证
            if (sample.singleCheck()) {
                /* 逻辑改变了：可以等到遍历sub task的时候获得本次排程有关的整车类型的样件列表
                // 将所有包含整车的sampleGroupId 放入对应的 function 中
                if(StringUtils.equals(sample.getSampleType(), "整车")){
                    Integer sampleGroupId = sample.getSampleGroupId();
                    function.getVehicleGroupIds().add(sampleGroupId);
                }
                 */

                // 获取样品组和样品的列表的map
                Integer sampleGroupId = sample.getSampleGroupId();
                if (!sampleGroupSamplesHashMap.containsKey(sampleGroupId)){
                    ArrayList<EntitySample> samples = new ArrayList<>();
                    sampleGroupSamplesHashMap.put(sampleGroupId, samples);
                }
                ArrayList<EntitySample> samples = sampleGroupSamplesHashMap.get(sampleGroupId);
                samples.add(sample);

            }
        }

        // 验证所有 Task， 并将数据存放到 hashmap 中
        Iterator<EntityTask> taskIterator = dataContainer.getTaskIterator();
        ArrayList<EntityTask> toBePlannedTasks = function.getToBePlannedTasks();
        while (taskIterator.hasNext()){
            EntityTask task = taskIterator.next();
            // 单实体验证
            if (task.singleCheck()){
                taskHashMap.put(task.getTaskNo(), task);
                if (plan.getPlanMode() == PLAN_MODE_AUTOMATIC) {
                    //前提：task的HashMap中只保留了“待排程”的任务单
                    toBePlannedTasks.add(task);
                    task.setIsToBePlanned(true);
                }
            }
        }

        // 验证所有 SubTask， 并将数据存放到 hashmap 中
        Iterator<EntitySubTask> subTaskIterator = dataContainer.getSubTaskIterator();
        while (subTaskIterator.hasNext()){
            EntitySubTask subTask = subTaskIterator.next();

            // 调用批次的单实体验证
            if (subTask.singleCheck()){
                // 为了方便跨实体的验证
                // 向该 subTask 的 id 加到对应的 Task 中
                String taskNo = subTask.getTaskNo();
                EntityTask task = taskHashMap.get(taskNo);
                if (task != null){
                    ArrayList<EntitySubTask> subTasks = task.getSubTasks();
                    subTasks.add(subTask);

                    ArrayList<EntitySubTask> toBePlannedSubTasks = task.getToBePlannedSubTasks();

                    // 设置sub task的is_to_be_planned属性
                    if (plan.getPlanMode() == PLAN_MODE_AUTOMATIC) {
                        //前提：task的HashMap中只保留了“待排程”的任务单
                        subTask.setIsToBePlanned(true);
                    } else if (plan.getPlanMode() == PLAN_MODE_MANUAL_TRIGGERED) {
                        if (toBePlannedSubTaskNos.contains(subTask.getSubTaskNo())) {
                            subTask.setIsToBePlanned(true);
                            if (!toBePlannedTasks.contains(task)){
                                toBePlannedTasks.add(task);
                                task.setIsToBePlanned(true);
                            }
                        }
                    }

                    // 对待排程任务单下所有批次 进行的数据准备
                    if (task.getIsToBePlanned()) {
                        //为subTask添加sample列表
                        int sampleGroupId = subTask.getSampleGroupId();
                        ArrayList<EntitySample> samples = sampleGroupSamplesHashMap.computeIfAbsent(sampleGroupId, k-> new ArrayList<>());
                        subTask.setSamples(samples);

                        //整车相关处理
                        int sizeOfSamples = samples.size();
                        for (EntitySample sample : samples) {
                            if (StringUtils.equals(sample.getSampleType(), "整车")) {
                                //haolei todo: 如果该批次的样品包含整车，则该样品组应该只有一个样品
                                if (sizeOfSamples > 1) {
                                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ERROR_NOT_SCHEDULED,
                                            "批次的样品包含整车，该样品组应该只有一个样品");
                                }
                                //为subTask添加整车
                                String sampleNo = sample.getSampleNo();
                                subTask.setUniqueSampleNo(sampleNo);
                                //添加主模型的整车列表， 并为整车添加subtask列表
                                ArrayList<EntitySubTask> subTasksTemp
                                        = uniqueSampleSupportedSubtasks.computeIfAbsent(sampleNo, k -> new ArrayList<>());
                                subTasksTemp.add(subTask);
                            }
                        }

                     }


                    if (subTask.getIsToBePlanned()){
                        toBePlannedSubTasks.add(subTask);
                        EntityProcedure procedure = procedureHashMap.get(subTask.getProcedureNo());
                        EntityPlanConfig planConfig = planConfigHashMap.get(procedure.getProcedureNo());
                        subTask.setPlanConfig(planConfig);
                        //如果批次本身没有被赋予 排程颗粒度和时间限制的值，则使用对应规范的值
                        if (subTask.getPlanGranul() == null) {
                            if (planConfig == null) {
                                subTask.setPlanGranul(DEFAULT_PLAN_GRANULARITY);
                            } else {
                                subTask.setPlanGranul(planConfig.getPlanGranul());
                            }
                        }
                        //设置其他排程相关属性
                        //Sophia todo: 逻辑有些乱
                        if (subTask.getPlanTimeLimit() == null) {
                            if (planConfig == null) {
                                subTask.setPlanTimeLimit(DEFAULT_PLAN_TIME_LIMIT);
                            } else {
                                subTask.setPlanTimeLimit(planConfig.getTimeLimit());
                            }
                        }
                        if (planConfig == null) {
                            subTask.setCalendarDays(DEFAULT_PLAN_CALENDAR_DAYS);
                        } else {
                            subTask.setCalendarDays(planConfig.getCalendarDays());
                        }
                        if (planConfig == null) {
                            Date minPlanStartTime = EntityPlanConfig.calculateMinPlanStartTime(
                                    plan.getPlanTriggeredTime(),
                                    DEFAULT_PLAN_PREPARE_TIME,
                                    DEFAULT_PLAN_CONFIG_IS_WEEKEND_INCLUDE, new HashMap<Date, EntitySpecialDay>());
                            subTask.setMinPlanStartTime(minPlanStartTime);
                            subTask.setMaxPlanEndTime(DateUtil.addDay(
                                    subTask.getMinPlanStartTime(),subTask.getCalendarDays()));
                        } else {
                            if (planConfig.getMinPlanStartTime() == null){
                                planConfig.setMinPlanStartTime(EntityPlanConfig.calculateMinPlanStartTime(
                                        plan.getPlanTriggeredTime(),
                                        planConfig.getLeadFence(),
                                        planConfig.getIsWeekendINC().equals(PLAN_CONFIG_IS_WEEKEND_INCLUDE)? true:false,
                                        new HashMap<Date, EntitySpecialDay>()));
                                planConfig.setPlanEndTime(DateUtil.addDay(
                                        planConfig.getMinPlanStartTime(),planConfig.getCalendarDays()));
                            }
                            subTask.setMinPlanStartTime(planConfig.getMinPlanStartTime());
                            subTask.setMaxPlanEndTime(planConfig.getPlanEndTime());
                        }
                        //设置批次对应的子模型名称
                        String subModelName = createSubModelName(subTask.getPlanGranul());
                        //subTask.setSubModelName(subModelName);
                        //如果没有生成过该sub model，生成这个sub model，并且放入sub model的map中
                        SubModel subModel = subModelHashMap.get(subModelName);
                        if (subModel == null){
                            subModel = new SubModel(plan.getPlanId(), schedulingTask, subModelName);
                            subModelHashMap.put(subModelName, subModel);
                        }
                        //更新sub model的plan config 参数
                        SubModelData subModelData = subModel.getSubModelData();
                        subModelData.setPlanGranularity(subTask.getPlanGranul());
                        subModelData.setMaxCalendarDays(subTask.getCalendarDays());
                        subModelData.setMaxTimeLimit(subTask.getPlanTimeLimit());
                        subModelData.setMinPlanStartTime(subTask.getMinPlanStartTime());
                        subModelData.setMaxPlanEndTime(subTask.getMaxPlanEndTime());
                        plan.setMaxPlanPeriodEndTime(subTask.getMaxPlanEndTime());
                        //将批次加入sub model data的子模型待排程批次中
                        HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMapInSubModel
                                = subModelData.getToBePlannedSubTaskMap();
                        if (!toBePlannedSubTaskMapInSubModel.containsKey(taskNo)) {
                            ArrayList<EntitySubTask> tempSubTasks = new ArrayList<>();
                            toBePlannedSubTaskMapInSubModel.put(taskNo, tempSubTasks);
                        }
                        ArrayList<EntitySubTask> tempSubTasks = toBePlannedSubTaskMapInSubModel.get(taskNo);
                        tempSubTasks.add(subTask);

                        //添加bom属性
                        subTask.setBom(procedure.getBom());
                    }

                }

                subTaskHashMap.put(subTask.getSubTaskNo(), subTask);
            }

        }

        // 验证所有 Step ， 并将数据存放到 hashmap 中
        Iterator<EntityStep> stepIterator = dataContainer.getStepIterator();
        while (stepIterator.hasNext()){
            EntityStep step = stepIterator.next();

            // 单实体验证
            if (step.singleCheckStep() == RESULT_VALID) {

                // bom 中包含的所有的 step
                String bomNo = step.getBomNo();
                EntityBom bom = bomHashMap.get(bomNo);

//                // 用 bomId + stepId + "" 的字符串来充当 step 的id
                String index = "" + step.getBomNo() + step.getStepId();
                bom.getSteps().add(step);

                stepHashMap.put(index,step);
            }
        }

        // 特殊日期
        Iterator<EntitySpecialDay> specialDayIterator = dataContainer.getSpecialDayIterator();
        while (specialDayIterator.hasNext()){
            EntitySpecialDay specialDay = specialDayIterator.next();
            specialDayHashMap.put(specialDay.getSpecialDay(), specialDay);
        }

/*


    // authProcedure 放入到 hashmap 中
        Iterator<EntityAuthProcedure> authProcedureIterator = dataContainer.getAuthProcedureIterator();
        while (authProcedureIterator.hasNext()){
            EntityAuthProcedure authProcedure = authProcedureIterator.next();
            Integer procedureId = authProcedure.getProcedureId();

            // 如果 procedureId 存在，则直接添加，不存在，则创建新的 list 后添加
            if (authProcedureHashMap.containsKey(procedureId)){
                authProcedureHashMap.get(procedureId).add(authProcedure.getStaffId());
            }else {
                ArrayList<String> list = new ArrayList<>();
                list.add(authProcedure.getStaffId());
                authProcedureHashMap.put(procedureId,list);
            }
        }

        // authBom 放入到 hashmap 中
        Iterator<EntityAuthBom> authBomIterator = dataContainer.getAuthBomIterator();
        while (authBomIterator.hasNext()){
            EntityAuthBom authBom = authBomIterator.next();
            Integer bomId = authBom.getBomId();

            // 如果 bomId 存在，则直接添加，不存在，则创建新的 list 后添加
            if (authBomHashMap.containsKey(bomId)){
                authBomHashMap.get(bomId).add(authBom.getStaffId());
            }else {
                ArrayList<String> list = new ArrayList<>();
                list.add(authBom.getStaffId());
                authBomHashMap.put(bomId, list);
            }
        }

        // authStep 放入到 hashmap 中
        Iterator<EntityAuthStep> authStepIterator = dataContainer.getAuthStepIterator();
        while (authStepIterator.hasNext()){
            EntityAuthStep authStep = authStepIterator.next();
            Integer bomId = authStep.getBomId();
            Integer stepId = authStep.getStepId();
            String authStepId = "" + bomId + stepId;

            // 如果 authStepId 存在，则直接添加，不存在，则创建新的 list 后添加
            if (authStepHashMap.containsKey(authStepId)){
                authStepHashMap.get(authStepId).add(authStep.getStaffId());
            }else {
                ArrayList<String> list = new ArrayList<>();
                list.add(authStep.getStaffId());
                authStepHashMap.put(authStepId, list);

            }
        }

        // 验证所有 stepSkill ， 并将数据存放到 hashmap 中
        Iterator<EntityStepSkill> stepSkillIterator = dataContainer.getStepSkillIterator();
        while (stepSkillIterator.hasNext()){
            EntityStepSkill stepSkill = stepSkillIterator.next();

            String index = stepSkill.getBomId() + stepSkill.getStepId() + "";
            // 统计每个阶段所需的技师总数
            EntityStep step = stepHashMap.get(index);
            Integer technicianCount = step.getTechnicianCount();
            step.setTechnicianCount(technicianCount + stepSkill.getTechnicianCount());

            if (stepSkillHashMap.containsKey(index)){
                stepSkillHashMap.get(index).add(stepSkill);
            }else {
                ArrayList<EntityStepSkill> stepSkills = new ArrayList<>();
                stepSkills.add(stepSkill);
                stepSkillHashMap.put(index, stepSkills);
            }

        }

        // 验证所有 stepEquipmentGroup ， 并将数据存放到 hashmap 中
        Iterator<EntityStepEquipmentGroup> stepEquipmentGroupIterator = dataContainer.getStepEquipmentIterator();
        while (stepEquipmentGroupIterator.hasNext()){
            EntityStepEquipmentGroup stepEquipmentGroup = stepEquipmentGroupIterator.next();
            // 单实体验证
            if (stepEquipmentGroup.singleCheck()) {

                String index = stepEquipmentGroup.getBomId() + stepEquipmentGroup.getBomId() + "";
                stepEquipmentGroupHashMap.put(index, stepEquipmentGroup);
            }
        }

        // 验证所有 staff , 并将数据存放到 hashmap 中
        Iterator<EntityStaff> staffIterator = dataContainer.getStaffIterator();
        while (staffIterator.hasNext()){
            EntityStaff staff = staffIterator.next();
            // 单实体验证
            if (staff.singleCheck()) {
                // 统计工程师和技师的个数
                if (StringUtils.equals(staff.getStaffClassId(), "0")){
                    engineerTotal++;
                }else {
                    technicianTotal++;
                }

                staffHashMap.put(staff.getStaffId(), staff);
            }
        }

        // 验证所有 StaffCalendar  ， 并将数据存放到 hashmap 中
        Iterator<EntityStaffCalendar> staffCalendarIterator = dataContainer.getStaffCalendarIterator();
        while (staffCalendarIterator.hasNext()){
            EntityStaffCalendar staffCalendar = staffCalendarIterator.next();
            // 单实体验证
            if (staffCalendar.singleCheck()) {
                staffCalendarHashMap.put(staffCalendar.getStaffId(), staffCalendar);
            }
        }

        // 验证所有 skill  ， 并将数据存放到 hashmap 中
        Iterator<EntitySkill> skillIterator = dataContainer.getSkillIterator();
        while (skillIterator.hasNext()){
            EntitySkill skill = skillIterator.next();
            // 单实体验证
            if (skill.singleCheck()) {
                skillHashMap.put(skill.getSkillId(), skill);
            }
        }

        //  technicianSkill  ， 并将数据存放到 hashmap 中
        Iterator<EntityTechnicianSkill> technicianSkillIterator = dataContainer.getTechnicianSkillIterator();
        while (technicianSkillIterator.hasNext()){
            EntityTechnicianSkill technicianSkill = technicianSkillIterator.next();
            String staffId = technicianSkill.getStaffId();

            // 如果 staffId 存在，则直接添加，不存在，则创建新的 list 后添加
            if (technicianSkillHashMap.containsKey(staffId)){
                technicianSkillHashMap.get(staffId).add(technicianSkill.getSkillId());
            }else {
                HashSet<String> set = new HashSet<>();

                set.add(technicianSkill.getSkillId());
                technicianSkillHashMap.put(staffId, set);
            }
        }

        // 验证所有 calendar ， 并将数据存放到 hashmap 中
        Iterator<EntityCalendar> calendarIterator = dataContainer.getCalendarIterator();
        while (calendarIterator.hasNext()){
            EntityCalendar calendar = calendarIterator.next();
            // 单实体验证
            if (calendar.singleCheck()) {
                calendarHashMap.put(calendar.getCalendarId(), calendar);
            }
        }

        // 验证所有 rollCalendar ， 并将数据存放到 hashmap 中
        Iterator<EntityRollCalendar> rollCalendarIterator = dataContainer.getRollCalendarIterator();
        while (rollCalendarIterator.hasNext()){
            EntityRollCalendar rollCalendar = rollCalendarIterator.next();
            // 单实体验证
            if (rollCalendar.singleCheck()) {
                rollCalendarHashMap.put(rollCalendar.getCalendarId(), rollCalendar);
            }
        }

        // 验证所有 equipmentGroup ， 并将数据存放到 hashmap 中
        Iterator<EntityEquipmentGroup> equipmentGroupIterator = dataContainer.getEquipmentGroupIterator();
        while (equipmentGroupIterator.hasNext()){
            EntityEquipmentGroup equipmentGroup = equipmentGroupIterator.next();
            // 单实体验证
            if (equipmentGroup.singleCheck()) {
                equipmentGroupHashMap.put(equipmentGroup.getEquipmentGroupId(), equipmentGroup);
            }
        }

        // 验证所有 equipment ， 并将数据存放到 hashmap 中
        Iterator<EntityEquipment> equipmentIterator = dataContainer.getEquipmentIterator();
        while (equipmentIterator.hasNext()){
            EntityEquipment equipment = equipmentIterator.next();
            // 单实体验证
            if (equipment.singleCheck()) {
                equipmentHashMap.put(equipment.getEquipmentId(), equipment);
            }
        }

        // 设备组和设备的关系(equipmentGroupId, list(equipmentId)) 存放到 hashmap 中
        Iterator<EntityEquipmentGroupRel> equipmentGroupRelIterator = dataContainer.getEquipmentGroupRelIterator();
        while (equipmentGroupRelIterator.hasNext()){
            EntityEquipmentGroupRel equipmentGroupRel = equipmentGroupRelIterator.next();
            Integer equipmentGroupId = equipmentGroupRel.getEquipmentGroupId();

            // 如果 equipmentGroupId 存在，则直接添加，不存在，则创建新的 list 后添加
            if (equipmentGroupRelHashMap.containsKey(equipmentGroupId)){
                equipmentGroupRelHashMap.get(equipmentGroupId).add(equipmentGroupRel.getEquipmentId());
            }else {
                ArrayList<String> list = new ArrayList<>();
                list.add(equipmentGroupRel.getEquipmentId());
                equipmentGroupRelHashMap.put(equipmentGroupId, list);
            }
        }

        // 将 EquipmentCalendar 存放到 hashmap 中
        Iterator<EntityEquipmentCalendar> equipmentCalendarIterator = dataContainer.getEquipmentCalendarIterator();
        while (equipmentCalendarIterator.hasNext()){
            EntityEquipmentCalendar equipmentCalendar = equipmentCalendarIterator.next();
            equipmentCalendarHashMap.put(equipmentCalendar.getEquipmentId(), equipmentCalendar);
        }


        // 验证所有 taskPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntityTaskPlanInput> taskPlanInputIterator = dataContainer.getTaskPlanInputIterator();
        while (taskPlanInputIterator.hasNext()){
            EntityTaskPlanInput taskPlanInput = taskPlanInputIterator.next();
            // 单实体验证
            if (taskPlanInput.singleCheck()) {
                taskPlanInputHashMap.put(taskPlanInput.getTaskId(), taskPlanInput);
            }
        }

        // 验证所有 subTaskPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntitySubTaskPlanInput> subTaskPlanInputIterator = dataContainer.getSubTaskPlanInputIterator();
        while (subTaskPlanInputIterator.hasNext()){
            EntitySubTaskPlanInput subTaskPlanInput = subTaskPlanInputIterator.next();
            // 单实体验证
            if (subTaskPlanInput.singleCheck()) {
                subTaskPlanInputHashMap.put(subTaskPlanInput.getSubTaskId(), subTaskPlanInput);
            }
        }

        // 验证所有 stepPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntityStepPlanInput> stepPlanInputIterator = dataContainer.getStepPlanInputIterator();
        while (stepPlanInputIterator.hasNext()){
            EntityStepPlanInput stepPlanInput = stepPlanInputIterator.next();
            // 单实体验证
            if (stepPlanInput.singleCheck()) {
                stepPlanInputHashMap.put(stepPlanInput.getStepPlanId(), stepPlanInput);
            }
        }

        // 验证所有 equipmentPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntityEquipmentPlanInput> equipmentPlanInputIterator = dataContainer.getEquipmentPlanInputIterator();
        while (equipmentPlanInputIterator.hasNext()){
            EntityEquipmentPlanInput equipmentPlanInput = equipmentPlanInputIterator.next();
            // 单实体验证
            if (equipmentPlanInput.singleCheck()) {
                equipmentPlanInputHashMap.put(equipmentPlanInput.getEquipmentPlanId(), equipmentPlanInput);
            }
        }

        // 验证所有 staffPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntityStaffPlanInput> staffPlanInputIterator = dataContainer.getStaffPlanInputIterator();
        while (staffPlanInputIterator.hasNext()){
            EntityStaffPlanInput staffPlanInput = staffPlanInputIterator.next();
            // 单实体验证
            if (staffPlanInput.singleCheck()) {
                staffPlanInputHashMap.put(staffPlanInput.getStaffPlanId(), staffPlanInput);
            }
        }
        */
    }

    private String createSubModelName(Integer planGranularity) {
        return String.format("%03d", (1000-planGranularity));
    }

    @Deprecated
    private String createSubModelName(Integer planGranularity, int calendarDays, Integer planTimeLimit) {
        return String.format("%05d", planGranularity)
                +"_"+String.format("%03d", calendarDays)
                +"_"+String.format("%04d", planTimeLimit);
    }

    /**
     * 跨实体数据准备
     */
    //Sophia todo: 看是否需要，不需要就删掉
    private void prepareCrossEntityData() {
        EntityFunction function = functionHashMap.get(functionId);
        ArrayList<EntityTask> toBePlannedTasks = function.getToBePlannedTasks();
        int size = toBePlannedTasks.size();
        for (EntityTask task : toBePlannedTasks) {
            ArrayList<EntitySubTask> allSubTasks = task.getSubTasks();
            int sizeOfSubTask = allSubTasks.size();
            for (EntitySubTask subTask : allSubTasks) {
            }
        }

    }
    /**
     * 跨实体验证（HashMap 中）
     */
    public void checkMasterData(){
        checkTask();
        checkSubTask();
        //checkSubTaskGroup();
        checkStepSkill();

        checkSubTaskPlanInput();


        /*
        checkAuthProcedure();
        checkBom();
        checkAuthBom();
        checkStep();
        checkAuthStep();
        checkStepEquipmentGroup();
        checkStaffCalendar();
        checkTechnicianSkill();
        checkEquipmentGroup();
        checkEquipment();
        checkEquipmentGroupRel();
        checkEquipmentMaint();
        checkSample();
        checkEquipmentPlanInput();
        checkStepPlanInput();
        checkStaffPlanInput();
        */
    }


    /**
     * 遍历 taskHashMap 验证 Task 跨实体逻辑
     */
    private void checkTask() {

        EntityFunction function = functionHashMap.get(functionId);

        for (EntityTask task : function.getToBePlannedTasks()) {

            String taskNo = task.getTaskNo();
            String respUserId = task.getRespUserId();
            // 是否所有 Task 都有对应的 SubTask 任务单没有对应的试验批次
            if ( task.getSubTasks().size() == 0){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                        "任务单 " + taskNo + " 没有对应的试验批次，无法进行排程");
            }

            // 任务单下没有待排程批次
            // 应该不会出现这种情况了，因为是按照待排程的subtask来抓的数据
//            if (task.getToBePlannedSubTasks().size() == 0){
//                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_NOT_ENOUGH_SCHEDULING_CONDITIONS,
//                        "任务单 " + taskNo + " 没有需要排程的排次，导致该任务单无法排程");
//                task.setIsToBePlanned(false);
//            }



            // 任务单要求开始时间（审批完成时间）较晚

            // 任务单夹具准备完毕时间较晚


            // 任务单中的规范没有任何试验阶段

            /*
            // 如果任务单指定了优先工程师，需要保证该工程师在该任务单下面的所有批次中，每个批次的小阶段授权人员（可能的工程师）至少有一个包含该优先工程师
            if (!StringUtils.isEmpty(respUserId)){
                for (Integer subTaskId : subTaskIds) {
                    EntitySubTask subTask = subTaskHashMap.get(subTaskId);
                    ArrayList<String> stepIds = subTask.getStepIds();

                    for (String stepId : stepIds) {
                        // 获取所有的 staffId
                        ArrayList<String> staffIds = authStepHashMap.get(stepId);
                        boolean b = staffIds.contains(task.getRespUserId());
                        if (!b){

                        }
                    }
                }
            }
             */
        }
    }

    /**
     * 遍历 subTaskGroupHashMap 验证 subTaskGroup 跨实体逻辑
     */
    public void checkSubTaskGroup() {
        // 一个批次组内，如果前序批次有错误，后续批次和相应的任务单全部不排。
        // 批次组顺序：有比待排程批次顺序号更大的批次已经排程的话，报warning
        // 根据待排程批次在批次组中的位置设置最早开始时间和最晚结束时间
        // 为待排程批次设置前序批次
        // 如果同一批次组的待排程批次的BOM对应的排程时间颗粒度不同，即需要在不同的子模型中排程，给warning

        for(Map.Entry<String, ArrayList<EntitySubTask>> entry: subTaskGroupHashMap.entrySet()){
            // 获得顺序号的Map，不管subtask本身的状态，已排程，或者待排程，或者数据错误
            ArrayList<EntitySubTask> subtasks = entry.getValue();
            HashMap<Integer,EntitySubTask> orderSubTaskNoMap = new HashMap<>();
            HashSet<Integer> granularities = new HashSet<>();
            // 如果序号有相同，并且不能接受，整个批次组都不排了
            boolean errorFlag = false;
            for (EntitySubTask subTask: subtasks){
                Integer order = subTask.getTaskSeqNo();
                if (orderSubTaskNoMap.containsKey(order)){
                    // 顺序号相同报错,
                    EntitySubTask existedSubTask = orderSubTaskNoMap.get(order);

                    if (subTask.isFixed() && existedSubTask.isFixed()){
                        //如果都是已排程批次，warning，忽略；
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_SCHEDULED_BATCH_CONFLICT,
                                "已排程批次 " + subTask.getSubTaskNo() + " 和已排程批次" + existedSubTask.getSubTaskNo()
                                + " 都在批次组 "+ entry.getKey() + " 中，排程顺序都是 " + order + "，忽略该冲突，继续排程");
                    } else if (subTask.getIsToBePlanned() && existedSubTask.isFixed()) {
                        // 如果一个是已排程，一个是待排程，错误，待排程任务单不能排程
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT,
                                "待排程批次 " + subTask.getSubTaskNo() + " 和已排程批次 " + existedSubTask.getSubTaskNo()
                                        + " 都在批次组 "+ entry.getKey() + " 中，排程顺序都是 " + order + "，导致相关任务单 "
                                + subTask.getTaskNo() + " 和该批次组无法排程");
                        EntityTask task = taskHashMap.get(subTask.getTaskNo());
                        task.setIsToBePlanned(false);
                        errorFlag = true;
                    } else if (subTask.isFixed() && existedSubTask.getIsToBePlanned()) {
                        // 如果一个是已排程，一个是待排程，错误，待排程任务单不能排程
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT,
                                "待排程批次 " + existedSubTask.getSubTaskNo() + " 和已排程批次 " + subTask.getSubTaskNo()
                                        + " 都在批次组 "+ entry.getKey() + " 中，排程顺序都是 " + order + "，导致相关任务单 "
                                        + existedSubTask.getTaskNo() + " 和该批次组无法排程");
                        EntityTask task = taskHashMap.get(existedSubTask.getTaskNo());
                        task.setIsToBePlanned(false);
                        errorFlag = true;
                    }else if (subTask.getIsToBePlanned() && existedSubTask.getIsToBePlanned()) {
                        errorFlag = true;
                        // 如果都是待排程批次，相关的任务单都不能排程；
                        if (subTask.getTaskNo().equals(existedSubTask.getTaskNo())) {
                            // 同一个任务单
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT,
                                    "待排程批次 " + existedSubTask.getSubTaskNo() + " 和待排程程批次 " + subTask.getSubTaskNo()
                                            + " 都在批次组 " + entry.getKey() + " 中，排程顺序都是 " + order + "，导致相关任务单 "
                                            + existedSubTask.getTaskNo() + " 和该批次组无法排程");
                            EntityTask task = taskHashMap.get(existedSubTask.getTaskNo());
                            task.setIsToBePlanned(false);
                        } else {
                            // 不同的任务单
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT,
                                    "待排程批次 " + existedSubTask.getSubTaskNo() + " 和待排程程批次 " + subTask.getSubTaskNo()
                                            + " 都在批次组 " + entry.getKey() + " 中，排程顺序都是 " + order + "，导致相关任务单 "
                                            + existedSubTask.getTaskNo() + " 和任务单 " + subTask.getTaskNo() + " 和该批次组无法排程");
                            EntityTask task1 = taskHashMap.get(existedSubTask.getTaskNo());
                            task1.setIsToBePlanned(false);
                            EntityTask task2 = taskHashMap.get(subTask.getTaskNo());
                            task2.setIsToBePlanned(false);
                        }
                    }
                } else {
                    orderSubTaskNoMap.put(order, subTask);
                }
            }
            if (errorFlag){
                continue;
            }
            ArrayList<Integer> sortedOrder = new ArrayList<>(orderSubTaskNoMap.keySet());
            Collections.sort(sortedOrder);
            ArrayList<EntitySubTask> sortedSubTasks = new ArrayList<>();
            EntitySubTask previousToBePlanned = null;
            EntitySubTask previousSubTask = null;
            EntitySubTask previousFixedSubTask = null;
            Date lastEndTime = plan.getPlanPeriodStartTime();
            int minGranularity = 0;
            for (Integer order: sortedOrder){
                EntitySubTask subTask = orderSubTaskNoMap.get(order);
                EntityTask task = taskHashMap.get(subTask.getTaskNo());
                if (errorFlag){
                    break;
                }
                if (task!= null && !task.getIsToBePlanned()){
                    //task 有数据错误
                    break;
                }
                if (!subTask.getIsToBePlanned() && !subTask.isFixed() && !subTask.isIgnored()){
                    //subtask有数据问题（现在还没有is planned）
                    break;
                }

                if (subTask.getIsToBePlanned()) {
                    sortedSubTasks.add(subTask);
                    //toBePlannedSubTaskNos.add(subTask.getSubTaskNo());
                    EntityPlanConfig planConfig = subTask.getBom().getPlanConfig();
                    Integer planGranularity = planConfig.getPlanGranul();
                    if (!granularities.contains(planGranularity)) {
                        if (granularities.size() == 0) {
                            //第一个subTask
                            minGranularity = planGranularity;
                        } else {
                            //已经有了别的granularity
                            if (solveMode != SOLVE_MODE_BY_SUBTASK_GROUP_AND_GRANULARITY &&  minGranularity < planGranularity) {
                                // 判断是否前面的granularity更大，更大没有问题，会先排；更小有问题，给提示
                                // 比如，前面的gran 是120，现在的是60，ok；如果前面是60，现在是120，给提示
                                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_POTENTIAL_SCHEDULING_CONFLICTS,
                                        "待排程批次 " + subTask.getSubTaskNo() + " 的时间颗粒度为 " + planGranularity
                                                + " ，其所在的批次组 " + entry.getKey() + " 内的前序批次存在时间颗粒度更小（为 " + minGranularity
                                                + "）的情况，可能导致排程失败，建议统一设置");
                            } else {
                                minGranularity = planGranularity;
                            }
                        }
                        granularities.add(planGranularity);
                    }

                    if (previousSubTask!= null){
                        subTask.setPrevious(previousToBePlanned);
                        log.info("subTask " + subTask.getSubTaskNo() + "的 previous 是 subTask" + previousToBePlanned.getSubTaskNo());

                    }
                    previousToBePlanned = subTask;
                    previousSubTask = subTask;

                    if (lastEndTime.after(subTask.getMaxPlanEndTime())) {
                        // 检查最早开始时间是否在排程结束时间之内
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                                "待排程批次 " + subTask.getSubTaskNo() + " 在考虑其在批次组 " + entry.getKey()
                                        + " 中的顺序要求后，可能的最早开始时间 " + DateUtil.getDateString(lastEndTime)
                                        + " 已经晚于该批次的最晚排程结束时间 " + DateUtil.getDateString(subTask.getMaxPlanEndTime())
                                        + "，导致相关任务单 " + subTask.getTaskNo() + "无法排程");
                        subTask.setIsToBePlanned(false);
                        task.setIsToBePlanned(false);
                    } else {
                        if (subTask.getMinPlanStartTime().after(lastEndTime)) {
                            // 批次的最早开始时间晚于last end time
                            lastEndTime = subTask.getMinPlanStartTime();
                        } else {
                            subTask.setMinPlanStartTime(lastEndTime);
                        }
                    }

                    // 这时是否已经计算了bom length？答：把这个函数放在计算bom length之后了。批次组相关逻辑
                    lastEndTime = DateUtil.addHour(lastEndTime, (int) subTask.getBom().getBomLength());
                    if (lastEndTime.after(subTask.getMaxPlanEndTime())) {
                        // 检查结束时间是否在排程结束时间之内
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                                "待排程批次 " + subTask.getSubTaskNo() + " 在考虑其在批次组 " + entry.getKey()
                                        + " 中的顺序要求和批次对应的BOM的最小时长后，可能的最早结束时间 " + DateUtil.getDateString(lastEndTime)
                                        + " 已经晚于该批次的最晚排程结束时间 " + DateUtil.getDateString(subTask.getMaxPlanEndTime())
                                        + "，导致相关任务单 " + subTask.getTaskNo() + "无法排程");
                        subTask.setIsToBePlanned(false);
                        task.setIsToBePlanned(false);
                    }

                } else {
                    // 本身是fixed
                    if (previousToBePlanned!=null && previousToBePlanned.getSubTaskNo().equals(previousSubTask.getSubTaskNo())) {
                        //如果前面紧挨着的是to be planned
                        // 设置前面待排程批次的最晚结束时间
                        if (subTask.getStartTime().before(previousToBePlanned.getMaxPlanEndTime())) {
                            previousToBePlanned.setMaxPlanEndTime(subTask.getStartTime());
                        }

                        EntityBom bom = previousToBePlanned.getBom();
                        Date minEndTimeOfPreviousToBePlanned = DateUtil.addHour(previousToBePlanned.getMinPlanStartTime(),
                                (int)bom.getBomLength());
                        if (minEndTimeOfPreviousToBePlanned.after(subTask.getStartTime())){
                            // 检查上一个待排程批次的最晚结束时间，是否来得及
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                                    "待排程批次 " + subTask.getSubTaskNo() + " 在考虑其在批次组 " + entry.getKey()
                                            + " 中的顺序要求和批次对应的BOM的最小时长后，可能的最早结束时间 "
                                            + DateUtil.getDateString(minEndTimeOfPreviousToBePlanned)
                                            + "，已经晚于该批次的后续已排程批次的开始时间 "+ DateUtil.getDateString(subTask.getStartTime())
                                            +"，导致相关任务单 " + previousToBePlanned.getTaskNo() + "无法排程");
                            EntityTask previousTask = taskHashMap.get(previousToBePlanned.getTaskNo());
                            previousToBePlanned.setIsToBePlanned(false);
                            previousTask.setIsToBePlanned(false);
                        } else {
                            // 报warning，fixed批次前面有不是fixed批次，可能导致排程失败
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_POTENTIAL_SCHEDULING_CONFLICTS,
                                    "待排程批次 " +previousToBePlanned.getSubTaskNo() + " 在批次组 " + entry.getKey()
                                            + " 中的顺序号是 "+ previousToBePlanned.getTaskSeqNo()
                                            +"，存在后续（顺序号为 "+ subTask.getTaskSeqNo() + "）的批次 "+ subTask.getSubTaskNo()
                                            +" 已经排程的情况， 可能导致排程失败");
                        }
                    }
                    // 取最大值，以防已经排程的批次组内的批次，顺序是反的。
                    lastEndTime = DateUtil.getMaxTime(lastEndTime,subTask.getEndTime());
                    if (previousFixedSubTask != null){
                        if (subTask.getStartTime().before(previousFixedSubTask.getEndTime())){
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_SCHEDULED_BATCH_CONFLICT,
                                    "已排程批次 " + subTask.getSubTaskNo() + " 和批次 " + previousFixedSubTask.getSubTaskNo() +
                                            " 都在批次组 " + entry.getKey() + " 中并且已经排程，但是不符合要求的排程顺序，忽略该冲突，继续排程");
                        }
                    }
                    previousFixedSubTask = subTask;
                    previousSubTask = subTask;
                }
            }
            if (sortedSubTasks.size()!=0) {
                sortedSubTaskGroupHashMap.put(entry.getKey(), sortedSubTasks);
            }
        }


    }
    /**
     * 遍历 subTaskHashMap 验证 subTask 跨实体逻辑
     */
    private void checkSubTask() {
        EntityFunction function = functionHashMap.get(functionId);
        for (EntityTask task : function.getToBePlannedTasks()) {
            for (EntitySubTask subTask : task.getToBePlannedSubTasks()) {
                //为sub task是否有报告撰写赋值
                if (subTask.getIsToBePlanned() && subTask.getBom().isHasReportingStep()){
                    subTask.setHasReportingStep(true);
                }

                String subTaskNo = subTask.getSubTaskNo();
                EntityPlanConfig planConfig = subTask.getPlanConfig();

                Date maxAcceptedSubTaskStartTime = planConfig.getMaxAcceptedSubTaskStartTime();
                String taskNo = task.getTaskNo();

                // 验证任务单夹具准备完毕时间
                Date fixtureReadyTime = task.getFixtureReadyTime();
                //是否需要新的夹具
                String isNeedNewFixture = task.getIsNeedNewFixture();
                // 任务单夹具准备完毕时间为空
                // 首先判断任务单是否需要新夹具，当需要新夹具时候，判断是否具有夹具的准备时间
                if (isNeedNewFixture.equals(IS_NEED_NEW_FIXTURE)){
                    // fixtureReadyTime为空在task单体验证中已经检查了
                    // 任务单夹具准备完毕时间晚于排程结束时间
                    if (fixtureReadyTime != null && fixtureReadyTime.after(subTask.getMaxPlanEndTime())){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                                "任务单 " + taskNo + " 需要新夹具，而“FIXTURE_READY_TIME”的时间是 " + fixtureReadyTime +
                                        "，晚于批次 " + subTaskNo+ " 的排程结束时间 " + subTask.getMaxPlanEndTime()
                                        + "，导致相关任务单无法排程");
                        task.setIsToBePlanned(false);
                    }

                    // 夹具准备完毕时间晚于委托天数
                    if (task.getIsNeedNewFixture().equals(IS_NEED_NEW_FIXTURE)) {
                        if (task.getFixtureReadyTime().after(maxAcceptedSubTaskStartTime)){
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                                    "任务单 " + taskNo + " 需要新夹具，而“FIXTURE_READY_TIME” + 的时间是 " +
                                            task.getFixtureReadyTime() + "，晚于批次 "+ subTaskNo
                                            +" 委托天数 "  + maxAcceptedSubTaskStartTime
                                            + "，导致相关任务单无法排程");

                            subTask.setIsToBePlanned(false);
                            task.setIsToBePlanned(false);
                        }
                    }
                }
                // 样件提供时间晚于排程结束时间
                Date provideDate = subTask.getProvideDate();
                Date planPeriodEndTime = plan.getPlanPeriodEndTime();
                if (provideDate != null && provideDate.after(subTask.getMaxPlanEndTime())){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                            "批次 " + subTaskNo + " 的样件提供时间是 " + provideDate + "，晚于排程结束时间 " + subTask.getMaxPlanEndTime() +
                                    " ，该批次和相应的任务单 " + taskNo + " 不会参与排程" );
                    task.setIsToBePlanned(false);
                }
                // 样件提供时间晚于委托天数
                if (provideDate != null && provideDate.after(maxAcceptedSubTaskStartTime)){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                            "批次 " + subTaskNo + " 的样件提供时间是 " + provideDate + "，晚于委托天数 " + maxAcceptedSubTaskStartTime
                                    + "，该批次和相应的任务单 " + taskNo + " 不会参与排程");
                    task.setIsToBePlanned(false);
                }

//                String sampleNo = subTask.getUniqueSampleNo();

                ArrayList<EntitySample> samples = subTask.getSamples();
                for (EntitySample sample : samples) {
                    // 相关 task 不排程
                    if (!sample.singleCheckTask()){
                        task.setIsToBePlanned(false);
                    }
                }

                String bomNo = subTask.getBomNo();
                EntityBom bom = bomHashMap.get(bomNo);
                if (bom != null){
                    ArrayList<EntityStep> steps = bom.getSteps();
                    if (steps.size() == 0){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                                "批次 " + subTaskNo + " 的BOM " + bomNo + " 没有定义任何小阶段，导致相关任务单 "+ taskNo+ " 无法排程");
                    }


                }
                // 检查任务单优先工程师是否有授权
                String respUserId = task.getRespUserId();
                if (respUserId == null){
                    continue;
                }
                if (bom.getProcedureNo() == null){
                    continue;
                }
                ArrayList<String> authProcedureStaffs = authProcedureHashMap.get(bom.getProcedureNo());
                if (authProcedureStaffs == null){
                    continue;
                }
                if (!authProcedureStaffs.contains(respUserId)){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                            "任务单 " + taskNo + " 的优先指定责任工程师 " + respUserId
                                    +" 在批次 "+ subTaskNo + " 对应的规范 " + bom.getProcedureNo()
                                    +" 授权中不存在，所以不符合授权条件，忽略该设置，继续排程");
                    // 设置为null，后续不再检查
                    task.setRespUserId(null);
                }

            }

        }

    }

    /**
     * 计算auth bom
     */
    private void calculateAuthBomPerResourceType(EntityBom bom, String resourceType,
                                                 ArrayList<EntityStaff> authProcedure,
                                                 ArrayList<EntityStaff> authBom,
                                                 ArrayList<EntityStaff> authBomAndProcedure ) {
        String bomType = bom.getBomType();
        String resourceTypeMessage = resourceType.equals(RESOURCE_TYPE_ENGINEER)? "工程师":"技师";
        // 标准BOM，有规范授权时取交集，BOM 中有 规范中没有的授权 WARNNING 提示，
        // 标准BOM，没有规范授权，报错，无法排程
        // 非标准BOM，不需要管是否有规范授权，BOM授权人员不变
        if (!bomType.equals(BOM_TYPE_STANDARD)){
            authBomAndProcedure.addAll(authBom);
        } else {
            // 标准BOM
            String bomNo = bom.getBomNo();
            String procedureNo = bom.getProcedureNo();
            EntityProcedure procedure = procedureHashMap.get(procedureNo);
            if (procedure == null) {
                // 不会出现，前面已经验证过
            }
            if (authProcedure.size() == 0) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                        "对应标准BOM " + bomNo + " 的规范 " +
                                procedureNo + " 没有授权"+resourceTypeMessage+"信息，导致相关任务单无法排程");
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);
            } else if (authBom.size() == 0) {
                authBomAndProcedure.addAll(authProcedure);
            } else {
                for (EntityStaff staff : authBom) {
                    if (authProcedure.contains(staff)) {
                        authBomAndProcedure.add(staff);
                    } else {
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                "BOM " + bomNo + " 是标准BOM，但是该BOM的授权 " + staff.resourceTypeMessage()
                                        + staff.getStaffName() + "（ID " + staff.getStaffId()
                                        + "）没有获得相应规范 " + procedure.getProcedureNo()
                                        + " 的授权，该人员被考虑为未授权，继续排程。");
                    }
                }
                if (authBomAndProcedure.size() == 0) {
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                            "BOM " + bomNo + " 是标准BOM，但是该BOM的授权"+resourceTypeMessage+"全部没有获得相应规范 "
                                    + procedureNo + " 的授权，导致相关任务单无法排程。");
                    invalidBom.add(bomNo);
                    setSameBomTasksNotScheduling(bomNo);
                }
            }
        }
    }
    /**
     * 计算auth bom
     */
    private void calculateAuthBom() {

        for (EntityBom bom : bomHashMap.values()){
            if (invalidBom.contains(bom)){
                continue;
            }
            ArrayList<EntityStaff> authProcedureEngineers = new ArrayList<>();
            ArrayList<EntityStaff> authProcedureTechnicians = new ArrayList<>();
            if (bom.getBomType().equals(BOM_TYPE_STANDARD)) {

                String procedureNo = bom.getProcedureNo();
                EntityProcedure procedure = procedureHashMap.get(procedureNo);
                if (procedure == null) {
                    // 不会出现，前面已经验证过
                }else {
                    authProcedureEngineers = procedure.getAuthEngineers();
                    authProcedureTechnicians = procedure.getAuthTechnicians();
                }
            }
            calculateAuthBomPerResourceType(bom, RESOURCE_TYPE_ENGINEER,
                    authProcedureEngineers,bom.getAuthBomEngineers(),
                    bom.getAuthBomAndProcedureEngineers());
            if (bom.isNeedTechnician()) {
                calculateAuthBomPerResourceType(bom, RESOURCE_TYPE_TECHNICIAN,
                        authProcedureTechnicians,bom.getAuthBomTechnicians(),
                        bom.getAuthBomAndProcedureTechnicians());
            }
        }
    }

    private void checkStepSkill(){
        for (Map.Entry<String, EntityStepSkill> entry : stepSkillHashMap.entrySet()) {
            // 小阶段所需的技师数量超过技师总数
            EntityStepSkill stepSkill = entry.getValue();

            String bomNo = stepSkill.getBomNo();
            int stepId = stepSkill.getStepId();
            String skillIdSet = stepSkill.getSkillIdSet();
            EntityStep step = stepHashMap.get(bomNo + stepId);
            ArrayList<EntityStaff> authTechniciansConsideredBOM = step.getAuthStepTechnicians();
            int size = authTechniciansConsideredBOM.size();

            if (skillIdSet == null || skillIdSet.equals("")) {
                if (stepSkill.getTechnicianCount() > size) {
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                            "BOM " + bomNo + " 的小阶段 " + step.getStepName()
                                    + "（阶段顺序号 " + step.getStepOrder() + "，阶段ID " + step.getStepId()
                                    + " )的技师技能组 "
                                    + stepSkill.getSkillIdSet() + " 要求的数量为 " + stepSkill.getTechnicianCount() +
                                    "，但有资格进行该小阶段的技师只有 " + size + "，导致相关任务单无法排程");
                    invalidBom.add(bomNo);
                    setSameBomTasksNotScheduling(bomNo);
                }
                continue;
            }

            // 计算出该技能技师的个数
            String[] skillIds = skillIdSet.split(",");
            String skillIdSetNew = "";
            // 技能集中的技能在技能表中不存在
            for (String skillId : skillIds) {
                if (skillId.equals("")) {
                    continue;
                }
                if (!skillHashMap.containsKey(skillId)) {
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                            "BOM " + bomNo + " 的小阶段 " + step.getStepName() + "（阶段顺序号 "
                                    + step.getStepOrder() + "，ID " + stepId + "）的技能集 " + skillIdSet
                                    + " 中的技能 " + skillId + " 在技能表中不存在，忽略该技能，继续排程");
                }
            }

            boolean zeroPossibleStaffFlag = false;
            // 获得具有该 skillIdSet 所有技能的技师
            HashSet<EntityStaff> allTechnicians = new HashSet<>();
            if (skillTechnicianHashMap.get(skillIds[0]) == null) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                        "BOM " + bomNo + " 的小阶段 " + step.getStepName()
                                + "（阶段顺序号 " + step.getStepOrder() + "，阶段ID " + stepId
                                + " ）的技师技能组 " + stepSkill.getSkillIdSet() + " 要求的数量为 " + stepSkill.getTechnicianCount() +
                                "，但有技能 " + skillIds[0] + " 的技师只有 0 人，导致相关任务单无法排程");
                zeroPossibleStaffFlag = true;
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);
            } else {
                allTechnicians.addAll(skillTechnicianHashMap.get(skillIds[0]));
                int length = skillIds.length;
                if (length > 1) {
                    for (int i = 1; i < length; i++) {
                        ArrayList<EntityStaff> technicians = skillTechnicianHashMap.get(skillIds[i]);
                        if (technicians == null) {
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                                    "BOM " + bomNo + " 的小阶段 " + step.getStepName()
                                            + "（阶段顺序号 " + step.getStepOrder() + "，阶段ID " + stepId
                                            + " ）的技师技能组 " + stepSkill.getSkillIdSet() + " 要求的数量为 " + stepSkill.getTechnicianCount() +
                                            "，但有技能 " + skillIds[0] + " 的技师只有 0 人，导致相关任务单无法排程");
                            invalidBom.add(bomNo);
                            setSameBomTasksNotScheduling(bomNo);
                            zeroPossibleStaffFlag = true;
                            allTechnicians.clear();
                        } else {
                            allTechnicians.retainAll(technicians);
                        }
                    }
                }
            }

            int technicianCount = allTechnicians.size();
            technicianCount = technicianCount >= size ? technicianCount : size;
            // 小阶段所需的技师数量超过技师总数
            if (zeroPossibleStaffFlag || stepSkill.getTechnicianCount() > technicianCount) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                        "BOM " + bomNo + " 的小阶段 " + step.getStepName()
                                + "（阶段顺序号 " + step.getStepOrder() + "，阶段ID " + step.getStepId()
                                + " )的技师技能组 "
                                + stepSkill.getSkillIdSet() + " 要求的数量为 " + stepSkill.getTechnicianCount() +
                                "，但有资格进行该小阶段的技师只有 " + technicianCount + "，导致相关任务单无法排程");
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);
            }

        }
    }

    /**
     * 遍历 authStepHashMap 验证 AuthStep 跨实体逻辑
     */
    @Deprecated
    private void checkAuthStep () {

        for (Map.Entry<String, ArrayList<String>> entry : authStepHashMap.entrySet()) {
            // 小阶段授权的员工id在员工表中不存在
            ArrayList<String> authStepStaffIds = entry.getValue();
            for (String authStepStaffId : authStepStaffIds) {
                CheckDataUtil.notExistTable(staffHashMap, authStepStaffId, ConstantUtil.MESSAGE_SEVERITY_ERROR,
                        MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                        "小阶段" + entry.getKey() + "对应的STAFF_ID在员工表中不存在，无法进行排程");
            }

        }
    }

    /**
     * 遍历 stepEquipmentGroupHashMap 验证 StepEquipmentGroup 跨实体逻辑
     */
    @Deprecated
    private void checkStepEquipmentGroup () {

        for (EntityStepEquipmentGroup stepEquipmentGroup : stepEquipmentGroupHashMap.values()) {
            Integer equipmentGroupId = stepEquipmentGroup.getEquipmentGroupId();
            // 需要的设备数
            Integer equipmentNum = stepEquipmentGroup.getEquipmentNum();
            ArrayList<String> equipmentIds = equipmentGroupRelHashMap.get(equipmentGroupId);
            // 需求每个设备的容量
            Integer needEquipmentCap = stepEquipmentGroup.getEquipmentQty();
            // 记录设备组中设备的数量
            Integer equipmentTotal = 0;
            for (String equipmentId : equipmentIds) {
                EntityEquipment equipment = equipmentHashMap.get(equipmentId);
                Integer equipmentQty = equipment.getEquipmentQty();
                Integer equipmentCap = equipment.getEquipmentCap();

                // 小阶段需求的设备容量大于设备本身的容量，导致无法满足
                CheckDataUtil.numCompare(needEquipmentCap, equipmentCap, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                        "");

                equipmentTotal += equipmentQty;
            }
            // 小阶段需求的设备数量大于可提供的设备数量，导致无法排程
            CheckDataUtil.numCompare(equipmentNum, equipmentTotal, MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                    "小阶段" + stepEquipmentGroup.getStepId() + "对应的BOM要求的设备数量不满足，无法进行排程");


            // 设备可用数量少于试验规范的阶段所需数量
        }
    }



    /**
     * 遍历 TechnicianSkillHashMap 验证 TechnicianSkill 跨实体逻辑
     */
    @Deprecated
    private void checkTechnicianSkill () {

        for (Map.Entry<String, ArrayList<String>> entry : technicianSkillHashMap.entrySet()) {
            // 该技能id对应的所有员工不在员工表中
            String technicianId = entry.getKey();
            CheckDataUtil.notExistTable(staffHashMap, technicianId, ConstantUtil.MESSAGE_SEVERITY_ERROR,
                    MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                    "技能" + entry.getKey() + "对应的所有STAFF_ID在员工表中不存在，无法进行排程");

        }
    }

    /**
     * 遍历 EquipmentGroupHashMap 验证 EquipmentGroup 跨实体逻辑
     */
    @Deprecated
    private void checkEquipmentGroup () {

        for (EntityEquipmentGroup equipmentGroup : equipmentGroupHashMap.values()) {
            // 设备组labId对应的lab不存在
            Integer labId = equipmentGroup.getLabId();
            CheckDataUtil.notExistTable(laboratoryHashMap, labId, ConstantUtil.MESSAGE_SEVERITY_WARNNING, ConstantUtil.
                            MESSAGE_TYPE_FIELD_MISS, "设备组" + equipmentGroup.getEquipmentGroupId() + "对应的lab_id在试验室表中不存在，但可以进行排程");

        }
    }

    /**
     * 遍历 EquipmentHashMap 验证 Equipment 跨实体逻辑
     */
    @Deprecated
    private void checkEquipment () {

        for (EntityEquipment equipment : equipmentHashMap.values()) {

            // 小阶段所需设备的数量超过设备数量范围

            // 当设备id存在时，设备的功能模块不能为空

        }
    }

    /**
     * 遍历 EquipmentGroupRelHashMap 验证 EquipmentGroupRel 跨实体逻辑
     */
    @Deprecated
    private void checkEquipmentGroupRel () {

        for (Map.Entry<Integer, ArrayList<String>> entry : equipmentGroupRelHashMap.entrySet()) {
            // 设备组id在设备组表中不存在
            Integer equipmentGroupId = entry.getKey();
            CheckDataUtil.notExistTable(equipmentGroupHashMap, equipmentGroupId, ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                    ConstantUtil.MESSAGE_TYPE_FIELD_MISS, "设备组" + equipmentGroupId + "对应的设备组ID在设备组表中不存在，但可以进行排程");

            // 设备id在设备表中不存在
            ArrayList<String> equipmentIds = entry.getValue();
            for (String equipmentId : equipmentIds) {
                CheckDataUtil.notExistTable(equipmentHashMap, equipmentId, ConstantUtil.MESSAGE_SEVERITY_WARNNING,
                        ConstantUtil.MESSAGE_TYPE_FIELD_MISS, "设备" + equipmentId + "对应的设备组ID在设备组表中不存在，但可以进行排程");
            }
        }
    }



    /**
     * 遍历 SampleHashMap 验证 Sample 跨实体逻辑
     */
    /* //逻辑需要重新梳理- commented by Sophia
    private void checkSample () {

        for (EntitySample sample : sampleHashMap.values()) {
            // 一个样品组出现在多个任务单中
            Integer sampleGroupId = sample.getSampleGroupId();
            Integer taskId = sample.getTaskId();
            // key 为 sampleGroupId， value 为 taskId
            HashMap<Integer, Integer> map = new HashMap<>();
            if (!map.containsKey(sampleGroupId)) {
                map.put(sampleGroupId, taskId);
            } else {
                CheckDataUtil.isNotEquals(taskId, map.get(sampleGroupId), ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_ERROR_NOT_SCHEDULED,
                        "样品" + sample.getSampleId() + "对应的样品组出现在多个任务单中，无法进行排程");
            }

            // 任务单id在任务单表中不存在
            CheckDataUtil.notExistTable(taskHashMap, taskId, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS_NOT_SCHEDULED,
                    "样品" + sample.getSampleId() + "对应的任务单id在任务单表中不存在，无法进行排程");

            Integer functionId = taskHashMap.get(taskId).getFunctionId();
            // 所有的整车样品组Id
            ArrayList<Integer> vehicleGroupIds = functionHashMap.get(functionId).getVehicleGroupIds();
            // 样品组包括“整车”类型样件，该样品组又包含其他样件
            if (!StringUtils.equals("整车", sample.getSampleType())) {
                boolean b = vehicleGroupIds.contains(sampleGroupId);
                CheckDataUtil.isTure(b, ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_ERROR_NOT_SCHEDULED,
                        "样品" + sample.getSampleId() + "对应的样品组包含“整车”又包含其他样件，无法进行排程");

            }
        }
    }

     */

    /**
     * 遍历 SubTaskPlanInputHashMap 验证 SubTaskPlanInput 跨实体逻辑
     */
    private void checkSubTaskPlanInput () {

        for (Iterator<Map.Entry<String, EntitySubTaskPlanInput>> it = subTaskPlanInputHashMap.entrySet().iterator();it.hasNext();){

            Map.Entry<String, EntitySubTaskPlanInput> item = it.next();
            EntitySubTaskPlanInput subTaskPlanInput = item.getValue();
            String subTaskNo = subTaskPlanInput.getSubTaskNo();
            // 当批次id存在时，批次负责人不在人员表中
            String respEngineerId = subTaskPlanInput.getRespEngineerId();

            if (!CheckDataUtil.notExistTable(staffHashMap, respEngineerId, MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_FIELD_MISS,
                    "已排程批次 " + subTaskNo + " 对应的指定负责人 " + respEngineerId
                            +" 不在人员表中，忽略该设置，继续排程")) {
                subTaskPlanInput.setRespEngineerId(null);
                continue;
            }
            // 试验批次的指定负责人不是工程师
            EntityStaff staff = staffHashMap.get(respEngineerId);
            if (!CheckDataUtil.isNotEquals(staff.getStaffClassId(), STAFF_CLASS_ENGINEER, MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                    "已排程批次 " + subTaskNo + " 对应的指定负责人 " + staff.getStaffName() + "（ID "+respEngineerId
                            + "）不是工程师，忽略该设置，继续排程")){
                subTaskPlanInput.setRespEngineerId(null);
                continue;
            }

            // 试验批次的指定负责人不在试验规范的允许人员范围内
            EntityTask task = taskHashMap.get(subTaskPlanInput.getTaskNo());
            if (task == null){
                // 该批次可能已经无效
                it.remove();
                continue;
            }

        }


    }


    /**
     * 遍历 EquipmentPlanInputHashMap 验证 EquipmentPlanInput 跨实体逻辑
     */
    @Deprecated
    private void checkEquipmentPlanInput () {

        for (EntityEquipmentPlanInput equipmentPlanInput : equipmentPlanInputHashMap.values()) {
            String equipmentPlanId = equipmentPlanInput.getEquipmentPlanId();
            // 设备编号id在设备表中不存在
            String equipmentId = equipmentPlanInput.getEquipmentId();
            CheckDataUtil.notExistTable(equipmentHashMap, equipmentId, ConstantUtil.MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                    "设备计划" + equipmentPlanId + "对应的设备id在设备表中不存在，无法进行排程");

            // 小阶段计划id不为空，且在小阶段计划表中存在
            String stepPlanId = equipmentPlanInput.getStepPlanId();
            if (StringUtils.isEmpty(stepPlanId) || !stepPlanInputHashMap.containsKey(stepPlanId)) {
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                        "设备计划" + equipmentPlanId + "对应的小阶段计划为空或者在小阶段计划表中不存在，无法进行排程");
            }
        }
    }

    /**
     * 遍历 StepPlanInputHashMap 验证 StepPlanInput 跨实体逻辑
     */
    @Deprecated
    private void checkStepPlanInput () {

        for (EntityStepPlanInput stepPlanInput : stepPlanInputHashMap.values()) {
            // 小阶段计划id存在时，小阶段id在小阶段表中不为空
            String bomStepId = stepPlanInput.getBomNo() + stepPlanInput.getStepId() + "";
            CheckDataUtil.notExistTable(stepHashMap, bomStepId, ConstantUtil.MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                    "小阶段计划" + stepPlanInput.getStepPlanId() + "对应的小阶段ID在小阶段表中不存在，无法进行排程");
        }
    }

    /**
     * 遍历 StaffPlanInputHashMap 验证 StaffPlanInput 跨实体逻辑
     */
    @Deprecated
    private void checkStaffPlanInput () {

        for (EntityStaffPlanInput staffPlanInput : staffPlanInputHashMap.values()) {
            String staffPlanId = staffPlanInput.getStaffPlanId();
            // 当员工id存在时，员工id在员工表中不存在
            String staffId = staffPlanInput.getStaffId();
            CheckDataUtil.notExistTable(staffHashMap, staffId, ConstantUtil.MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                    "员工计划" + staffPlanId + "对应的员工ID在员工表中不存在，无法进行排程");

            // 当员工id存在时，小阶段计划id小阶段计划表中存在
            String stepPlanId = staffPlanInput.getStepPlanId();
            CheckDataUtil.notExistTable(stepPlanInputHashMap, stepPlanId, ConstantUtil.MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                    "员工计划" + staffPlanId + "对应的小阶段ID在小阶段表中不存在，无法进行排程");
        }

    }

    /**
     * 初始化 整个计划周期内的所有的工作和不工作的切换时刻
     */
    public void initializeWorkdaysMode(){

        // 添加两种工作模式
        workdaysModeTimes.put(STEP_WEEKDAYS_WORK, new ArrayList<>());
        workdaysModeValues.put(STEP_WEEKDAYS_WORK, new ArrayList<>());
        workdaysModeTimes.put(STEP_WEEKDAYS_WEEKEND_WORK, new ArrayList<>());
        workdaysModeValues.put(STEP_WEEKDAYS_WEEKEND_WORK, new ArrayList<>());


        // 将 Date 类型的日期转换成为 Calendar 类型，方便对日期进行计算
        Calendar start = DateUtil.dateToCalendar(plan.getPlanPeriodStartTime());
        if (plan.getPlanPeriodEndTime() == null){
            return;
        }
        Calendar end = DateUtil.dateToCalendar(plan.getPlanPeriodEndTime());



        // 初始化开始时间，设置时间为0点 0 分 0 秒
        Calendar cal = start;
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // 获得开始时间的一个副本
        Calendar cal2 = DateUtil.dateToCalendar(cal.getTime());

        // 向主模型中添加两种工作模式
        executeWorkdaysMode(STEP_WEEKDAYS_WORK, cal, end);
        executeWorkdaysMode(STEP_WEEKDAYS_WEEKEND_WORK, cal2, end);

        log.info("仅工作日的切换时刻:" + workdaysModeTimes.get(STEP_WEEKDAYS_WORK));
        log.info("仅工作日是否工作的数值:" + workdaysModeValues.get(STEP_WEEKDAYS_WORK));
        log.info("工作日和周末工作的切换时刻:" + workdaysModeTimes.get(STEP_WEEKDAYS_WEEKEND_WORK));
        log.info("工作日和周末工作是否工作的数值:" + workdaysModeValues.get(STEP_WEEKDAYS_WEEKEND_WORK));
    }

    /**
     * 计算出整个计划周期内的所有的工作和不工作的切换时刻，和是否工作对应的数值
     * @param stepWorkMode
     * @param cal
     * @param end
     */
    private void executeWorkdaysMode(String stepWorkMode, Calendar cal, Calendar end){
        // 记录当前状态：上班 or 休息
        Integer flag = calculateStartState(cal, stepWorkMode);
        workdaysModeValues.get(stepWorkMode).add(flag);

        // 遍历所有日期
        while (cal.before(end) || cal.equals(end)){
            // 特殊日期
            if (specialDayHashMap.containsKey(cal.getTime())) {
                EntitySpecialDay specialDay = specialDayHashMap.get(cal.getTime());
                String workDayType = specialDay.getWorkDayType();
                // 周末工作日
                if(SPECIAL_DAY_WEEKEND_WORK.equals(workDayType) && REST.equals(flag)){
                    flag = WORK;
                    addWorkState(stepWorkMode, cal, flag);
                // 非周末休息日和节假日周末
                }else if (SPECIAL_DAY_WORKDAY_REST.equals(workDayType) || SPECIAL_DAY_HOLIDAYS_WEEKEND.equals(workDayType)){
                    if(WORK.equals(flag)){
                        flag = REST;
                        addWorkState(stepWorkMode, cal, flag);
                    }
                }
                // 非特殊日期
            }else {

                // 该天为星期几
                int day = cal.get(Calendar.DAY_OF_WEEK);

                // 仅工作日
                if (StringUtils.equals(stepWorkMode, STEP_WEEKDAYS_WORK)){
                    // 周末
                    if (day == Calendar.SATURDAY || day == Calendar.SUNDAY){
                        if (WORK.equals(flag)){
                            flag = REST;
                            addWorkState(stepWorkMode, cal, flag);
                        }
                    }else if (REST.equals(flag)){
                        flag = WORK;
                        addWorkState(stepWorkMode, cal, flag);
                    }
                // 工作日和周末工作
                }else {
                    if (REST.equals(flag)){
                        flag = WORK;
                        addWorkState(stepWorkMode, cal, flag);
                    }
                }
            }
            // 时间加一天
            cal.add(Calendar.DATE, 1);
        }

    }

    /**
     * 计算当前时间是否上班
     * @param cal
     * @param stepWorkMode
     * @return
     */
    public Integer calculateStartState(Calendar cal,  String stepWorkMode){
        // 记录当前状态：上班 or 休息
        Integer flag;
        // 判断开始时间是上班还是休息
        if (specialDayHashMap.containsKey(cal.getTime())){
            String startWorkDayType = specialDayHashMap.get(cal.getTime()).getWorkDayType();
            if (SPECIAL_DAY_WORKDAY_REST.equals(startWorkDayType) ||
                    SPECIAL_DAY_HOLIDAYS_WEEKEND.equals(startWorkDayType)){
                flag = REST;
            }else {
                flag = WORK;
            }
        }else {
            // 仅工作日
            if (StringUtils.equals(stepWorkMode, STEP_WEEKDAYS_WORK)){
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                    flag = REST;
                }else {
                    flag = WORK;
                }
                // 工作日和周末工作
            }else {
                flag = WORK;
            }
        }

        return flag;
    }

    /**
     * 切换 flag，并向集合中添加数据
     * @param cal
     * @param flag
     */
    private void addWorkState(String stepWorkMode, Calendar cal, Integer flag){
        // workdaysModeTime 中添加节点
        workdaysModeTimes.get(stepWorkMode).add(cal.getTime());
        // workdaysModeValue 中添加 value
        workdaysModeValues.get(stepWorkMode).add(flag);
    }

    /**
     * Phase1 data include function，task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
     * @return 是否包含导致无法排程的错误
     */
    public Boolean loadAndProcessPhase1Data()  {
        loadAndProcessFunctionData();
        loadAndProcessTaskData();
        if (!schedulingFlag){
            return schedulingFlag;
        }
        loadAndProcessProcedureData();
        loadAndProcessPlanConfigData();
        // Bom data processing needs Plan config data
        loadAndProcessBomData();
        loadAndProcessSampleData();
        loadAndProcessSpecialDayData();
        // Sub task data processing needs all above data
        loadAndProcessSubTaskData();
        // Sub task plan input data processing needs sub task data
        loadAndProcessSubTaskPlanInputOfSameTaskData();
        loadAndProcessSubTaskPlanInputOfSameSubTaskGroupData();
        loadAndProcessSubTaskPlanInputOfSameUniqueSampleNoData();

        return schedulingFlag;
    }

    private void loadAndProcessFunctionData() {
        int functionId = plan.getFunctionId();

        loadData.loadFunctionData(dataContainer,functionId);
        // 过滤function，单体验证，得到function name，存放hashmap
        Iterator<EntityFunction> functionIterator = dataContainer.getFunctionIterator();
        int size = dataContainer.getFunctionArrayList().size();
        log.info("Loaded FUNCTION_TABLE data with sub task filtering: " + size + " records");

        while (functionIterator.hasNext()){
            EntityFunction function = functionIterator.next();
            if (function.getFunctionId() == functionId) {
                // 单实体验证
                function.singleCheck();
                functionHashMap.put(function.getFunctionId(), function);
                if(function.getFunctionName()!=null) {
                    log.info("功能块名称是 " + function.getFunctionName());
                }
                //只关心本次排程的功能块的数据
                break;
            }
        }
        if (functionHashMap.size() == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA_CAN_SCHEDULED,
                    "功能块" + functionId + "没有对应的功能块记录，但不影响排程");
            // 新建一个默认功能块
            EntityFunction function = new EntityFunction();
            function.setFunctionId(functionId);
            functionHashMap.put(functionId,function);
        }
        log.info("Loaded FUNCTION_TABLE data after validation: " + functionHashMap.size() + " records");
    }
    private void loadAndProcessTaskData() {
        // 单体验证，得到待排程任务单的hashmap
        loadData.loadTaskData(dataContainer,functionId);
        Iterator<EntityTask> taskIterator = dataContainer.getTaskIterator();
        int size = dataContainer.getTaskArrayList().size();
        log.info("Loaded TASK data with sub task filtering: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_INFO, MESSAGE_TYPE_NOT_ENOUGH_SCHEDULING_CONDITIONS,
                    "功能块" + functionId + "没有任何待排程任务单数据，不会启动排程");
            schedulingFlag = false;
            return;
        }
        Date maxApprovalDateOfTasks = null;
        while (taskIterator.hasNext()){
            EntityTask task = taskIterator.next();
            // 单实体验证
            if (task.singleCheck()){
                if (taskNos != null) {
                    //只排要求的task
                    if (taskNos.contains(task.getTaskNo())){
                        taskHashMap.put(task.getTaskNo(), task);
                    }
                } else {
                    taskHashMap.put(task.getTaskNo(), task);
                }
                if (maxApprovalDateOfTasks == null){
                    maxApprovalDateOfTasks = task.getApproveCompleteDate();
                } else {
                    if (maxApprovalDateOfTasks.before(task.getApproveCompleteDate())){
                        maxApprovalDateOfTasks = task.getApproveCompleteDate();
                    }
                }
            }
        }
        EntityFunction function = functionHashMap.get(functionId);
        function.setMaxApprovalDateOfTasks(maxApprovalDateOfTasks);
        size = taskHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_NOT_ENOUGH_SCHEDULING_CONDITIONS,
                    "功能块" + functionId + "没有有效的待排程任务单数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded TASK data after validation: " + size + " records");
    }
    private void loadAndProcessProcedureData() {
        // 单体验证，得到待排程批次对应的规范的hashmap
        loadData.loadProcedureData(dataContainer,functionId);
        Iterator<EntityProcedure> procedureIterator = dataContainer.getProcedureIterator();
        int size = dataContainer.getProcedureArrayList().size();
        log.info("Loaded PROCEDURE_TABLE data with sub task filtering: " + size + " records");
        if (size == 0) {
            // 规范表可以为空，规范的唯一意义是查看规范授权，有可能非标BOM，对应非标规范，没有授权
            return;
        }
        while (procedureIterator.hasNext()){
            EntityProcedure procedure = procedureIterator.next();
            // 单实体验证
            if (procedure.singleCheck()) {
                procedureHashMap.put(procedure.getProcedureNo(), procedure);
            }
        }
        size = procedureHashMap.size();
        // 规范表可以为空，规范的唯一意义是查看规范授权，有可能非标BOM，对应非标规范，没有授权
        log.info("Loaded PROCEDURE_TABLE data after validation: " + size + " records");
    }
    private void loadAndProcessPlanConfigData() {
        // 单体验证，得到待排程批次对应的BOM对应的 排程参数的 hashmap
        loadData.loadPlanConfigData(dataContainer,functionId);
        Iterator<EntityPlanConfig> planConfigIterator = dataContainer.getPlanConfigIterator();
        int size = dataContainer.getPlanConfigArrayList().size();
        log.info("Loaded PLAN_CONFIG_TEMPLATE data with sub task filtering: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有任何排程参数模板数据，不会启动排程");
            schedulingFlag = false;
            return;
        }
        while (planConfigIterator.hasNext()){
            EntityPlanConfig planConfig = planConfigIterator.next();
            // 单实体验证
            if (planConfig.singleCheck()) {
                planConfigHashMap.put(planConfig.getPlanConfigId(), planConfig);
            }
        }
        size = planConfigHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有有效的排程参数模板数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded PLAN_CONFIG_TEMPLATE data after validation: " + size + " records");
    }
    private void loadAndProcessBomData() {
        // 单体验证，得到待排程批次对应的BOM的hashmap
        loadData.loadBomData(dataContainer,functionId);
        Iterator<EntityBom> bomIterator = dataContainer.getBOMIterator();
        int size = dataContainer.getBomArrayList().size();
        log.info("Loaded BOM data with sub task filtering: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有任何BOM数据，不会启动排程");
            schedulingFlag = false;
            return;
        }

        ArrayList<String> procedureNoes = new ArrayList<>();
        while (bomIterator.hasNext()){
            EntityBom bom = bomIterator.next();
            // 单实体验证
            if (bom.singleCheck()) {
                String bomNo = bom.getBomNo();
                // BOM_NO有重复
                if (bomHashMap.containsKey(bomNo)){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_REPEAT_DATA,
                            "BOM " + bomNo + " 有重复记录，忽略多余记录，继续排程");
                }else {
                    // PROCEDURE_NO有重复
                    if (procedureNoes.contains(bom.getProcedureNo())){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_REPEAT_DATA,
                                "BOM " + bomNo + " 对应的TEST_PROCEDURE_NO有重复记录，忽略多余记录，继续排程");
                    }else {
                        // 在规范中添加 bomId
                        EntityProcedure procedure = procedureHashMap.get(bom.getProcedureNo());

                        if (procedure == null) {
                            //  标准BOM应该有对应的规范，检查是否应该在这里做？
                            // 目前是在loadAndProcessSubTaskData中做的
                        } else {
                            procedure.setBom(bom);
                        }
                        EntityPlanConfig planConfig = planConfigHashMap.get(bom.getPlanConfigId());
                        if (planConfig != null) {

                            // planConfig 数据验证相关 task 是否排程
                            if (!planConfig.singleCheckTask()){
                                // 相关 task 不排程
                                invalidBom.add(bomNo);
                                //todo: 这时bom和task的map还没建立吧？下面一句没有用吧？
                                setSameBomTasksNotScheduling(bomNo);
                            }
                            bom.setPlanConfig(planConfig);
                        } else{
                            invalidBom.add(bomNo);
                        }
                        bomHashMap.put(bom.getBomNo(), bom);
                    }
                }
            }
        }
        size = bomHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有有效的BOM数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded BOM data after validation: " + size + " records");
    }
    private void loadAndProcessSampleData() {
        // 单体验证，得到待排程批次和待排程批次所在任务单中的已完成批次使用的样品组，
        // 样品组内的样品(sample group和其包含的sample list的hashmap)
        // 获得样品组和其中的unique sample的map
        loadData.loadSampleBySubTask(dataContainer,functionId);
        Iterator<EntitySample> sampleIterator = dataContainer.getSampleIterator();
        int size = dataContainer.getSampleArrayList().size();
        log.info("Loaded SAMPLE data with sub task filtering: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有样品组数据，不会启动排程");
            schedulingFlag = false;
            return;
        }

        while (sampleIterator.hasNext()){
            EntitySample sample = sampleIterator.next();
            // 单实体验证
             if (sample.singleCheck()) {
                String sampleType = sample.getSampleType();
                Integer sampleId = sample.getSampleId();
                String sampleNo = sample.getSampleNo();


                // 获取样品组和样品的列表的map
                Integer sampleGroupId = sample.getSampleGroupId();
                ArrayList<EntitySample> samples = sampleGroupSamplesHashMap.computeIfAbsent(sampleGroupId, k->new ArrayList<>());
                samples.add(sample);
                sampleGroupSamplesHashMap.put(sampleGroupId, samples);
                // 整车处理

                if (sampleType != null && sampleType.equals(SAMPLE_TYPE_CAR)){
                    // 获取整车唯一编号
//                    String sampleNo = sample.getSampleNo();
                    //添加sample group内包含整车编号的map
                    String sampleNoAlreadyIn = sampleGroupUniqueSampleNoMap.computeIfAbsent(sampleGroupId, k->sampleNo);
                    if (!sampleNoAlreadyIn.equals(sampleNo)) {
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_TIME_IGNORE_REDUNDANT_RECORDS,
                                "批次的样品组"+ sampleGroupId + "包含整车，但该样品组包含多个样品，忽略其他样件，继续排程");
                    }
                }
            }
        }
        size = sampleGroupSamplesHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有有效的样品组数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded SAMPLE GROUP data after validation: " + size + " records");
        log.info("Loaded SAMPLE GROUP data with unique sample no: " + sampleGroupUniqueSampleNoMap.size() + " records");
    }
    private void loadAndProcessSpecialDayData() {
        // 得到特殊日期的hashmap，用于后续配合模型参数模板计算子模型的准备时间，委托天数等
        loadData.loadSpecialDayData(dataContainer);
        Iterator<EntitySpecialDay> specialDayIterator = dataContainer.getSpecialDayIterator();
        int size = dataContainer.getSpecialDayArrayList().size();
        log.info("Loaded SPECIAL_DAY data with date filtering: " + size + " records");
        while (specialDayIterator.hasNext()){
            EntitySpecialDay specialDay = specialDayIterator.next();
            specialDayHashMap.put(specialDay.getSpecialDay(), specialDay);
        }
        log.info("Loaded SPECIAL_DAY data after validation: " + specialDayHashMap.size() + " records");
    }

    /**
     * 单体验证, 得到待排程批次列表
     * 验证相应的task，procedure，bom, plan_config, sample是否都有效
     * 组装unique sample和subtask的map
     * 决定是否需要拆分模型，即分配待排程的批次到几个模型中依次solve
     * 设置sub task和task的isToBePlanned属性，组装task下的待排程批次和批次等数据结构
     */
    private void loadAndProcessSubTaskData()  {
        loadData.loadSudTaskData(dataContainer, functionId);
        Iterator<EntitySubTask> subTaskIterator = dataContainer.getSubTaskIterator();
        int size = dataContainer.getSubTaskArrayList().size();
        log.info("Loaded SUB_TASK data: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块 " + functionId + " 没有任何待排程批次数据，导致无法进行排程");
            schedulingFlag = false;
            return;
        }
        while (subTaskIterator.hasNext()){
            EntitySubTask subTask = subTaskIterator.next();
            String subTaskNo = subTask.getSubTaskNo();
            // 调用批次的单实体验证
            // 过滤 subTask
            if (!subTask.singleCheck()){
                continue;
            }
            // 判断相关的 task 是否排程
            if (!subTask.singleCheckTask()){
                EntityTask task = taskHashMap.get(subTask.getTaskNo());
                if (task != null){
                    task.setIsToBePlanned(false);
                    continue;
                }
            }

            // 获得function
            int functionId = subTask.getFunctionId();
            EntityFunction function = functionHashMap.get(functionId);

            boolean errorFlag = false;
            // 验证和任务单的关系
            String taskNo = subTask.getTaskNo();
            EntityTask task = taskHashMap.get(taskNo);
            if (task == null) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                        "待排程批次 " + subTaskNo + " 对应的任务单 " + taskNo
                               + " 在任务单表中不存在 或 无效，导致该批次无法排程");

                errorFlag = true;
            }
            // 验证和规范的关系

            // 验证和BOM的关系
            String bomNo = subTask.getBomNo();
            EntityBom bom = bomHashMap.get(bomNo);
            if (bom == null) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                        "待排程批次 " + subTaskNo + " 对应的BOM "+ bomNo +" 在BOM表中不存在 或 无效，导致相关任务单"
                                + taskNo + " 无法排程");
                errorFlag = true;
            } else {
                String procedureNo = bom.getProcedureNo();
                EntityProcedure procedure = procedureHashMap.get(procedureNo);
                if (bom.getBomType().equals(BOM_TYPE_STANDARD) && procedure == null){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                            "BOM " + bomNo + " 为标准BOM，但没有对应的规范，导致相关任务单"
                            + taskNo + "无法排程");
                    errorFlag = true;
                }

                if (bom.getPlanConfig() == null) {
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_MISS,
                            "待排程批次 " + subTaskNo + " 对应的BOM " + bomNo
                                    +" 对应的排程参数模板 "+ bom.getPlanConfigId() + " 在排程参数表中不存在 或 无效，导致相关任务单 "
                                    + taskNo +" 无法排程");
                    invalidBom.add(bomNo);
                    setSameBomTasksNotScheduling(bomNo);
                    errorFlag = true;
                }

                if (!bomHashMap.containsKey(bom.getBomNo())){
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                            "待排程批次" + subTaskNo + "对应的BOM "+ bomNo +" 在BOM表中不存在 或 无效，导致相关任务单 "
                                    + taskNo +" 无法排程");
                    invalidBom.add(bomNo);
                    setSameBomTasksNotScheduling(bomNo);
                    errorFlag = true;
                }
            }

            // 验证和sample group的关系
            if (!sampleGroupSamplesHashMap.containsKey(subTask.getSampleGroupId())) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                        "待排程批次" + subTaskNo + " 的样品组 " + subTask.getSampleGroupId()
                                + " 在样品表中不存在 或 无效，忽略样品组相关要求，继续排程");
                //errorFlag = true;
            }

            if (errorFlag){
                continue;
            }else {
                // subTask 对应的 bom 没有问题
                // 统计相同 BOM 的 task hashSet
                HashSet<EntityTask> bomTasks = bomSameTaskMap.computeIfAbsent(bomNo, k -> new HashSet<>());
                bomTasks.add(task);
            }

            //为 subtask 内属性赋值
            subTask.setBom(bom);
            EntityPlanConfig planConfig = bom.getPlanConfig();

            subTask.setPlanConfig(planConfig);


            // 考虑根据排程参数划分子模型, 以及判断批次样件提供时间是否晚于委托天数，如果晚于，排除该批次和任务单
            //如果批次本身没有被赋予 排程颗粒度和时间限制的值，则使用BOM对应的排程参数的值
            if (subTask.getPlanGranul() == null) {
                subTask.setPlanGranul(planConfig.getPlanGranul());
            }

            //设置其他排程相关属性
            if (subTask.getPlanTimeLimit() == null) {
                subTask.setPlanTimeLimit(planConfig.getTimeLimit());
            }
            subTask.setCalendarDays(planConfig.getCalendarDays());

            if (planConfig.getMinPlanStartTime() == null){
                boolean isWeekendInclude = false;
                if (planConfig.getIsWeekendINC().equals(PLAN_CONFIG_IS_WEEKEND_INCLUDE)){
                    isWeekendInclude = true;
                }
                // planConfig内的计算还没有做过，进行各种计算
                planConfig.setMinPlanStartTime(EntityPlanConfig.calculateMinPlanStartTime(
                        plan.getPlanPeriodStartTime(),
                        planConfig.getLeadFence(),
                        isWeekendInclude,
                        specialDayHashMap));
                //更改为从trigger的时间开始算
                //planConfig.setPlanEndTime(DateUtil.addDay(planConfig.getMinPlanStartTime(),planConfig.getCalendarDays()));
                planConfig.setPlanEndTime(DateUtil.addDay(plan.getPlanPeriodStartTime(),planConfig.getCalendarDays()));
                planConfig.setMaxAcceptedSubTaskStartTime(EntityPlanConfig.calculateMaxAcceptedSubTaskStartTime(
                        plan.getPlanPeriodStartTime(),
                        planConfig.getEntrustDays(),
                        isWeekendInclude,
                        specialDayHashMap));
            }
            subTask.setMinPlanStartTime(planConfig.getMinPlanStartTime());
            subTask.setMaxPlanEndTime(planConfig.getPlanEndTime());
            // 判断批次的样件提供时间是否晚于委托天数，如果晚，则不考虑该批次和相应的任务单
            if (subTask.getProvideDate().after(planConfig.getMaxAcceptedSubTaskStartTime())){
                //该批次样件提供时间晚于委托天数，不会被考虑在排程的范围内，相应的任务单也不会被考虑
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_INVALID_DATA_CAN_SCHEDULED,
                        "批次 " + subTaskNo + " 的样件提供时间是 "+ subTask.getProvideDate()
                                +"，晚于委托天数 "+ planConfig.getEntrustDays()
                                +"，不会被考虑在排程的范围内，相应的任务单 "+ taskNo +" 不会参与排程");
                subTask.setIsToBePlanned(false);
                task.setIsToBePlanned(false);
                errorFlag = true;
            }
            // 判断批次的样件提供时间是否晚于模型结束时间，如果晚，则不考虑该批次和相应的任务单
            if (subTask.getProvideDate().after(planConfig.getPlanEndTime())){
                //warning,该批次样件提供时间晚于生成日历天数，不会被考虑在排程的范围内，相应的任务单也不会被考虑
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                        "批次 " + subTaskNo + " 样件提供时间是 "+ subTask.getProvideDate()
                                +"，晚于生成日历天数 "+ planConfig.getCalendarDays()
                                +"，不会被考虑在排程的范围内，相应的任务单 "+ taskNo +" 不会参与排程");
                subTask.setIsToBePlanned(false);
                task.setIsToBePlanned(false);
                errorFlag = true;
            }


            if (errorFlag){
                subTask.setIsToBePlanned(false);
                if (task != null){
                    task.setIsToBePlanned(false);
                }
                continue;
            }
            // 根据样件提供时间和排程开始时间的最大值来设置批次的最早开始时间
            subTask.setMinPlanStartTime(DateUtil.getMaxTime(subTask.getMinPlanStartTime(),subTask.getProvideDate()));

            // 处理没有数据问题的批次
            // 将 sub task 加到 No 和实体对应的 map中
            subTaskHashMap.put(subTask.getSubTaskNo(), subTask);

            // 将该 subTask 加到 Task 中
            ArrayList<EntitySubTask> subTasks = task.getSubTasks();
            subTasks.add(subTask);

            plan.setMaxPlanPeriodEndTime(subTask.getMaxPlanEndTime());
            //为subTask添加sample列表，给整车hashmap赋值
            processSampleDataForSubTask(subTask);
            // 将 subTask 按照批次组分组，并存入到 subTaskGroupHashMap 中
            // 批次组
            processSubTaskGroupForSubTask(subTask);

            // 将 sub task 加到 task 的 toBePlannedSubTasks中
            ArrayList<EntitySubTask> toBePlannedSubTasks = task.getToBePlannedSubTasks();
            toBePlannedSubTasks.add(subTask);
            // 设置sub task的is_to_be_planned属性
            subTask.setIsToBePlanned(true);
            // 将对应的task 加入toBePlannedTasks 中，并设置is_to_be_planned属性
            ArrayList<EntityTask> toBePlannedTasks = function.getToBePlannedTasks();
            if (!task.getIsToBePlanned()){
                toBePlannedTasks.add(task);
                task.setIsToBePlanned(true);
            }
            //设置批次对应的granularity hashmap
            Integer granularity = subTask.getPlanGranul();
            //如果没有生成过该sub model，生成这个sub model，并且放入sub model的map中
            ArrayList<EntitySubTask> subTasks2 = granularitySubTaskHashMap.computeIfAbsent(granularity, k->new ArrayList<>());
            subTasks2.add(subTask);

            //设置批次对应的子模型名称
//            String subModelName = createSubModelName(subTask.getPlanGranul());
//            subTask.setSubModelName(subModelName);
//            //如果没有生成过该sub model，生成这个sub model，并且放入sub model的map中
//            SubModel subModel = subModelHashMap.get(subModelName);
//            if (subModel == null){
//                subModel = new SubModel(String.valueOf(plan.getPlanId()), schedulingTask, subModelName);
//                subModelHashMap.put(subModelName, subModel);
//            }
//            //更新sub model的plan config 参数
//            SubModelData subModelData = subModel.getSubModelData();
//            subModelData.setPlanGranularity(subTask.getPlanGranul());
//            subModelData.setMaxCalendarDays(subTask.getCalendarDays());
//            subModelData.setMaxTimeLimit(subTask.getPlanTimeLimit());
//            subModelData.setMinPlanStartTime(subTask.getMinPlanStartTime());
//            subModelData.setMaxPlanEndTime(subTask.getMaxPlanEndTime());
//            plan.setMaxPlanPeriodEndTime(subTask.getMaxPlanEndTime());
//            //将批次加入sub model data的子模型待排程批次中
//            HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMapInSubModel
//                    = subModelData.getToBePlannedSubTaskMap();
//            if (!toBePlannedSubTaskMapInSubModel.containsKey(taskNo)) {
//                ArrayList<EntitySubTask> tempSubTasks = new ArrayList<>();
//                toBePlannedSubTaskMapInSubModel.put(taskNo, tempSubTasks);
//            }
//            ArrayList<EntitySubTask> tempSubTasks = toBePlannedSubTaskMapInSubModel.get(taskNo);
//            tempSubTasks.add(subTask);
        }
        size = subTaskHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块 " + functionId + " 没有有效的待排程任务单数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded SUB_TASK data after validation: " + size + " records");
    }

    /**
     * 将 subTask 按照批次组分组，并存入到 subTaskGroupHashMap 中
     */
    private void processSubTaskGroupForSubTask(EntitySubTask subTask) {
        String taskGroup = subTask.getTaskGroup();
        if (taskGroup != null && !taskGroup.equals("")){
            ArrayList<EntitySubTask> subTasks = subTaskGroupHashMap.computeIfAbsent(taskGroup, k -> new ArrayList<>());
            subTasks.add(subTask);
        }
    }

    /**
     * 为subTask添加sample列表，给整车hashmap uniqueSampleSupportedSubtasks赋值
     * @param subTask
     */
    private void processSampleDataForSubTask(EntitySubTask subTask){
        int sampleGroupId = subTask.getSampleGroupId();
        ArrayList<EntitySample> samples = sampleGroupSamplesHashMap.get(sampleGroupId);
        subTask.setSamples(samples);
        String sampleNo = sampleGroupUniqueSampleNoMap.get(sampleGroupId);
        if (sampleNo != null){
            //为subTask添加整车
            subTask.setUniqueSampleNo(sampleNo);
            //添加主模型的整车列表， 并为整车添加subtask列表
            ArrayList<EntitySubTask> subTasksTemp
                    = uniqueSampleSupportedSubtasks.computeIfAbsent(sampleNo, k -> new ArrayList<>());
            subTasksTemp.add(subTask);
        }
    }

    /**
     * 单体验证，得到待排程任务单下的已排程批次
     * 创建对应Sub Task实体，设置各种属性
     * 完成任务单下完整批次的列表
     */
    private void loadAndProcessSubTaskPlanInputOfSameTaskData() {
        loadData.loadSubTaskPlanInputOfSameTaskData(dataContainer, functionId);
        Iterator<EntitySubTaskPlanInput> subTaskPlanInputIterator = dataContainer.getSubTaskPlanInputIterator();
        int size = dataContainer.getSubTaskPlanInputArrayList().size();
        log.info("Loaded SUB_TASK_PLAN_INPUT data of same task: " + size + " records");
        while (subTaskPlanInputIterator.hasNext()){
            EntitySubTaskPlanInput subTaskPlanInput = subTaskPlanInputIterator.next();
            EntityTask task = taskHashMap.get(subTaskPlanInput.getTaskNo());
            String subTaskNo = subTaskPlanInput.getSubTaskNo();
            // 单体验证，得到待排程任务单下的已排程批次
            if (task.getIsToBePlanned() && !subTaskHashMap.containsKey(subTaskNo)
                    && subTaskPlanInput.singleCheck(true)) {
                subTaskPlanInputHashMap.put(subTaskPlanInput.getSubTaskNo(), subTaskPlanInput);
                // 设置subTaskPlanInput的一些属性
                subTaskPlanInput.calculateSubTaskStartEndInModel();

                // 新建对应的sub task实体
                EntitySubTask subTask = new EntitySubTask();
                subTask.setTaskNo(task.getTaskNo());
                subTask.setSubTaskNo(subTaskNo);
                //subTask.setTaskGroup(subTaskPlanInput.getTaskGroup());
                //subTask.setTaskSeqNo(subTaskPlanInput.getTaskSeqNo());
                subTask.setAssignedRespEngineerId(subTaskPlanInput.getRespEngineerId());
                subTask.setStartTime(subTaskPlanInput.getSubTaskStartInModel());
                subTask.setEndTime(subTaskPlanInput.getSubTaskEndInModel());
                subTask.setIsToBePlanned(false);
                // 因为不知道该批次所在的任务单内的待排程批次的排程时间范围，所以无法现在就判断是否是ignored，都设置为fixed
                subTask.setFixed(true);
                //subTask.setSampleGroupId(subTaskPlanInput.getSampleGroupId());
                //为subTask添加sample列表，给整车hashmap赋值
                //应该不需要，在处理整车sample的函数里面处理
                //processSampleDataForSubTask(subTask);
                // 将 subTask 按照批次组分组，并存入到 task 中
                //应该不需要，在处理批次组的函数里面处理
                //processSubTaskGroupForSubTask(subTask,task);

                // 将 sub task 加到 No 和实体对应的 map中
                subTaskHashMap.put(subTask.getSubTaskNo(), subTask);

                // 将该 subTask 加到 Task 下所有批次的列表中
                ArrayList<EntitySubTask> subTasks = task.getSubTasks();
                subTasks.add(subTask);

            }
        }
        log.info("Loaded SUB_TASK_PLAN_INPUT data after validation: " + subTaskPlanInputHashMap.size() + " records");
    }

    /**
     * 单体验证，得到和待排程批次在同一个批次组的已排程批次
     * 创建对应Sub Task实体，结果存储到subTaskGroupHashMap
     * 完成完整批次组和批次组下批次的列表
     */
    private void loadAndProcessSubTaskPlanInputOfSameSubTaskGroupData() {
        loadData.loadSubTaskPlanInputOfSameSubTaskGroupData(dataContainer, functionId);
        Iterator<EntitySubTaskPlanInput> subTaskPlanInputIterator = dataContainer.getSubTaskPlanInputOfSameSubTaskGroupArrayList().iterator();
        int size = dataContainer.getSubTaskPlanInputOfSameSubTaskGroupArrayList().size();
        log.info("Loaded SUB_TASK_PLAN_INPUT data of same sub task group: " + size + " records");
        while (subTaskPlanInputIterator.hasNext()){
            EntitySubTaskPlanInput subTaskPlanInput = subTaskPlanInputIterator.next();
            // 单体验证，得到待排程任务单下的已排程批次
            if (!subTaskPlanInput.singleCheck(false)) {
                continue;
            }
            String taskGroup = subTaskPlanInput.getTaskGroup();
            if (taskGroup == null || taskGroup.equals("")) {
                continue;
            }
            subTaskPlanInputForSubTaskGroupHashMap.put(subTaskPlanInput.getSubTaskNo(), subTaskPlanInput);
            // 设置subTaskPlanInput的一些属性
            subTaskPlanInput.calculateSubTaskStartEndInModel();

            // 新建对应的sub task实体
            EntitySubTask subTask = new EntitySubTask();
            subTask.setSubTaskNo(subTaskPlanInput.getSubTaskNo());
            subTask.setTaskGroup(subTaskPlanInput.getTaskGroup());
            // 验证taskSeqNo不为空
            // 当批次id存在时，试验批次顺序号不得为空

            if (!CheckDataUtil.notBlank(subTaskPlanInput.getTaskSeqNo(), MESSAGE_SEVERITY_WARNNING,
                    ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                        "已排程批次 " + subTaskPlanInput.getSubTaskNo() + " 设置了批次组 " + taskGroup
                                + "，但没有对应的试验顺序批次号，忽略该批次，继续排程 ")){
                continue;
            }
            subTask.setTaskSeqNo(subTaskPlanInput.getTaskSeqNo());
            subTask.setStartTime(subTaskPlanInput.getSubTaskStartInModel());
            subTask.setEndTime(subTaskPlanInput.getSubTaskEndInModel());
            subTask.setIsToBePlanned(false);
            subTask.setFixed(true);
            ArrayList<EntitySubTask> subTasks = subTaskGroupHashMap.get(taskGroup);
            if (subTasks != null) {
                subTasks.add(subTask);
            }
        }
        log.info("Loaded SUB_TASK_PLAN_INPUT data of same sub task group after validation: " + subTaskPlanInputHashMap.size() + " records");
    }

    /**
     * 得到使用了待排程批次的unique sample的已排程批次
     */
    private void loadAndProcessSubTaskPlanInputOfSameUniqueSampleNoData() {
        Set uniqueSampleNoSet = uniqueSampleSupportedSubtasks.keySet();
        if (uniqueSampleNoSet.size() == 0 ){
            return;
        }
        loadData.loadSubTaskPlanInputOfSameUniqueSampleData(dataContainer, functionId, uniqueSampleNoSet);
        Iterator<EntitySubTaskPlanInput> subTaskPlanInputIterator = dataContainer.getSubTaskPlanInputOfSameUniqueSampleNoIterator();
        int size = dataContainer.getSubTaskPlanInputOfSameUniqueSampleNoArrayList().size();
        log.info("Loaded SUB_TASK_PLAN_INPUT data of same unique sample no: " + size + " records");
        while (subTaskPlanInputIterator.hasNext()) {
            EntitySubTaskPlanInput subTaskPlanInput = subTaskPlanInputIterator.next();

            // 单体验证，得到待排程任务单下的已排程批次
            if (!subTaskPlanInput.singleCheck(false)) {
                continue;
            }
            String sampleNo = subTaskPlanInput.getSampleNo();
            subTaskPlanInputForSampleHashMap.put(subTaskPlanInput.getSubTaskNo(), subTaskPlanInput);
            subTaskPlanInput.calculateSubTaskStartEndInModel();
            ArrayList<EntitySubTaskPlan> subTaskPlans =
                    uniqueSampleSupportedSubtaskPlans.computeIfAbsent(sampleNo, k -> new ArrayList<>());
            subTaskPlans.add(subTaskPlanInput);
        }
        log.info("Loaded total SUB_TASK_PLAN_INPUT data after validation: " + subTaskPlanInputForSampleHashMap.size() + " records");
    }
    /**
     * Phase2 data include step, special_day
     * @return 是否包含导致无法排程的错误
     */
    public boolean loadAndProcessPhase2Data() {
        loadAndProcessStepData();
        return schedulingFlag;
    }

    private void loadAndProcessStepData(){
        // 获取待排程BOM中的小阶段 Map
        loadData.loadStepData(dataContainer, functionId);
        Iterator<EntityStep> stepIterator = dataContainer.getStepIterator();
        int size = dataContainer.getStepArrayList().size();
        log.info("Loaded STEP data with sub task filtering: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有任何待排程批次对应的小阶段数据");
            return;
        }
        while (stepIterator.hasNext()){
            EntityStep step = stepIterator.next();
            // bom 中包含的所有的 step
            String bomNo = step.getBomNo();
            EntityBom bom = bomHashMap.get(bomNo);
            if (bom == null){
                continue;
            }
            // 单实体验证
            int resultOfSingleCheck = step.singleCheckStep();
            if (resultOfSingleCheck == RESULT_INVALID) {
                continue;
            } else if(resultOfSingleCheck == RESULT_CAN_NOT_SCHEDULE){
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);
                continue;
            }
            step.calculateStepHours();
            double timeLength = step.getTimeLengthConsideredWorkMode();

            // 用 bomNo + stepId + "" 的字符串来充当 step 的id
            String index = "" + step.getBomNo() + step.getStepId();
            bom.getSteps().add(step);
            if (bom.getPlanConfig() == null) {
                continue;
            }
            Integer calendarDays = bom.getPlanConfig().getCalendarDays();
            // 按天工作时，工作天数>生成日历天数
            if (RESOURCE_GROUP_DAILY_MODE_BY_DAY.equals(step.getEngineerDailyMode())){
                if (step.getEngineerDayNum() > calendarDays) {
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ILLEGAL,
                            "BOM " + bomNo + " 的小阶段 " + step.getStepId() + " 的工程师工作天数过长，为 " +
                                    step.getEngineerDayNum() + "，导致相关任务单无法排程");
                    // 将用到该 bom 的所有 task 都不排程
                    invalidBom.add(bomNo);
                    setSameBomTasksNotScheduling(bomNo);
                }

                // 按天工作时，每天工作时长>工程师的最大每天工时
                if (step.getEngineerDailyWorktime().doubleValue() > MAX_DAILY_WORK_TIME){
                    CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_ILLEGAL,
                            "BOM " + bomNo + " 的小阶段 " + step.getStepId() + " 的工程师每天工作时长过长，为 "
                                    + step.getEngineerDailyWorktime() + "，导致相关任务单无法排程");
                    invalidBom.add(bomNo);
                    setSameBomTasksNotScheduling(bomNo);
                }
            }
            stepHashMap.put(index,step);


            // 从小阶段可以开始排程时间到排程结束时间之内，不足小阶段的时长
            int planLength = DateUtil.getDistanceIntTime(plan.getPlanPeriodEndTime(), plan.getPlanPeriodStartTime(), 1000 * 60 * 60);
            if (timeLength > planLength){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
                        "BOM " + bomNo + " 的小阶段 " + step.getStepId() + " 可以开始的时间为 " + DateUtil.getDateString(plan.getPlanPeriodStartTime()) +
                        "，工作时间要求（考虑工作模式后）为 " + timeLength + " 小时，排程结束时间为 "  + DateUtil.getDateString(plan.getPlanPeriodEndTime()) + "，不可能完成该小阶段，导致相关任务单无法排程");
                // 将该 BOM 相关的 task 都设置为不排程
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);

            }
            if (step.getTestPhase().equals(REPORTING_PHASE_NAME)){
                bom.setHasReportingStep(true);
                log.info("STEP "+ index + " is a reporting phase." );
            }
            if (!step.singleCheckStepEngineer()) {
                continue;
            }
            // 做 engineer 的需求 的相关预处理
            log.info("STEP "+ step.getStepId() + " of BOM " + step.getBomNo() + " requires " + step.getEngineerCount() + " engineers.");
            EntityResourceGroup resourceGroup = new EntityResourceGroup();
            resourceGroup.setResourceType(RESOURCE_TYPE_ENGINEER);
            resourceGroup.setResourceGroupName(DEFAULT_ENGINEER_RESOURCE_GROUP_NAME);
            resourceGroup.setRequestedResourceQuantity(step.getEngineerCount());
            String dailyMode = step.getEngineerDailyMode();
            resourceGroup.setRequestedDailyMode(dailyMode);
            if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)){
                resourceGroup.setRequestedWorkTime(step.getEngineerWorktime().doubleValue());
            } else {
                resourceGroup.setRequestedWorkTime(step.getEngineerDailyWorktime().doubleValue());
                resourceGroup.setRequestedDayQuantity(step.getEngineerDayNum());
            }
            if (step.getEngineerWorktimeType().equals(STAFF_SAME_START)){
                resourceGroup.setSameStart(true);
            } else if (step.getEngineerWorktimeType().equals(STAFF_SAME_END)){
                resourceGroup.setSameEnd(true);
            }
            if (step.getIsEngineerConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT)){
                resourceGroup.setConstraint(true);
            }
            step.getResourceGroupHashMap().put(resourceGroup.getResourceGroupName(), resourceGroup);

        }
        size = stepHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块 " + functionId + " 没有有效的待排程批次对应的小阶段数据");
        }
        log.info("Loaded STEP data after validation: " + size + " records");

        // 初始化 整个计划周期内的所有的工作和不工作的切换时刻
        initializeWorkdaysMode();
    }

    /**
     * 将该 BOM 相关的 task 都设置为不排程
     * @param bomNo
     */
    private void setSameBomTasksNotScheduling(String bomNo){
        HashSet<EntityTask> tasks = bomSameTaskMap.get(bomNo);
        if (tasks != null && tasks.size() != 0){
            for (EntityTask task : tasks) {
                task.setIsToBePlanned(false);
            }
        }
    }
    /**
     * // Phase3 data include resources related data
     * @return 是否包含导致无法排程的错误
     */
    public boolean loadAndProcessPhase3Data() {
        loadAndProcessStaff();
        loadAndProcessStepSkill();
        loadAndProcessEngineer();
        loadAndProcessTechnician();
        loadAndProcessSkill();
        loadAndProcessStaffCalendar();
        loadAndProcessEquipment();
        loadAndProcessEquipmentCalendar();
        loadAndProcessCalendar();
        loadAndProcessRollCalendar();
        checkStaffCalendar();
        checkEquipmentCalendar();

        loadAndProcessTechnicianSkill();
        loadAndProcessAuthProcedure();
        loadAndProcessAuthBom();
        loadAndProcessAuthStep();
//        calculateStepPossibleStaffs();

        loadAndProcessEquipmentGroupRel();
        loadAndProcessStepEquipmentGroup();
        loadAndProcessStaffPlanInput();
        loadAndProcessEquipmentPlanInput();
        loadAndProcessTaskPlanInput();

        return schedulingFlag;
    }

    private void loadAndProcessStepSkill(){
        // 得到待排程阶段要求的技能组
        loadData.loadStepSkill(dataContainer, functionId);
        Iterator<EntityStepSkill> stepSkillIterator = dataContainer.getStepSkillIterator();
        int size = dataContainer.getStepSkillArrayList().size();
        log.info("Loaded STEP_SKILL data with sub task filtering: " + size + " records");
        while (stepSkillIterator.hasNext()){
            EntityStepSkill stepSkill = stepSkillIterator.next();
            String index = stepSkill.getBomNo() + stepSkill.getStepId() + "";
            EntityStep step = stepHashMap.get(index);
            if (step == null){
                if (!invalidBom.contains(stepSkill.getBomNo())){
                    // 已经报错的BOM就不再这里再提示了
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                            "BOM " + stepSkill.getBomNo() + " 的小阶段 " + stepSkill.getStepId()
                                    + " 在小阶段技师资源需求表中存在，但在小阶段表中不存在 或 无效，忽略该记录，继续排程");
                }
                continue;
            }

            if (stepSkill.singleCheck(step)){
                Integer technicianCount = stepSkill.getTechnicianCount();
                // 按天工作时，工作天数>生成日历天数
                String bomNo = stepSkill.getBomNo();
                EntityBom bom = bomHashMap.get(bomNo);
                if (bom != null){
                    bom.setNeedTechnician(true);
                    if (stepSkill.getTechnicianDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
                        Integer technicianDayNum = stepSkill.getTechnicianDayNum();
                        Integer calendarDays = bom.getPlanConfig().getCalendarDays();
                        if (technicianDayNum > calendarDays){
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ILLEGAL,
                                    "BOM " + bomNo + " 的小阶段 " + step.getStepId() + " 的技师技能组 " +
                                            stepSkill.getSkillIdSet() + " 工作天数过长，为 " +technicianDayNum+ "，导致相关任务单无法排程");
                            // 将用到该 BOM 所有的 task 不排程
                            invalidBom.add(bomNo);
                            setSameBomTasksNotScheduling(bomNo);
                        }
                        // 按天工作时，每天工作时长>工程师的最大每天工时
                        double technicianDailyWorktime = stepSkill.getTechnicianDailyWorktime().doubleValue();
                        if (technicianDailyWorktime > MAX_DAILY_WORK_TIME){
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ILLEGAL,
                                    "BOM" + bomNo + "的小阶段" + step.getStepId() + "的技师技能组" + stepSkill.getSkillIdSet() +
                                            "每天工作时长过长，为" + technicianDailyWorktime + "，导致相关任务单无法排程");
                            invalidBom.add(bomNo);
                            setSameBomTasksNotScheduling(bomNo);
                        }
                    }
                }
                String skillIdSet = stepSkill.getSkillIdSet();
                if (skillIdSet == null) {
                    skillIdSet = "";
                }

                // 处理多余的逗号和空格
                if (skillIdSet != null && !skillIdSet.equals("")){
                    String[] skills = skillIdSet.split(",");
                    String skillsNew = "";
                    for (int i =0; i< skills.length; i++){
                        if (!skills[i].equals("")) {
                            skillsNew += skills[i].trim() + ",";
                        }
                    }
                    if (skillsNew.length() == 0) {
                        skillIdSet = null;
                    }else{
                        skillIdSet = skillsNew.substring(0,skillsNew.length()-1);
                        stepSkill.setSkillIdSet(skillIdSet);
                    }
                }
                // 添加到 map 中
                stepSkillHashMap.put(index+skillIdSet, stepSkill);

                // 做 technician 的需求 的相关预处理
                stepSkill.setResourceType(RESOURCE_TYPE_TECHNICIAN);
                if (skillIdSet ==null || skillIdSet.equals("")){
                    stepSkill.setResourceGroupName(DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME);
                } else {
                    stepSkill.setResourceGroupName(stepSkill.getSkillIdSet());
                }
                stepSkill.setRequestedResourceQuantity(technicianCount);
                String dailyMode = stepSkill.getTechnicianDailyMode();
                stepSkill.setRequestedDailyMode(dailyMode);
                if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)){
                    // for debug
                    //log.info(stepSkill.getStepSkillId());
                    stepSkill.setRequestedWorkTime(stepSkill.getTechnicianWorktime().doubleValue());
                } else {
                    stepSkill.setRequestedWorkTime(stepSkill.getTechnicianDailyWorktime().doubleValue());
                    stepSkill.setRequestedDayQuantity(stepSkill.getTechnicianDayNum());
                }
                if (stepSkill.getTechnicianWorktimeType().equals(STAFF_SAME_START)){
                    stepSkill.setSameStart(true);
                } else if (stepSkill.getTechnicianWorktimeType().equals(STAFF_SAME_END)){
                    stepSkill.setSameEnd(true);
                }
                if (stepSkill.getIsTechnicianConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT)){
                    stepSkill.setConstraint(true);
                }

                if (step != null){
                    step.getResourceGroupHashMap().put(stepSkill.getResourceGroupName(), stepSkill);
                }
            }

        }
        size = stepSkillHashMap.size();
        log.info("Loaded STEP_SKILL data after validation: " + size + " records");
    }

    private void loadAndProcessStaff(){
        // 得到功能块可能的工程师的集合
        loadData.loadAllStaffs(dataContainer);
        Iterator<EntityStaff> staffIterator = dataContainer.getStaffIterator();
        int size = dataContainer.getAllStaffArrayList().size();
        log.info("Loaded STAFF data: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有任何人员数据，不会启动排程");
            schedulingFlag = false;
            return;
        }
        while (staffIterator.hasNext()) {
            EntityStaff staff = staffIterator.next();
            initialStaffHashMap.put(staff.getStaffId(), staff);
            staff.assignResourceType();
        }

        size = initialStaffHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有有效的人员数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded STAFF data after validation: " + size + " records");
    }

    private void loadAndProcessEngineer(){
        // 得到功能块可能的工程师的集合
        loadData.loadEngineer(dataContainer, functionId);
        Iterator<EntityStaff> engineerIterator = dataContainer.getEngineerIterator();
        int size = dataContainer.getEngineerArrayList().size();
        log.info("Loaded ENGINEER data with function filtering: " + size + " records");
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有任何工程师数据，不会启动排程");
            schedulingFlag = false;
            return;
        }
        while (engineerIterator.hasNext()) {
            EntityStaff staff = engineerIterator.next();
            // 单实体验证
            if (staff.singleCheck()) {
                engineerHashMap.put(staff.getStaffId(), staff);
                staff.assignResourceType();
            }
        }
        staffHashMap.putAll(engineerHashMap);
        size = engineerHashMap.size();
        if (size == 0){
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_MISSING_DATA,
                    "功能块" + functionId + "没有有效的工程师数据，不会启动排程");
            schedulingFlag = false;
        }
        log.info("Loaded ENGINEER data after validation: " + size + " records");
    }

    private void loadAndProcessTechnician(){
        // 小阶段需要的技能集合是否为空，分类进行查询
        Boolean flag = getAllSkillSet();

        if (flag){
            loadData.loadAllTechnician(dataContainer);
        }else {
            loadData.loadTechnicianBySkillSet(dataContainer, allSkillSet);
        }

        Iterator<EntityStaff> technicianIterator = dataContainer.getTechnicianIterator();
        int size = dataContainer.getTechnicianArrayList().size();
        log.info("Loaded TECHNICIAN data with function filtering: " + size + " records");
        while (technicianIterator.hasNext()) {
            EntityStaff staff = technicianIterator.next();
            // 单实体验证
            if (staff.singleCheck()) {
                technicianHashMap.put(staff.getStaffId(), staff);
                staff.assignResourceType();
            }
        }
        staffHashMap.putAll(technicianHashMap);
        size = technicianHashMap.size();
        log.info("Loaded TECHNICIAN data after validation: " + size + " records");
    }

    private void loadAndProcessSkill(){
        if (allSkillSet.size() == 0) {
            return;
        }
        loadData.loadSkill(dataContainer, allSkillSet);
        Iterator<EntitySkill> skillIterator = dataContainer.getSkillIterator();
        int size = dataContainer.getSkillArrayList().size();
        log.info("Loaded SKILL data with sub task filtering: " + size + " records");

        while (skillIterator.hasNext()) {
            EntitySkill skill = skillIterator.next();
            skillHashMap.put(skill.getSkillId(),skill);
        }
        size = skillHashMap.size();
        log.info("Loaded SKILL data after validation: " + size + " records");
    }

    private void loadAndProcessStaffCalendar(){
        // 获取所有需要的 staffId
        Set<String> engineerIds = engineerHashMap.keySet();
        Set<String> technicianIds = technicianHashMap.keySet();
        allStaffIdSet.addAll(engineerIds);
        allStaffIdSet.addAll(technicianIds);

        loadData.loadStaffCalendar(dataContainer, allStaffIdSet);

        // 验证所有 StaffCalendar  ， 并将数据存放到 hashmap 中
        Iterator<EntityStaffCalendar> staffCalendarIterator = dataContainer.getStaffCalendarIterator();
        int size = dataContainer.getStaffCalendarArrayList().size();
        log.info("Loaded STAFF_CALENDAR data with sub task filtering: " + size + " records");
        size = 0;
        while (staffCalendarIterator.hasNext()){
            EntityStaffCalendar staffCalendar = staffCalendarIterator.next();
            String staffId = staffCalendar.getStaffId();

            // 单实体验证
            if (staffCalendar.singleCheck()) {
                // 统计正常日历和翻班日历
                Integer calendarId = staffCalendar.getCalendarId();
                if (StringUtils.equals(staffCalendar.getCalendarClass(), CALENDAR_CLASS_NORMAL)){
                    allCalendarIdSet.add(calendarId);
                }else {
                    allRollCalendarIdSet.add(calendarId);
                }
                ArrayList<EntityStaffCalendar> staffCalendars = staffCalendarHashMap.computeIfAbsent(staffId, k -> new ArrayList<>());
                staffCalendars.add(staffCalendar);

                // 在 staff 中添加 staffCalendar
                EntityStaff staff = staffHashMap.get(staffId);
                staff.getStaffCalendar().add(staffCalendar);
                size ++;
            // staffCalendar 不过的对应的人员移除
            }else {
                noCalendarStaffSet.add(staffId);
                engineerHashMap.remove(staffId);
                technicianHashMap.remove(staffId);
                staffHashMap.remove(staffId);
            }
        }
        int staffQty = staffCalendarHashMap.size();
        log.info("Loaded STAFF_CALENDAR data after validation: " + size + " records for " + staffQty + " staffs.");
    }
    private void loadAndProcessEquipment(){
        loadData.loadEquipment(dataContainer, functionId);
        // 验证所有 equipment ， 并将数据存放到 hashmap 中
        Iterator<EntityEquipment> equipmentIterator = dataContainer.getEquipmentIterator();
        int size = dataContainer.getEquipmentArrayList().size();
        log.info("Loaded EQUIPMENT data with sub task filtering: " + size + " records");
        while (equipmentIterator.hasNext()){
            EntityEquipment equipment = equipmentIterator.next();
            // 单实体验证
            if (equipment.singleCheck()) {
                equipmentHashMap.put(equipment.getEquipmentId(), equipment);
                // 计算设备的capacity
                equipment.setCapacityInModel(equipment.getEquipmentCap() * equipment.getEquipmentQty());
            }
        }
        size = equipmentHashMap.size();
        log.info("Loaded EQUIPMENT data after validation: " + size + " records");
    }
    private void loadAndProcessEquipmentCalendar(){
        loadData.loadEquipmentCalendar(dataContainer, functionId);
        // 单实体数据验证
        Iterator<EntityEquipmentCalendar> equipmentCalendarIterator = dataContainer.getEquipmentCalendarIterator();
        int size = dataContainer.getEquipmentCalendarArrayList().size();
        log.info("Loaded EQUIPMENT_CALENDAR data with sub task filtering: " + size + " records");
        while(equipmentCalendarIterator.hasNext()){
            EntityEquipmentCalendar equipmentCalendar = equipmentCalendarIterator.next();
            if (equipmentCalendar.singleCheck()){
                // 统计正常日历和翻班日历
                Integer calendarId = equipmentCalendar.getCalendarId();
                if (StringUtils.equals(equipmentCalendar.getCalendarClass(), CALENDAR_CLASS_NORMAL)){
                    allCalendarIdSet.add(calendarId);
                }else {
                    allRollCalendarIdSet.add(calendarId);
                }
                equipmentCalendarHashMap.put(equipmentCalendar.getEquipmentId(), equipmentCalendar);

                // 向 equipment 中添加  equipmentCalendar
                EntityEquipment equipment = equipmentHashMap.get(equipmentCalendar.getEquipmentId());
                if(equipment != null){
                    equipment.getEquipmentCalendar().add(equipmentCalendar);
                }

            }else {
                // 将该人员从相应的 map 中移除
                equipmentHashMap.remove(equipmentCalendar.getEquipmentId());
            }
        }
        size = equipmentCalendarHashMap.size();
        log.info("Loaded EQUIPMENT_CALENDAR data after validation: " + size + " records");
    }

    private void loadAndProcessRollCalendar(){
        // 只有当有需要 rollCalendar 时再加载数据
        if (allRollCalendarIdSet != null && allRollCalendarIdSet.size() != 0){
            loadData.loadRollCalendar(dataContainer, allRollCalendarIdSet);

            // 验证所有 rollCalendar ， 并将数据存放到 hashmap 中
            Iterator<EntityRollCalendar> rollCalendarIterator = dataContainer.getRollCalendarIterator();
            int size = dataContainer.getRollCalendarArrayList().size();
            log.info("Loaded ROLL_CALENDAR data with sub task filtering: " + size + " records");
            while (rollCalendarIterator.hasNext()){
                EntityRollCalendar rollCalendar = rollCalendarIterator.next();
                // 单实体验证
                if (rollCalendar.singleCheck("翻班日历")) {
                    ArrayList<EntityRollCalendar> rollCalendars = rollCalendarHashMap.computeIfAbsent(rollCalendar.getCalendarId(), k -> new ArrayList<>());
                    rollCalendars.add(rollCalendar);
                }
            }
            size = rollCalendarHashMap.size();
            log.info("Counted ROLL_CALENDAR after validation: " + size + " records");
        }
    }
    private void loadAndProcessCalendar(){
        loadData.loadCalendar(dataContainer, allCalendarIdSet);
        // 验证所有 calendar ， 并将数据存放到 hashmap 中
        Iterator<EntityCalendar> calendarIterator = dataContainer.getCalendarIterator();
        int size = dataContainer.getCalendarArrayList().size();
        log.info("Loaded CALENDAR data with sub task filtering: " + size + " records");
        while (calendarIterator.hasNext()){
            EntityCalendar calendar = calendarIterator.next();
            // 单实体验证
            if (calendar.singleCheck("日历")) {

                // 日历对应的每一天是否工作的切换时间初始化
                /*Calendar startTime = DateUtil.dateToCalendar(plan.getPlanPeriodStartTime());
                Calendar endTime = DateUtil.dateToCalendar(plan.getPlanPeriodEndTime());

                // todo：startTime 不是 00:00 的情况
                Calendar tempTime = startTime;

                String weekWorkDay = calendar.getWeekWorkDay();
                char[] chars = weekWorkDay.toCharArray();

                // 每天切换的工作时间点
                String[] shiftStart = null;
                String shiftStartStr = calendar.getShiftStart();
                if (shiftStartStr != null){
                    shiftStart = shiftStartStr.split(":");
                }

                String[] breakStart = null;
                String breakStartStr = calendar.getBreakStart();
                if (breakStartStr != null){
                    breakStart = breakStartStr.split(":");
                }

                String[] breakEnd = null;
                String breakEndStr = calendar.getBreakEnd();
                if (breakEndStr != null){
                    breakEnd = breakEndStr.split(":");
                }


                String[] supperBreakStart = null;
                String supperBreakStartStr = calendar.getSupperBreakStart();
                if (supperBreakStartStr != null){
                    supperBreakStart = supperBreakStartStr.split(":");
                }

                String[] supperBreakEnd = null;
                String supperBreakEndStr = calendar.getSupperBreakStart();
                if (supperBreakEndStr != null){
                    supperBreakEnd = supperBreakStartStr.split(":");
                }

                String[] shiftEnd = null;
                String shiftEndStr = calendar.getShiftEnd();
                if (shiftEndStr != null){
                    shiftEnd = shiftEndStr.split(":");
                }*/

                // 以下结构后面没有用到，所以暂时被注释
                // todo: 后面确认没用可以删掉
/*
                // 遍历排程时间，生成对应的时间切换的 map
                HashMap<Date, ArrayList<Date>> switchTimePerDaySpecialDayNotWork = calendar.getSwitchTimePerDaySpecialDayNotWork();
                HashMap<Date, ArrayList<Double>> switchValuePerDaySpecialDayNotWork = calendar.getSwitchValuePerDaySpecialDayNotWork();
                HashMap<Date, ArrayList<Date>> switchTimePerDaySpecialDayWork = calendar.getSwitchTimePerDaySpecialDayWork();
                HashMap<Date, ArrayList<Double>> switchValuePerDaySpecialDayWork = calendar.getSwitchValuePerDaySpecialDayWork();

                while (tempTime.before(endTime)){

                    ArrayList<Date> times = new ArrayList<>();
                    ArrayList<Double> values = new ArrayList<>();
                    ArrayList<Date> timesNotWork = new ArrayList<>();
                    ArrayList<Double> valuesNotWork = new ArrayList<>();

                    // 获取星期几与 chars 对应的下标
                    int dayOfWeek = tempTime.get(Calendar.DAY_OF_WEEK) - 2;
                    char c ;
                    if (dayOfWeek >= 0){
                        c = chars[dayOfWeek];
                    }else {
                        // dayOfWeek 为星期日 -1 时，特殊考虑
                        c = chars[6];
                    }

                    // 添加开始时间
                    times.add(tempTime.getTime());
                    values.add(NOT_WORK_VALUE);
                    timesNotWork.add(tempTime.getTime());
                    valuesNotWork.add(NOT_WORK_VALUE);

                    // 考虑 specialDay 不工作
                    if (specialDayHashMap.containsKey(tempTime)){
                        EntitySpecialDay specialDay = specialDayHashMap.get(tempTime.getTime());
                        String workDayType = specialDay.getWorkDayType();
                        // 周末工作
                        if (StringUtils.equals(workDayType, SPECIAL_DAY_WEEKEND_WORK)){
                            calculateCalendarDayTimesAndValues(tempTime, timesNotWork, valuesNotWork,
                                    shiftStart, breakStart, breakEnd, supperBreakStart,supperBreakEnd, shiftEnd);
                        }
                        // 不工作的两种情况
                        // 非 specialDay
                    }else {
                        if (c == '1'){
                            calculateCalendarDayTimesAndValues(tempTime, timesNotWork, valuesNotWork,
                                    shiftStart, breakStart, breakEnd, supperBreakStart,supperBreakEnd, shiftEnd);
                        }
                    }

                    // 不考虑 specialDay
                    // 工作
                    if (c == '1'){
                        calculateCalendarDayTimesAndValues(tempTime, times, values,
                                shiftStart, breakStart, breakEnd, supperBreakStart,supperBreakEnd, shiftEnd);
                    }

                    // 计算结束时间 00:00，即下一天开始时间
                    Date nextDay = DateUtil.getNextDay(tempTime.getTime());
                    times.add(nextDay);
                    timesNotWork.add(nextDay);

                    tempTime.setTime(nextDay);

                    // 添加到该 Calendar 的 map 中
                    switchTimePerDaySpecialDayWork.put(times.get(0), times);
                    switchValuePerDaySpecialDayWork.put(times.get(0), values);
                    switchTimePerDaySpecialDayNotWork.put(timesNotWork.get(0), timesNotWork);
                    switchValuePerDaySpecialDayNotWork.put(timesNotWork.get(0), valuesNotWork);
                }
                log.info("Calendar" + calendar.getCalendarId() + "的switchTimePerDaySpecialDayWork：" + switchTimePerDaySpecialDayWork.size());
                log.info("Calendar" + calendar.getCalendarId() + "的switchValuePerDaySpecialDayWork：" + switchValuePerDaySpecialDayWork.size());
                log.info("Calendar" + calendar.getCalendarId() + "的switchTimePerDaySpecialDayNotWork：" + switchTimePerDaySpecialDayNotWork.size());
                log.info("Calendar" + calendar.getCalendarId() + "的switchValuePerDaySpecialDayNotWork：" + switchValuePerDaySpecialDayNotWork.size());
*/
                calendarHashMap.put(calendar.getCalendarId(), calendar);
            }
        }
        size = calendarHashMap.size();
        log.info("Loaded CALENDAR data after validation: " + size + " records");
    }
    /**
     * 遍历 staffCalendarHashMap 验证 StaffCalendar 跨实体逻辑
     */
    private void checkStaffCalendar () {
        log.info("Started to check staffs' calendar information: there are " + staffHashMap.size() + " staffs.");
        // 检查是否有的人员没有日历
        boolean removeFlag;
        Iterator<Map.Entry<String,EntityStaff>> it = staffHashMap.entrySet().iterator();
        while(it.hasNext()){
            removeFlag = false;
            Map.Entry<String, EntityStaff> entry = it.next();
            String staffId = entry.getKey();
            EntityStaff staff = entry.getValue();
            String staffClass = staff.resourceTypeMessage();

            if (!staffCalendarHashMap.containsKey(staffId)){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING,MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                        staffClass + staff.getStaffName() + "（ID "+staffId + "）找不到对应的工作日历，该人员无法排程，忽略该人员，继续排程");
                removeFlag = true;
            } else {
                ArrayList<EntityStaffCalendar> staffCalendars = staffCalendarHashMap.get(staffId);
                for (EntityStaffCalendar staffCalendar : staffCalendars) {
                    Integer calendarId = staffCalendar.getCalendarId();
                    String calendarClass = staffCalendar.getCalendarClass();
                    // 工作日历id在日历表中不存在或无效
                    Boolean errorFlag = false;
                    if (calendarClass.equals(CALENDAR_CLASS_NORMAL) && !calendarHashMap.containsKey(calendarId)){
                        errorFlag = true;

                    }else if(calendarClass.equals(CALENDAR_CLASS_ROLL) && !rollCalendarHashMap.containsKey(calendarId)){
                        errorFlag = true;
                    }

                    if (errorFlag){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING,MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                staffClass + staff.getStaffName() + "（ID "+staffId + "）对应的工作日历 " + calendarId + " 在日历表中不存在或日历无效，该人员无法排程，忽略该人员，继续排程");
                        removeFlag = true;
                    }
                }
            }
            if (removeFlag){
                // 将该人员从相应的 map 中移除
                engineerHashMap.remove(staffId);
                technicianHashMap.remove(staffId);
                it.remove();
            }
        }
        log.info("After checking, there are " + staffHashMap.size() + " staffs.");
    }
    /**
     * 遍历 EquipmentCalendarHashMap 验证 EquipmentCalendar 跨实体逻辑
     */
    private void checkEquipmentCalendar () {

        for (EntityEquipmentCalendar equipmentCalendar : equipmentCalendarHashMap.values()) {
            // 工作日历编号id在日历表中不存在
            Integer calendarId = equipmentCalendar.getCalendarId();
            String equipmentId = equipmentCalendar.getEquipmentId();
            String calendarClass = equipmentCalendar.getCalendarClass();
            Boolean errorFlag = false;
            if (calendarClass.equals(CALENDAR_CLASS_NORMAL) && !calendarHashMap.containsKey(calendarId)){
                errorFlag = true;

            }else if(calendarClass.equals(CALENDAR_CLASS_ROLL) && !rollCalendarHashMap.containsKey(calendarId)){
                errorFlag = true;
            }

            if (errorFlag){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_EQUIPMENT_NOT_ENOUGH,
                        "设备 " + equipmentId + " 对应的工作日历 " + calendarId + " 在日历表中不存在或日历无效，该设备无法排程，忽略该设备，继续排程");
                equipmentHashMap.remove(equipmentId);
            }

        }
    }

    private void loadAndProcessTechnicianSkill(){
        // 得到技师的技能
        loadData.loadTechnicianSkill(dataContainer, allSkillSet);
        Iterator<EntityTechnicianSkill> technicianSkillIterator = dataContainer.getTechnicianSkillIterator();
        while (technicianSkillIterator.hasNext()){
            EntityTechnicianSkill technicianSkill = technicianSkillIterator.next();
            String staffId = technicianSkill.getStaffId();
            String skillId = technicianSkill.getSkillId();

            // 每个技师所掌握的技能
            // 如果 staffId 存在，则直接添加，不存在，则创建新的 list 后添加
            ArrayList<String> skillIds = technicianSkillHashMap.computeIfAbsent(staffId, k -> new ArrayList<>());
            skillIds.add(skillId);

            // 掌握该技能的所有技师
            ArrayList<EntityStaff> staffs = skillTechnicianHashMap.computeIfAbsent(skillId, k -> new ArrayList<>());
            EntityStaff tech = staffHashMap.get(staffId);
            if (tech != null) {
                staffs.add(staffHashMap.get(staffId));
            }
        }
    }

    private void loadAndProcessAuthProcedure(){
        // 得到规范人员授权
        loadData.loadAuthProcedure(dataContainer, functionId);
        Iterator<EntityAuthProcedure> authProcedureIterator = dataContainer.getAuthProcedureIterator();
        int size = dataContainer.getAuthProcedureArrayList().size();
        log.info("Loaded AUTH_PROCEDURE data with function filtering: " + size + " records");
        size = 0;
        while (authProcedureIterator.hasNext()){
            EntityAuthProcedure authProcedure = authProcedureIterator.next();
            String procedureNo = authProcedure.getProcedureNo();
            String staffId = authProcedure.getStaffId();
            EntityProcedure procedure = procedureHashMap.get(procedureNo);

            // 规范ID授权的人员ID在人员表中不存在
            if (!staffHashMap.containsKey(staffId)){
                staffValidationMessage(staffId, "规范 " + procedureNo + " 的授权人员 " );
            }else {
                // 如果 procedureId 存在，则直接添加，不存在，则创建新的 list 后添加
                ArrayList<String> staffIds = authProcedureHashMap.computeIfAbsent(procedureNo, k -> new ArrayList<>());
                staffIds.add(staffId);

                // procedure下的[auth_engineer]和[auth_technician]
                if (engineerHashMap.containsKey(staffId)){
                    procedure.getAuthEngineers().add(engineerHashMap.get(staffId));
                }else {
                    procedure.getAuthTechnicians().add(technicianHashMap.get(staffId));
                }
                size++;
            }
        }

        log.info("Loaded AUTH_PROCEDURE data after validation: " + size + " records");

    }

    // todo
    private void staffValidationMessage(String staffId, String objectName) {
        String staffMsg = staffId;
        EntityStaff staff = initialStaffHashMap.get(staffId);
        if (staff == null) {
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                    objectName + staffMsg
                            + " 不在人员表中，忽略该人员，继续排程");
        } else {
            staffMsg = staff.getStaffName() + "（ID " + staffId + "）";
            if (STAFF_CLASS_ENGINEER.equals(staff.getStaffClassId()) && (!engineerHashMap.containsKey(staffId))
                && !noCalendarStaffSet.contains(staffId)) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                        objectName + staff.resourceTypeMessage() + staffMsg
                                + " 没有被分配给该功能块，忽略该人员，继续排程");
            } else if (!staffCalendarHashMap.containsKey(staffId)) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                        objectName + staff.resourceTypeMessage() + staffMsg
                                + " 没有对应的日历，忽略该人员，继续排程");
            } else if (STAFF_CLASS_TECHNICIAN.equals(staff.getStaffClassId())) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                        objectName + staff.resourceTypeMessage() + staffMsg
                                + " 不满足技能要求，忽略该人员，继续排程");
            } else {
                // 不应该到这个分支
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                        objectName  + staffMsg
                                + " 无效，忽略该人员，继续排程");
            }
        }
    }

    private void loadAndProcessAuthBom(){
        // 得到BOM人员授权
        loadData.loadAuthBom(dataContainer, functionId);
        Iterator<EntityAuthBom> authBomIterator = dataContainer.getAuthBomIterator();
        int size = dataContainer.getAuthBomArrayList().size();
        log.info("Loaded AUTH_BOM data with function filtering: " + size + " records");
        while (authBomIterator.hasNext()){
            EntityAuthBom authBom = authBomIterator.next();
            String bomNo = authBom.getBomNo();
            String staffId = authBom.getStaffId();
            EntityBom bom = bomHashMap.get(bomNo); //BOM不会为null，因为是通过BOM做的数据库查询
            //procedure又可能为空，但对于非标准BOM，不应为空，前面load subtask的时候应该检查过了。这里只判断不输出信息
            EntityProcedure procedure = procedureHashMap.get(bom.getProcedureNo());

            EntityStaff staff = staffHashMap.get(staffId);
            if (staff == null){
                staffValidationMessage(staffId, "BOM " + bomNo + " 的授权人员 " );

            }else {
                // 如果 bomId 存在，则直接添加，不存在，则创建新的 list 后添加
                ArrayList<String> staffIds = authBomHashMap.computeIfAbsent(bomNo, k -> new ArrayList<>());
                staffIds.add(staffId);

                String staffType = staff.getStaffClassId();
                String bomType = bom.getBomType();

                // todo：确认逻辑 12月15 重构
                // bom下的auth engineer/technician
                if (staffType.equals(STAFF_CLASS_ENGINEER)){
                    bom.getAuthBomEngineers().add(staff);
                }else {
                    bom.getAuthBomTechnicians().add(staff);
                }

            }
        }
        size = authBomHashMap.size();
        log.info("Loaded AUTH_BOM data after validation: " + size + " records");
    }
    private void loadAndProcessAuthStep(){
        // 得到小阶段人员授权
        loadData.loadAuthStep(dataContainer, functionId);
        Iterator<EntityAuthStep> authStepIterator = dataContainer.getAuthStepIterator();
        int size = dataContainer.getAuthStepArrayList().size();
        log.info("Loaded AUTH_STEP data with function filtering: " + size + " records");
        while (authStepIterator.hasNext()){
            EntityAuthStep authStep = authStepIterator.next();
            String staffId = authStep.getStaffId();
            String bomNo = authStep.getBomNo();
            Integer stepId = authStep.getStepId();

            String authStepId = "" + bomNo + stepId;
            EntityStep step = stepHashMap.get(authStepId);
            EntityBom bom = bomHashMap.get(bomNo);
            EntityProcedure procedure = procedureHashMap.get(bom.getProcedureNo());

            // 小阶段授权中的阶段不存在
            if (stepHashMap.get(authStepId) == null){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_INVALID_DATA,
                        "小阶段授权表中的小阶段 " + stepId + " 不存在，忽略该记录，继续排程");
                continue;
            }else {
                // 如果 authStepId 存在，则直接添加，不存在，则创建新的 list 后添加
                ArrayList<String> staffIds = authStepHashMap.computeIfAbsent(authStepId, k -> new ArrayList<>());
                staffIds.add(staffId);
            }

            EntityStaff staff = staffHashMap.get(staffId);
            if (staff == null){
                staffValidationMessage(staffId, "BOM "+ bomNo+" 的小阶段 " + step.getStepName() + " （顺序号 "+ step.getStepOrder()
                        + "，ID "+ stepId + "）" + "的授权人员 " );
            }else {


                if (technicianHashMap.containsKey(staffId)){
                    step.getAuthStepTechnicians().add(staff);
                }else {
                    step.getAuthStepEngineers().add(staff);
                }

              /*  // 考虑了有BOM和小阶段授权，取交集的情况
                // 技师
                if (technicianHashMap.containsKey(staffId)){
                    if ((!bom.getBomType().equals(BOM_TYPE_STANDARD) && procedure.getAuthTechnicians().size() == 0)
                            ||procedure.getAuthTechnicians().contains(staff)) {
                        //非标准BOM，没有规范授权；或者有规范授权并且包括这个staff
                        if (bom.getAuthTechniciansConsideredProcedure().contains(staff)
                                // 确认:不可用BOM为空，却有小阶段
                                || bom.getAuthEngineersConsideredProcedure().size()==0)
                        {
                            step.getAuthTechniciansConsideredBOM().add(staff);
                        }
                    }
                    // 工程师
                }else {
                    if ((!bom.getBomType().equals(BOM_TYPE_STANDARD) && procedure.getAuthTechnicians().size() == 0)
                            ||procedure.getAuthEngineers().contains(staff)){
                        //非标准BOM，没有规范授权；或者有规范授权并且包括这个staff
                        if (bom.getAuthEngineersConsideredProcedure().contains(staff)
                                // 确认: 不可用BOM为空，而有小阶段
                                || bom.getAuthEngineersConsideredProcedure().size()==0)
                        {
                            step.getAuthEngineersConsideredBOM().add(staff);
                        }
                    }
                }*/
            }
        }
        size = authStepHashMap.size();
        log.info("Loaded AUTH_STEP data after validation: " + size + " records");
    }

    private void loadAndProcessEquipmentGroupRel(){
        loadData.loadEquipmentGroupRel(dataContainer, functionId);
        // TODO： 数据验证逻辑
        Iterator<EntityEquipmentGroupRel> equipmentGroupRelIterator = dataContainer.getEquipmentGroupRelIterator();
        int size = dataContainer.getEquipmentGroupRelArrayList().size();
        log.info("Loaded EQUIPMENT_GROUP_REL data with sub task filtering: " + size + " records");
        size = 0;
        while (equipmentGroupRelIterator.hasNext()){
            EntityEquipmentGroupRel equipmentGroupRel = equipmentGroupRelIterator.next();

            if (equipmentGroupRel.singleCheck()){
             /*   String equipmentGroupId = equipmentGroupRel.getEquipmentGroupId() + "";
                if (stepEquipmentGroupHashMap.containsKey(equipmentGroupId)){

                    EntityStepEquipmentGroup stepEquipmentGroup = stepEquipmentGroupHashMap.get(equipmentGroupId);
                    Integer stepId = stepEquipmentGroup.getStepId();
                    String bomNo = stepEquipmentGroup.getBomNo();
                    String Index = bomNo + stepId;

                    EntityStep step = stepHashMap.get(Index);
                    EntityResourceGroup resourceGroup = step.getResourceGroupHashMap().get(DEFAULT_EQUIPMENT_RESOURCE_GROUP_NAME);

                    String equipmentId = equipmentGroupRel.getEquipmentId();
                    resourceGroup.getPossibleResources().add(engineerHashMap.get(equipmentId));
                }
                // 将设备按 equipmentGroupId 分组
                ArrayList<EntityEquipment> equipments = equipmentByEquipmentGroupIdHashMap.computeIfAbsent(equipmentGroupId, k -> new ArrayList<>());
                equipments.add(equipmentHashMap.get(equipmentGroupRel.getEquipmentId()));*/

                ArrayList<String> equipmentsSameGroup = equipmentGroupRelHashMap.computeIfAbsent(equipmentGroupRel.getEquipmentGroupId(), k -> new ArrayList<>());
                equipmentsSameGroup.add(equipmentGroupRel.getEquipmentId());

                equipmentGroupRelByEquipmentIdHashMap.put(equipmentGroupRel.getEquipmentId(), equipmentGroupRel);
                size ++;
            }

        }

        log.info("Loaded EQUIPMENT_GROUP_REL data after validation: " + size + " records");
    }

    private void loadAndProcessStepEquipmentGroup(){
        loadData.loadStepEquipmentGroup(dataContainer, functionId);
        // 验证所有 stepEquipmentGroup ， 并将数据存放到 hashmap 中
        Iterator<EntityStepEquipmentGroup> stepEquipmentGroupIterator = dataContainer.getStepEquipmentIterator();
        int size = dataContainer.getStepEquipmentGroupArrayList().size();
        log.info("Loaded STEP_EQUIPMENT_GROUP data with sub task filtering: " + size + " records");
        while (stepEquipmentGroupIterator.hasNext()){
            EntityStepEquipmentGroup stepEquipmentGroup = stepEquipmentGroupIterator.next();
            String equipmentGroupName = stepEquipmentGroup.getEquipmentGroupName();
            Integer equipmentGroupId = stepEquipmentGroup.getEquipmentGroupId();
            int stepId = stepEquipmentGroup.getStepId();
            String bomNo = stepEquipmentGroup.getBomNo();
            String index = bomNo+stepId;
            EntityStep step = stepHashMap.get(index);
            if (step == null) {
                if (!invalidBom.contains(bomNo)){
                    log.error("Can not find step " + stepId + " in step equipment group record of bom no " + bomNo
                            + ", step " + stepId + ", equipment group id " + equipmentGroupId);
                }
                continue;
            }
            String stepName = step.getStepName();
            int stepOrder = step.getStepOrder();
            // 单实体验证
            if (!stepEquipmentGroup.singleCheck(stepName,stepOrder)) {
                continue;
            }

            // 小阶段需求的设备容量大于设备本身的容量，需要考虑每个批次乘以有效样件数量之后的容量
            Integer equipmentNum = stepEquipmentGroup.getEquipmentNum();

            // 做 stepEquipmentGroup 的需求 的相关预处理
            stepEquipmentGroup.setResourceType(RESOURCE_TYPE_EQUIPMENT);
            stepEquipmentGroup.setResourceGroupName(equipmentGroupName);
            stepEquipmentGroup.setRequestedResourceQuantity(equipmentNum);
            stepEquipmentGroup.setRequestedResourceCapacity(stepEquipmentGroup.getEquipmentQty());
            String dailyMode = stepEquipmentGroup.getDailyMode();
            stepEquipmentGroup.setRequestedDailyMode(dailyMode);
            if (StringUtils.equals(dailyMode, RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)){
                if (stepEquipmentGroup.getWorkTime() == null){
                    // 前面已经判断在设置了syn的情况，不会为空；所以这里直接设置为0，没有意义，仅为避免空指针
                    stepEquipmentGroup.setRequestedWorkTime(0);
                } else {
                    stepEquipmentGroup.setRequestedWorkTime(stepEquipmentGroup.getWorkTime().doubleValue());
                }
            }else {
                stepEquipmentGroup.setRequestedWorkTime(stepEquipmentGroup.getDailyWorkTime().doubleValue());
                stepEquipmentGroup.setRequestedDayQuantity(stepEquipmentGroup.getDayNum());
            }
            if(StringUtils.equals(stepEquipmentGroup.getIsSyncStep(), EQUIPMENT_IS_SYNC_STEP )
                 && stepEquipmentGroup.getDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)){
                //只有不按天的情况，才可以设置同时synchronization
                stepEquipmentGroup.setSameStart(true);
                stepEquipmentGroup.setSameEnd(true);
            }
            if (StringUtils.equals(stepEquipmentGroup.getIsConstraint(), RESOURCE_GROUP_IS_CONSTRAINT)){
                stepEquipmentGroup.setConstraint(true);
            }
            if (StringUtils.equals(stepEquipmentGroup.getIsExpandableString(), RESOURCE_GROUP_IS_EXPANDABLE)){
                stepEquipmentGroup.setExpandable(true);
            }
            if (stepEquipmentGroup.getEquipmentStatus() != null){
                stepEquipmentGroup.setState(stepEquipmentGroup.getEquipmentStatus());
                // 统计设备状态的种类
                equipmentStates.add(stepEquipmentGroup.getEquipmentStatus());
            }

            // 获取该设备组中的相关设备，放到 PossibleResources 中
            ArrayList<Object> possibleResources = stepEquipmentGroup.getPossibleResources();

            ArrayList<String> equipmentIds = equipmentGroupRelHashMap.get(equipmentGroupId);
            if (equipmentIds != null && equipmentIds.size() != 0){
                for (String equipmentId : equipmentIds) {
                    EntityEquipment equipment = equipmentHashMap.get(equipmentId);
                    if (equipment != null) {
                        possibleResources.add(equipment);
                    }
                }
            }

            // 将该小阶段设备组需求，存放到 step 中
            step.getResourceGroupHashMap().put(stepEquipmentGroup.getResourceGroupName(), stepEquipmentGroup);


            // 设备组名称为空
            if (equipmentGroupName == null || "".equals(equipmentGroupName)){
                CheckDataUtil.packageAndLog(ConstantUtil.MESSAGE_SEVERITY_ERROR, ConstantUtil.MESSAGE_TYPE_FIELD_MISS,
                        "设备组 " + equipmentGroupId + " 对应的设备组名称为空，导致相关任务单无法排程");
                // 相关 task 无法排程
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);
            }


            EntityBom bom = bomHashMap.get(bomNo);
            if (bom != null){
                Integer calendarDays = bom.getPlanConfig().getCalendarDays();
                if (stepEquipmentGroup.getDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
                    // 按天工作时，工作天数>生成日历天数
                    Integer dayNum = stepEquipmentGroup.getDayNum();
                    if (dayNum > calendarDays){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ILLEGAL,
                                "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 "+ stepOrder + "，ID "+stepId
                                        + "）的设备组 " + equipmentGroupName + " 工作天数过长，为 " + dayNum + "，导致相关任务单无法排程");
                        // 相关任务单无法排程
                        invalidBom.add(bomNo);
                        setSameBomTasksNotScheduling(bomNo);
                    }
                    // 按天工作时，每天工作时长>工程师的最大每天工时
                    double dailyWorkTime = stepEquipmentGroup.getDailyWorkTime().doubleValue();
                    if (dailyWorkTime > MAX_DAILY_WORK_TIME){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_FIELD_ILLEGAL,
                                "BOM " + bomNo + " 的小阶段 " + stepName + " (阶段顺序号 "+ stepOrder + "，ID "+stepId
                                        + "）的设备组 " + equipmentGroupName +
                                        " 每天工作时长过长，为 " + dailyWorkTime + "，导致相关任务单无法排程");
                        invalidBom.add(bomNo);
                        setSameBomTasksNotScheduling(bomNo);
                    }
                }
            }

            // 小阶段所需设备的数量超过容量足够的设备数量
            if (equipmentNum > equipmentHashMap.size()){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                        "BOM " + bomNo + " 的小阶段 " + stepName
                                + "（阶段顺序号 " + stepOrder +"，阶段ID " + stepId
                                + " ）的设备组 "+ equipmentGroupName + " 要求的设备数量为 " + equipmentNum
                                + "，但设备组中满足容量条件的设备数量仅为 "  + equipmentHashMap.size()+
                                " 个，导致相关任务单无法排程");
                invalidBom.add(bomNo);
                setSameBomTasksNotScheduling(bomNo);
            }

            // 存放到对应的 map 中
            String index2 = index + stepEquipmentGroup.getEquipmentGroupName();
            stepEquipmentGroupHashMap.put(index2, stepEquipmentGroup);
        }

        size = stepEquipmentGroupHashMap.size();
        log.info("Loaded STEP_EQUIPMENT_GROUP data after validation: " + size + " records");
    }

    /**
     * 按照输入每天的字符串数组，算出每天日历工作的时间切换
     * @param tempTime
     * @param times
     * @param values
     * @param shiftStart
     * @param breakStart
     * @param breakEnd
     * @param supperBreakStart
     * @param supperBreakEnd
     * @param shiftEnd
     */
    public void calculateCalendarDayTimesAndValues(Calendar tempTime, ArrayList<Date> times, ArrayList<Double> values,
                                            String[] shiftStart, String[] breakStart, String[] breakEnd,
                                            String[] supperBreakStart, String[] supperBreakEnd, String[] shiftEnd){

        if (shiftStart != null){
            tempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(shiftStart[0]));
            tempTime.set(Calendar.MINUTE, Integer.parseInt(shiftStart[1]));
            times.add(tempTime.getTime());
            values.add(WORK_VALUE);
        }

        if (breakStart != null){
            tempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(breakStart[0]));
            tempTime.set(Calendar.MINUTE, Integer.parseInt(breakStart[1]));
            times.add(tempTime.getTime());
            values.add(NOT_WORK_VALUE);
        }

        if (breakEnd != null){
            tempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(breakEnd[0]));
            tempTime.set(Calendar.MINUTE, Integer.parseInt(breakEnd[1]));
            times.add(tempTime.getTime());
            values.add(WORK_VALUE);
        }

        if (supperBreakStart != null){
            tempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(supperBreakStart[0]));
            tempTime.set(Calendar.MINUTE, Integer.parseInt(supperBreakStart[1]));
            times.add(tempTime.getTime());
            values.add(NOT_WORK_VALUE);
        }

        if (supperBreakEnd != null){
            tempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(supperBreakEnd[0]));
            tempTime.set(Calendar.MINUTE, Integer.parseInt(supperBreakEnd[1]));
            times.add(tempTime.getTime());
            values.add(WORK_VALUE);
        }

        if (shiftEnd != null){
            tempTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(shiftEnd[0]));
            tempTime.set(Calendar.MINUTE, Integer.parseInt(shiftEnd[1]));
            times.add(tempTime.getTime());
            values.add(NOT_WORK_VALUE);
        }
    }


    private void loadAndProcessStaffPlanInput(){
        Date planPeriodStartTime = plan.getPlanPeriodStartTime();
        loadData.loadStaffPlanInput(dataContainer, allStaffIdSet, planPeriodStartTime);
        // 验证所有 staffPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntityStaffPlanInput> staffPlanInputIterator = dataContainer.getStaffPlanInputIterator();
        int size = dataContainer.getStaffPlanInputArrayList().size();
        log.info("Loaded STAFF_PLAN_INPUT data with sub task filtering: " + size + " records");
        while (staffPlanInputIterator.hasNext()){
            EntityStaffPlanInput staffPlanInput = staffPlanInputIterator.next();
            // 单实体验证
            if (staffPlanInput.singleCheck()) {
                staffPlanInputHashMap.put(staffPlanInput.getStaffPlanId(), staffPlanInput);

                // 人员资源在排程时间范围内的资源占用活动
                String staffId = staffPlanInput.getStaffId();
                ArrayList<EntityStaffPlan> staffPlans = staffStaffPlanInputHashMap.computeIfAbsent(staffId, k -> new ArrayList<>());
                staffPlans.add(staffPlanInput);

                // 根据是否有实际开始结束时间计算模型中用的开始结束实际，和占用的资源
                Date staffActualStart = staffPlanInput.getStaffActualStart();
                Date staffActualEnd = staffPlanInput.getStaffActualEnd();
                Date staffPlanStart = staffPlanInput.getStaffPlanStart();
                Date staffPlanEnd = staffPlanInput.getStaffPlanEnd();
                if (staffActualStart != null){
                    staffPlanInput.setStaffStartInModel(staffActualStart);
                }else {
                    staffPlanInput.setStaffStartInModel(staffPlanStart);
                }

                if (staffActualEnd != null){
                    staffPlanInput.setStaffEndInModel(staffActualEnd);
                }else {
                    staffPlanInput.setStaffEndInModel(staffPlanEnd);
                }

                double occupiedCapacity = calculateOccupiedCapacity(staffPlanInput.getIsConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT), false,
                        (double)DEFAULT_RESOURCE_CAPACITY, (double)DEFAULT_RESOURCE_CAPACITY, false, 1);
                staffPlanInput.setOccupiedCapacity(occupiedCapacity);
            }
        }
        checkStaffPlanInputConflicts();
        size = staffPlanInputHashMap.size();
        log.info("Loaded STAFF_PLAN_INPUT data after validation: " + size + " records");
    }
    /**
     * 检查是否已排程人员活动中有资源占用冲突
     */
    public void checkStaffPlanInputConflicts(){
        for (Map.Entry<String, ArrayList<EntityStaffPlan>> entry: staffStaffPlanInputHashMap.entrySet()){
            // 对同一个staff，按照活动的开始时间sort每一个活动，然后查找是否有冲突，如果有冲突，报警告，并且，截短某个活动的长度，使他们不冲突
            String staffId = entry.getKey();

            ArrayList<EntityStaffPlan> staffPlans = entry.getValue();
            Collections.sort(staffPlans, new SortForStaffPlans());
            EntityStaffPlan previousPlan = null;
            Date rangeStart = null; //start time of the occupied period
            Date rangeEnd = null;
            Iterator<EntityStaffPlan> it = staffPlans.iterator();
            while (it.hasNext()){
                EntityStaffPlan staffPlan = it.next();
                if (!RESOURCE_GROUP_IS_CONSTRAINT.equals(staffPlan.getIsConstraint())){
                    continue;
                }
                if (previousPlan == null){
                    rangeStart = staffPlan.getStaffStartInModel();
                    rangeEnd = staffPlan.getStaffEndInModel();
                    previousPlan = staffPlan;
                } else {
                    Date currentStart = staffPlan.getStaffStartInModel();
                    Date currentEnd = staffPlan.getStaffEndInModel();
                    if (currentStart.equals(rangeEnd) || currentStart.after(rangeEnd)){
                        // previous activity has finished
                        rangeStart = currentStart;
                        rangeEnd = currentEnd;
                        previousPlan = staffPlan;
                    } else {
                        // there is conflict
                        EntityStaff staff = staffHashMap.get(staffId);
                        if (rangeEnd.before(currentEnd)){
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_NOT_SCHEDULED_RESOURCE_CONFLICT,
                                    staff.resourceTypeMessage() + staff.getStaffName() + "（ID "
                                            + staffId +  "）的已排程活动 "+ staffPlan.getStaffPlanId() + " 从 "
                                            + DateUtil.getDateString(currentStart)
                                            + " 到 " + DateUtil.getDateString(rangeEnd)
                                            + " 有冲突，忽略冲突，继续排程。 ");
                            // new activity ended later,cut previous activity's end time
                            previousPlan.setStaffEndInModel(currentStart);
                            rangeStart = currentStart;
                            rangeEnd = currentEnd;
                            previousPlan = staffPlan;

                        } else {
                            // previous activity is very long, ended even after current activity, remove current activity
                            it.remove();
                            rangeStart = currentStart;
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_NOT_SCHEDULED_RESOURCE_CONFLICT,
                                    staff.resourceTypeMessage() + staff.getStaffName() + "（ID "
                                            + staffId +  "）的已排程活动 "+ staffPlan.getStaffPlanId() + " 从 "
                                            + DateUtil.getDateString(currentStart)
                                            + " 到 " + DateUtil.getDateString(currentEnd)
                                            + " 有冲突，忽略冲突，继续排程。 ");
                        }
                    }
                }
            }

        }
    }



    /**
     * 计算资源活动占用的容量
     */
    public double calculateOccupiedCapacity(boolean isConstraint, boolean isExpandable, double capacityInModel,
                                           double requestedResourceCapacity, boolean isExpand, int validSampleQuantity) {
        double occupiedCapacity;
        if (isConstraint && isExpandable == false){
            // 占用完整资源（暂时不考虑 排它资源 的方式）
            occupiedCapacity = capacityInModel;
        } else if (isConstraint && isExpandable){
            // 占用部分资源（资源可共享）
            occupiedCapacity = requestedResourceCapacity;
            if (isExpand){
                occupiedCapacity = occupiedCapacity * validSampleQuantity;
            }
        } else {
            // 不考虑占用资源
            occupiedCapacity = DEFAULT_RESOURCE_OCCUPATION;
        }
        return occupiedCapacity;
    }

    private void loadAndProcessEquipmentPlanInput(){
        Set<String> equipmentIdSet = equipmentHashMap.keySet();
        Date planPeriodStartTime = plan.getPlanPeriodStartTime();
        if (equipmentIdSet == null || equipmentIdSet.size()== 0 ) {
            return;
        }
        loadData.loadEquipmentPlanInput(dataContainer, equipmentIdSet, planPeriodStartTime);
        // 验证所有 equipmentPlanInput ， 并将数据存放到 hashmap 中
        Iterator<EntityEquipmentPlanInput> equipmentPlanInputIterator = dataContainer.getEquipmentPlanInputIterator();
        int size = dataContainer.getEquipmentPlanInputArrayList().size();
        log.info("Loaded EQUIPMENT_PLAN_INPUT data with sub task filtering: " + size + " records");
        while (equipmentPlanInputIterator.hasNext()){
            EntityEquipmentPlanInput equipmentPlanInput = equipmentPlanInputIterator.next();
            // 单实体验证
            if (equipmentPlanInput.singleCheck()) {
                equipmentPlanInputHashMap.put(equipmentPlanInput.getEquipmentPlanId(), equipmentPlanInput);

                String equipmentId = equipmentPlanInput.getEquipmentId();
                ArrayList<EntityEquipmentPlan> equipmentPlans = equipmentEquipmentPlanInputHashMap.computeIfAbsent(equipmentId, k -> new ArrayList<>());
                equipmentPlans.add(equipmentPlanInput);

                // 得到所有需要考虑的设备资源在排程时间范围内的资源占用活动
                Date equipmentActualStart = equipmentPlanInput.getEquipmentActualStart();
                Date equipmentActualEnd = equipmentPlanInput.getEquipmentActualEnd();
                Date equipmentPlanStart = equipmentPlanInput.getEquipmentPlanStart();
                Date equipmentPlanEnd = equipmentPlanInput.getEquipmentPlanEnd();
                if (equipmentActualStart != null){
                    equipmentPlanInput.setEquipmentStartInModel(equipmentActualStart);
                }else {
                    equipmentPlanInput.setEquipmentStartInModel(equipmentPlanStart);
                }

                if (equipmentActualEnd != null){
                    equipmentPlanInput.setEquipmentEndInModel(equipmentActualEnd);
                }else {
                    equipmentPlanInput.setEquipmentEndInModel(equipmentPlanEnd);
                }

                EntityEquipment equipment = equipmentHashMap.get(equipmentId);
                double occupiedCapacity = calculateOccupiedCapacity(equipmentPlanInput.getIsConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT),
                        equipmentPlanInput.getIsExpandable().equals(RESOURCE_GROUP_IS_EXPANDABLE),
                        (double)equipment.getCapacityInModel(), (double)equipmentPlanInput.getEquipmentQty(),
                        false, 1);
                equipmentPlanInput.setOccupiedCapacity(occupiedCapacity);

                if (equipmentPlanInput.getEquipmentStatus() != null){
                    // 统计设备状态的种类
                    equipmentStates.add(equipmentPlanInput.getEquipmentStatus());
                }

            }
        }
        // 生成设备状态的 map
        int i = 0;
        for (String equipmentState : equipmentStates) {
            equipmentStateStringToIntMap.put(equipmentState, i);
            i++;
        }
        log.info("equipmentStateStringToIntMap : " + equipmentStateStringToIntMap);
        checkEquipmentPlanInputConflicts();
        size = equipmentPlanInputHashMap.size();
        log.info("Loaded EQUIPMENT_PLAN_INPUT data after validation: " + size + " records");
    }

    /**
     * 检查是否已排程设备活动中有资源占用冲突，需要考虑共享情况
     */
    public void checkEquipmentPlanInputConflicts(){
        for (Map.Entry<String, ArrayList<EntityEquipmentPlan>> entry: equipmentEquipmentPlanInputHashMap.entrySet()){
            // 对同一个staff，按照活动的开始时间sort每一个活动，然后查找是否有冲突，如果有冲突，报警告，并且，截短某个活动的长度，使他们不冲突
            String equipmentId = entry.getKey();
            log.debug("Debugging on check equipment plan input conflicts. Equipment is " + equipmentId);
            ArrayList<EntityEquipmentPlan> equipmentPlanInputs = entry.getValue();
            EntityEquipment equipment = equipmentHashMap.get(equipmentId);
            double equipmentCapacity = equipment.getCapacityInModel();
            SortedMap<String, ArrayList<EntityEquipmentPlan>> segmentPointsMap = new TreeMap<>();
            for (EntityEquipmentPlan equipmentPlan: equipmentPlanInputs){
                String start = DateUtil.getDateString(equipmentPlan.getEquipmentStartInModel())+"s";
                String end = DateUtil.getDateString(equipmentPlan.getEquipmentEndInModel())+"e";
                ArrayList<EntityEquipmentPlan> tempE = segmentPointsMap.computeIfAbsent(end, k->new ArrayList<>());
                tempE.add(equipmentPlan);
                ArrayList<EntityEquipmentPlan> tempS = segmentPointsMap.computeIfAbsent(start, k->new ArrayList<>());
                tempS.add(equipmentPlan);
            }
            log.debug(" size of segment point map: " + segmentPointsMap.size());
            ArrayList<EntityEquipmentPlan> inRangeList = new ArrayList<>();
            String previous = null;
            double segmentCapacity = 0;
            HashMap<String, ArrayList<EntityEquipmentPlan>> inRangeEquipmentStates = new HashMap<>();
            Iterator<Map.Entry<String, ArrayList<EntityEquipmentPlan>>> it = segmentPointsMap.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, ArrayList<EntityEquipmentPlan>> entry2 = it.next();
                String dateString = entry2.getKey();
                log.debug(" time point is " + dateString);

                ArrayList<EntityEquipmentPlan> plansStartOrEndAtThePoint = entry2.getValue();
                log.debug(" plans start or end at the point are: " + plansStartOrEndAtThePoint.toString());
                log.debug(" in range plans are: " + inRangeList.toString());
                log.debug(" segment capacity is " + segmentCapacity);
                if (previous == null){
                    // first time point
                    previous = dateString;
                    for (EntityEquipmentPlan plan: plansStartOrEndAtThePoint) {
                        inRangeList.add(plan);
                        segmentCapacity += plan.getOccupiedCapacity();
                        handleEquipmentStateWhenAddPlanInRange(plan, inRangeEquipmentStates);
                    }
                    if (segmentCapacity > equipmentCapacity){
                        // 超过限制, do nothing
                    }
                    continue;
                }
                // not first time point
                if (dateString.charAt(dateString.length() - 1) == 'e'){
                    // it's an end time, it must in inRangeList
                    for (EntityEquipmentPlan plan: plansStartOrEndAtThePoint) {
                        for (EntityEquipmentPlan inRangePlan : inRangeList) {
                            if (plan.getEquipmentPlanId().equals(inRangePlan.getEquipmentPlanId())){
                                segmentCapacity = segmentCapacity - plan.getOccupiedCapacity();
                                inRangeList.remove(plan);
                                if (segmentCapacity + plan.getOccupiedCapacity() > equipmentCapacity){
                                    // 超过限制
                                    // start must be previous
                                    //if (previous.substring(0, previous.length()-1).equals(DateUtil.getDateString(plan.getEquipmentStartInModel()))){
                                    if (segmentCapacity >= equipmentCapacity){
                                        // after remove this plan, still bigger than equipment capacity
                                        // set occupied capacity to 0 this equipment plan
                                        plan.setOccupiedCapacity(0);
                                    } else {
                                        // after remove this plan, capacity is enough
                                        // set the capacity of this equipment plan to the gap
                                        plan.setOccupiedCapacity(equipmentCapacity- segmentCapacity);
                                    }
                                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_NOT_SCHEDULED_RESOURCE_CONFLICT,
                                            "设备 "+ equipment.getEquipmentName() +" 的已排程活动 "
                                                    + plan.getEquipmentPlanId() + " 从 "
                                                    + DateUtil.getDateString(plan.getEquipmentStartInModel())
                                                    + " 到 " + DateUtil.getDateString(plan.getEquipmentEndInModel())
                                                    + " 有冲突，忽略冲突，继续排程。 ");
                                } else {
                                    // capacity is enough, nothing to do
                                }
                                handleEquipmentStateWhenRemovePlanInRange(plan, equipment, inRangeEquipmentStates);
                                break;
                            }
                        }
                        // handle inRandList plans
                        if (inRangeList.size() == 0){
                            continue;
                        }
                    }
                    Date currentDate = plansStartOrEndAtThePoint.get(0).getEquipmentEndInModel();
                    ArrayList<EntityEquipmentPlan> plansStartAtThePoint = new ArrayList<>();
                    segmentCapacity = handleInRangeList(currentDate, inRangeList, segmentCapacity, equipmentCapacity,
                            equipment, plansStartAtThePoint,equipmentPlanInputs, inRangeEquipmentStates);

                    previous = dateString;
                } else{
                    // it's a start time, handle inRangeList plans
                    Date currentDate = plansStartOrEndAtThePoint.get(0).getEquipmentStartInModel();
                    if (dateString.substring(0, dateString.length()-1).equals(previous.substring(0, previous.length()-1))){
                        // only add new started plans
                        for (EntityEquipmentPlan plan: plansStartOrEndAtThePoint) {
                            inRangeList.add(plan);
                            segmentCapacity += plan.getOccupiedCapacity();
                            handleEquipmentStateWhenAddPlanInRange(plan, inRangeEquipmentStates);
                        }
                    } else {
                        segmentCapacity = handleInRangeList(currentDate, inRangeList, segmentCapacity, equipmentCapacity,
                                equipment, plansStartOrEndAtThePoint, equipmentPlanInputs, inRangeEquipmentStates);
                    }
                    previous = dateString;
                }
            }
        }
    }
    /**
     * 在 checkEquipmentPlanInputConflicts 函数中调用，当向inRangeList中增加plan时对state的处理
     */
    private void handleEquipmentStateWhenAddPlanInRange(EntityEquipmentPlan plan, HashMap<String, ArrayList<EntityEquipmentPlan>> inRangeEquipmentStates) {
        if (plan.getEquipmentStatus() != null && !plan.getEquipmentStatus().equals("")){
            ArrayList<EntityEquipmentPlan> equipmentPlansForStates
                    = inRangeEquipmentStates.computeIfAbsent(plan.getEquipmentStatus(), k-> new ArrayList<>());
            equipmentPlansForStates.add(plan);
        }
    }

    /**
     * 在 checkEquipmentPlanInputConflicts 函数中调用，当向inRangeList中remove plan时对state的处理
     */
    private void handleEquipmentStateWhenRemovePlanInRange(EntityEquipmentPlan plan, EntityEquipment equipment,
                                                           HashMap<String, ArrayList<EntityEquipmentPlan>> inRangeEquipmentStates) {
        String equipmentStateOfThisPlan = plan.getEquipmentStatus();
        if (equipmentStateOfThisPlan != null && !equipmentStateOfThisPlan.equals("")
                && inRangeEquipmentStates.size() > 1){
            // states has conflict
            plan.setEquipmentStatus(null);
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_NOT_SCHEDULED_RESOURCE_CONFLICT,
                    "设备 "+ equipment.getEquipmentName() +" 的已排程活动 "
                            + plan.getEquipmentPlanId() + " 从 "
                            + DateUtil.getDateString(plan.getEquipmentStartInModel())
                            + " 到 " + DateUtil.getDateString(plan.getEquipmentEndInModel())
                            + " 有状态冲突，忽略冲突，继续排程。 ");

            ArrayList<EntityEquipmentPlan> equipmentPlansForStates = inRangeEquipmentStates.get(equipmentStateOfThisPlan);
            equipmentPlansForStates.remove(plan);
            if (equipmentPlansForStates.size() == 0){
                inRangeEquipmentStates.remove(equipmentStateOfThisPlan);
            }
        }
    }

    /**
     * 在 checkEquipmentPlanInputConflicts 函数中调用
     */
    private double handleInRangeList(Date currentDate, ArrayList<EntityEquipmentPlan> inRangeList,
                                   double segmentCapacity, double equipmentCapacity,
                                   EntityEquipment equipment, ArrayList<EntityEquipmentPlan> plansStartOrEndAtThePoint,
                                   ArrayList<EntityEquipmentPlan> equipmentPlanInputs,
                                   HashMap<String, ArrayList<EntityEquipmentPlan>>  inRangeEquipmentStates) {
        Iterator<EntityEquipmentPlan> it2 = inRangeList.iterator();
        while(it2.hasNext()){
            EntityEquipmentPlan equipmentPlan = it2.next();
            Date startOfEquipmentPlan = equipmentPlan.getEquipmentStartInModel();
            double occupiedCapacityOfEquipmentPlan = equipmentPlan.getOccupiedCapacity();
            equipmentPlan.setEquipmentStartInModel(currentDate);
            if (segmentCapacity <= equipmentCapacity){
                // do nothing
            } else {
                occupiedCapacityOfEquipmentPlan = Math.max(equipmentCapacity
                        - (segmentCapacity - equipmentPlan.getOccupiedCapacity()),0);
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_NOT_SCHEDULED_RESOURCE_CONFLICT,
                        "设备 "+ equipment.getEquipmentName() +" 的已排程活动 "
                                + equipmentPlan.getEquipmentPlanId() + " 从 "
                                + DateUtil.getDateString(startOfEquipmentPlan)
                                + " 到 " + DateUtil.getDateString(currentDate)
                                + " 有冲突，忽略冲突，继续排程。 ");
            }
            segmentCapacity = segmentCapacity - equipmentPlan.getOccupiedCapacity();
            it2.remove();

            EntityEquipmentPlan newPlan = new EntityEquipmentPlan(); //the finished one is the new plan
            newPlan.setEquipmentPlanId(equipmentPlan.getEquipmentPlanId()+"_1");
            newPlan.setEquipmentStartInModel(startOfEquipmentPlan);
            newPlan.setEquipmentEndInModel(currentDate);
            newPlan.setOccupiedCapacity(occupiedCapacityOfEquipmentPlan);
            newPlan.setEquipmentStatus(equipmentPlan.getEquipmentStatus());
            newPlan.setEquipmentId(equipmentPlan.getEquipmentId());
            newPlan.setFunctionId(equipmentPlan.getFunctionId());
            newPlan.setIsConstraint(equipmentPlan.getIsConstraint());
            newPlan.setIsExpandable(equipmentPlan.getIsExpandable());
            newPlan.setEquipmentQty(equipmentPlan.getEquipmentQty());
            handleEquipmentStateWhenRemovePlanInRange(newPlan, equipment, inRangeEquipmentStates);
            equipmentPlanInputs.add(newPlan);
            plansStartOrEndAtThePoint.add(equipmentPlan);
            log.debug(" new split equipment plan is " + newPlan.toString());
        }
        for (EntityEquipmentPlan plan: plansStartOrEndAtThePoint) {
            inRangeList.add(plan);
            segmentCapacity += plan.getOccupiedCapacity();
        }
        return segmentCapacity;
    }

    private void loadAndProcessTaskPlanInput(){
        loadData.loadTaskPlanInput(dataContainer, functionId);
        Iterator<EntityTaskPlanInput> taskPlanInputIterator = dataContainer.getTaskPlanInputIterator();
        int size = dataContainer.getTaskPlanInputArrayList().size();
        log.info("Loaded TASK_PLAN_INPUT data with sub task filtering: " + size + " records");
        while (taskPlanInputIterator.hasNext()){
            EntityTaskPlanInput taskPlanInput = taskPlanInputIterator.next();
            if (taskPlanInput.singleCheck()){
                taskPlanInputHashMap.put(taskPlanInput.getTaskNo(), taskPlanInput);
                // 部分批次待排程的任务单，直接取已经指定的批次负责人
                String resEngineerId = taskPlanInput.getRespEngineerId();

                EntityTask task = taskHashMap.get(taskPlanInput.getTaskNo());
                if (task != null){

                    // 责任工程师id在人员表中不存在
                    if (!initialStaffHashMap.containsKey(resEngineerId)){
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                                "已部分排程任务单 " + task.getTaskNo() + " 对应的责任工程师（ID "+ resEngineerId
                                        + "）不在人员表中，忽略该设置，继续排程");
                        taskPlanInput.setRespEngineerId(null);
//                        task.setIsToBePlanned(false);
                    } else if (!engineerHashMap.containsKey(resEngineerId)){
                        EntityStaff staff = initialStaffHashMap.get(resEngineerId);
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_MISSING_DATA,
                                "已部分排程任务单 " + task.getTaskNo() + " 对应的责任工程师 " + staff.getStaffName() +
                                        "（ID "+ resEngineerId + ")不是工程师，忽略该设置，继续排程");
                        taskPlanInput.setRespEngineerId(null);
//                        task.setIsToBePlanned(false);
                    }
                    task.setAssignedRespEngineer(staffHashMap.get(resEngineerId));
                }
            }
        }
        size = taskPlanInputHashMap.size();
        log.info("Loaded TASK_PLAN_INPUT data after validation: " + size + " records");
    }

    /**
     * technician 和 technician_skill 读取数据预处理
     * @return flag 小阶段需要的技能集合是否为空
     */
    private Boolean getAllSkillSet(){
        ArrayList<EntityStepSkill> allStepSkill = dataContainer.getStepSkillArrayList();
        if (allStepSkill.size() == 0){
            return true;
        }
        Boolean flag = false;
        for (EntityStepSkill stepSkill : allStepSkill) {
            String skillIdSet = stepSkill.getSkillIdSet();
            // 判断小阶段需要的技能集合是否为空
            if (skillIdSet == null || StringUtils.isEmpty(skillIdSet)){
                flag = true;
                continue;
            }
            allSkillSet.addAll(Arrays.asList(skillIdSet.split(",")));
        }

        return flag;
    }


    /**
     * 计算 task，subTask 的可能授权人员
     */
    public void calculateAllPossiblePerson(){
        calculateAuthBom();

        HashSet<String> bomSet = new HashSet<>();
        EntityFunction function = functionHashMap.get(functionId);
        ArrayList<EntityTask> toBePlannedTasks = function.getToBePlannedTasks();
        for(EntityTask task: toBePlannedTasks) {
            if (!task.getIsToBePlanned()){
                continue;
            }
            String taskNo = task.getTaskNo();
            String respUserId = task.getRespUserId();
            // 任务单的指定负责人不在人员列表内
            if (respUserId != null && !staffHashMap.containsKey(respUserId)) {
                staffValidationMessage(respUserId, "任务单 " + task.getTaskNo() + " 的优先指定责任工程师 ");
                respUserId = null;
                task.setRespUserId(null);
            }
            // 用于储存每个任务单下所有的优先工程师
            HashSet<EntityStaff> authRespEngineersIntersection = new HashSet<>();

            //ArrayList<EntitySubTask> allSubTasks = task.getSubTasks();
            ArrayList<EntitySubTask> toBePlannedSubTasks = task.getToBePlannedSubTasks();
            // 遍历该 task 下面所有的 subTask
            int size = toBePlannedSubTasks.size();

            for (int i = 0; i < size; i++) {
                EntitySubTask subTask = toBePlannedSubTasks.get(i);
                if (!subTask.getIsToBePlanned()){
                    //仅考虑待排程批次
                    continue;
                }
                //(subTask.isFixed() || subTask.isPlanned()   || toBePlannedSubTasks.contains(subTask)){

                // 用于存放当批次下没有“报告撰写”大阶段时，所有小阶段可选工程师的并集
                HashSet<EntityStaff> subTaskRespEngineers = new HashSet<>();
                EntityBom bom = subTask.getBom();
                String bomNo = bom.getBomNo();

                ArrayList<EntityStep> steps = bom.getSteps();
                // 记录是否有小阶段授权人员包含

                for (EntityStep step : steps) {

                    if (!bomSet.contains(bomNo)) {
                        // 如果还没有处理过这个BOM
                        // 技师
                        addStepPossibleTechnicians(step, bom);

                        Integer engineerCount = step.getEngineerCount();
                        if (engineerCount == null || engineerCount == 0) {
                            continue;
                        }
                        if (!step.getResourceGroupHashMap().containsKey(DEFAULT_ENGINEER_RESOURCE_GROUP_NAME)){
                            continue;
                        }
                        // 工程师
                        addStepPossibleEngineers(step, bom);

                        // 所需工程师数量比有资格进行的该小阶段的工程师人数多
                        ArrayList<EntityStaff> authEngineersConsideredBOM = step.getAuthStepEngineers();

                        // 计算可能工程师的每周最大工作天数
                        int maxDayCount = calculateEngineerMaxWorkDaysPerWeek(authEngineersConsideredBOM);

                        // 计算按天工作时考虑工程师日历后需要的工时
                        double enHours = calculateEngineerHoursPerStepForWorkByDay(step, maxDayCount);
                        // 小阶段考虑模式后的时长
                        double timeLength = step.getTimeLengthConsideredWorkMode();

                        // 因为是对BOM计算的，所以timeLength没有考虑repeat，pass等的情况
                        // 统计 bom 中所有阶段所要的总时长
                        double bomLength = bom.getBomLength();
                        // 取小阶段时长和工程师时长的最大值计算
                        if (timeLength >= enHours ){
                            bom.setBomLength(bomLength + timeLength);
                        }else {
                            // 按天工作时，考虑工程师日历后是否工程师时长超过小阶段时长
                            DecimalFormat df = new DecimalFormat("#0.00");
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_TIME_CONFICT,
                                    "BOM " + bomNo + " 的小阶段 " + step.getStepName() + " (阶段顺序号 " + step.getStepOrder() + "，ID " + step.getStepId() +
                                            "）的工作时间要求（考虑工作模式后）为" + timeLength + "小时（"
                                            + df.format(timeLength/ 24) + "天），工程师的工作天数要求为 "
                                            + step.getEngineerDayNum() +
                                            " 天（在考虑人员日历后至少需要 " + enHours / 24 +" 天），大于小阶段工作时长，建议修改数据以匹配时长要求");

                            bom.setBomLength(bomLength + enHours);
                        }

                        int authCount = authEngineersConsideredBOM.size();
                        if (engineerCount > authCount){
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_RESOURCES_NOT_ENOUGH,
                                    "BOM " + bomNo+ " 的小阶段 " + step.getStepName()
                                            + "（阶段顺序号 " + step.getStepOrder() +"，阶段ID " + step.getStepId()
                                            + "）要求的工程师数量为 "+ engineerCount +
                                   " ，但有资格进行该小阶段的工程师只有 " +  authCount + " 个，导致相关任务单无法进行排程");
                            // 用到该 BOM 的所有 task 都无法排程
                            invalidBom.add(bomNo);
                            setSameBomTasksNotScheduling(bomNo);
                        }
                    }

                    // 考虑各种授权后待排程 subTask 的可能的“实验批次负责人”
                    ArrayList<EntityStaff> stepAuthEngineers = step.getAuthStepEngineers();
                    String testPhase = step.getTestPhase();
                    // 找出批次下“报告撰写”大阶段下的小阶段对应的step的engineer_group
                    if (StringUtils.equals(testPhase, REPORTING_PHASE_NAME)) {
                        subTask.getAuthRespEngineers().addAll(stepAuthEngineers);
                    } else {
                        // 获取该 subTask 下所有小阶段的并集
                        subTaskRespEngineers.addAll(stepAuthEngineers);
                    }
                }
                // 将做过人员授权的 BOM，加入到其中防止重复加载
                bomSet.add(bomNo);

                // 如果批次下没有“报告撰写”大阶段，则取该批次下所有小阶段的[auth_engineer_considered_bom]的并集
                if (subTask.getAuthRespEngineers().size() == 0){
                    subTask.setAuthRespEngineers(new HashSet<>(subTaskRespEngineers));
                }

                // 如果任务单指定了优先工程师，需要保证该工程师在该任务单下面的所有批次中
                if (respUserId != null){
                    if (!subTask.getAuthRespEngineers().contains(engineerHashMap.get(respUserId))) {
                        EntityStaff staff = initialStaffHashMap.get(respUserId);
                        if (staff == null) {
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                    "任务单 " + taskNo + " 的优先指定责任工程师 " + respUserId
                                            + " 在批次 " + subTask.getSubTaskNo() + " 的BOM中或该BOM中的每个小阶段中都没有授权，所以不符合授权条件，忽略该设置，继续排程");
                        } else {
                            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                    "任务单 " + taskNo + " 的优先指定责任工程师 " + staff.getStaffName()
                                    + "（ID "+ respUserId + "）在批次 " + subTask.getSubTaskNo()
                                            + " 的规范或BOM或该BOM中的任何一个小阶段中都没有授权，所以不符合授权条件，忽略该设置，继续排程");
                        }
                        task.setRespUserId(null);
                    }
                }

                // 获取该 task 下所有批次指定工程师的交集
                HashSet<EntityStaff> authRespEngineers = subTask.getAuthRespEngineers();
                if (i == 0){
                    authRespEngineersIntersection.addAll(authRespEngineers);
                }
                authRespEngineersIntersection.retainAll(authRespEngineers);
                //}


                // 从批次可以开始排程时间到排程结束时间之内，不足批次的时长
                // 因为要考虑repeat，放到subModelData中做了
//                Double bomLength = bom.getBomLength();
//                Date planPeriodEndTime = plan.getPlanPeriodEndTime();
//                int planLength = DateUtil.getDistanceIntTime(planPeriodEndTime, plan.getPlanPeriodStartTime(), 1000 * 60 * 60);
//                if (bomLength > planLength){
//                    DecimalFormat df = new DecimalFormat("#0.00");
//                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
//                            "批次 " + subTask.getSubTaskNo() + " 可以开始的时间为 " + DateUtil.getDateString(plan.getPlanPeriodStartTime())
//                                    + "，下属小阶段的工作时间（考虑工程师日历后）总和为 " + bomLength +
//                                    " 小时（"+  df.format(bomLength/24) +" 天），排程结束时间为 " + DateUtil.getDateString(planPeriodEndTime) + "，不可能完成该批次，导致相关任务单 " + taskNo + " 无法排程");
//                    task.setIsToBePlanned(false);
//              }  else
//                {
//                    // 计算每个 subTask 的长度
//                    String taskGroup = subTask.getTaskGroup();
//                    Double subTaskLength = 0.0;
//                    if (taskGroup != null && taskGroup != ""){
//                        // 有批次组 Id 的，计算组内 subTask 的总和
//                        // todo: remove, 批次组
//                        //ArrayList<EntitySubTask> subTasks = task.getSubTaskGroupMap().get(taskGroup);
//                        ArrayList<EntitySubTask> subTasks = subTaskGroupHashMap.get(taskGroup);
//                        if (subTasks != null && subTasks.size() != 0){
//                            for (EntitySubTask entitySubTask : subTasks) {
//                                if (entitySubTask.getTaskNo().equals(taskNo)){
//                                    //只有这个任务单下的批次才考虑
//                                    if (entitySubTask.getIsToBePlanned()) {
//                                        subTaskLength += entitySubTask.getBom().getBomLength();
//                                    } else
//                                        //if(entitySubTask.isFixed() && entitySubTask.getTaskSeqNo() >)
//                                    {
//                                        // todo: 批次组，需要按照fixed时间来计算可能要什么时候
//
//                                    }
//                                }
//                            }
//                        }
//                    }else {
//                        subTaskLength = bomLength;
//                    }
//
//                    // 统计 task 下所有 step 所要时间总和
//                    double taskLength = task.getTaskLength();
//                    if (subTaskLength > taskLength){
//                        task.setTaskLength(subTaskLength);
//                    }
//                }
            }
            // 待排程任务单执行工程师的可能的人选——不会被子模型更新的列表
            taskInitialPossibleRespEngineers(task, authRespEngineersIntersection);

            // 在考虑了各种人员授权和工程师分配等要求后，任务单的可用工程师人数为0
            if (task.getSubTasks().size() > 0 && task.getPossibleRespEngineers().size() == 0){
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                        "任务单" + taskNo + " 的批次之间没有可公用的任务单负责人，导致该任务单无法排程");
                task.setIsToBePlanned(false);
            }

            // 计算从排程开始到结束时间之内根据任务单下小阶段实施时间的要求是否根本不可能完成整个任务单
//            int planLength = DateUtil.getDistanceIntTime(plan.getPlanPeriodEndTime(), plan.getPlanPeriodStartTime(), 1000 * 60 * 60);
//
//            if (task.getTaskLength() > planLength){
//                DecimalFormat df = new DecimalFormat("#0.00");
//                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_TIME_CONFICT,
//                        "任务单 " + task.getTaskNo() + " 可以开始的时间为 " + DateUtil.getDateString(plan.getPlanPeriodStartTime()) +
//                                "，下属批次的工作时间（考虑批次组顺序要求后）为 " + task.getTaskLength() + " 小时（"
//                                + df.format(task.getTaskLength()/24)
//                                +" 天），排程结束时间为 " + DateUtil.getDateString(plan.getPlanPeriodEndTime()) +
//                                "，不可能完成该任务单，导致该任务单无法排程");
//
//                task.setIsToBePlanned(false);
//            }
        }

    }



    /**
     * 工程师的possible resource的日历中的每周工作时间
     * @param step
     * @param maxDayCount
     * @return
     */
    private double calculateEngineerHoursPerStepForWorkByDay(EntityStep step, int maxDayCount) {
        // timeLength没有考虑repeat，pass等的情况
        Integer engineerDayNum = step.getEngineerDayNum();
        // 工程师的possible resource的日历中的每周工作时间
        double enHours = 0;
        if (engineerDayNum != null && engineerDayNum > 0 && step.getEngineerDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
            if (engineerDayNum != 1){
                // 计算工程师时间
                int i1 = engineerDayNum % maxDayCount;
                int i2 = 7 - maxDayCount;
                if (i1 == 0){
                    enHours = (engineerDayNum / maxDayCount * 7 - i2) * 24;
                }else {
                    enHours = (engineerDayNum / maxDayCount * 7 + i1) * 24;
                }
            }  else{
                enHours = step.getEngineerDailyWorktime().doubleValue();
            }
        }
        return enHours;
    }

    /**
     *计算可能工程师的每周最大工作天数
     */
    private int calculateEngineerMaxWorkDaysPerWeek(ArrayList<EntityStaff> authEngineersConsideredBOM) {
        int maxDayCount = 0;
        for (EntityStaff staff : authEngineersConsideredBOM) {
            ArrayList<EntityStaffCalendar> staffCalendars = staff.getStaffCalendar();
            for (EntityStaffCalendar staffCalendar : staffCalendars) {
                Integer calendarId = staffCalendar.getCalendarId();
                if (staffCalendar.getCalendarClass().equals(CALENDAR_CLASS_NORMAL)) {
                    EntityCalendar calendar = calendarHashMap.get(calendarId);
                    String weekWorkDay = calendar.getWeekWorkDay();
                    char[] chars = weekWorkDay.trim().toCharArray();
                    int count = 0;
                    for (char c : chars) {
                        if (c == '1'){
                            count++;
                        }
                    }
                    if (count > maxDayCount){
                        maxDayCount = count;
                    }
                }
            }
        }
        return maxDayCount;
    }

    /**
     * 添加小阶段的可选工程师
     * @param step
     * @param bom
     */
    private void addStepPossibleEngineers(EntityStep step, EntityBom bom){
        EntityResourceGroup resourceGroup = step.getResourceGroupHashMap().get(DEFAULT_ENGINEER_RESOURCE_GROUP_NAME);
        if (resourceGroup == null){
            return;
        }
        String bomType = bom.getBomType();
        ArrayList<EntityStaff> authStep = step.getAuthStepEngineers();
        ArrayList<EntityStaff> authBom = bom.getAuthBomEngineers();
        ArrayList<EntityStaff> authBomAndProcedure = bom.getAuthBomAndProcedureEngineers();

        // 同一个小阶段的第一个step skill，需要计算授权
        addStepAuthStaffs(step, bom, RESOURCE_TYPE_ENGINEER,authStep,  authBom,
                authBomAndProcedure);

        Integer resourceQuantity = resourceGroup.getRequestedResourceQuantity();
        if (resourceQuantity > authStep.size()) {
            CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                    "小阶段 " + step.getStepName() + "(阶段顺序号 " + step.getStepOrder() +
                            ", ID " + step.getStepId() + "）的授权工程师个数为" + authStep.size() +
                            " 个小于需要的工程师个数 " + resourceQuantity + "，导致相关任务单无法排程");
            invalidBom.add(bom.getBomNo());
            setSameBomTasksNotScheduling(bom.getBomNo());
        }

        // 向模型需要的资源中添加
        ArrayList<Object> possibleResources = resourceGroup.getPossibleResources();
        possibleResources.addAll(authStep);

        // 将可选工程师添加到所有可能的人员列表中
        allPossibleStaffs.addAll(authStep);
    }

    /**
     * 添加小阶段人员, 为authStep重新赋值
     * @param step
     */
    private boolean addStepAuthStaffs(EntityStep step, EntityBom bom, String resourceType,
                                   ArrayList<EntityStaff> authStep,  ArrayList<EntityStaff> authBom,
                                   ArrayList<EntityStaff> authBomAndProcedure) {
        // calculateAuthBom()中已经检查了procedure相关的授权，这里只需要关心小阶段授权和bom授权的关系即可。
        // 如果bom原来size为0，小阶段为0，认为都可以做；小阶段不为0，报错
        // 如果bom原来size不为0，小阶段为0，bom的值赋给小阶段；小阶段不为0，求交集；如果交集后小于需要的人数，报错。

        String resourceTypeMessage = resourceType.equals(RESOURCE_TYPE_ENGINEER)? "工程师":"技师";
        if (authBomAndProcedure.size() == 0){
            if (authStep.size() == 0){
                // 所有人都可以做(3种授权都没有)，就赋值所有可能的技师
                if (resourceType.equals(RESOURCE_TYPE_ENGINEER)) {
                    authStep.addAll(new ArrayList<>(engineerHashMap.values()));
                } else {
                    authStep.addAll(new ArrayList<>(technicianHashMap.values()));
                }
            } else {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                        "小阶段 " + step.getStepName()
                                + "(阶段顺序号 " + step.getStepOrder() + ", ID "+ step.getStepId()
                                + "）有"+resourceTypeMessage+"授权，但BOM " + bom.getBomNo()
                                + " 没有" +resourceTypeMessage+ "授权，导致相关任务单无法排程");
                invalidBom.add(bom.getBomNo());
                setSameBomTasksNotScheduling(bom.getBomNo());
                return false;
            }
        } else {
            // 如果bom原来size不为0，小阶段为0，bom考虑过procedure的值赋给小阶段；小阶段不为0，求交集；如果交集后小于需要的人数，报错。
            if (authStep.size() == 0 ){
                authStep.addAll(authBomAndProcedure);
            } else {
                for (EntityStaff staff: authStep) {
                    if (!authBomAndProcedure.contains(staff)) {
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                "小阶段 " + step.getStepName()
                                        + "(阶段顺序号 " + step.getStepOrder() + ", ID "+ step.getStepId()
                                        + "）的授权 " + staff.resourceTypeMessage()
                                        + staff.getStaffName() + "（ID " + staff.getStaffId()
                                        + "）没有获得相应BOM " + bom.getBomNo()
                                        + " 的授权，该人员被考虑为未授权，继续排程。");
                    }
                }
                authStep.retainAll(authBomAndProcedure);
            }
        }
        return true;
    }

    /**
     * 添加小阶段可选技师
     * @param step
     */
    private void addStepPossibleTechnicians(EntityStep step, EntityBom bom){

        // 为小阶段添加技师相关资源
        HashMap<String, EntityResourceGroup> resourceGroupHashMap = step.getResourceGroupHashMap();
        int indexOfStepSkillInSameStep = 0;
        for (Map.Entry<String, EntityResourceGroup> entry: resourceGroupHashMap.entrySet()) {
            EntityResourceGroup resourceGroup = entry.getValue();
            //本函数只处理technician的情况
            if (!resourceGroup.getResourceType().equals(RESOURCE_TYPE_TECHNICIAN)){
                continue;
            }


            // calculateAuthBom()中已经检查了procedure相关的授权，这里只需要关心小阶段授权和bom授权的关系即可。
            // 如果bom原来size为0，小阶段为0，认为都可以做；小阶段不为0，报错
            // 如果bom原来size不为0，小阶段为0，bom的值赋给小阶段；小阶段不为0，求交集；如果交集后小于需要的人数，报错。
            ArrayList<EntityStaff> authStep = step.getAuthStepTechnicians();
            ArrayList<EntityStaff> authBom = bom.getAuthBomTechnicians();
            ArrayList<EntityStaff> authBomAndProcedure = bom.getAuthBomAndProcedureTechnicians();

            if (indexOfStepSkillInSameStep == 0 ) {
                // 同一个小阶段的第一个step skill，需要计算授权
                addStepAuthStaffs(step, bom, RESOURCE_TYPE_TECHNICIAN,authStep,  authBom,
                        authBomAndProcedure);
            }
            indexOfStepSkillInSameStep++;

            Integer technicianCount = resourceGroup.getRequestedResourceQuantity();
            if (technicianCount > authStep.size()) {
                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                        "小阶段 " + step.getStepName() + "(阶段顺序号 " + step.getStepOrder() +
                                ", ID " + step.getStepId() + "）的授权技师个数为" + authStep.size() +
                                " 个小于需要的技师个数 " + technicianCount + "，导致相关任务单无法排程");
                invalidBom.add(bom.getBomNo());
                setSameBomTasksNotScheduling(bom.getBomNo());
                continue;
            }
            // 根据小阶段所需要的技师技能进行筛选
            String index = step.getBomNo() + step.getStepId();
            String stepSkillKey = index;
            if (!resourceGroup.getResourceGroupName().equals(DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME)){
                stepSkillKey = index + entry.getKey();
            }
            EntityStepSkill stepSkill = stepSkillHashMap.get(stepSkillKey);
            // step 没有对应的 stepSkill 就说明没有技师要求
            if (stepSkill == null){
                // 不应该到这里
            }
            // skillIdSet 为空时
            String skillIdSet = stepSkill.getSkillIdSet();
            if (skillIdSet == null || skillIdSet.equals("")) {
                resourceGroup.getPossibleResources().addAll(authStep);
                // 将可选技师添加到所有可能的人员列表中
                allPossibleStaffs.addAll(authStep);
                // skillIdSet 不为空时
            } else {
                String[] skillIds = skillIdSet.split(",");
                // 获得掌握所有 skills 技能的授权技师
                ArrayList<EntityStaff> reTechnicians = new ArrayList<>();
                reTechnicians.addAll(authStep);

                ArrayList<EntityStaff> allSkillTechnicians = new ArrayList<>();
                if (skillTechnicianHashMap.get(skillIds[0])!=null) {
                    allSkillTechnicians.addAll(skillTechnicianHashMap.get(skillIds[0]));
                }
                for (int i = 0; i < skillIds.length; i++) {
                    ArrayList<EntityStaff> technicians = skillTechnicianHashMap.get(skillIds[i]);
                    // 取交集
                    if (technicians == null){
                        // 这种情况在checkStepSkill里面已经报错
                        reTechnicians.clear();
                        allSkillTechnicians.clear();
                    } else {
                        allSkillTechnicians.retainAll(technicians);
                        reTechnicians.retainAll(technicians);
                    }
                }
                if (reTechnicians.size() < authStep.size()){
                    if (allSkillTechnicians.size() >= authStep.size()) {
                        // 如果有技能的人数比被授权的人数多，按照授权来报错
                        for (EntityStaff tech : authStep) {
                            if (!reTechnicians.contains(tech)) {
                                // 已授权人员但是不满足所有skill要求
                                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                        "技师 " + tech.getStaffName() + "（ID "+tech.getStaffId() + "）在BOM " + bom.getBomNo() + " 的小阶段 " + step.getStepName()
                                                + "(阶段顺序号 " + step.getStepOrder() + ", ID " + step.getStepId()
                                                + "）有授权，但不满足技能组 " + skillIdSet + " 的所有技能要求，忽略该人员，继续排程");
                            }
                        }
                    } else {
                        // 被授权的人多，有技能的人少，按照技能报warning
                        for (EntityStaff tech : allSkillTechnicians) {
                            if (!reTechnicians.contains(tech)) {
                                // 已授权人员但是不满足所有skill要求
                                CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                                        "技师 " + tech.getStaffName() + "（ID "+tech.getStaffId() + "）在BOM " + bom.getBomNo() + " 的小阶段 " + step.getStepName()
                                                + "(阶段顺序号 " + step.getStepOrder() + ", ID " + step.getStepId()
                                                + "）满足技能组 " + skillIdSet + " 的所有技能要求，但是没有授权，忽略该人员，继续排程");
                            }
                        }

                    }
                }
                // 将该这些工程师添加到 step 技师的资源组中
                resourceGroup.getPossibleResources().addAll(reTechnicians);
                // 将可选技师添加到所有可能的人员列表中
                allPossibleStaffs.addAll(reTechnicians);
            }
        }
    }


    /**
     * 给 task 中的 initialPossibleRespEngineers 赋值
     * @param task
     * @param authRespEngineersIntersection
     */
    private void taskInitialPossibleRespEngineers(EntityTask task, HashSet<EntityStaff> authRespEngineersIntersection){
        // 判断该任务单是全部批次待排程
        if (task.getToBePlannedSubTasks().size() == task.getSubTasks().size()){

            String respUserId = task.getRespUserId();
            // 有优先指定工程师
            if (respUserId != null){
                EntityStaff staff = staffHashMap.get(respUserId);
                // 工程师是否在交集内
                if (authRespEngineersIntersection.contains(staff)){
                    task.getInitialPossibleRespEngineers().add(staff);
                    task.getPossibleRespEngineers().add(staff);
                }else {
                    // 不在，给warning，然后集合中为前面计算得到的批次的可能的工程师的交集
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_WARNNING, MESSAGE_TYPE_STAFF_NOT_ENOUGH,
                            "任务单 " + task.getTaskNo() + " 优先指定工程师不在该任务单的所有批次的授权工程师中");
                    task.setInitialPossibleRespEngineers(new ArrayList<>(authRespEngineersIntersection));
                    task.setPossibleRespEngineers(new ArrayList<>(authRespEngineersIntersection));
                }
            }else {
                task.setInitialPossibleRespEngineers(new ArrayList<>(authRespEngineersIntersection));
                task.setPossibleRespEngineers(new ArrayList<>(authRespEngineersIntersection));
            }
            // 部分批次待排程的任务单
        }else {
            EntityStaff assignedRespEngineer = task.getAssignedRespEngineer();
            task.getInitialPossibleRespEngineers().add(assignedRespEngineer);
            task.getPossibleRespEngineers().add(assignedRespEngineer);
        }
    }
    public void createSubModelsByTasksAndGranularity() {
        //获得task的approval date，并按这个sort，approve早的先排
        // subModelHashMap的key是date+task no+(1000-granularity)
        EntityFunction function = functionHashMap.get(functionId);
        for (EntityTask task: function.getToBePlannedTasks()){
            String taskNo = task.getTaskNo();
            if (task.getIsToBePlanned() == false){
                continue;
            }
            Date approvalDate = task.getApproveCompleteDate();
            ArrayList<EntitySubTask> subTasks = task.getToBePlannedSubTasks();
            for (int i = 0; i< subTasks.size(); i++ ) {
                EntitySubTask subTask = subTasks.get(i);
                int granularity = subTask.getPlanGranul();
                String subModelName = DateUtil.getDateString(approvalDate)
                        + "_" + taskNo + "_"+createSubModelName(granularity);
                if (!subModelHashMap.containsKey(subModelName)) {
                    // 创新新的子模型
                    SubModel subModel = new SubModel(plan.getPlanId(), schedulingTask,subModelName);
                    subModelHashMap.put(subModelName, subModel);
                }
                // 添加当前批次到子模型中
                addSubTaskToSubModel(subTask, subModelHashMap.get(subModelName));

            }
        }
    }

    /**
     * 根据批次组，兼顾时间颗粒度来生成子模型
     * @param separateSubTaskGroup 是否强制拆分每个批次组
     */
    public void createSubModelsBySubTaskGroupAndGranularity(boolean separateSubTaskGroup) {

        int previousGranularity = 0;
        SubModel previousSubModel = null ;
        int subModelIndex = 1;

        sortedSubTaskGroupHashMap = getValidSortedSubTaskGroupHashMap(sortedSubTaskGroupHashMap,
                "由于数据问题无法排程");

        for (Map.Entry<String, ArrayList<EntitySubTask>> entry: sortedSubTaskGroupHashMap.entrySet()) {
            if(separateSubTaskGroup){
                previousGranularity = 0;
                previousSubModel = null ;
            }
            String subTaskGroup = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            for (int i = 0; i< subTasks.size(); i++ ){
                EntitySubTask subTask = subTasks.get(i);
                if (!subTask.getIsToBePlanned()) {
                    // 应该不会到这个分支
                    continue;
                }
                EntityTask task = taskHashMap.get(subTask.getTaskNo());
                if (!task.getIsToBePlanned()){
                    // 应该不会到这个分支
                    continue;
                }
                int granularity = subTask.getPlanGranul();
                if (previousGranularity != granularity) {
                    // 创新新的子模型
                    String subModelName = subModelIndex + "_" + subTaskGroup + "_" + granularity;
                    previousSubModel = new SubModel(plan.getPlanId(), schedulingTask, subModelName);
                    subModelHashMap.put(subModelName, previousSubModel);
                    previousGranularity = granularity;
                    subModelIndex++;
                }
                // 添加当前批次到子模型中
                addSubTaskToSubModel(subTask, previousSubModel);

            }
        }
        if(separateSubTaskGroup){
            previousGranularity = 0;
            previousSubModel = null ;
        }
        // 处理不在批次组内的批次
        createSubModelsByGranularity(true,previousGranularity, previousSubModel);

    }

    public void createSubModelsByGranularity(boolean considerSubTaskGroup,
                                              int previousGranularity,
                                              SubModel previousSubModel) {

        for (Map.Entry<Integer, ArrayList<EntitySubTask>> entry: granularitySubTaskHashMap.entrySet()){
            Integer granularity = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            for (EntitySubTask subTask: subTasks){
                if (!subTask.getIsToBePlanned()) {
                    continue;
                }
                EntityTask task = taskHashMap.get(subTask.getTaskNo());
                if (!task.getIsToBePlanned()){
                    continue;
                }
                if (considerSubTaskGroup) {
                    if (subTask.getTaskGroup() != null && !subTask.getTaskGroup().equals("")) {
                        continue;
                    }
                }

                if (previousGranularity != granularity){
                    // 创新新的子模型
                    String subModelName = "noSubTaskGroup" + "_" + createSubModelName(granularity);
                    previousSubModel = new SubModel(plan.getPlanId(), schedulingTask, subModelName);
                    subModelHashMap.put(subModelName, previousSubModel);
                    previousGranularity = granularity;
                }
                // 添加当前批次到子模型中
                addSubTaskToSubModel(subTask, previousSubModel);
            }
        }
    }

    public HashMap<String, ArrayList<EntitySubTask>> getValidSortedSubTaskGroupHashMap(
            HashMap<String, ArrayList<EntitySubTask>> sortedSubTaskGroupHashMap,
            String reasonMessage) {
        HashMap<String, ArrayList<EntitySubTask>> validSortedSubTaskGroupHashMap = new HashMap<>();
        for (Map.Entry<String, ArrayList<EntitySubTask>> entry: sortedSubTaskGroupHashMap.entrySet()) {
            String subTaskGroup = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            if (subTasks.size() == 0){
                continue;
            }
            boolean subTaskCanSchedule = true;
            ArrayList<EntitySubTask> validSubTasks = new ArrayList<>();
            for (int i = 0; i< subTasks.size(); i++ ){
                EntitySubTask subTask = subTasks.get(i);
                EntityTask task = taskHashMap.get(subTask.getTaskNo());
                if (!subTask.getIsToBePlanned() || !task.getIsToBePlanned()){
                    if (subTaskCanSchedule && i != subTasks.size() -1 ){
                        // 之前是可以排的，到这里不能排程了，而且不是最后一个，提示批次组内后续不能排程了
                        CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT,
                                "批次 " + subTask.getSubTaskNo() + " 在批次组 " + subTaskGroup
                                        + " 内（顺序号 " + subTask.getTaskSeqNo() + "），其本身或其所在的任务单 "
                                        + task.getTaskNo() + " "+ reasonMessage
                                        +"，导致批次组内后续批次和相关任务单无法排程");
                    }
                    subTaskCanSchedule = false;
                    subTask.setIsToBePlanned(false);
                    task.setIsToBePlanned(false);
                    continue;
                }
                if (!subTaskCanSchedule) {
                    // 终止这个批次后续的排程
                    CheckDataUtil.packageAndLog(MESSAGE_SEVERITY_ERROR, MESSAGE_TYPE_SUB_TASK_GROUP_CONFLICT,
                            "批次 " + subTask.getSubTaskNo() + " 在批次组 " + subTaskGroup
                                    + " 内（顺序号 " + subTask.getTaskSeqNo() + "）有前序批次"+ reasonMessage
                            + "，导致该批次和相关任务单 "
                                    + subTask.getTaskNo() + " 无法排程");
                    subTask.setIsToBePlanned(false);
                    task.setIsToBePlanned(false);
                } else {
                    validSubTasks.add(subTask);
                }
            }
            if (validSubTasks.size() > 0 ) {
                validSortedSubTaskGroupHashMap.put(subTaskGroup, validSubTasks);
            }
        }
        return validSortedSubTaskGroupHashMap;
    }

    private void addSubTaskToSubModel(EntitySubTask subTask, SubModel subModel) {
        subTask.setSubModelName(subModel.getSubModelName());
        //更新sub model的plan config 参数
        SubModelData subModelData = subModel.getSubModelData();
        subModelData.setPlanGranularity(subTask.getPlanGranul());
        subModelData.setMaxCalendarDays(subTask.getCalendarDays());
        subModelData.setMaxTimeLimit(subTask.getPlanTimeLimit());
        subModelData.setMinPlanStartTime(subTask.getMinPlanStartTime());
        subModelData.setMaxPlanEndTime(subTask.getMaxPlanEndTime());
        plan.setMaxPlanPeriodEndTime(subTask.getMaxPlanEndTime());
        //将批次加入sub model data的子模型待排程批次中
        HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMapInSubModel
                = subModelData.getToBePlannedSubTaskMap();
        ArrayList<EntitySubTask> subTasks = toBePlannedSubTaskMapInSubModel.computeIfAbsent(subTask.getTaskNo(),
                k-> new ArrayList<>());
        subTasks.add(subTask);
// todo, remove应该用不到这个 ，批次组

//        String subTaskGroup = subTask.getTaskNo();
//        if (subTaskGroup != null || subTaskGroup.equals("")){
//            //添加subtask 到对应的子模型的subtask group hashmap里面
//            HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskGroupMapInSubModel
//                    = subModelData.getToBePlannedSubTaskGroupMap();
//            ArrayList<EntitySubTask> subTasks2 = toBePlannedSubTaskGroupMapInSubModel.computeIfAbsent(subTaskGroup,
//                    k-> new ArrayList<>());
//            subTasks2.add(subTask);
//        }
    }

//    private SubModel createNewSubModel(EntitySubTask subTask, String subModelName) {
//
//        SubModel subModel = new SubModel(String.valueOf(plan.getPlanId()), schedulingTask, subModelName);
//        addSubTaskToSubModel(subTask, subModel);
//        return subModel;
//    }
    public class SortForEquipmentPlans implements Comparator<EntityEquipmentPlan>{

        @Override
        public int compare(EntityEquipmentPlan o1, EntityEquipmentPlan o2) {
            return getCompareString(o1.getEquipmentStartInModel(), o1.getEquipmentEndInModel())
                    .compareTo(getCompareString(o2.getEquipmentStartInModel(), o2.getEquipmentEndInModel()));
        }

        private String getCompareString(Date startInModel, Date endInModel) {
            return DateUtil.getDateString(startInModel) +DateUtil.getDateString(endInModel);
        }
    }

    class SortForStaffPlans implements Comparator<EntityStaffPlan>{
        @Override
        public int compare(EntityStaffPlan o1, EntityStaffPlan o2) {
            return getCompareString(o1.getStaffStartInModel(), o1.getStaffEndInModel())
                    .compareTo(getCompareString(o2.getStaffStartInModel(), o2.getStaffEndInModel()));
        }

        private String getCompareString(Date staffStartInModel, Date staffEndInModel) {
            return DateUtil.getDateString(staffStartInModel) +DateUtil.getDateString(staffEndInModel);
        }
    }
}


