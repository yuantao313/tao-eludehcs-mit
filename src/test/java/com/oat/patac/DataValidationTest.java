package com.oat.patac;

import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.engine.PatacSchedulingTask;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.oat.common.utils.ConstantUtil.*;
import static com.oat.common.utils.DateUtil.TIME_ZONE;

/**
 * @author:yhl
 * @create: 2022-08-25 14:30
 * @Description: 数据验证测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class DataValidationTest extends PatacTest {

    @After
    public void tearDown() throws Exception {
        PatacSchedulingTask schedulingTask = service.getSchedulingTask();
        int functionId = schedulingTask.getEngineProcessedData().getFunctionId();
        exportCsvData();
        resultCompareBetweenDbAndCsv(schedulingTask,functionId, true);
    }

    @Test
    public void dataCheckTest_01() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest1");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_01", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }

    @Test
    public void dataCheckTest_02() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest2");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_02", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }

    @Test
    public void dataCheckTest_03() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest3");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_03", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }

    @Test
    public void dataCheckTest_04() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest4");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_04", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }

    @Test
    public void dataCheckTest_05() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest5");
        // 根据test case的设计设置时间chu
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_05", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }

    @Test
    public void dataCheckTest_06() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest6");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_06", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }

    @Test
    public void dataCheckTest_07() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>START TO RUN TEST CASE "
                + Thread.currentThread().getStackTrace()[1].getMethodName());
        HandlerDataSource.putDataSource("datasourceDataCheckTest7");
        // 根据test case的设计设置时间
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.set(2022,  Calendar.OCTOBER, 5, 0, 0, 0);
        Date planTriggeredTime = calendar.getTime();
        clearData(1);
        service.createPlanForTest(1, "dataCheck_07", null, true,
                planTriggeredTime, MODEL_VERSION_4, true);
    }
}
