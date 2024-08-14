package com.oat.patac;

import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.dataAccess.DataContainer;
import com.oat.patac.dataAccess.LoadData;
import com.oat.patac.engine.EngineProcessedData;
import com.oat.patac.engine.PatacSchedulingTask;
import com.oat.patac.engine.SubModel;
import com.oat.patac.engine.SubModelData;
import com.oat.patac.entity.*;
import com.oat.patac.service.PlanService;
import ilog.concert.IloException;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.MODEL_VERSION_3;
import static com.oat.common.utils.ConstantUtil.SOLVE_MODE_BY_GRANULARITY;
import static com.oat.common.utils.DateUtil.TIME_ZONE;

/**
 * @author:yhl
 * @create: 2022-11-10 15:03
 * @Description: 数据处理的测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class DataProcessingTest {

    @Resource
    private PlanService service;

    @Resource
    private LoadData loadData;

    @Test
    public void DataProcessingTest() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasourceDataTest");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 14, 00, 00, 00);
        calendar.set(Calendar.MILLISECOND, 0);
        Date planTriggeredTime = calendar.getTime();

        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //设置约束是否起作用
        EntityPlan plan = dataContainer.getPlan();
        if (planTriggeredTime != null ){
            plan.initialize(planTriggeredTime);
        }
        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData,dataContainer,null,SOLVE_MODE_BY_GRANULARITY);
        //生成模型需要的数据，并同时检查数据
        // 根据是否有无法进行排程的问题，决定是否调用下面的solve（）
        if (!schedulingTask.loadAndProcessData()) {
            log.info("加载和处理数据出现错误或没有有效任务单需要排程，引擎退出");
        } else {
            //建立模型并求解
            if (schedulingTask.getSubModelHashMap().size() < 0) {
                log.info("没有需要求解的模型，引擎退出");
            } else if (!schedulingTask.solve(MODEL_VERSION_3)) {
                // 没有排程任何一个任务单
                log.info("模型求解过程中出现错误，引擎退出");
            } else {
                // 至少排出一个任务单
                log.info("模型部分或完全求解成功，输出结果");
            }
        }

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        HashMap<String, SubModel> subModelHashMap = engineProcessedData.getSubModelHashMap();

        boolean result = false;

        for (Map.Entry<String, SubModel> subModel : subModelHashMap.entrySet()) {
            SubModelData subModelData = subModel.getValue().getSubModelData();

            HashMap<String, ArrayList<Double>> stepFunctionStaffTimeExpect = new HashMap<>();
            HashMap<String, ArrayList<Double>> stepFunctionStaffValueExpect = new HashMap<>();

            // 小阶段的3种工作模式对应的step function，仅工作日、工作日及周末、全年，以及special day的处理
            HashMap<String, ArrayList<Double>> stepFunctionStepTime = subModelData.getStepFunctionStepTime();
            HashMap<String, ArrayList<Double>> stepFunctionStepValue = subModelData.getStepFunctionStepValue();

//            printAllData(stepFunctionStepTime);
//            printAllData(stepFunctionStepValue);
//
//            log.info("");
            log.info("-------------------- stepFunctionstepTime/value 测试 --------------------");

            HashMap<String, ArrayList<Double>> stepFunctionStepTimeExpect = new HashMap<>();
            HashMap<String, ArrayList<Double>> stepFunctionStepValueExpect = new HashMap<>();

            Double[] stepTimesArray1 = new Double[]{0.0 ,48.0 ,96.0 ,192.0 ,240.0};
            ArrayList<Double> stepTimes1 = new ArrayList<>(Arrays.asList(stepTimesArray1));
            stepFunctionStepTimeExpect.put("0", stepTimes1);

            Double[] stepTimesArray2 = new Double[]{0.0 ,72.0 ,96.0 ,192.0 ,216.0 ,240.0};
            ArrayList<Double> stepTimes2 = new ArrayList<>(Arrays.asList(stepTimesArray2));
            stepFunctionStepTimeExpect.put("1", stepTimes2);

            Double[] stepValuesArray1 = new Double[]{100.0 ,0.0 ,100.0 ,0.0};
            ArrayList<Double> stepValues1 = new ArrayList<>(Arrays.asList(stepValuesArray1));
            stepFunctionStepValueExpect.put("0", stepValues1);

            Double[] stepValuesArray2 = new Double[]{100.0 ,0.0 ,100.0 ,0.0 ,100.0};
            ArrayList<Double> stepValues2 = new ArrayList<>(Arrays.asList(stepValuesArray2));
            stepFunctionStepValueExpect.put("1", stepValues2);

            result = stepFunctionStepTimeExpect.equals(stepFunctionStepTime);
            log.info("stepFunctionStepTime 的测试结果：" + result);
            result = result && stepFunctionStepValueExpect.equals(stepFunctionStepValue);
            log.info("stepFunctionStepValue 的测试结果：" + result);
            Assert.assertTrue(result);


            log.info("----------------------- stepFunctionStaffTime/Value 测试 ------------------------------");
            // staff 对应的 Calendar
            HashMap<String, ArrayList<Double>> stepFunctionStaffTime = subModelData.getStepFunctionStaffTime();
            HashMap<String, ArrayList<Double>> stepFunctionStaffValue = subModelData.getStepFunctionStaffValue();

            printAllData(stepFunctionStaffTime);
            printAllData(stepFunctionStaffValue);

            Double[] staffTimesArray1 = new Double[]{0.0 ,8.0 ,11.0 ,12.0 ,17.0 ,32.0 ,35.0 ,36.0 ,41.0 ,104.0 ,107.0 ,108.0 ,113.0 ,128.0 ,131.0 ,132.0 ,137.0 ,152.0 ,155.0 ,156.0 ,161.0 ,176.0 ,179.0 ,180.0 ,185.0 ,240.0};
            ArrayList<Double> staffTimes1 = new ArrayList<>(Arrays.asList(staffTimesArray1));
            stepFunctionStaffTimeExpect.put("10001", staffTimes1);

            Double[] staffTimesArray2 = new Double[]{0.0 ,8.0 ,11.0 ,12.0 ,17.0 ,80.0 ,83.0 ,84.0 ,89.0 ,104.0 ,107.0 ,108.0 ,113.0 ,128.0 ,131.0 ,132.0 ,137.0 ,152.0 ,155.0 ,156.0 ,161.0 ,176.0 ,179.0 ,180.0 ,185.0 ,240.0};
            ArrayList<Double> staffTimes2 = new ArrayList<>(Arrays.asList(staffTimesArray2));
            stepFunctionStaffTimeExpect.put("10002", staffTimes2);

            Double[] staffTimesArray3 = new Double[]{0.0, 20.0, 32.0, 128.0, 140.0, 164.0, 176.0, 240.0};
            ArrayList<Double> staffTimes3 = new ArrayList<>(Arrays.asList(staffTimesArray3));
            stepFunctionStaffTimeExpect.put("10003", staffTimes3);

            Double[] staffTimesArray4 = new Double[]{0.0, 56.0, 68.0, 92.0, 104.0, 176.0, 188.0, 212.0, 224.0, 240.0};
            ArrayList<Double> staffTimes4 = new ArrayList<>(Arrays.asList(staffTimesArray4));
            stepFunctionStaffTimeExpect.put("10004", staffTimes4);

            Double[] staffTimesArray5 = new Double[]{0.0, 56.0, 68.0, 92.0, 104.0, 144.0, 152.0, 164.0, 176.0, 188.0, 200.0, 212.0, 240.0};
            ArrayList<Double> staffTimes5 = new ArrayList<>(Arrays.asList(staffTimesArray5));
            stepFunctionStaffTimeExpect.put("10005", staffTimes5);


            Double[] staffTimesArray6 = new Double[]{0.0, 8.0, 11.0, 12.0, 17.0, 80.0, 83.0, 84.0, 89.0, 104.0, 107.0, 108.0, 113.0, 128.0, 131.0, 132.0, 137.0, 144.0, 152.0, 164.0, 176.0, 188.0, 200.0, 212.0, 240.0};
            ArrayList<Double> staffTimes6 = new ArrayList<>(Arrays.asList(staffTimesArray6));
            stepFunctionStaffTimeExpect.put("10006", staffTimes6);

            Double[] staffTimesArray7 = new Double[]{0.0, 8.0, 11.0, 12.0, 17.0, 72.0, 80.0, 83.0, 84.0, 89.0, 128.0, 131.0, 132.0, 137.0, 144.0, 152.0, 164.0, 176.0, 188.0, 200.0, 212.0, 240.0};
            ArrayList<Double> staffTimes7 = new ArrayList<>(Arrays.asList(staffTimesArray7));
            stepFunctionStaffTimeExpect.put("10007", staffTimes7);

//            log.info(stepFunctionStaffTimeExpect);
//            log.info(stepFunctionStaffTime);
            boolean b = stepFunctionStaffTimeExpect.equals(stepFunctionStaffTime);
            log.info("stepFunctionStaffTime 的测试结果：" + b);
            result = result && b;

            Double[] staffValuesArray1 = new Double[]{0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0};
            ArrayList<Double> staffValues1 = new ArrayList<>(Arrays.asList(staffValuesArray1));
            stepFunctionStaffValueExpect.put("10001", staffValues1);

            Double[] staffValuesArray2 = new Double[]{0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0 ,100.0 ,0.0};
            ArrayList<Double> staffValues2 = new ArrayList<>(Arrays.asList(staffValuesArray2));
            stepFunctionStaffValueExpect.put("10002", staffValues2);

            Double[] staffValuesArray3 = new Double[]{0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0};
            ArrayList<Double> staffValues3 = new ArrayList<>(Arrays.asList(staffValuesArray3));
            stepFunctionStaffValueExpect.put("10003", staffValues3);

            Double[] staffValuesArray4 = new Double[]{0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0};
            ArrayList<Double> staffValues4 = new ArrayList<>(Arrays.asList(staffValuesArray4));
            stepFunctionStaffValueExpect.put("10004", staffValues4);

            Double[] staffValuesArray5 = new Double[]{0.0, 100.0, 0.0, 100.0, 0.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0};
            ArrayList<Double> staffValues5 = new ArrayList<>(Arrays.asList(staffValuesArray5));
            stepFunctionStaffValueExpect.put("10005", staffValues5);

            Double[] staffValuesArray6 = new Double[]{0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0};
            ArrayList<Double> staffValues6 = new ArrayList<>(Arrays.asList(staffValuesArray6));
            stepFunctionStaffValueExpect.put("10006", staffValues6);

            Double[] staffValuesArray7 = new Double[]{0.0, 100.0, 0.0, 100.0, 0.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0, 0.0, 100.0, 0.0, 100.0, 0.0, 100.0, 0.0};
            ArrayList<Double> staffValues7 = new ArrayList<>(Arrays.asList(staffValuesArray7));
            stepFunctionStaffValueExpect.put("10007", staffValues7);

//            log.info("");
//            log.info(stepFunctionStaffValueExpect);
//            log.info(stepFunctionStaffValue);

            boolean b1 = stepFunctionStaffValueExpect.equals(stepFunctionStaffValue);
            log.info("stepFunctionStaffValue 的测试结果：" + b1);
            result = result && b1;
            Assert.assertTrue(result);

            log.info("------------------------------ 拆并箱标志测试 -----------------------------");
            HashMap<String, EntitySubTask> subTaskHashMap = engineProcessedData.getSubTaskHashMap();
            EntitySubTask subtask1 = subTaskHashMap.get("Task1_Subtask1");
            ArrayList<EntityStepActivity> stepActivities1 = subtask1.getStepActivities();
            EntitySubTask subtask2 = subTaskHashMap.get("Task1_Subtask2");
            ArrayList<EntityStepActivity> stepActivities2 = subtask2.getStepActivities();
            boolean b2 = stepActivities1.size() == 2 && stepActivities2.size() == 1;
            log.info("拆并箱 pass 标志的测试结果：" + b2);
            result = result && b2;

            EntityStepActivity stepA0 = stepActivities1.get(0);
            EntityStepActivity stepA1 = stepActivities1.get(1);
            boolean b3 = stepA0.getValidSampleQuantity() == 1 && stepA0.getSize() == 24.0
                    && stepA1.getValidSampleQuantity() == 5 && stepA1.getSize() == 120.0;
            log.info("拆并箱 repeat和非标准标志的测试结果：" + b3);
            result = result && b3;
            Assert.assertTrue(result);

            log.info("------------------------------ 授权测试 -------------------------------");
            // 授权相关测试
            EntityProcedure procedure_1 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_1");
            EntityBom bom1 = engineProcessedData.getBomHashMap().get("BOM1");
            EntityStep bom1step101 = engineProcessedData.getStepHashMap().get("BOM1101");
            boolean b4 = procedure_1.getAuthEngineers().size() == 2 && procedure_1.getAuthTechnicians().size() == 1
                    && bom1.getAuthBomEngineers().size() == 0 && bom1.getAuthBomTechnicians().size() == 0
                    && bom1step101.getAuthStepEngineers().size() == 2 && bom1step101.getAuthStepTechnicians().size() == 1;

            EntityProcedure procedure_2 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_2");
            EntityBom bom2 = engineProcessedData.getBomHashMap().get("BOM2");
            EntityStep bom2step101 = engineProcessedData.getStepHashMap().get("BOM2101");
            boolean b5 = procedure_2.getAuthEngineers().size() == 0 && procedure_2.getAuthTechnicians().size() == 0
                    && bom2.getAuthBomEngineers().size() == 0 && bom2.getAuthBomTechnicians().size() == 0
                    && bom2step101.getAuthStepEngineers().size() == 3 && bom2step101.getAuthStepTechnicians().size() == 4;

            EntityProcedure procedure_3 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_3");
            EntityBom bom3 = engineProcessedData.getBomHashMap().get("BOM3");
            EntityStep bom3step101 = engineProcessedData.getStepHashMap().get("BOM3101");
            boolean b6 = procedure_3.getAuthEngineers().size() == 0 && procedure_3.getAuthTechnicians().size() == 0
                    && bom3.getAuthBomEngineers().size() == 1 && bom3.getAuthBomTechnicians().size() == 1
                    && bom3step101.getAuthStepEngineers().size() == 1 && bom3step101.getAuthStepTechnicians().size() == 1;

            EntityProcedure procedure_4 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_4");
            EntityBom bom4 = engineProcessedData.getBomHashMap().get("BOM4");
            EntityStep bom4step101 = engineProcessedData.getStepHashMap().get("BOM4101");
            boolean b7 = procedure_4.getAuthEngineers().size() == 2 && procedure_4.getAuthTechnicians().size() == 2
                    && bom4.getAuthBomEngineers().size() == 1 && bom4.getAuthBomTechnicians().size() == 1
                    && bom4step101.getAuthStepEngineers().size() == 1 && bom4step101.getAuthStepTechnicians().size() == 1;

            EntityProcedure procedure_5 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_5");
            EntityBom bom5 = engineProcessedData.getBomHashMap().get("BOM5");
            EntityStep bom5step101 = engineProcessedData.getStepHashMap().get("BOM5101");
            boolean b8 = procedure_5.getAuthEngineers().size() == 3 && procedure_5.getAuthTechnicians().size() == 4
                    && bom5.getAuthBomEngineers().size() == 1 && bom5.getAuthBomTechnicians().size() == 2
                    && bom5step101.getAuthStepEngineers().size() == 1 && bom5step101.getAuthStepTechnicians().size() == 1;


            EntityProcedure procedure_6 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_6");
            EntityBom bom6 = engineProcessedData.getBomHashMap().get("BOM6");
            EntityStep bom6step101 = engineProcessedData.getStepHashMap().get("BOM6101");
            boolean b9 = procedure_6.getAuthEngineers().size() == 1 && procedure_6.getAuthTechnicians().size() == 0
                    && bom6.getAuthBomEngineers().size() == 1 && bom6.getAuthBomTechnicians().size() == 0
                    && bom6step101.getAuthStepEngineers().size() == 1 && bom6step101.getAuthStepTechnicians().size() == 0;


            EntityProcedure procedure_7 = engineProcessedData.getProcedureHashMap().get("PROCEDURE_7");
            EntityBom bom7 = engineProcessedData.getBomHashMap().get("BOM7");
            EntityStep bom7step101 = engineProcessedData.getStepHashMap().get("BOM7101");
            boolean b10 = procedure_7.getAuthEngineers().size() == 1 && procedure_7.getAuthTechnicians().size() == 0
                    && bom7.getAuthBomEngineers().size() == 2 && bom7.getAuthBomTechnicians().size() == 0
                    && bom7step101.getAuthStepEngineers().size() == 1 && bom7step101.getAuthStepTechnicians().size() == 0;



            boolean authRe = b4 && b5 && b6 && b7 && b8 && b9 && b10;
            log.info("授权测试结果是：" + authRe);
            result = result && b4;
            Assert.assertTrue(result);
        }

        log.info("-----------------------------------------");
        Assert.assertTrue(result);
        log.info("数据处理的测试结果是：" + result);
    }


    public void printAllData(HashMap<String, ArrayList<Double>> stepFunctionStaffValue){
        for (Map.Entry<String, ArrayList<Double>> staffValueEntry : stepFunctionStaffValue.entrySet()) {
            String staffId = staffValueEntry.getKey();
            ArrayList<Double> staffValues = staffValueEntry.getValue();
//            log.info("staffId：" + staffId);
            String array = "{";
            for (Double staffValue : staffValues) {
                array += staffValue + " ,";
            }
            array += "}";
//            log.info("array：" + array);
        }
    }

}
