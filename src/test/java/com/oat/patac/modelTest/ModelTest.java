package com.oat.patac.modelTest;

import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.engine.PatacSchedulingTask;
import com.oat.patac.PatacTest;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;
import static com.oat.common.utils.DateUtil.TIME_ZONE;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class ModelTest extends PatacTest {

    @Before
    public void setUp() {
        super.setUp();
    }
    @After
    public void tearDown() throws Exception {
        PatacSchedulingTask schedulingTask = service.getSchedulingTask();
        int functionId = schedulingTask.getEngineProcessedData().getFunctionId();
        exportCsvData();
        resultCompareBetweenDbAndCsv(schedulingTask,functionId, false);
        //caseResultTest(schedulingTask,functionId);
    }

//    @Test
//    public void testCreatePlan5()   {
//        HandlerDataSource.putDataSource("datasource5");
//        HashMap<String, Boolean> constraintSelection = new HashMap<>();
//        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
//        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
////        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
////        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
////        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
////        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
//        // 根据test case的设计设置时间
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
//        calendar.set(2022, 9, 5, 0, 0, 0);
//        Date planTriggeredTime = calendar.getTime();
//        service.createPlanForTest(1, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
//    }
//
//    @Test
//    public void testCreatePlan6()   {
//        HandlerDataSource.putDataSource("datasource6");
//        HashMap<String, Boolean> constraintSelection = new HashMap<>();
//        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
//        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
//        // 根据test case的设计设置时间
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
//        calendar.set(2022, 9, 5, 0, 0, 0);
//        Date planTriggeredTime = calendar.getTime();
//        service.createPlanForTest(1, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
//    }

    @Test
    //测试任务单和批次，保证任务单时间上包含批次，且任务单和批次的状态是保持一致的
    public void test_01()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource9");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "01", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3, true);

    }
    @Test
    //测试任务单和批次，保证任务单时间上包含批次，且任务单和批次的状态是保持一致的，并且考虑固定批次
    public void test_02()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource10");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "02", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //测试批次组的顺序，且考虑固定的批次
    //为结果的稳定性，增加了stepA的forbid start end约束
    public void test_03()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource11");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstraintStepAForbidStartEnd, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "03", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3, true);
    }

    @Test
    //测试批次组的顺序，且考虑固定的批次
    public void test_04()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource12");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "04", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //测试小阶段组内的顺序
    public void test_05()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource13");

        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "05", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3, true);
    }

    @Test
    //测试小阶段组的最大时间间隔
    public void test_06()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource14");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "06", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //测试同一个样件（特殊样品组内的）同一个时间不能做多个批次
    public void test_07()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource15");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "07", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }
    @Test
    //测试同一个样件（特殊样品组内的）同一个时间不能做多个批次
    public void test_07_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource15");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "07", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //小阶段活动和资源不按天的的活动关系：一同开始，一同结束；无需一同开始和结束（工程师/技师/设备）以及资源的个数
    public void test_08()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource16");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "08", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //小阶段活动和资源不按天的的活动关系：一同开始，一同结束；无需一同开始和结束（工程师/技师/设备）以及资源的个数
    public void test_08_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource16");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "08", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //每个待排程资源不按天的活动的开始结束时间不应该在Step Function值为0的时候，即该资源不工作的时候
    public void test_09()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource17");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "09", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //每个待排程资源不按天的活动的开始结束时间不应该在Step Function值为0的时候，即该资源不工作的时候
    public void test_09_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource17");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "09", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //小阶段活动和资源按天的的活动关系：一同开始，一同结束；无需一同开始和结束（工程师/技师/设备）以及资源的个数
    public void test_10()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource18");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "10", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //小阶段活动和资源按天的的活动关系：一同开始，一同结束；无需一同开始和结束（工程师/技师/设备）以及资源的个数
    public void test_10_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource18");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "10", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //每个待排程资源按天的活动的开始结束时间不应该在Step Function值为0的时候，即该资源不工作的时候
    public void test_11()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource19");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "11", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //每个待排程资源按天的活动的开始结束时间不应该在Step Function值为0的时候，即该资源不工作的时候
    public void test_11_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource19");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "11", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //有状态要求的资源需要在相应活动时在要求的状态中，针对不按天的设备的活动
    public void test_12()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource20");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAState, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "12", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3, true);
    }

    @Test
    //有状态要求的资源需要在相应活动时在要求的状态中，针对不按天的设备的活动
    public void test_12_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource20");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);


        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAState, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "12", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4, true);
    }
    @Test
    //有状态要求的资源需要在相应活动时在要求的状态中，针对按天的设备的活动
    public void test_13()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource21");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAState, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "13", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3, true);
    }
    @Test
    //有状态要求的资源需要在相应活动时在要求的状态中，针对按天的设备的活动
    public void test_13_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource21");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAState, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "13", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4, true);
    }
    @Test
    //有状态的要求的资源，再已经排好的活动需要设置再要求的状态中
    public void test_14()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource22");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAState, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAFixedState, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "14", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3, true);
    }

    @Test
    //有状态的要求的资源，再已经排好的活动需要设置再要求的状态中
    public void test_14_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource22");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAState, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAFixedState, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "14", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4, true);
    }
    @Test
    //批次中是否使用某资源组的0/1变量；批次中任何一个小阶段中使用的资源作为批次使用的资源；批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多
    public void test_15()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource23");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true); //
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true); //
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);  // 每个待排程资源活动的开始结束时间不应该在step function值为0的时候，即该资源不工作的时候
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true); // 小阶段活动资源分配0，1变量和interval变量的关系，针对设备组不按天的情况
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true); // 批次中任何一个小阶段中使用的资源作为批次使用的资源
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true); // 批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true); //每个按天资源组活动工作天范围变量的开始结束时间应该在stepFunction值为10的时候，即每天的开始结束时间
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true); // 小阶段活动资源分配0，1变量和interval变量的关系，针对设备按天的情况

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "15", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }
    @Test
    //批次中是否使用某资源组的0/1变量；批次中任何一个小阶段中使用的资源作为批次使用的资源；批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多
    public void test_15_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource23");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true); //
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);  // 每个待排程资源活动的开始结束时间不应该在step function值为0的时候，即该资源不工作的时候
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true); // 小阶段活动资源分配0，1变量和interval变量的关系，针对设备组不按天的情况
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true); // 批次中任何一个小阶段中使用的资源作为批次使用的资源
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true); // 批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true); //每个按天资源组活动工作天范围变量的开始结束时间应该在stepFunction值为100的时候，即每天的开始结束时间
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true); // 小阶段活动资源分配0，1变量和interval变量的关系，针对设备按天的情况

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "15", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //cumul function 容量的限制, 包括设备的容量可共享和不可共享的情况
    public void test_16()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource24");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true); //
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);  // 每个待排程资源活动的开始结束时间不应该在step function值为0的时候，即该资源不工作的时候
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true); // 小阶段活动资源分配0，1变量和interval变量的关系，针对设备组不按天的情况
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true); // 批次中任何一个小阶段中使用的资源作为批次使用的资源
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true); // 批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多
        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);

        service.createPlanForTest(1, "16", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //cumul function 容量的限制, 包括设备的容量可共享和不可共享的情况
    public void test_16_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource24");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true); //
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);  // 每个待排程资源活动的开始结束时间不应该在step function值为0的时候，即该资源不工作的时候
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true); // 小阶段活动资源分配0，1变量和interval变量的关系，针对设备组不按天的情况
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true); // 批次中任何一个小阶段中使用的资源作为批次使用的资源
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true); // 批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多
        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "16", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);

        service.createPlanForTest(1, "16", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);

    }

    @Test
    //每个待排程的任务单和可分配的责任工程师为维度的0/1决策变量；
    // 每个待排程任务单应该分配一个责任工程师；且如果小阶段的大阶段是“报告撰写”，则该小阶段的工程师和任务单责任工程师为一个人；
    // 如果批次内没有“报告撰写”大阶段，则每个批次中至少一个小阶段活动的工程师和任务单责任工程师是一个人
    public void test_17()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource25");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "17", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //每个待排程的任务单和可分配的责任工程师为维度的0/1决策变量；
    // 每个待排程任务单应该分配一个责任工程师；且如果小阶段的大阶段是“报告撰写”，则该小阶段的工程师和任务单责任工程师为一个人；
    // 如果批次内没有“报告撰写”大阶段，则每个批次中至少一个小阶段活动的工程师和任务单责任工程师是一个人
    public void test_17_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource25");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "17", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

//    @Test
    //目标函数： 尽可能多排入的任务单
//    public void test_18CreatePlan()   {
//        HandlerDataSource.putDataSource("datasource26");
//        HashMap<String, Boolean> constraintSelection = new HashMap<>();
//
//        // 根据test case的设计设置时间
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
//        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
//        Date planTriggeredTime = calendar.getTime();
//        service.createPlanForTest(1, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
//    }

    @Test
    //目标函数： 尽量在“批次期望完成时间”之前完成
    public void test_19()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource27");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true); //任务单时间上包含批次
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //任务单和批次的状态一致
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true); //批次组的顺序
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true); //小阶段活动的开始结束时间限制
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true); //批次包含对应的小阶段
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true); //批次和小阶段的状态一致
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true); // 小阶段的顺序约束
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true); //考虑小阶段之间最大的Gap
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true); //同一个样品上时间不能重叠

        constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true); //尽量在批次“试验期望完成时间”之前完成
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "19", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 尽量减少计划完成时间和任务单“审批完成时间”的差
    public void test_20()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource28");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();

        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true); //任务单时间上包含批次
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //任务单和批次的状态一致
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true); //批次组的顺序
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true); // 小阶段活动的开始结束时间限制
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true); //批次包含对应的小阶段
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true); //批次和小阶段的状态一致
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true); // 小阶段的顺序约束
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true); //考虑小阶段之间最大的Gap
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true); //同一个样品上时间不能重叠



        constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true); // 尽量减少计划完成时间和任务单“审批完成时间”的差

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "20", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 尽量减少每个批次在每个资源组中使用资源的数量
    public void test_21()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource29");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true); // 尽量减少每个批次在每个资源组中使用资源的数量
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "21", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 尽量减少每个批次在每个资源组中使用资源的数量
    public void test_21_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource29");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true); // 尽量减少每个批次在每个资源组中使用资源的数量
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "21", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 人员工作量尽量均衡
    public void test_22()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource32");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true); // 人员工作量尽量均衡
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "22", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 人员工作量尽量均衡
    public void test_22_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource32");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true); // 人员工作量尽量均衡
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "22", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_22
    public void test_23()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource30");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "23", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_22
    public void test_23_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource30");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "23", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 整体测试:patac_test_23
    public void test_24()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource31");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
//        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true); // 每个待排程任务单应该span（时间上包含）其下的批次
//        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); // 任务单和其下属批次的被排入状态应该一致
//        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true); // 每个待排程任务单下的待排程批次的小阶段活动的开始结束时间不应该在step function值为0的时候，即不工作的时候
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true); // 每个待排程任务单的每个批次应该span（时间上包含）其下的小阶段
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true); // 批次和其下属的小阶段活动的被排入状态应该一致
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true); // 小阶段活动被安排的顺序要满足bom中定义的顺序
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true); // 与上一个小阶段的最大间隔小时
//        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true); // 同一个样件（特殊样品组内的）同一时间不能做多个批次
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true); // 小阶段和其下属的不按天的资源组活动的被排入状态应该一致
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true); // 小阶段活动比相关的资源活动早开始，针对资源组不按天的情况和不需要与阶段时间一同开始的情况
//        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
//        //目标函数
//        constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true); //尽量在批次“试验期望完成时间”之前完成
//        constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true); // 尽量减少计划完成时间和任务单“审批完成时间”的差
//        constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true); // 尽量减少每个批次在每个资源组中使用资源的数量
//        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true); // 人员工作量尽量均衡


        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 14, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "24", constraintSelection, true, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_25 (Task1无法排程)
    public void test_25()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource33");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "25", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_25 (Task1无法排程)
    public void test_25_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource33");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "25", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 子模型测试,测试数据: patac_test_26 (Task2无法排程)
    public void test_26()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource34");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "26", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_26 (Task2无法排程)
    public void test_26_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource34");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "26", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 子模型测试,测试数据: patac_test_27 （Task3无法排程）
    public void test_27()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource35");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "27", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_27 （Task3无法排程）
    public void test_27_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource35");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "27", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_28 (Task1和Task3无法排程)
    public void test_28()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource36");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "28", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_28 (Task1和Task3无法排程)
    public void test_28_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource36");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "28", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 子模型测试,测试数据: patac_test_29 (同一个任务单中部分批次出现无法排程,批次1和批次3无法完成)
    public void test_29()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource37");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "29", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_29 (同一个任务单中部分批次出现无法排程,批次1和批次3无法完成)
    public void test_29_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource37");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "29", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 子模型测试,测试数据: patac_test_30 (同一个任务单中部分批次出现无法排程,批次2无法完成)
    public void test_30()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource38");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "30", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_30 (同一个任务单中部分批次出现无法排程,批次2无法完成)
    public void test_30_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource38");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "30", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 子模型测试,测试数据: patac_test_31 (时间颗粒度大于休息时间)
    public void test_31()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource39");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "31", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_31 (时间颗粒度大于休息时间)
    public void test_31_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource39");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "31", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //目标函数： 子模型测试,测试数据: patac_test_32,子模型在同一个任务单中排程
    public void test_32()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource40");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "32", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //目标函数： 子模型测试,测试数据: patac_test_32,子模型在同一个任务单中排程
    public void test_32_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource40");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "32", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    // 跨任务单的批次使用相同的整车的case
    public void test_33()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource41");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "33", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 已排程批次使用和待排程批次一样的整车的case
    public void test_34()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource42");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "34", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true);
    }
    @Test
    // 已排程批次使用和待排程批次一样的整车的case
    public void test_34_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource42");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "34", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    // 不同的sample group id对应相同的sample no（整车）
    public void test_35()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource43");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "35", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 分子模型的情况中，前面的子模型的结果里面有使用和后面的待排程批次一样的整车的case
    public void test_36()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource44");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap , true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "36", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }


    @Test
    // 已排程的工程师、技师活动（和待排程批次使用同一个资源的情况），不按天的情况
    public void test_37() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource45");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "37", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 已排程的工程师、技师活动（和待排程批次使用同一个资源的情况），不按天的情况
    public void test_37_model4() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource45");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstraintResourceCapacity,true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "37", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true);
    }


    @Test
    // 已排程的设备活动（和待排程批次使用同一个资源的情况）
    public void test_38()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource46");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "38", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 已排程的设备活动（和待排程批次使用同一个资源的情况）
    public void test_38_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource46");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "38", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }


    @Test
    // 分子模型的情况中，前面的子模型的结果里面有使用和后面的待排程批次一样的资源（工程师、技师、设备）的情况
    public void test_39()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource47");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "39", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 分子模型的情况中，前面的子模型的结果里面有使用和后面的待排程批次一样的资源（工程师、技师、设备）的情况
    public void test_39_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource47");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "39", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }


    @Test
    // 对于设备，分别考虑已排程设备容量可共享（Expandable）（Plan Input）
    public void test_40()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource48");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "40", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 对于设备，分别考虑已排程设备容量可共享（Expandable）（Plan Input）
    public void test_40_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource48");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "40", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 对于设备，分别考虑已排程设备容量不可共享（Expandable）（Plan Input）
    public void test_41()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource49");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "41", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 对于设备，分别考虑已排程设备容量不可共享（Expandable）（Plan Input）
    public void test_41_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource49");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "41", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 对于设备，分别考虑已排程设备容量不可共享（Expandable）（子模型）
    public void test_42()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource50");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "42", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 对于设备，分别考虑已排程设备容量不可共享（Expandable）（子模型）
    public void test_42_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource50");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "42", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 对于设备，分别考虑已排程设备容量可共享（Expandable）（子模型）
    public void test_43()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource51");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "43", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 对于设备，分别考虑已排程设备容量可共享（Expandable）（子模型）
    public void test_43_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource51");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "43", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    //关于批次组可跨任务单的修改(同一个颗粒度的，不分子模型),默认模式
    public void test_44() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource52");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "44", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_3,true,null ,SOLVE_MODE_DEFAULT);
    }

    @Test
    //关于批次组可跨任务单的修改(同一个颗粒度的，不分子模型),默认模式
    public void test_44_model4() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource52");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "44", constraintSelection, false,
                planTriggeredTime, MODEL_VERSION_4,true,null ,SOLVE_MODE_DEFAULT);
    }
    @Test
    //关于批次组可跨任务单的修改(不同的颗粒度的，分子模型)，默认模式
    public void test_45(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource53");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "45", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true,null,SOLVE_MODE_DEFAULT);
    }

    @Test
    //关于批次组可跨任务单的修改(不同的颗粒度的，分子模型)，默认模式
    public void test_45_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource53");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "45", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true,null,SOLVE_MODE_DEFAULT);
    }
    @Test
    // 按照时间颗粒度分子模型，执行顺序按照颗粒度从长到短，原模式
    public void test_46(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource54");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "46", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true,null,SOLVE_MODE_BY_GRANULARITY);
    }

    @Test
    // 按照时间颗粒度分子模型，执行顺序按照颗粒度从长到短，原模式
    public void test_46_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource54");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "46", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true,null,SOLVE_MODE_BY_GRANULARITY);
    }
    @Test
    // 按照任务单和时间颗粒度分子模型，前面子模型的结果保留，（模式2）
    public void test_47(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource55");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "47", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true,null, SOLVE_MODE_BY_TASK);
    }

    @Test
    // 按照任务单和时间颗粒度分子模型，前面子模型的结果保留，（模式2）
    public void test_47_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource55");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "47", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true,null, SOLVE_MODE_BY_TASK);
    }

    @Test
    // 按任务单和时间颗粒度分子模型，前面子模型的结果不保留——帮助确认是哪个任务单的问题（模式3）
    public void test_48(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource56");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "48", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true, null, SOLVE_MODE_BY_TASK_NOT_SAVE);
    }

    @Test
    // 按任务单和时间颗粒度分子模型，前面子模型的结果不保留——帮助确认是哪个任务单的问题（模式3）
    public void test_48_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource56");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        ArrayList<String> taskNos = new ArrayList<>();
        taskNos.add("Task1");
        taskNos.add("Task2");
        taskNos.add("Task3");
        service.createPlanForTest(1, "48", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true, taskNos, SOLVE_MODE_BY_TASK_NOT_SAVE);
    }


    @Test
    // 按批次组一组一组分子模型，组内按顺序兼顾时间颗粒度分子模型，前面子模型的结果保留（模式4）
    public void test_49(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource57");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "49", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true, null, SOLVE_MODE_BY_SUBTASK_GROUP);
    }

    @Test
    // 按批次组一组一组分子模型，组内按顺序兼顾时间颗粒度分子模型，前面子模型的结果保留（模式4）
    public void test_49_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource57");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "49", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true, null, SOLVE_MODE_BY_SUBTASK_GROUP);
    }

    @Test
    // 按批次组一组一组分子模型，组内按顺序兼顾时间颗粒度分子模型，前面子模型的结果不保留 （模式5）
    public void test_50(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource58");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "50", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true, null, SOLVE_MODE_BY_SUBTASK_GROUP_NOT_SAVE);
    }

    @Test
    // 按批次组一组一组分子模型，组内按顺序兼顾时间颗粒度分子模型，前面子模型的结果不保留 （模式5）
    public void test_50_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource58");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "50", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true, null, SOLVE_MODE_BY_SUBTASK_GROUP_NOT_SAVE);
    }
    @Test
    // 拆并箱标志： pass测试，pass只和任务组有关，需要在任务组和样品以及小阶段上标记
    public void test_51(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource59");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
//        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
//        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "51", constraintSelection, true, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 拆并箱标志： pass测试，pass只和任务组有关，需要在任务组和样品以及小阶段上标记
    public void test_51_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource59");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
//        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
//        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
//        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
//        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
//        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "51", constraintSelection, true, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 拆并箱标志：repeat测试，repeat只和样品有关系，但是要区分按天不按天：不按天
    public void test_52(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource60");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "52", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 拆并箱标志：repeat测试，repeat只和样品有关系，但是要区分按天不按天：不按天
    public void test_52_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource60");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "52", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 拆并箱标志：repeat测试，repeat只和样品有关系，但是要区分按天不按天：按天
    public void test_53(){
        HandlerDataSource.putDataSource("datasource61");
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "53", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 拆并箱标志：repeat测试，repeat只和样品有关系，但是要区分按天不按天：按天
    public void test_53_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource61");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "53", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 拆并箱标志：expand测试，expand和小阶段（其实是里面的设备，但是数据在小阶段中）有关，考虑Is_Expandable为0
    public void test_54(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource62");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "54", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 拆并箱标志：expand测试，expand和小阶段（其实是里面的设备，但是数据在小阶段中）有关，考虑Is_Expandable为0
    public void test_54_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource62");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "54", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 拆并箱标志：expand测试，expand和小阶段（其实是里面的设备，但是数据在小阶段中）有关，考虑Is_Expandable为1
    public void test_55(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource63");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "55", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 拆并箱标志：expand测试，expand和小阶段（其实是里面的设备，但是数据在小阶段中）有关，考虑Is_Expandable为1
    public void test_55_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource63");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "55", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
    @Test
    // 拆并箱标志：非标准标志
    public void test_56(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource64");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "56", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 拆并箱标志：非标准标志
    public void test_56_model4(){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource64");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, 9, 17, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "56", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    // 对于设备，容量可扩展的情况（包括已排程批次在时间上的冲突），容量上不超过
    public void test_57()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource65");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "57", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    // 对于设备，容量可扩展的情况（包括已排程批次在时间上的冲突），容量上不超过
    public void test_57_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource65");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "57", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //对于设备，容量可扩展的情况（包括已排程批次在时间上的冲突），容量上超过
    public void test_58()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource66");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "58", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }

    @Test
    //对于设备，容量可扩展的情况（包括已排程批次在时间上的冲突），容量上超过
    public void test_58_Model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource66");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "58", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }

    @Test
    //对于设备，有状态的情况，包括已排程批次在同一时间有不同状态的情况
    public void test_59()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource67");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);
        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "59", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3,true);
    }


    @Test
    //对于设备，有状态的情况，包括已排程批次在同一时间有不同状态的情况
    public void test_59_model4()   {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasource67");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstraintResourceAInStepARange, true);
        constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);
        constraintSelection.put(ConstraintResourceCapacity,true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 17, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "59", constraintSelection, false, planTriggeredTime, MODEL_VERSION_4,true);
    }
}




