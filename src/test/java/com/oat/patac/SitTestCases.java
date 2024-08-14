package com.oat.patac;

import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.engine.PatacSchedulingTask;
import ilog.concert.IloException;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;
import static com.oat.common.utils.DateUtil.TIME_ZONE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class SitTestCases extends PatacTest{

    /**
     * EMC Test（100630，有数据，LAB100225）
     * model3
     *  1 tasks, each task has 7 subtask
     *  1 BOM, over 10 steps, step 1043540 with 60 days * 1 hour per day engineer work， *1 个设备24小时一天？
     *  有的小阶段不需要工程师？
     *  有by day，和not by day，有报告撰写?；一个可用工程师?，没有任何auth?
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanFunctionId100630ModelVersion3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasourceOAT");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);

        constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true);
        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true);
        constraintSelection.put(ObjectiveSumOfStepASize, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.OCTOBER, 30, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100630, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }

    /**
     * Battery Test（100560，有数据，LAB100160）
     * model3
     *  9个task，28个批次
     *
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100560() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.OCTOBER, 30, 0, 5, 12);
        calendar.set(Calendar.MILLISECOND, 0);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT", 100560, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }
    /**
     * Battery Test（100560，有数据，LAB100160）
     * model3
     *  长周期的2个任务单LWT-NEE-10902，LWT-NEE-10937
     *
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100560Long() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.AUGUST, 11, 1, 5, 12);
        calendar.set(Calendar.MILLISECOND, 0);
        Date planTriggeredTime = calendar.getTime();
        ArrayList<String> taskNos = new ArrayList<>();
        //taskNos.add("LWT-NEE-10902");
        taskNos.add("LWT-NEE-10937");
        wholeModelTestWithoutPlanId("datasourceOAT", 100560, true,
                planTriggeredTime, false, taskNos, SOLVE_MODE_BY_SUBTASK_GROUP);
    }

    /**
     * Battery Test（100560，有数据，LAB100160）
     * model3
     *  长周期的2个任务单LWT-NEE-10902，LWT-NEE-10937
     *
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100560LongOneSubtask() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.AUGUST, 11, 0, 5, 12);
        calendar.set(Calendar.MILLISECOND, 0);
        Date planTriggeredTime = calendar.getTime();
        ArrayList<String> taskNos = new ArrayList<>();
        taskNos.add("LWT-NEE-10902-new");
        taskNos.add("LWT-NEE-10937-new");
        wholeModelTestWithoutPlanId("datasourceOAT", 100560, false,
                planTriggeredTime, false, taskNos, SOLVE_MODE_BY_GRANULARITY);
    }
    /**
     * Metal and Fastening（100300，有数据， LAB1005）
     * model3
     *  1 tasks, each task has 7 subtask？
     *  1 BOM, over 10 steps, step 1043540 with 60 days * 1 hour per day engineer work， *1 个设备24小时一天？
     *  有的小阶段不需要工程师？
     *  有by day，和not by day，有报告撰写?；一个可用工程师?，没有任何auth?
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testCreatePlanFunctionId100300ModelVersion3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasourceOAT");
        HashMap<String, Boolean> constraintSelection = new HashMap<>();
        constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
        constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true); //not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);//not tested in this case

        constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd,true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
        constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);

        constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupANotByDayInStepARange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangePresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeSequence, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeInStepARange, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupADayRangeForbidStartEnd, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayPresence, true);//not tested in this case
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAByDayInResourceGroupADayRange, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAForbidStartEnd, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceGroupAAlternativeResource, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceCapacity, true);//

        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceNotByDay, true);
        constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);

        constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
        constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);

        constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true);
        constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true);
        constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true);
        constraintSelection.put(ObjectiveSumOfStepASize, true);

        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.OCTOBER, 30, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        service.createPlanForTest(100300, "1", constraintSelection, false, planTriggeredTime, MODEL_VERSION_3);
    }

    /**
     * Vehicle Emission Test（100401，有数据， LAB100072）
     * model3
     *  44 tasks, 255 subtask
     *  4 BOM
     *  数据问题：缺少BOM，技工人数不足
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100401DataSourceOAT() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 9, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT", 100401, false,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Vehicle Emission Test（100401，有数据， LAB100072）
     * model3
     *  24 tasks, 135 subtask
     *  数据特点：2个小阶段是报告，每个小阶段需要2个工程师
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100401DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 16, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100401, false,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Test（100771，有数据）
     * model3
     *  1 tasks, 1 subtask
     *  数据特点：设备组找不到
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100771DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 16, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100771, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * GMW3172 Test（100591，有数据）
     * model3
     *  1 tasks, 1 subtask
     *  数据特点：没有BOM授权，有小阶段授权
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100591DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 18, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100591, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Non-destructive Testing（100750，有数据）
     * model3
     *  3 tasks, 4 subtask
     *  数据特点：正常
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100750DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 16, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100750, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Test（100409，有数据）
     * model3
     *  39 tasks, ？ subtask
     *  数据特点：
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100409DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 16, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100409, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }
    /**
     * Test（100444，有数据， Structure Lab 1009）
     * model3
     *  5 tasks
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100444DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 16, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100444, false,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }
    /**
     * Test（100630）
     * model3
     *  3 tasks
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100630DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 23, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100630, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Test（100412）
     * model3
     *  3 tasks
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100412DataSourceOAT1116() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 25, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT1116", 100412, true,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Test（62）
     * model3
     *  2 tasks
     *
     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction62DataSourceOAT1116() throws IloException, ParseException, IllegalAccessException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 23, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        ArrayList<String> taskNos = new ArrayList<>();
        taskNos.add("LWT-STR-14874");
        taskNos.add("LWT-STR-14877");
        wholeModelTestWithPlanId("datasourceOAT1116", 62, true,
                planTriggeredTime, false, taskNos, SOLVE_MODE_DEFAULT,"UAT_dataset4_62");
        PatacSchedulingTask schedulingTask = service.getSchedulingTask();
        int functionId = schedulingTask.getEngineProcessedData().getFunctionId();
        exportCsvData();
        resultCompareBetweenDbAndCsv(schedulingTask,functionId, false);
    }

    /**
     * 试验室：Propulsion System DYNO Development Lab
     * 功能块：EE Development  Test
     * 涉及任务单：TOCWT-DEV-11750

     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100414DataSourceOAT1203() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.DECEMBER, 4, 5, 5, 12);
        Date planTriggeredTime = calendar.getTime();
//        ArrayList<String> taskNos = new ArrayList<>();
//        taskNos.add("LWT-STR-14874");
//        taskNos.add("LWT-STR-14877");
        wholeModelTestWithoutPlanId("datasourceOAT1203", 100414, false,
                planTriggeredTime, false, null, SOLVE_MODE_DEFAULT);
    }

    /**
     * 试验室：NEE Lab
     * 功能块：Battery Test
     * 涉及任务单：LWT-NEE-11020

     * @throws IloException
     * @throws ParseException
     */
    @Test
    public void testFunction100560DataSourceOAT1209() throws IloException, ParseException, IllegalAccessException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.DECEMBER, 10, 1, 5, 12);
        Date planTriggeredTime = calendar.getTime();
//        ArrayList<String> taskNos = new ArrayList<>();
//        taskNos.add("LWT-STR-14874");
//        taskNos.add("LWT-STR-14877");
        wholeModelTestWithPlanId("datasourceOAT1209", 100560, true,
                planTriggeredTime, false, null, SOLVE_MODE_DEFAULT,
                "UAT_dataset1_100560");
        PatacSchedulingTask schedulingTask = service.getSchedulingTask();
        int functionId = schedulingTask.getEngineProcessedData().getFunctionId();
        exportCsvData();
        resultCompareBetweenDbAndCsv(schedulingTask,functionId, false);
    }
    /**
     * CWT（65，有数据， Thermal Lab 1010）
     * model3
     *  10 tasks, 22 subtask
     * 数据问题：缺少BOM或者工程师不足问题
     * 有一单可以排出
     *
     * @throws IloException
     * @throws ParseException
     */

    @Test
    public void testFunction65() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.NOVEMBER, 9, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT", 65, false,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Structure Safety（58，有数据， Structure Lab 1009）
     * model3
     *  2 tasks, 4 subtask
     * 数据问题：缺少BOM
     *
     * @throws IloException
     * @throws ParseException
     */

    @Test
    public void testFunction58() throws IloException, ParseException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        // 注意：实际的月份是这里是设置的月份+1
        calendar.set(2022, Calendar.NOVEMBER, 9, 0, 5, 12);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithoutPlanId("datasourceOAT", 58, false,
                planTriggeredTime, false, null, SOLVE_MODE_BY_GRANULARITY);
    }

    /**
     * Chassis Brake（100440，有数据， Structure Lab 1009）
     * model3
     *  6 tasks, 6 subtask
     * 数据特点：按天倍乘
     *
     * @throws IloException
     * @throws ParseException
     */

    @Test
    public void testFunction100440() throws IloException, ParseException, IllegalAccessException {
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022, Calendar.NOVEMBER, 9, 1, 5, 12);
        calendar.set(Calendar.MILLISECOND,0);
        Date planTriggeredTime = calendar.getTime();
        wholeModelTestWithPlanId("datasourceOATUat2", 100440, true,
                planTriggeredTime, false, null, SOLVE_MODE_DEFAULT,"UAT_dataset2_100440");
        PatacSchedulingTask schedulingTask = service.getSchedulingTask();
        int functionId = schedulingTask.getEngineProcessedData().getFunctionId();
        exportCsvData();
        resultCompareBetweenDbAndCsv(schedulingTask,functionId, false);
    }

    public void wholeModelTestWithoutPlanId(String dataSource, int functionId, boolean saveDataFlag,
                               Date planTriggeredTime, boolean modelSelectionFlag, ArrayList<String> taskNos, int solveMode
            ) throws IloException, ParseException {
        wholeModelTestWithPlanId(dataSource, functionId, saveDataFlag,
        planTriggeredTime, modelSelectionFlag, taskNos, solveMode , "1");
    }
    public void wholeModelTestWithPlanId(String dataSource, int functionId, boolean saveDataFlag,
                                            Date planTriggeredTime, boolean modelSelectionFlag, ArrayList<String> taskNos, int solveMode,
            String planId) throws IloException, ParseException {
        HandlerDataSource.putDataSource(dataSource);

        if (saveDataFlag) {
            // 清空数据库
            clearData(functionId);
        }

        if (!modelSelectionFlag){
            service.createPlanForTest(functionId, planId, null, true,
                    planTriggeredTime, MODEL_VERSION_4, saveDataFlag, taskNos, solveMode);
        } else{
            HashMap<String, Boolean> constraintSelection = new HashMap<>();
            constraintSelection.put(ConstantUtil.ConstraintTaskSpanSubtask, true);
            constraintSelection.put(ConstantUtil.ConstraintTaskSubtaskPresence, true);
            constraintSelection.put(ConstantUtil.ConstraintSubtaskGroupSequence, true);

            constraintSelection.put(ConstantUtil.ConstraintStepAForbidStartEnd, true);
            constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskSpanStepA, true);
            constraintSelection.put(ConstantUtil.ConstraintStepARelationSubtaskStepAPresence, true);
            constraintSelection.put(ConstantUtil.ConstraintStepARelationStepASequence, true);
            constraintSelection.put(ConstantUtil.ConstraintStepARelationStepAMaxGap, true);

            constraintSelection.put(ConstantUtil.ConstraintSampleANoOverlap, true);

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
            constraintSelection.put(ConstantUtil.ConstraintResourceAPresenceByDay, true);

            constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskStepA, true);

            constraintSelection.put(ConstantUtil.ConstraintResourceAssignmentRelationSubtaskAssignSameRes, true);

            constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForReportingPhase, true);
            constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTaskAndStepA, true);
            constraintSelection.put(ConstantUtil.ConstraintEngineerAssignmentForTask, true);

            constraintSelection.put(ConstantUtil.ObjectiveLateSubTasks, true);
            constraintSelection.put(ConstantUtil.ObjectiveLateTasks, true);
            constraintSelection.put(ConstantUtil.ObjectiveAdditionalUsedResourceQty, true);
            constraintSelection.put(ConstantUtil.ObjectiveResourceUnbalance, true);
            constraintSelection.put(ObjectiveSumOfStepASize, true);

            constraintSelection.put(LengthMaxOfStepA, true);
            constraintSelection.put(LengthMaxOfResourceA, true);

            constraintSelection.put(ConstraintResourceAInStepARange, true);
            constraintSelection.put(ConstraintResourceABeAssignedQuantity, true);

            service.createPlanForTest(functionId, planId, constraintSelection, false,
                    planTriggeredTime, MODEL_VERSION_4, saveDataFlag, taskNos, solveMode);


        }
        exportCsvData();
    }
}
