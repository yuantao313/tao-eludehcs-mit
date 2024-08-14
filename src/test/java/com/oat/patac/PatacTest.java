package com.oat.patac;

import com.oat.common.utils.CsvFileUtil;
import com.oat.patac.dao.*;
import com.oat.patac.engine.EngineProcessedData;
import com.oat.patac.engine.PatacSchedulingTask;
import com.oat.patac.entity.*;
import com.oat.patac.service.PlanService;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.ArrayList;

import static com.oat.common.utils.ConstantUtil.*;

@Log4j2
public class PatacTest extends TestCase {
    @Resource
    protected TaskPlanOutputMapper taskPlanOutputMapper;
    @Resource
    protected SubTaskPlanOutputMapper subTaskPlanOutputMapper;
    @Resource
    protected StepPlanOutputMapper stepPlanOutputMapper;
    @Resource
    protected StaffPlanOutputMapper staffPlanOutputMapper;
    @Resource
    protected EquipmentPlanOutputMapper equipmentPlanOutputMapper;
    @Resource
    protected PlanMapper planMapper;
    @Resource
    protected MessageMapper messageMapper;
    @Resource
    protected PlanService service;

    @Before
    public void setUp() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring.xml");
        this.messageMapper = ac.getBean(MessageMapper.class);
        this.planMapper = ac.getBean(PlanMapper.class);
        this.taskPlanOutputMapper = ac.getBean(TaskPlanOutputMapper.class);
        this.subTaskPlanOutputMapper = ac.getBean(SubTaskPlanOutputMapper.class);
        this.stepPlanOutputMapper = ac.getBean(StepPlanOutputMapper.class);
        this.staffPlanOutputMapper = ac.getBean(StaffPlanOutputMapper.class);
        this.equipmentPlanOutputMapper = ac.getBean(EquipmentPlanOutputMapper.class);
        this.service = ac.getBean(PlanService.class);
        EngineProcessedData.messages.clear();
    }
    /**
     * case结果测试
     * @return
     */
    public void caseResultTest(PatacSchedulingTask schedulingTask, Integer functionId){
        //获得Output表
        ArrayList<EntityTaskPlanOutput> allTaskPlanOutput = taskPlanOutputMapper.getAllTaskPlanOutput(functionId);
        ArrayList<EntitySubTaskPlanOutput> allSubTaskPlanOutputs = subTaskPlanOutputMapper.getAllSubTaskPlanOutputs(functionId);
        ArrayList<EntityStepPlanOutput> allStepPlanOutputs = stepPlanOutputMapper.getAllStepPlanOutputs(functionId);
        ArrayList<EntityStaffPlanOutput> allStaffPlanOutputs = staffPlanOutputMapper.getAllStaffPlanOutputs(functionId);
        ArrayList<EntityEquipmentPlanOutput> allequipmentPlanOutputs = equipmentPlanOutputMapper.getAllEquipmentPlanOutputs(functionId);

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 获得输出对象 planOutputs 对象
        ArrayList<EntityTaskPlanOutput> taskPlanOutputs = new ArrayList<>(engineProcessedData.getTaskPlanOutputHashMap().values()) ;
        ArrayList<EntitySubTaskPlanOutput> subTaskPlanOutputs = new ArrayList<>(engineProcessedData.getSubTaskPlanOutputHashMap().values());
        ArrayList<EntityStepPlanOutput> stepPlanOutputs = new ArrayList<>(engineProcessedData.getStepPlanOutputHashMap().values());
        ArrayList<EntityStaffPlanOutput> staffPlanOutputs = new ArrayList<>();
        for (ArrayList<EntityStaffPlan> staffPlans : engineProcessedData.getStaffStaffPlanOutputHashMap().values()) {
            for (EntityStaffPlan staffPlan : staffPlans) {
                staffPlanOutputs.add((EntityStaffPlanOutput) staffPlan);
            }
        }
        ArrayList<EntityEquipmentPlanOutput> equipmentPlanOutputs = new ArrayList<>();
        for (ArrayList<EntityEquipmentPlan> equipmentPlans : engineProcessedData.getEquipmentEquipmentPlanOutputHashMap().values()) {
            for (EntityEquipmentPlan equipmentPlan : equipmentPlans) {
                equipmentPlanOutputs.add((EntityEquipmentPlanOutput) equipmentPlan);
            }
        }
        Boolean b = testResult(taskPlanOutputs ,allTaskPlanOutput);
        b = testResult(subTaskPlanOutputs ,allSubTaskPlanOutputs) && b;
        b = testResult(stepPlanOutputs ,allStepPlanOutputs) && b;
        b = testResult(staffPlanOutputs ,allStaffPlanOutputs) && b;
        b = testResult(equipmentPlanOutputs ,allequipmentPlanOutputs) && b;
        if(b){
            log.info("运行结果测试通过！");
        }else{
            log.info("运行结果测试失败！");
        }
        // 断言测试结果
        Assert.assertTrue(b);
    }
    /**
     * 测试结果比对
     * @return
     */
    public  Boolean resultCompareBetweenStringList( ArrayList<String> actualResults,  ArrayList<String> expectedResults){
        // （测试案例时）
        // 判断运行的testResults 与预测的结果 expectedResults是否一致
        log.info("Actual Results :" );
        actualResults.forEach(v->log.info(v));
        log.info("Expected Results :" );
        expectedResults.forEach(v->log.info(v));

        Boolean b = actualResults.containsAll(expectedResults) && expectedResults.containsAll(actualResults);
//        log.info("测试案例结果：" + b);
        // 打印出执行结果和预期结果不同的部分
        if (!b){
            ArrayList<?> result2 = (ArrayList<?>) actualResults.clone();
            // 获取相同部分，写入到 messages2
            result2.retainAll(expectedResults);
            // 分别移除相同部分
            actualResults.removeAll(result2);
            log.info("测试的结果中不同的部分：" );
            actualResults.forEach(v->log.info(v));
            expectedResults.removeAll(result2);
            log.info("期望的结果中不同的部分：");
            expectedResults.forEach(v->log.info(v));
        }

        return  b;
    }
    /**
     * 测试结果查询
     * @return
     */
    public  Boolean testResult( ArrayList<?> testResults,  ArrayList<?> expectedResults){
        // （测试案例时）
        // 判断运行的testResults 与预测的结果 expectedResults是否一致
        log.info("Test Results :" + testResults);
        log.info("Expected Results :" + expectedResults);

        Boolean b = testResults.containsAll(expectedResults) && expectedResults.containsAll(testResults);
//        log.info("测试案例结果：" + b);
        // 打印出执行结果和预期结果不同的部分
        if (!b){
            ArrayList<?> result2 = (ArrayList<?>) testResults.clone();
            // 获取相同部分，写入到 messages2
            result2.retainAll(expectedResults);
            // 分别移除相同部分
            testResults.removeAll(result2);
            log.info("测试的结果中不同的部分：" + testResults);
            expectedResults.removeAll(result2);
            log.info("期望的结果中不同的部分：" + expectedResults);
        }

        return  b;
    }

    /**
     * case结果测试
     * @return
     */
    public void resultCompareBetweenDbAndCsv(PatacSchedulingTask schedulingTask, Integer functionId,
                                             boolean compareMessage) throws IllegalAccessException {

        //获得Output表，是本次执行结果
        ArrayList<EntityTaskPlanOutput> allTaskPlanOutputs = taskPlanOutputMapper.getAllTaskPlanOutput(functionId);
        ArrayList<EntitySubTaskPlanOutput> allSubTaskPlanOutputs = subTaskPlanOutputMapper.getAllSubTaskPlanOutputs(functionId);
        ArrayList<EntityStepPlanOutput> allStepPlanOutputs = stepPlanOutputMapper.getAllStepPlanOutputs(functionId);
        ArrayList<EntityStaffPlanOutput> allStaffPlanOutputs = staffPlanOutputMapper.getAllStaffPlanOutputs(functionId);
        ArrayList<EntityEquipmentPlanOutput> allEquipmentPlanOutputs = equipmentPlanOutputMapper.getAllEquipmentPlanOutputs(functionId);
        ArrayList<EntityMessage> allMessageOutputs = messageMapper.selectAllMessage(functionId);

        // 转换为string array
        ArrayList<String> actualTaskPlanOutputs = CsvFileUtil.transferEntityToLines(TASK_PLAN_OUTPUT_PROPERTIES, allTaskPlanOutputs);
        ArrayList<String> actualSubTaskPlanOutputs = CsvFileUtil.transferEntityToLines(SUB_TASK_PLAN_OUTPUT_PROPERTIES, allSubTaskPlanOutputs);
        ArrayList<String> actualStepPlanOutputs = CsvFileUtil.transferEntityToLines(STEP_PLAN_OUTPUT_PROPERTIES, allStepPlanOutputs);
        ArrayList<String> actualStaffPlanOutputs = CsvFileUtil.transferEntityToLines(STAFF_PLAN_OUTPUT_PROPERTIES, allStaffPlanOutputs);
        ArrayList<String> actualEquipmentPlanOutputs = CsvFileUtil.transferEntityToLines(EQUIPMENT_PLAN_OUTPUT_PROPERTIES, allEquipmentPlanOutputs);
        ArrayList<String> actualMessageOutputs = CsvFileUtil.transferEntityToLines(MESSAGE_OUTPUT_PROPERTIES, allMessageOutputs);

        // 获得csv 文件内容——Expected Results
        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        EntityPlan plan = engineProcessedData.getPlan();
        //ArrayList<String[]> expectedPlanOutputs = new ArrayList<>();
        ArrayList<String> expectedMessageOutputs = new ArrayList<>();
        ArrayList<String> expectedTaskPlanOutputs = new ArrayList<>();
        ArrayList<String> expectedSubTaskPlanOutputs = new ArrayList<>();
        ArrayList<String> expectedStepPlanOutputs = new ArrayList<>();
        ArrayList<String> expectedStaffPlanOutputs = new ArrayList<>();
        ArrayList<String> expectedEquipmentPlanOutputs = new ArrayList<>();

        CsvFileUtil.readLinesOfCsvFile(CSV_FILE_EXPECTED_RESULT_FOLDER+"/"+plan.getPlanId()+"/task_plan_output.csv",expectedTaskPlanOutputs);
        CsvFileUtil.readLinesOfCsvFile(CSV_FILE_EXPECTED_RESULT_FOLDER+"/"+plan.getPlanId()+"/sub_task_plan_output.csv",expectedSubTaskPlanOutputs);
        CsvFileUtil.readLinesOfCsvFile(CSV_FILE_EXPECTED_RESULT_FOLDER+"/"+plan.getPlanId()+"/step_plan_output.csv",expectedStepPlanOutputs);
        CsvFileUtil.readLinesOfCsvFile(CSV_FILE_EXPECTED_RESULT_FOLDER+"/"+plan.getPlanId()+"/staff_plan_output.csv",expectedStaffPlanOutputs);
        CsvFileUtil.readLinesOfCsvFile(CSV_FILE_EXPECTED_RESULT_FOLDER+"/"+plan.getPlanId()+"/equipment_plan_output.csv",expectedEquipmentPlanOutputs);
        CsvFileUtil.readLinesOfCsvFile(CSV_FILE_EXPECTED_RESULT_FOLDER+"/"+plan.getPlanId()+"/messages.csv",expectedMessageOutputs);

        Boolean b = resultCompareBetweenStringList(actualTaskPlanOutputs ,expectedTaskPlanOutputs);
        b = resultCompareBetweenStringList(actualSubTaskPlanOutputs ,expectedSubTaskPlanOutputs) && b;
        b = resultCompareBetweenStringList(actualStepPlanOutputs ,expectedStepPlanOutputs) && b;
        b = resultCompareBetweenStringList(actualStaffPlanOutputs ,expectedStaffPlanOutputs) && b;
        b = resultCompareBetweenStringList(actualEquipmentPlanOutputs ,expectedEquipmentPlanOutputs) && b;

        if (compareMessage){
            b = resultCompareBetweenStringList(actualMessageOutputs ,expectedMessageOutputs) && b;
        }
        if(b){
            log.info("运行结果测试通过！");
        }else{
            log.info("运行结果测试失败！");
        }
        // 断言测试结果
        Assert.assertTrue(b);
    }

    public void clearData(int functionId){
        // 清空数据库
        messageMapper.deleteMessagesByFunctionId(functionId);
        planMapper.deletePlanByFunctionId(functionId);
        taskPlanOutputMapper.deleteTaskPlanOutputsByFunctionId(functionId);
        subTaskPlanOutputMapper.deleteSubTaskPlanOutputsByFunctionId(functionId);
        stepPlanOutputMapper.deleteStepPlanOutputsByFunctionId(functionId);
        staffPlanOutputMapper.deleteStaffPlanOutputsByFunctionId(functionId);
        equipmentPlanOutputMapper.deleteEquipmentPlanOutputsByFunctionId(functionId);
    }

    public void exportCsvData(){
        EngineProcessedData engineProcessedData = service.getSchedulingTask().getEngineProcessedData();
        EntityPlan plan = engineProcessedData.getPlan();
        ArrayList<EntityMessage> messages = EngineProcessedData.messages;
        // 获得输出

        ArrayList<EntityTaskPlanOutput> taskPlanOutputs = new ArrayList<>(engineProcessedData.getTaskPlanOutputHashMap().values()) ;
        ArrayList<EntitySubTaskPlanOutput> subTaskPlanOutputs = new ArrayList<>(engineProcessedData.getSubTaskPlanOutputHashMap().values());
        ArrayList<EntityStepPlanOutput> stepPlanOutputs = new ArrayList<>(engineProcessedData.getStepPlanOutputHashMap().values());
        for (EntityStepPlanOutput stepPlanOutput : stepPlanOutputs) {
            stepPlanOutput.setPlanDuration(stepPlanOutput.getPlanDuration().setScale(6, RoundingMode.HALF_UP));
            stepPlanOutput.setReqProcTime(stepPlanOutput.getReqProcTime().setScale(6, RoundingMode.HALF_UP));
            stepPlanOutput.setPlanProcTime(stepPlanOutput.getPlanProcTime().setScale(6, RoundingMode.HALF_UP));
        }

        ArrayList<EntityStaffPlanOutput> staffPlanOutputs = new ArrayList<>();
        for (ArrayList<EntityStaffPlan> staffPlans : engineProcessedData.getStaffStaffPlanOutputHashMap().values()) {
            for (EntityStaffPlan staffPlan : staffPlans) {
                EntityStaffPlanOutput staffPlanOutput = (EntityStaffPlanOutput) staffPlan;
                staffPlanOutput.setPlanWorkTime(staffPlanOutput.getPlanWorkTime().setScale(6, RoundingMode.HALF_UP));
                staffPlanOutput.setReqWorkTime(staffPlanOutput.getReqWorkTime().setScale(6, RoundingMode.HALF_UP));
                staffPlanOutputs.add((EntityStaffPlanOutput) staffPlan);
            }
        }
        ArrayList<EntityEquipmentPlanOutput> equipmentPlanOutputs = new ArrayList<>();
        for (ArrayList<EntityEquipmentPlan> equipmentPlans : engineProcessedData.getEquipmentEquipmentPlanOutputHashMap().values()) {
            for (EntityEquipmentPlan equipmentPlan : equipmentPlans) {
                EntityEquipmentPlanOutput equipmentPlanOutput = (EntityEquipmentPlanOutput) equipmentPlan;
                equipmentPlanOutput.setPlanWorkTime(equipmentPlanOutput.getPlanWorkTime().setScale(6, RoundingMode.HALF_UP));
                equipmentPlanOutput.setReqWorkTime(equipmentPlanOutput.getReqWorkTime().setScale(6, RoundingMode.HALF_UP));
                equipmentPlanOutputs.add((EntityEquipmentPlanOutput) equipmentPlan);
            }
        }

        String planId = plan.getPlanId();

        try {
            ArrayList<EntityPlan> plans = new ArrayList<>();
            plans.add(plan);
            CsvFileUtil.exportCsv(PLAN_OUTPUT_PROPERTIES, plans, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/plan.csv");
            CsvFileUtil.exportCsv(MESSAGE_OUTPUT_PROPERTIES, messages, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/messages.csv");
            CsvFileUtil.exportCsv(TASK_PLAN_OUTPUT_PROPERTIES, taskPlanOutputs, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/task_plan_output.csv");
            CsvFileUtil.exportCsv(SUB_TASK_PLAN_OUTPUT_PROPERTIES, subTaskPlanOutputs, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/sub_task_plan_output.csv");
            CsvFileUtil.exportCsv(STEP_PLAN_OUTPUT_PROPERTIES, stepPlanOutputs, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/step_plan_output.csv");
            CsvFileUtil.exportCsv(STAFF_PLAN_OUTPUT_PROPERTIES, staffPlanOutputs, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/staff_plan_output.csv");
            CsvFileUtil.exportCsv(EQUIPMENT_PLAN_OUTPUT_PROPERTIES, equipmentPlanOutputs, CSV_FILE_OUTPUT_FOLDER + "/" + planId + "/equipment_plan_output.csv");
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

}
