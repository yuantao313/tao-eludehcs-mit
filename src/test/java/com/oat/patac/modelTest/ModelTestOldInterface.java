package com.oat.patac.modelTest;

import com.oat.common.utils.datasource.HandlerDataSource;
import com.oat.patac.service.PlanService;
import ilog.concert.IloException;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.ParseException;

import static com.oat.common.utils.ConstantUtil.PLAN_MODE_AUTOMATIC;

public class ModelTestOldInterface {
    @Resource
    private PlanService service;

    @Test
    @Deprecated
    public void testCreatePlan() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource2");
        service.createPlan(1, PLAN_MODE_AUTOMATIC, null);
    }

    @Test
    @Deprecated
    public void testCreatePlan2() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource3");
        service.createPlan(1, PLAN_MODE_AUTOMATIC, null);
    }

    /**
     * tims-oat 数据库
     * @throws IloException
     * @throws ParseException
     */
    @Test
    @Deprecated
    public void testCreatePlan3() throws IloException, ParseException {
        HandlerDataSource.putDataSource("datasource4");
        service.createPlan(65, PLAN_MODE_AUTOMATIC, null);
    }
}
