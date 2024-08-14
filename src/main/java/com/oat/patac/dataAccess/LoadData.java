package com.oat.patac.dataAccess;

import com.alibaba.druid.util.StringUtils;
import com.oat.patac.dao.*;
import com.oat.patac.entity.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.PLAN_MODE_UNKNOWN;

/**
 * @author:yhl
 * @create: 2022-08-17 09:03
 * @Description: 读取数据库中的数据，初始化DataContainer
 */
@Log4j2
@Service
@Data
public class LoadData {

    @Resource
    TestAreaMapper testAreaMapper;
    @Resource
    LaboratoryMapper laboratoryMapper;
    @Resource
    FunctionMapper functionMapper;

    @Resource
    PlanConfigMapper planConfigMapper;
    @Resource
    ProcedureMapper procedureMapper;
    @Resource
    BomMapper bomMapper;

    @Resource
    TaskMapper taskMapper;
    @Resource
    SubTaskMapper subTaskMapper;

    @Resource
    AuthProcedureMapper authProcedureMapper;
    @Resource
    AuthBomMapper authBomMapper;
    @Resource
    StepMapper stepMapper;
    @Resource
    AuthStepMapper authStepMapper;
    @Resource
    StepSkillMapper stepSkillMapper;
    @Resource
    StepEquipmentGroupMapper stepEquipmentGroupMapper;
    @Resource
    StaffMapper staffMapper;
    @Resource
    StaffCalendarMapper staffCalendarMapper;
    @Resource
    SkillMapper skillMapper;
    @Resource
    TechnicianSkillMapper technicianSkillMapper;
    @Resource
    CalendarMapper calendarMapper;
    @Resource
    RollCalendarMapper rollCalendarMapper;
    @Resource
    SpecialDayMapper specialDayMapper;

    @Resource
    EquipmentGroupMapper equipmentGroupMapper;
    @Resource
    EquipmentMapper equipmentMapper;
    @Resource
    EquipmentGroupRelMapper equipmentGroupRelMapper;
    @Resource
    EquipmentCalendarMapper equipmentCalendarMapper;
    @Resource
    SampleMapper sampleMapper;

    @Resource
    TaskPlanInputMapper taskPlanInputMapper;
    @Resource
    SubTaskPlanInputMapper subTaskPlanInputMapper;
    @Resource
    StepPlanInputMapper stepPlanInputMapper;
    @Resource
    EquipmentPlanInputMapper equipmentPlanInputMapper;
    @Resource
    StaffPlanInputMapper staffPlanInputMapper;



    /**
     * 创建和初始化DataContainer数据容器
     * @return
     * todo: 改完新模式后，可以去掉
     */
    public DataContainer initializeDataContainer(Integer functionId, String planId) {
        DataContainer dataContainer = new DataContainer();

        // 获取Sub Task
        ArrayList<EntitySubTask> subTasks = subTaskMapper.getSubTask();
        dataContainer.setSubTaskArrayList(subTasks);

        if (functionId == null) {
            //默认使用第一个
            if (subTasks.size() == 0){
                //todo 报错：没有需要排程的批次
            } else {
                functionId = subTasks.get(0).getFunctionId();
                log.info("功能块ID是 " + functionId);
            }
        } else {
            log.info("功能块ID是 " + functionId);
        }

        EntityPlan plan = new EntityPlan(functionId, planId, PLAN_MODE_UNKNOWN);
        dataContainer.setPlan(plan);

        return dataContainer;
    }

    /**
     * 获取本次待排程的功能块实体
     * @param dataContainer
     * @param functionId
     */
    public void loadFunctionData(DataContainer dataContainer, int functionId) {
        dataContainer.setFunctionArrayList(functionMapper.getFunction(functionId));
    }

    /**
     * 获取本次待排程的任务单实体
     * @param dataContainer
     * @param functionId
     */
    public void loadTaskData(DataContainer dataContainer, int functionId) {
        dataContainer.setTaskArrayList(taskMapper.getTaskBySubTask(functionId));
    }

    /**
     * 单体验证, 得到待排程批次列表
     * @param dataContainer
     * @param functionId
     */
    public void loadSudTaskData(DataContainer dataContainer, int functionId){
        dataContainer.setSubTaskArrayList(subTaskMapper.getSubTaskByFunctionId(functionId));
    }

    /**
     * 获取本次待排程的批次对应的规范
     * @param dataContainer
     * @param functionId
     */
    public void loadProcedureData(DataContainer dataContainer, int functionId) {
        dataContainer.setProcedureArrayList(procedureMapper.getProcedureBySubTask(functionId));
    }
    /**
     * 获取本次待排程的批次对应的BOM对应的排程参数模板
     * @param dataContainer
     * @param functionId
     */
    public void loadPlanConfigData(DataContainer dataContainer, int functionId) {
        dataContainer.setPlanConfigArrayList(planConfigMapper.getPlanConfigBySubTaskBOM(functionId));
    }
    /**
     * 获取本次待排程的批次对应的BOM
     * @param dataContainer
     * @param functionId
     */
    public void loadBomData(DataContainer dataContainer, int functionId) {
        dataContainer.setBomArrayList(bomMapper.getBomBySubTask(functionId));
    }
    /**
     * 仅获取本次待排程批次和待排程批次所在任务单中的已完成批次使用的样品实体
     * @param dataContainer
     * @param functionId
     */
    public void loadSampleBySubTask(DataContainer dataContainer, int functionId) {
        dataContainer.setSampleArrayList(sampleMapper.getSampleBySubTask(functionId));
    }

    /**
     * 仅获取从某特定时间之后的特殊日期记录
     * @param dataContainer
     * @param time: 取这个时间之后的特殊日期记录
     */
    public void loadSpecialDayAfterSpecificTimeData(DataContainer dataContainer, Date time) {
        dataContainer.setSpecialDayArrayList(specialDayMapper.getSpecialDayAfterSpecificTime(time));
    }
    /**
     * 获取特殊日期记录
     * @param dataContainer
     */
    public void loadSpecialDayData(DataContainer dataContainer) {
        dataContainer.setSpecialDayArrayList(specialDayMapper.getAllSpecialDay());
    }

    /**
     * 获取批次中存在任务单（即本次待排程的任务单）的已排程批次
     * @param dataContainer
     * @param functionId
     */
    public void loadSubTaskPlanInputOfSameTaskData(DataContainer dataContainer, int functionId){
        dataContainer.setSubTaskPlanInputArrayList(subTaskPlanInputMapper.getSubTaskPlanInputOfSameTask(functionId));
        //dataContainer.setSubTaskPlanInputArrayList(subTaskPlanInputMapper.getAllSubTaskPlanInput(functionId));
    }
    /**
     * 获取和待排程批次在同一个批次组的已排程批次
     * @param dataContainer
     * @param functionId
     */
    public void loadSubTaskPlanInputOfSameSubTaskGroupData(DataContainer dataContainer, int functionId){
        dataContainer.setSubTaskPlanInputOfSameSubTaskGroupArrayList(subTaskPlanInputMapper.getSubTaskPlanInputOfSameSubTaskGroup(functionId));
    }
    /**
     * 获取批次中存在任务单（即本次待排程的任务单）的已排程批次
     * @param dataContainer
     * @param functionId
     */
    public void loadSubTaskPlanInputOfSameUniqueSampleData(DataContainer dataContainer, int functionId, Set<String> uniqueSampleNoSet){
        dataContainer.setSubTaskPlanInputOfSameUniqueSampleNoArrayList(
                subTaskPlanInputMapper.getSubTaskPlanInputOfSameUniqueSample(functionId, uniqueSampleNoSet));
    }

    /**
     * 得到待排程BOM中的小阶段
     * @param dataContainer
     * @param functionId
     */
    public void loadStepData(DataContainer dataContainer, int functionId){
        dataContainer.setStepArrayList(stepMapper.getAllStep(functionId));
    }

    /**
     * 得到规范人员授权
     * @param dataContainer
     * @param functionId
     */
    public void loadAuthProcedure(DataContainer dataContainer, int functionId){
        dataContainer.setAuthProcedureArrayList(authProcedureMapper.getAllAuthProcedure(functionId));
    }

    /**
     * 得到BOM人员授权
     * @param dataContainer
     * @param functionId
     */
    public void loadAuthBom(DataContainer dataContainer, int functionId){
        dataContainer.setAuthBomArrayList(authBomMapper.getAllAuthBom(functionId));
    }

    /**
     * 得到小阶段人员授权
     * @param dataContainer
     * @param functionId
     */
    public void loadAuthStep(DataContainer dataContainer, int functionId){
        dataContainer.setAuthStepArrayList(authStepMapper.getAllAuthStep(functionId));
    }

    /**
     * 得到需要的 skill 信息
     * @param dataContainer
     * @param skills
     */
    public void loadSkill(DataContainer dataContainer, HashSet<String> skills){
        dataContainer.setSkillArrayList(skillMapper.getAllSkill(skills));
    }

    /**
     * 得到待排程阶段要求的技能组
     * @param dataContainer
     * @param functionId
     */
    public void loadStepSkill(DataContainer dataContainer, int functionId){
        dataContainer.setStepSkillArrayList(stepSkillMapper.getAllStepSkills(functionId));
    }

    /**
     * 得到功能块可能的工程师的集合
     * @param dataContainer
     * @param functionId
     */
    public void loadEngineer(DataContainer dataContainer, int functionId){
        dataContainer.setEngineerArrayList(staffMapper.getAllEngineers(functionId));
    }

    /**
     * 得到所有的人员的集合
     * @param dataContainer
     */
    public void loadAllStaffs(DataContainer dataContainer){
        dataContainer.setAllStaffArrayList(staffMapper.getAllStaffs());
    }

    /**
     * 得到所有技师
     * @param dataContainer
     */
    public void loadAllTechnician(DataContainer dataContainer){
        dataContainer.setTechnicianArrayList(staffMapper.getAllTechnicians());
    }

    /**
     * 根据step需要的技能组筛选过的集合
     * @param dataContainer
     * @param skillSet
     */
    public void loadTechnicianBySkillSet(DataContainer dataContainer, HashSet<String> skillSet){
        dataContainer.setTechnicianArrayList(staffMapper.getAllTechniciansByStepSkill(skillSet));
    }

    /**
     * 得到待排程阶段要求的技能组
     * @param dataContainer
     * @param skillSet
     */

    public void loadTechnicianSkill(DataContainer dataContainer, HashSet<String> skillSet){
        ArrayList<EntityTechnicianSkill> allTechnicianSkill = technicianSkillMapper.getAllTechnicianSkill(skillSet);
        dataContainer.setTechnicianSkillArrayList(allTechnicianSkill);
    }


    /**
     * 得到每个小阶段要求的设备组
     * @param dataContainer
     * @param functionId
     */
    public void loadStepEquipmentGroup(DataContainer dataContainer, int functionId){
        dataContainer.setStepEquipmentGroupArrayList(stepEquipmentGroupMapper.getAllStepEquipment(functionId));
    }

    /**
     * 得到设备组和设备的关系
     * @param dataContainer
     * @param functionId
     */
    public void loadEquipmentGroupRel(DataContainer dataContainer, int functionId){
        dataContainer.setEquipmentGroupRelArrayList(equipmentGroupRelMapper.getAllEquipmentGroupRel(functionId));
    }

    /**
     * 得到每个设备组内的设备,得到所有需要考虑的设备列表
     * @param dataContainer
     * @param functionId
     */
    public void loadEquipment(DataContainer dataContainer, int functionId){
        dataContainer.setEquipmentArrayList(equipmentMapper.getAllEquipment(functionId));
    }

    /**
     * 得到人员对应的日历
     * @param dataContainer
     * @param staffIds
     */
    public void loadStaffCalendar(DataContainer dataContainer, Set<String> staffIds){
        dataContainer.setStaffCalendarArrayList(staffCalendarMapper.getAllStaffCalendars(staffIds));
    }

    /**
     * 得到设备对应的日历
     * @param dataContainer
     * @param functionId
     */
    public void loadEquipmentCalendar(DataContainer dataContainer, int functionId){
        dataContainer.setEquipmentCalendarArrayList(equipmentCalendarMapper.getAllEquipmentCalendar(functionId));
    }

    /**
     * 得到需要考虑的工程师和技师、设备的正常日历
     * @param dataContainer
     * @param calendarIds
     */
    public void loadCalendar(DataContainer dataContainer, Set<Integer> calendarIds){
        dataContainer.setCalendarArrayList(calendarMapper.getAllCalendar(calendarIds));
    }
    /**
     * 得到需要考虑的工程师和技师、设备的翻班日历
     * @param dataContainer
     * @param calendarIds
     */
    public void loadRollCalendar(DataContainer dataContainer, Set<Integer> calendarIds){
        dataContainer.setRollCalendarArrayList(rollCalendarMapper.getAllRollCalendar(calendarIds));
    }

    /**
     *
     * @param dataContainer
     * @param allStaffIds
     * @param planPeriodStartTime
     */
    public void loadStaffPlanInput(DataContainer dataContainer, Set<String> allStaffIds, Date planPeriodStartTime){
        dataContainer.setStaffPlanInputArrayList(staffPlanInputMapper.getAllStaffPlanInput(allStaffIds, planPeriodStartTime));
    }

    /**
     * 得到所有需要考虑的设备资源在排程时间范围内的资源占用活动
     * @param dataContainer
     * @param equipmentIdSet
     * @param planPeriodStartTime
     */
    public void loadEquipmentPlanInput(DataContainer dataContainer, Set<String> equipmentIdSet, Date planPeriodStartTime) {
        dataContainer.setEquipmentPlanInputArrayList(equipmentPlanInputMapper.getAllEquipmentPlanInput(equipmentIdSet, planPeriodStartTime));
    }

    public void loadTaskPlanInput(DataContainer dataContainer, Integer functionId){
        dataContainer.setTaskPlanInputArrayList(taskPlanInputMapper.getAllTaskPlanInput(functionId));
    }

    /**
     * 获取DataContainer数据容器
     * @return
     * todo: 改完新模式后，可以去掉
     */
    @Deprecated
    public DataContainer getDataContainer(Integer functionId, int planMode){

        // 获取对象集合
        ArrayList<EntityTestArea> allTestArea = testAreaMapper.getAllTestArea(functionId);
        ArrayList<EntityLaboratory> allLaboratory = laboratoryMapper.getAllLaboratory(functionId);
        ArrayList<EntityFunction> allFunction = functionMapper.getFunction(functionId);

        ArrayList<EntityPlanConfig> allPlanConfig = planConfigMapper.getAllPlanConfigs(functionId);
        ArrayList<EntityProcedure> allProcedure = procedureMapper.getProcedureBySubTask(functionId);
        ArrayList<EntityBom> allBom = bomMapper.getBomBySubTask(functionId);

        ArrayList<EntityTask> allTask = taskMapper.getTaskBySubTask(functionId);
        ArrayList<EntitySubTask> allSubTask = subTaskMapper.getSubTaskByFunctionId(functionId);
        ArrayList<EntityStep> allStep = stepMapper.getAllStep(functionId);

        ArrayList<EntityAuthProcedure> allAuthProcedure = authProcedureMapper.getAllAuthProcedure(functionId);
        ArrayList<EntityAuthBom> allAuthBom = authBomMapper.getAllAuthBom(functionId);

        ArrayList<EntityAuthStep> allAuthStep = authStepMapper.getAllAuthStep(functionId);
        ArrayList<EntityStepSkill> allStepSkill = stepSkillMapper.getAllStepSkills(functionId);
        ArrayList<EntityStepEquipmentGroup> allStepEquipment = stepEquipmentGroupMapper.getAllStepEquipment(functionId);


        ArrayList<EntitySpecialDay> allSpecialDay = specialDayMapper.getAllSpecialDay();

        // 读取所有的 staff
        ArrayList<EntityStaff> allEngineers = staffMapper.getAllEngineers(functionId);
        // 获取所有小阶段需要的技能集合
        HashSet<String> skillSet = new HashSet<>();
        Boolean flag = true;
        for (EntityStepSkill stepSkill : allStepSkill) {
            String skillIdSet = stepSkill.getSkillIdSet();
            // 判断小阶段需要的技能集合是否为空
            if (skillIdSet == null || StringUtils.isEmpty(skillIdSet)){
                flag = false;
                break;
            }
            skillSet.addAll(Arrays.asList(skillIdSet.split(",")));
        }
        // 根据需要技能集合的不同，分类获取所有的技师
        ArrayList<EntityStaff> allTechnicians;
        if (flag){
             allTechnicians = staffMapper.getAllTechniciansByStepSkill(skillSet);
        }else {
            allTechnicians = staffMapper.getAllTechnicians();
        }
        ArrayList<EntityStaff> allStaffs = new ArrayList<>();
        allStaffs.addAll(allEngineers);
        allStaffs.addAll(allTechnicians);

        // 所有的人员 Id
        Set<String> allStaffIds = new HashSet<>();
        for (EntityStaff staff : allStaffs) {
            allStaffIds.add(staff.getStaffId());
        }

        // 根据人员的 Id 进行过滤（由于需要的所有人员不能一次性查出来，所以需要遍历一下）
        ArrayList<EntityStaffCalendar> allStaffCalendar = staffCalendarMapper.getAllStaffCalendars(allStaffIds);

        // 通过 step_skill 查询 skill 表
        ArrayList<EntitySkill> allSkill = skillMapper.getAllSkill(skillSet);

        // 通过 skillSet 查询 TechnicianSkill 表
        ArrayList<EntityTechnicianSkill> allTechnicianSkill = technicianSkillMapper.getAllTechnicianSkill(skillSet);
//        ArrayList<EntityCalendar> allCalendar = calendarMapper.getAllCalendar();

        //todo: rollCalendar 过滤逻辑
//        ArrayList<EntityRollCalendar> allRollCalendar = rollCalendarMapper.getAllRollCalendar();


        // equipment_group按照lab_id过滤
        // 目前根据 step_equipment_group 表中需要的 equipment_group 进行过滤
        ArrayList<EntityEquipmentGroup> allEquipmentGroup = equipmentGroupMapper.getAllEquipmentGroup(functionId);

        // 三表查询，根据 step_equipment_group, equipment_group 中需要的 equipment
        ArrayList<EntityEquipment> allEquipment = equipmentMapper.getAllEquipment(functionId);

        // 联表查询，根据 step_equipment_group 中需要的equipment_group
        ArrayList<EntityEquipmentGroupRel> allEquipmentGroupRel = getEquipmentGroupRelMapper().getAllEquipmentGroupRel(functionId);

        // 联表查询，根据 equipment_group_rel 中需要的 equipment
        ArrayList<EntityEquipmentCalendar> allEquipmentCalendar = equipmentCalendarMapper.getAllEquipmentCalendar(functionId);

        ArrayList<EntitySample> allSample = sampleMapper.getAllSample(functionId);

        // todo: 几个 plan input额外按照排程开始时间过滤
        // todo： 避免ModelTest跑不过去，暂时注释掉，等有数据了去掉注释
        ArrayList<EntityTaskPlanInput> allTaskPlanInput = taskPlanInputMapper.getAllTaskPlanInput(functionId);
        ArrayList<EntitySubTaskPlanInput> allSubTaskPlanInput = subTaskPlanInputMapper.getAllSubTaskPlanInput(functionId);
        ArrayList<EntityStepPlanInput> allStepPlanInput = stepPlanInputMapper.getAllStepPlanInput(functionId);
//        ArrayList<EntityEquipmentPlanInput> allEquipmentPlanInput = equipmentPlanInputMapper.getAllEquipmentPlanInput(functionId);
//        ArrayList<EntityStaffPlanInput> allStaffPlanInput = staffPlanInputMapper.getAllStaffPlanInput(functionId);



        DataContainer dataContainer = new DataContainer();

        EntityPlan plan = new EntityPlan(functionId, null, planMode);
        dataContainer.setPlan(plan);

        // 向dataContainer中添加数据
        dataContainer.setTestAreaArrayList(allTestArea);
        dataContainer.setLaboratoryArrayList(allLaboratory);
        dataContainer.setFunctionArrayList(allFunction);
        // todo: 等有数据了去掉注释
        // dataContainer.setPlanConfigArrayList(allPlanConfig);
        dataContainer.setProcedureArrayList(allProcedure);
        dataContainer.setBomArrayList(allBom);
        dataContainer.setTaskArrayList(allTask);
        dataContainer.setSubTaskArrayList(allSubTask);


        dataContainer.setStepArrayList(allStep);
        dataContainer.setSpecialDayArrayList(allSpecialDay);
        dataContainer.setAuthProcedureArrayList(allAuthProcedure);

        dataContainer.setAuthBomArrayList(allAuthBom);

        dataContainer.setAuthStepArrayList(allAuthStep);
        dataContainer.setStepSkillArrayList(allStepSkill);
        dataContainer.setStepEquipmentGroupArrayList(allStepEquipment);
        dataContainer.setAllStaffArrayList(allStaffs);
        dataContainer.setStaffCalendarArrayList(allStaffCalendar);
        dataContainer.setSkillArrayList(allSkill);
        dataContainer.setTechnicianSkillArrayList(allTechnicianSkill);
//        dataContainer.setCalendarArrayList(allCalendar);
        dataContainer.setEquipmentGroupArrayList(allEquipmentGroup);
        dataContainer.setEquipmentArrayList(allEquipment);
        dataContainer.setEquipmentGroupRelArrayList(allEquipmentGroupRel);
        dataContainer.setSampleArrayList(allSample);

        dataContainer.setTaskPlanInputArrayList(allTaskPlanInput);
        dataContainer.setSubTaskPlanInputArrayList(allSubTaskPlanInput);
        dataContainer.setStepPlanInputArrayList(allStepPlanInput);
//        dataContainer.setEquipmentPlanInputArrayList(allEquipmentPlanInput);
//        dataContainer.setStaffPlanInputArrayList(allStaffPlanInput);

        return dataContainer;
    }



}
