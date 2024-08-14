package com.oat.patac.engine;

import com.oat.cp.CPModel;
import com.oat.patac.dataAccess.DataContainer;
import com.oat.patac.entity.*;
import ilog.concert.IloException;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class MainModel extends CPModel {
    private DataContainer dataContainer;
    private EngineProcessedData engineProcessedData;
    HashMap<String, SubModel> subModelHashMap;
    ArrayList<String> subModelNameList;

    public MainModel(String id, PatacSchedulingTask task) {
        //暂时使用plan id作为model id
        super(id, task);
        engineProcessedData = task.getEngineProcessedData();
        subModelHashMap = engineProcessedData.getSubModelHashMap();
    }

    public boolean buildAndSolve(String modelVersion) throws IloException {
        prepareData();
        boolean errorFlag = false;
        int solvedSubModels = 0;
        for (String model : subModelNameList){
            if (!subModelHashMap.get(model).buildAndSolve(modelVersion)) {
                errorFlag = true;
                break;
            } else {
                solvedSubModels++;
            }
        }
        if (solvedSubModels == 0){
            log.info("子模型构建或求解过程中发生错误，没有结果输出");
        } else{
            if (errorFlag){
                log.info("部分子模型构建或求解过程中发生错误，正在将已排程结果输出");
            }
            processResults();
        }
        return !errorFlag;
    }

    private void prepareData(){
        sortSubModels(subModelHashMap);
    }

    private void sortSubModels(HashMap<String, SubModel> subModelHashMap){
        //todo: 有可能需要调整该逻辑
        subModelNameList = new ArrayList<String>(subModelHashMap.keySet());
        Collections.sort(subModelNameList);
    }

    @Override
    //todo
    protected void saveConstraintCheckResult() {

    }

    @Override
    /**
     * 综合各个子模型的结果，比如算出整个任务单的开始结束时间。组装出输出表所需要的信息。
     * 可进行一定的check，比如是否所有需要排程的任务单或者批次都已经进行了排程，或者各个约束是否有违反的情况
     */
    protected void processResults() {
        //todo: haolei
        Integer functionId = engineProcessedData.getFunctionId();
        EntityFunction function = engineProcessedData.getFunctionHashMap().get(functionId);

        HashMap<String, EntityTaskPlanOutput> taskPlanOutputHashMap = engineProcessedData.getTaskPlanOutputHashMap();
        HashMap<String, EntitySubTaskPlanOutput> subTaskPlanOutputHashMap = engineProcessedData.getSubTaskPlanOutputHashMap();



        // 整个任务单的开始结束时间，组装出输出表所需要的信息
        for (EntityTask task: function.getToBePlannedTasks()) {

            // 根据任务单是否有ignored前序批次，如果有，取原任务单的start time。
            ArrayList<EntitySubTask> subTasks = task.getSubTasks();
            for (EntitySubTask subTask : subTasks) {
                // todo: 添加判断前序批次的条件
//                if (subTask.isIgnored() && ){
//                    EntityTaskPlanOutput taskPlanOutput = taskPlanOutputHashMap.get(task.getTaskId());
//                    taskPlanOutput.setTaskPlanStart(subTask.getStartTime());
//                }
            }


            // 是否所有 toBePlannedSubTask 都已经有了planStart,planEnd，即进行了排程
            ArrayList<EntitySubTask> toBePlannedSubTasks = task.getToBePlannedSubTasks();
            for (EntitySubTask toBePlannedSubTask : toBePlannedSubTasks) {
                String subTaskNo = toBePlannedSubTask.getSubTaskNo();
                if (subTaskPlanOutputHashMap.containsKey(subTaskNo)){
                    EntitySubTaskPlanOutput subTaskPlanOutput = subTaskPlanOutputHashMap.get(subTaskNo);
                    if (subTaskPlanOutput.getSubTaskPlanStart() == null || subTaskPlanOutput.getSubTaskPlanEnd() == null){
                        log.info("批次"  + toBePlannedSubTask.getSubTaskNo() + "没有进行排程！");
                    }
                }
            }

            // 是否所有toBePlannedTasks都已经有了planStart，planEnd
            if (taskPlanOutputHashMap.containsKey(task.getTaskNo())){
                EntityTaskPlanOutput taskPlanOutput = taskPlanOutputHashMap.get(task.getTaskNo());
                if (taskPlanOutput.getTaskPlanStart() == null || taskPlanOutput.getTaskPlanEnd() == null){
                    log.info("任务单"  + task.getTaskNo() + "没有进行排程！");
                }

            }
        }
    }

    @Override
    //todo
    protected void addObjective() {

    }
}
