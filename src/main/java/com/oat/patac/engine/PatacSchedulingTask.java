package com.oat.patac.engine;

import com.oat.cp.*;
import com.oat.patac.dataAccess.*;
import com.oat.patac.entity.*;
import ilog.concert.IloException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * 排程任务
 */
@Getter
@Setter
@Log4j2
public class PatacSchedulingTask extends SchedulingTask {

    private EngineProcessedData engineProcessedData;

    private DataContainer dataContainer;

    private EntityPlanResult planResult;

    private HashMap<String, SubModel> subModelHashMap;
    private ArrayList<String> sortedSubModelNameList;

    private int solveMode;
    /**
     * 老DB和老模式的构造函数
     * @deprecated
     */
    public PatacSchedulingTask(LoadData loadData, DataContainer dataContainer, int planMode, ArrayList<String> toBePlannedSubTaskIds) {

        this.dataContainer = dataContainer;
        engineProcessedData = new EngineProcessedData(loadData, this, null, 0);
        if (planMode == PLAN_MODE_MANUAL_TRIGGERED){
            engineProcessedData.setToBePlannedSubTaskNos(toBePlannedSubTaskIds);
        }
        planResult = new EntityPlanResult();
    }

    /**
     * 新DB和新模式的构造函数
     */
    public PatacSchedulingTask(LoadData loadData, DataContainer dataContainer, ArrayList<String> taskNos,
                               int solveMode) {

        this.dataContainer = dataContainer;
        engineProcessedData = new EngineProcessedData(loadData,this, taskNos, solveMode);
        //todo: 现在planResult没有什么用，考虑去掉
        planResult = new EntityPlanResult();
        this.solveMode = solveMode;
    }

    /**
     * 新DB和新模式
     * 数据读取和验证
     */
    public boolean loadAndProcessData(){
        // 用于记录数据是是否正确
        boolean flag;

        // Phase1 data include function, task, sub_task, sample, sub_task_plan_input, procedure_table, bom, plan_config_template
        flag = engineProcessedData.loadAndProcessPhase1Data();
        // Phase2 data include step, special_day
        if (flag){
            flag = engineProcessedData.loadAndProcessPhase2Data();
        }
        // Phase3 data include resources related data
        if (flag){
            flag = engineProcessedData.loadAndProcessPhase3Data();
        }
        if (flag){
            // 初始化人员授权
            engineProcessedData.calculateAllPossiblePerson();

            // 跨实体验证，生成 subTask previous
            engineProcessedData.checkMasterData();

            // 做批次组检查
            engineProcessedData.checkSubTaskGroup();

            subModelHashMap = engineProcessedData.getSubModelHashMap();
        }


        return flag;
    }

    public void addInfoToMessages(){
        // 遍历所有 message，给所有的 message 添加 functionId
        ArrayList<EntityMessage> messages = EngineProcessedData.messages;

        if (messages.size() != 0) {
            int i = 1;
            for (EntityMessage msg : messages) {
                msg.setMessageId(i);
                msg.setPlanId(dataContainer.getPlan().getPlanId());
                msg.setMessageDate(new Date());
                msg.setFunctionId(dataContainer.getPlan().getFunctionId());
                i++;
            }
        }
    }

    /**
     * 老DB和老模式
     * 数据读取和验证
     * @deprecated
     */
    public Boolean processAndCheckData() throws ParseException {
       return engineProcessedData.initialize();
    }

    @Override
    public boolean solve(String modelVersion)  {
        prepareData();
        if (subModelHashMap.size()==0){
            log.info("没有需要求解的模型，求解完成");
            return false;
        }
        boolean errorFlag = false;
        int solvedSubModels = 0;
        for (String model : sortedSubModelNameList){
            if (!subModelHashMap.get(model).buildAndSolve(modelVersion)) {
                errorFlag = true;
                //前面失败，后面接着算
                //break;
            } else {
                solvedSubModels++;
            }
        }
        // Sophia todo: 需要判断是否有排程成功的任务单
        if (subModelHashMap.size()> 0 && solvedSubModels == 0){
            log.info("子模型构建或求解过程中发生错误，没有结果输出");

        } else{
            if (errorFlag){
                log.info("部分子模型构建或求解过程中发生错误，正在将已排程结果输出");
            }
            processResults();
        }
        if (solvedSubModels > 0){
            // 需要输出模型结果
            return true;
        } else {
            return false;
        }
    }


    public EntityPlanResult getPlanResult() {
        return this.planResult;
    }

    private void prepareData(){
        if (solveMode == SOLVE_MODE_BY_SUBTASK_GROUP_AND_GRANULARITY){
            engineProcessedData.createSubModelsBySubTaskGroupAndGranularity(false);
        } else if (solveMode ==  SOLVE_MODE_BY_GRANULARITY){
            engineProcessedData.createSubModelsByGranularity(false,
            0,null);
        } else if (solveMode == SOLVE_MODE_BY_TASK || solveMode == SOLVE_MODE_BY_TASK_NOT_SAVE) {
            if (engineProcessedData.getSortedSubTaskGroupHashMap().size() > 0){
                log.info("本次排程中有批次组设置，不能使用按任务单排程模式，变为按批次组拆分子模型的排程模式");
                if (solveMode == SOLVE_MODE_BY_TASK){
                    solveMode = SOLVE_MODE_BY_SUBTASK_GROUP;
                } else {
                    solveMode = SOLVE_MODE_BY_SUBTASK_GROUP_NOT_SAVE;
                }
                engineProcessedData.setSolveMode(solveMode);
                engineProcessedData.createSubModelsBySubTaskGroupAndGranularity(true);
            } else {
                engineProcessedData.createSubModelsByTasksAndGranularity();
            }
        } else if (solveMode == SOLVE_MODE_BY_SUBTASK_GROUP || solveMode == SOLVE_MODE_BY_SUBTASK_GROUP_NOT_SAVE) {
            engineProcessedData.createSubModelsBySubTaskGroupAndGranularity(true);
        }
        subModelHashMap = engineProcessedData.getSubModelHashMap();
        sortSubModels(subModelHashMap, true);

    }



    private void sortSubModels(HashMap<String, SubModel> subModelHashMap, boolean ascOrder) {
        //todo: 有可能需要调整该逻辑
        sortedSubModelNameList = new ArrayList<String>(subModelHashMap.keySet());
        if (ascOrder){
            Collections.sort(sortedSubModelNameList);
        } else {
            Collections.sort(sortedSubModelNameList, Collections.reverseOrder());
        }
    }

    /**
     * 综合各个子模型的结果，比如算出整个任务单的开始结束时间。组装出输出表所需要的信息。
     * 可进行一定的check，比如是否所有需要排程的任务单或者批次都已经进行了排程，或者各个约束是否有违反的情况
     */
    private void processResults() {
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
                        log.info("批次 "  + toBePlannedSubTask.getSubTaskNo() + " 没有进行排程！");
                    }
                }
            }

            // 是否所有toBePlannedTasks都已经有了planStart，planEnd
            if (taskPlanOutputHashMap.containsKey(task.getTaskNo())){
                EntityTaskPlanOutput taskPlanOutput = taskPlanOutputHashMap.get(task.getTaskNo());
                if (taskPlanOutput.getTaskPlanStart() == null || taskPlanOutput.getTaskPlanEnd() == null){
                    log.info("任务单 "  + task.getTaskNo() + " 没有进行排程！");
                }

            }
        }
    }
}
