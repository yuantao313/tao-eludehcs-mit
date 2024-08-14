package com.oat.patac;

import com.oat.common.utils.ConstantUtil;
import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.dataAccess.DataContainer;
import com.oat.patac.dataAccess.LoadData;
import com.oat.patac.engine.EngineProcessedData;
import com.oat.patac.engine.PatacSchedulingTask;
import com.oat.patac.entity.EntityPlan;
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

import static com.oat.common.utils.ConstantUtil.MODEL_VERSION_3;
import static com.oat.common.utils.ConstantUtil.SOLVE_MODE_BY_GRANULARITY;
import static com.oat.common.utils.DateUtil.TIME_ZONE;

/**
 * @author:yhl
 * @create: 2022-11-14 14:14
 * @Description: 数据验证测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
@Log4j2
public class DataCheckTest {

    @Resource
    private LoadData loadData;

    @Test
    public void dataCheckTest_01_function1() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest1");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());


        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();
        engineProcessedData.checkSubTaskGroup();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }

    @Test
    public void dataCheckTest_01_function2() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest1");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());


        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();
        engineProcessedData.checkSubTaskGroup();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }

    @Test
    public void dataCheckTest_02() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest2");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }

    @Test
    public void dataCheckTest_03() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest3");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }

    @Test
    public void dataCheckTest_04() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest4");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }

    @Test
    public void dataCheckTest_05() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest5");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }

    @Test
    public void dataCheckTest_06() {
        log.info("数据验证开始...");
        HandlerDataSource.putDataSource("datasourceDataCheckTest6");
        //从数据库获得数据
        DataContainer dataContainer = loadData.initializeDataContainer(1, "1");

        //创建排程任务
        PatacSchedulingTask schedulingTask = new PatacSchedulingTask(loadData, dataContainer, null, SOLVE_MODE_BY_GRANULARITY);
        boolean result = false;
        EntityPlan plan = schedulingTask.getEngineProcessedData().getPlan();
//        plan.setPlanTriggeredTime(new Date());
        plan.initialize(new Date());

        EngineProcessedData engineProcessedData = schedulingTask.getEngineProcessedData();
        // 用于记录数据是是否正确
        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day

        engineProcessedData.loadAndProcessPhase2Data();

        engineProcessedData.loadAndProcessPhase3Data();


        // 跨实体验证，生成 subTask previous
        engineProcessedData.checkMasterData();

        // 初始化人员授权
        engineProcessedData.calculateAllPossiblePerson();

        result = schedulingTask.loadAndProcessData();


        log.info("数据验证结果：" + result);
    }
}
