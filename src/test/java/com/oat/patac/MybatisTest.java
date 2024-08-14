package com.oat.patac;

import com.oat.common.utils.GetBeanUtil;
import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.dao.*;
import com.oat.patac.dataAccess.DataContainer;
import com.oat.patac.dataAccess.LoadData;
import com.oat.patac.engine.PatacSchedulingTask;
import com.oat.patac.entity.*;

import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.PLAN_MODE_AUTOMATIC;


/**
 * @author:yhl
 * @create: 2022-07-28 14:38
 * @Description: 测试 mybatis 的数据库连接，即 mapper 相关的测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class MybatisTest {


    @Resource
    FunctionMapper functionMapper;
    @Resource
    TaskMapper taskMapper;
    @Resource
    SubTaskMapper subTaskMapper;
    @Resource
    BomMapper bomMapper;
    @Resource
    StepMapper stepMapper;
    @Resource
    StaffMapper staffMapper;
    @Resource
    StaffCalendarMapper staffCalendarMapper;
    @Resource
    EquipmentGroupMapper equipmentGroupMapper;
    @Resource
    EquipmentMapper equipmentMapper;
    @Resource
    SampleMapper sampleMapper;
    @Resource
    TestAreaMapper testAreaMapper;
    @Resource
    LaboratoryMapper laboratoryMapper;
    @Resource
    ProcedureMapper procedureMapper;
    @Resource
    SkillMapper skillMapper;
    @Resource
    CalendarMapper calendarMapper;
    @Resource
    RollCalendarMapper rollCalendarMapper;
    @Resource
    EquipmentCalendarMapper equipmentCalendarMapper;
    @Resource
    EquipmentMaintMapper equipmentMaintMapper;
    @Resource
    PlanConfigMapper planConfigMapper;
    @Resource
    SpecialDayMapper specialDayMapper;
    @Resource
    AuthBomMapper authBomMapper;
    @Resource
    AuthProcedureMapper authProcedureMapper;
    @Resource
    AuthStepMapper authStepMapper;
    @Resource
    TechnicianSkillMapper technicianSkillMapper;
    @Resource
    EquipmentGroupRelMapper equipmentGroupRelMapper;
    @Resource
    TaskPlanInputMapper taskPlanInputMapper;
    @Resource
    TaskPlanOutputMapper taskPlanOutputMapper;
    @Resource
    MessageMapper messageMapper;

    @Resource
    SubTaskPlanOutputMapper subTaskPlanOutputMapper;


//    @Resource
    LoadData loadData;
    //@Resource
    //EngineProcessedData engineProcessedData;

    @Test
    public void getAllSubTaskPlanOutput(){
        List<EntitySubTaskPlanOutput> subTaskPlanOutputs = subTaskPlanOutputMapper.selectAllSubTaskPlanOutputs(100560);
        System.out.println(subTaskPlanOutputs.size());

    }

    @Test
    public void getAllTechniciansTest(){
        ArrayList<EntityStaff> allTechnicians = staffMapper.getAllTechnicians();
        HashSet<String> hashSet = new HashSet<>();
//        hashSet.add("TEC-CAL-00048");
        hashSet.add("TEC-HVA-00045");
        ArrayList<EntityStaff> allTechniciansByStepSkill = staffMapper.getAllTechniciansByStepSkill(hashSet);
        log.info(allTechnicians.size());
        log.info(allTechniciansByStepSkill.size());
    }

    @Test
    public void getBeanTest(){
        LoadData loadData = GetBeanUtil.getBean(LoadData.class);
        DataContainer dataContainer = loadData.getDataContainer(65, PLAN_MODE_AUTOMATIC);
        System.out.println(dataContainer);
    }

    /**
     * SpecialDayMapper的测试
     */
    @Test
    public void specialDayMapperTest(){
        HandlerDataSource.putDataSource("datasource4");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse("2017-01-01 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        ArrayList<EntitySpecialDay> allSpecialDay = specialDayMapper.getSpecialDayAfterSpecificTime(date);
//        System.out.println(allSpecialDay);
    }

    // message 输出测试
    @Test
    public void messageMapperTest(){
        ArrayList<EntityMessage> messages = new ArrayList<>();
        EntityMessage message1 = new EntityMessage();
        EntityMessage message2 = new EntityMessage();
        EntityMessage message3 = new EntityMessage();
        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messageMapper.insertAllMessage(messages);
    }

    // task 结果输出测试
    @Test
    public void taskPlanOutputMapperTest(){
        EntityTaskPlanOutput taskPlanOutput1 = new EntityTaskPlanOutput( 2 + "");
        EntityTaskPlanOutput taskPlanOutput2 = new EntityTaskPlanOutput( 2 + "");
        EntityTaskPlanOutput taskPlanOutput3 = new EntityTaskPlanOutput( 2 + "");
        ArrayList<EntityTaskPlanOutput> taskPlanOutputs = new ArrayList<>();
        taskPlanOutputs.add(taskPlanOutput1);
        taskPlanOutputs.add(taskPlanOutput2);
        taskPlanOutputs.add(taskPlanOutput3);
        taskPlanOutputMapper.insertAllTaskPlanOutputs(taskPlanOutputs);
    }


    @Test
    public void testStaffCalendarMapper(){
        Set<String> list = new HashSet<>();
        list.add("1");
        list.add("2");
        ArrayList<EntityStaffCalendar> allStaffCalendars = staffCalendarMapper.getAllStaffCalendars(list);
        log.info(allStaffCalendars);
    }

    /**
     * 远程数据源连接测试
     */
    @Test
    public void testDatasource(){
        ArrayList<EntityTestArea> allTestArea = testAreaMapper.getAllTestArea(1);
        log.info(allTestArea);
    }

    /**
     * 数据验证 demo 测试
     */
    @Test
    public void demoTest() throws ParseException {
//        HandlerDataSource.putDataSource("datasource1");

        log.info("数据验证开始！");
        // 指定一个functionId
        Integer functionId = 11;
        //指定一个排程模式（任意值都可以）
        Integer planMode = PLAN_MODE_AUTOMATIC;
        //从数据库获得数据
        DataContainer dataContainer = loadData.getDataContainer(functionId, PLAN_MODE_AUTOMATIC);

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, planMode, null);
        //生成模型需要的数据，并同时检查数据
        schedulingTask.processAndCheckData();

        // （测试案例时）
        // 判断生成的 messages 与预测的结果 reMessages 是否一直
        ArrayList<EntityMessage> reMessages = messageMapper.selectAllMessage(11);
        ArrayList<EntityMessage> messages = schedulingTask.getEngineProcessedData().messages;
        //ArrayList<EntityMessage> messages = EngineProcessedData.messages;
        log.info(messages);
        log.info(reMessages);

        Boolean b = messages.containsAll(reMessages) && reMessages.containsAll(messages);

//        log.info("测试案例结果：" + b);
        // 打印出执行结果和预期结果不同的部分
        if (!b){
            ArrayList<EntityMessage> messages2 = (ArrayList<EntityMessage>) messages.clone();
            // 获取相同部分，写入到 messages2
            messages2.retainAll(reMessages);
            // 分别移除相同部分
            messages.removeAll(messages2);
            log.info("新出的message：" + messages);
            reMessages.removeAll(messages2);
            log.info("沒有出现的message：" + reMessages);
        }

        //log.info(engineProcessedData);

//        Boolean b = engineProcessedData.testResult();

        // 断言测试结果
        Assert.assertTrue(b);

        log.info("数据验证结束！");
    }

    /**
     * 测试结果查询
     * @return
     */
    public  Boolean testResult( ArrayList<EntityMessage> reMessages,  ArrayList<EntityMessage> messages){
        // （测试案例时）
        // 判断生成的 messages 与预测的结果 reMessages 是否一直
        //ArrayList<EntityMessage> reMessages = messageMapper.selectAllMessage(11);
        //ArrayList<EntityMessage> messages = EngineProcessedData.messages;
        log.info(messages);
        log.info(reMessages);

        Boolean b = messages.containsAll(reMessages) && reMessages.containsAll(messages);
//        log.info("测试案例结果：" + b);
        // 打印出执行结果和预期结果不同的部分
        if (!b){
            ArrayList<EntityMessage> messages2 = (ArrayList<EntityMessage>) messages.clone();
            // 获取相同部分，写入到 messages2
            messages2.retainAll(reMessages);
            // 分别移除相同部分
            messages.removeAll(messages2);
            log.info("执行结果多的message：" + messages);
            reMessages.removeAll(messages2);
            log.info("执行结果少的message：" + reMessages);
        }

//        log.info(engineProcessedData);
        return  b;
    }
    /**
     * 动态切换数据源的测试
     */
    @Test
    public void dataSourceTest(){
        // patac_schedule 数据源
        ArrayList<EntityTask> allTask0 = taskMapper.getAllTaskByFunctionId(11);
        log.info(allTask0);

        // 修改数据源
        HandlerDataSource.putDataSource("datasource1");

        // mybatis 数据源
        ArrayList<EntityTask> allTask1 = taskMapper.getAllTaskByFunctionId(11);
        log.info(allTask1);
    }



    /**
     * TaskPlanInputMapper TaskPlanOutputMapper的测试
     */
    @Test
    public void taskPlanMapperTest(){
        ArrayList<EntityTaskPlanInput> allTaskPlanInput = taskPlanInputMapper.getAllTaskPlanInput(11);
        for (EntityTaskPlanInput taskPlanInput:allTaskPlanInput) {
            System.out.println(taskPlanInput.getTaskNo());
            System.out.println(taskPlanInput.getTaskPlanStart());
            System.out.println(taskPlanInput.getTaskPlanEnd());
            System.out.println(taskPlanInput.getTaskActualStart());
            System.out.println(taskPlanInput.getTaskActualEnd());
            System.out.println(taskPlanInput.getRespEngineerId());
            System.out.println(taskPlanInput.getIsCompleted());
        }
    }

    /**
     * TestAreaMapper的测试
     */
    @Test
    public void testAreaMapperTest(){
//        HashMap<Integer, EntityTestArea> testAreaHashMap = testAreaMapper.getAllTestArea();
//
//        System.out.println(testAreaHashMap.getClass());
//        System.out.println();
//        System.out.println(testAreaHashMap);
//
//        for (Map.Entry<Integer, EntityTestArea> entry : testAreaHashMap.entrySet()) {
//            System.out.println(entry.getKey() + "-------" + entry.getValue());

//        testAreaMapper.addTestArea(new EntityTestArea(4, null));

        ArrayList<EntityTestArea> allTestArea = testAreaMapper.getAllTestArea(11);
        System.out.println(allTestArea);


    }   

    /**
     * LaboratoryMapper的测试
     */
    @Test
    public void laboratoryMapperTest(){
        ArrayList<EntityLaboratory> allLaboratory = laboratoryMapper.getAllLaboratory(11);
        System.out.println(allLaboratory);
    }
    /**
     * FunctionMapper测试
     */
    @Test
    public void functionMapperTest(){
        ArrayList<EntityFunction> allFunction = functionMapper.getFunction(11);
        System.out.println(allFunction);
    }
    /**
     * taskMapper测试
     */
    @Test
    public void taskMapperTest(){
//        ArrayList<EntityTask> allTask = taskMapper.getAllTask();
//
//        System.out.println(allTask);
    }
    /**
     * SubTaskMapper的测试
     */
    @Test
    public void batchMapperTest(){
        ArrayList<EntitySubTask> allBatch = subTaskMapper.getSubTaskByFunctionId(11);
        System.out.println(allBatch);
    }
    /**
     * ProcedureMapper的测试
     */
    @Test
    public void procedureMapperTest(){
        ArrayList<EntityProcedure> allProcedure = procedureMapper.getAllProcedure(11);
        System.out.println(allProcedure);
    }
    /**
     * bomMapper的测试
     */
    @Test
    public void bomMapperTest(){
        ArrayList<EntityBom> allBom = bomMapper.getAllBom(11);
        System.out.println(allBom);
    }
    /**
     * StepMapper的测试
     */
    @Test
    public void stepMapperTest(){
        ArrayList<EntityStep> allStep = stepMapper.getAllStep(11);
        System.out.println(allStep);
    }

    /**
     * StaffMapper的测试
     */
    @Test
    public void staffMapperTest(){
        ArrayList<EntityStaff> allEngineers = staffMapper.getAllEngineers(11);
        log.info(allEngineers);
    }
    /**
     * SkillMapper的测试
     */
    @Test
    public void skillMapperTest(){
        HandlerDataSource.putDataSource("datasource2");
//        ArrayList<EntitySkill> allSkill = skillMapper.getAllSkill(11);
//        System.out.println(allSkill);
    }

    /**
     * CalendarMapper的测试
     */
    @Test
    public void calendarMapperTest(){
//        ArrayList<EntityCalendar> allCalendar = calendarMapper.getAllCalendar(11);
//        System.out.println(allCalendar);
    }
    /**
     * RollCalendarMapper的测试
     */
    @Test
    public void rollCalendarMapperTest(){
//        ArrayList<EntityRollCalendar> allRollCalendar = rollCalendarMapper.getAllRollCalendar(11);
//        System.out.println(allRollCalendar);
    }
    /**
     * EquipmentGroupMapper的测试
     */
    @Test
    public void equipmentGroupMapperTest(){
        ArrayList<EntityEquipmentGroup> allEquipmentGroup = equipmentGroupMapper.getAllEquipmentGroup(11);
        System.out.println(allEquipmentGroup);
    }
    /**
     * EquipmentMapper的测试
     */
    @Test
    public void equipmentMapperTest(){
        ArrayList<EntityEquipment> allEquipment= equipmentMapper.getAllEquipment(11);
        System.out.println(allEquipment);
    }
    /**
     * EquipmentCalendarMapper的测试
     */
    @Test
    public void equipmentCalendarMapperTest(){
//        ArrayList<EntityEquipmentCalendar> allEquipmentCalendar = equipmentCalendarMapper.getAllEquipmentCalendar(11);
//        System.out.println(allEquipmentCalendar);
    }
    /**
     * EquipmentMaintMapper的测试
     */
    @Test
    public void equipmentMaintMapperTest(){
        ArrayList<EntityEquipmentMaint> allEquipmentMaint = equipmentMaintMapper.getAllEquipmentMaint(11);
        System.out.println(allEquipmentMaint);
    }
    /**
     * SampleMapper的测试
     */
    @Test
    public void sampleMapperTest(){
        ArrayList<EntitySample> allSample = sampleMapper.getAllSample(11);
        System.out.println(allSample);
    }
    /**
     * PlanConfigMapper的测试
     */
    @Test
    public void planConfigMapperTest(){
        ArrayList<EntityPlanConfig> allPlanConfigs = planConfigMapper.getAllPlanConfigs(11);
        System.out.println(allPlanConfigs);
    }
    /**
     * AuthBomMapper的测试
     */
    @Test
    public void authBomMapperTest(){
        ArrayList<EntityAuthBom> allAuthBom = authBomMapper.getAllAuthBom(11);
        System.out.println(allAuthBom);
    }
    /**
     * AuthProcedureMapper的测试
     */
    @Test
    public void authProcedureMapperTest(){
//        ArrayList<EntityAuthProcedure> allAuthProcedure = authProcedureMapper.getAllAuthProcedure(11);
//        System.out.println(allAuthProcedure);
    }
    /**
     * AuthStepMapper的测试
     */
    @Test
    public void authStepMapperTest(){
        ArrayList<EntityAuthStep> allAuthStep = authStepMapper.getAllAuthStep(11);
        System.out.println(allAuthStep);
    }
    /**
     * EquipmentGroupRelMapper的测试
     */
    @Test
    public void equipmentGroupRelMapperTest(){
//        ArrayList<EntityEquipmentGroupRel> allEquipmentGroupRel = equipmentGroupRelMapper.getAllEquipmentGroupRel(11);
//        System.out.println(allEquipmentGroupRel);
    }
    /**
     * TechnicianSkillMapper的测试
     */
    @Test
    public void technicianSkillMapperTest(){
//        ArrayList<EntityTechnicianSkill> allTechnicianSkill = technicianSkillMapper.getAllTechnicianSkill();
//        System.out.println(allTechnicianSkill);

        EntityCalendar entityCalendar = new EntityCalendar();
        entityCalendar.setCalendarId(1);
        System.out.println(entityCalendar.getCalendarId());
    }

}
