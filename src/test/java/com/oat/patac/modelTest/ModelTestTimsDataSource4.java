package com.oat.patac.modelTest;

import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.service.PlanService;
import ilog.concert.IloException;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static com.oat.common.utils.ConstantUtil.*;
import static com.oat.common.utils.ConstantUtil.MODEL_VERSION_2;
import static com.oat.common.utils.DateUtil.TIME_ZONE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class ModelTestTimsDataSource4 {
    @Resource
    private PlanService service;
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了20个约束, functionId=100440, model3
     * 1个task，一个批次，4个小阶段
     * 有by day，和not by day，有报告撰写；一个可用工程师，没有任何auth
     * 由于是标准BOM，不应该没有auth procedure，所以结果应该是不能排程。目前由于没有打开数据验证错误就不排程的开关，所以结果是没有资源相关的活动变量。
     * 暂时将BOM改为非标准BOM，应该可以排程。
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanAllDataSource4FunctionId100440Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
//        HandlerDataSource.putDataSource("datasourceOAT");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);


        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true); //not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);

        constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2019, 01, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100440, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 只加了3个约束, functionId=65
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanTaskSubtaskDataSource4FunctionId65Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2019, 03, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(65, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }

    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了7个约束, functionId=65
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanTaskSubtaskStepDataSource4FunctionId65Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
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
        calendar.set(2019, 03, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(65, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }

    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了8个约束, functionId=65
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanTaskSubtaskStepSampleDataSource4FunctionId65Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2019, 03, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(65, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }

    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了8个约束, functionId=100370
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanTaskSubtaskStepSampleDataSource4FunctionId100370Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);
        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2019, 03, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100370, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }

    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了16个约束, functionId=65
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanResourceDataSource4FunctionId65Model2() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAAssignResGroupRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartAtResANotByDay, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartBeforeResANotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAtResANotByDay, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAfterResANotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//not tested is_constraint=1 in this case
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2019, 03, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(65, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_2);
    }
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了16个约束, functionId=65， model3，resource group not by day
     * 未指定优先工程师，有auth procedure
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanResourceDataSource4FunctionId65Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//not tested is_constraint=1 in this case
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2019, 03, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(65, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了20个约束, functionId=100440， model2
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanResourceDataSource4FunctionId100440Model2() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAAssignResGroupRes, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartAtResANotByDay, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartBeforeResANotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAtResANotByDay, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAfterResANotByDay, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartBeforeResAByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAfterResAByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationResASpanDays, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//not tested is_constraint=1 in this case
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2019, 01, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100440, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_2);
    }
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了20个约束, functionId=100409
     * 4 tasks, each task has one subtask
     * 1 BOM, over 10 steps, one step with 60 days * 1 hour per day engineer work
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanResourceDataSource4FunctionId100409Model2() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);


        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAAssignResGroupRes, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartAtResANotByDay, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartBeforeResANotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAtResANotByDay, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAfterResANotByDay, true);
        //加之前的约束结果正常，加了一条下面的约束，结果step_1043539都往后错了107.5小时，没有原因
        //而且如果不设置size max，539的size会变大，没有原因
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);
        //加了下面两个约束之后，4个任务单、批次的时间各有不同了，从step 540开始，540是需要做60天的那个step
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAStartBeforeResAByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceARelationStepAEndAfterResAByDay, true);
        //加不加这个约束效果一样
        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//not tested is_constraint=1 in this case


        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2019, 01, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100409, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_2);
    }
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了20个约束, functionId=100440, model3
     * 1个task，一个批次，4个小阶段
     * 有by day，和not by day，有报告撰写；一个可用工程师，没有任何auth
     * 由于是标准BOM，不应该没有auth procedure，所以结果应该是不能排程。目前由于没有打开数据验证错误就不排程的开关，所以结果是没有资源相关的活动变量。
     * 暂时将BOM改为非标准BOM，应该可以排程。
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanResourceDataSource4FunctionId100440Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//not tested is_constraint=1 in this case
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2019, 01, 20, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100440, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }
    /**
     * 调用测试接口，使用晓风给的tims-oat数据， 加了20个约束, functionId=100409, model3
     *  4 tasks, each task has one subtask
     *  1 BOM, over 10 steps, step 1043540 with 60 days * 1 hour per day engineer work， *1 个设备24小时一天
     *  有的小阶段不需要工程师
     *  有by day，和not by day，有报告撰写?；一个可用工程师?，没有任何auth?
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanResourceAssignmentDataSource4FunctionId100409Model3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
 /*
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true); //not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);
*/
        constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true);
        //       constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true);//not tested in this case
//        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true);
        constraintSelection.put(ObjectiveSumOfStepASize, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2019, 00, 4, 00, 00, 00);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100409, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }
}
