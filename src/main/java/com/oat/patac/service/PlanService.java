package com.oat.patac.service;

import com.oat.patac.engine.PatacSchedulingTask;
import ilog.concert.IloException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public interface PlanService {

    // 老模式的接口，to be obsolete
    @Deprecated
    boolean createPlan(Integer functionId, int planMode, ArrayList<String> toBePlannedSubTaskIds) throws IloException, ParseException;

    // 新模式中晓风会调用的接口
    boolean createPlan(Integer functionId, String planId);

    // 用于测试约束，并可以给定排程触发时间
    boolean createPlanForTest(Integer functionId, String planId,
                              HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                              Date planTriggeredTime, String modelVersion) ;

    boolean createPlanForTest(Integer functionId, String planId,
                              HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                              Date planTriggeredTime, String modelVersion, boolean saveDataFlag) ;

    boolean createPlanForTest(Integer functionId, String planId,
                              HashMap<String, Boolean> constraintSelections, Boolean constraintDefaultValue,
                              Date planTriggeredTime, String modelVersion, boolean saveDataFlag,
                              ArrayList<String> taskNos, int solveMode);
    PatacSchedulingTask getSchedulingTask();
}
