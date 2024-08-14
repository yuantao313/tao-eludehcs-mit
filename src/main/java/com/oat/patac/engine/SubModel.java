package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.cp.SchedulingTask;
import com.oat.patac.entity.*;
import ilog.concert.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;

@Log4j2
// todo：测试用
@Getter
public class SubModel extends CPModel {

    private final SubModelData subModelData;
    public SubModelData getSubModelData() {
        return subModelData;
    }
    private final String subModelName;
    public String getSubModelName() {
        return subModelName;
    }

    private boolean subModelSolveSuccess = true;

    //todo: 最大时间轴，需要数据验证
    //private static final int END_MAX = 60*92;

    private Var1DArray ivTask = new Var1DArray(VARIABLE_TYPE_INTERVAL, "ivTask");
    public Var1DArray getIvTask() {return ivTask;    }

    private Var2DArray ivResourceAFixed = new Var2DArray(VARIABLE_TYPE_INTERVAL, "ivResourceAFixed");
    public Var2DArray getIvResourceAFixed(){
        return ivResourceAFixed;
    }

    private Var2DArray bvTaskRespEngineer = new Var2DArray(VARIABLE_TYPE_BINARY, "bvTaskRespEngineer");
    public Var2DArray getBvTaskRespEngineer() {return bvTaskRespEngineer;    }

    private Var2DArray ivSubTask = new Var2DArray(VARIABLE_TYPE_INTERVAL, "ivSubTask");
    public Var2DArray getIvSubTask() {return ivSubTask;    }

    private Var3DArray ivStepA = new Var3DArray(VARIABLE_TYPE_INTERVAL, "ivStepA");
    public Var3DArray getIvStepA() {return ivStepA;      }
    /**
     * 不按天的资源组活动变量（added from model1.3）
     */
    private Var4DArray ivResourceGroupANotByDay = new Var4DArray(VARIABLE_TYPE_INTERVAL, "ivResourceGroupANotByDay");
    public Var4DArray getIvResourceGroupANotByDay() {  return ivResourceGroupANotByDay;    }
    /**
     * 按天的资源组活动每工作天的范围变量（added from model1.3）
     */
    private Var5DArray ivResourceGroupADayRange = new Var5DArray(VARIABLE_TYPE_INTERVAL, "ivResourceGroupADayRange");
    public Var5DArray getIvResourceGroupADayRange() {    return ivResourceGroupADayRange;   }

    /**
     * 按天的资源组活动每工作天的变量（added from model1.3）
     */
    private Var5DArray ivResourceGroupAByDay = new Var5DArray(VARIABLE_TYPE_INTERVAL, "ivResourceGroupAByDay");
    public Var5DArray getIvResourceGroupAByDay() {    return ivResourceGroupAByDay;    }
    private Var4DArray bvSubtaskResource = new Var4DArray(VARIABLE_TYPE_INTERVAL, "bvSubtaskResource");

    public Var4DArray getBvSubtaskResource() {return bvSubtaskResource;     }
//    private Var4DArray bvSubtaskResourceForFixedResourceGroup = new Var4DArray(VARIABLE_TYPE_INTERVAL, "bvSubtaskResource");
//    public Var4DArray getBvSubtaskResourceForFixedResourceGroup() {
//        return bvSubtaskResourceForFixedResourceGroup;
//    }

    private Var5DArray ivResourceASpanDays = new Var5DArray(VARIABLE_TYPE_INTERVAL, "ivResourceASpanDays");
    public Var5DArray getIvResourceASpanDays(){
        return ivResourceASpanDays;
    }

    private Var5DArray bvStepAResGroupRes = new Var5DArray(VARIABLE_TYPE_BINARY, "bvStepAResGroupRes");
    public Var5DArray getBvStepAResGroupRes() {return bvStepAResGroupRes;       }

    public Var5DArray ivResourceANotByDay = new Var5DArray(VARIABLE_TYPE_BINARY, "ivResourceANotByDay");

    public Var5DArray getIvResourceANotByDay() {return ivResourceANotByDay;  }
    private Var6DArray ivResourceAByDay = new Var6DArray(VARIABLE_TYPE_INTERVAL, "ivResourceAByDay");
    public Var6DArray getIvResourceAByDay(){
        return ivResourceAByDay;
    }

    private Var1DArray statefResource = new Var1DArray(VARIABLE_TYPE_STATE_FUNCTION, "statefResource");
    public Var1DArray getStatefResource(){
        return statefResource;
    }

//    private Var1DArray staffCumulFunctionExpression = new Var1DArray(VARIABLE_TYPE_CUMUL_FUNCTION_EXPRESSION,"staffCumulfeResource");
//    public Var1DArray getStaffCumulFunctionExpression() {return staffCumulFunctionExpression; }
//    private Var1DArray equipmentCumulFunctionExpression = new Var1DArray(VARIABLE_TYPE_CUMUL_FUNCTION_EXPRESSION,"equipmentCumulfeResource");
//    public Var1DArray getEquipmentCumulFunctionExpression() {return equipmentCumulFunctionExpression; }

    private Var1DArray cumulFunctionExpression = new Var1DArray(VARIABLE_TYPE_CUMUL_FUNCTION_EXPRESSION,"cumulFunctionExpression");

    public Var1DArray getCumulFunctionExpression() {return cumulFunctionExpression; }

    /**
     * 多少optional的任务单没有被排进去的表达式，在目标函数中会minimize这个表达式
     */
    private IloIntExpr exprOptionalTasks;

    public void setExprOptionalTasks(IloIntExpr exprOptionalTasks) {
        this.exprOptionalTasks = exprOptionalTasks;
    }

    public void setExprLateSubtasks(IloIntExpr exprLateSubtasks) {
        this.exprLateSubtasks = exprLateSubtasks;
    }

    public void setExprLateTasks(IloNumExpr exprLateTasks) {
        this.exprLateTasks = exprLateTasks;
    }

    public void setExprAdditionalUsedResourceQty(IloIntExpr exprAdditionalUsedResourceQty) {
        this.exprAdditionalUsedResourceQty = exprAdditionalUsedResourceQty;
    }

    public void setExprResourceUsage(Var1DArray exprResourceUsage) {
        this.exprResourceUsage = exprResourceUsage;
    }

    public void setExprResourceUnbalance(IloNumExpr exprResourceUnbalance) {
        this.exprResourceUnbalance = exprResourceUnbalance;
    }

    /**
     * 比“试验期望完成时间“晚结束的时长之和，在目标函数中会minimize这个表达式
     */
    private IloIntExpr exprLateSubtasks;
    /**
     * 任务单计划完成时间和任务单”审批完成时间“的差，在目标函数中会minimize这个表达式
     */
    private IloNumExpr exprLateTasks;
    /**
     * 对小阶段使用相同资源组的批次，每个批次在每个资源组中使用资源的数量，在目标函数中会minimize这个表达式
     */
    private IloIntExpr exprAdditionalUsedResourceQty;
    /**
     * 每个人员的工作量
     */
    private Var1DArray exprResourceUsage = new Var1DArray(VARIABLE_TYPE_EXPRESSION,"exprResourceUsage");

    public IloIntExpr getExprSumOfStepASize() {
        return exprSumOfStepASize;
    }

    public void setExprSumOfStepASize(IloIntExpr exprSumOfStepASize) {
        this.exprSumOfStepASize = exprSumOfStepASize;
    }

    /**
     * 每个小阶段的size
     */
    private IloIntExpr exprSumOfStepASize;

    public IloIntExpr getExprOptionalTasks() {
        return exprOptionalTasks;
    }

    public IloIntExpr getExprLateSubtasks() {
        return exprLateSubtasks;
    }

    public IloNumExpr getExprLateTasks() {
        return exprLateTasks;
    }

    public IloIntExpr getExprAdditionalUsedResourceQty() {
        return exprAdditionalUsedResourceQty;
    }

    public Var1DArray getExprResourceUsage() {
        return exprResourceUsage;
    }

    public IloNumExpr getExprResourceUnbalance() {
        return exprResourceUnbalance;
    }

    /**
     * 每个人员被分配的多余平均值的工作量之和，在目标函数中会minimize这个表达式
     */
    private IloNumExpr exprResourceUnbalance;
    /**
     * 总的目标函数
     */
    private IloNumExpr objective;


    /**
     *
     * @param id
     * @param task
     * @param subModelName
     */

    public SubModel(String id, SchedulingTask task, String subModelName) {
        super(id, task);
        this.subModelName = subModelName;

        PatacSchedulingTask patacSchedulingTask = (PatacSchedulingTask) this.getTask();
        
        subModelData = new SubModelData(subModelName, patacSchedulingTask);
        log.info("New sub model "+ subModelName + " is created." );

    }



    public boolean buildAndSolve(String modelVersion)  {
        log.info("Start to build and solve sub model " + subModelName);
        if (!subModelData.prepareData()){
            // 没有待排程批次
            return false;
        }
        buildModel(modelVersion);
        setTimeLimit(subModelData.getMaxTimeLimit());
        boolean solveSucceed;
        try{
            solveSucceed = solveModel();
        } catch (IloException e){
            solveSucceed = false;
            e.printStackTrace();
            log.error("Sub model " + subModelName + " failed. Exit solving process." );
        }
        if(solveSucceed){
            int solveMode = subModelData.getPatacSchedulingTask().getSolveMode();
            if (solveMode != SOLVE_MODE_BY_TASK_NOT_SAVE
                && solveMode!= SOLVE_MODE_BY_SUBTASK_GROUP_NOT_SAVE) {
                try {
                    processResults();
                } catch(IloException e){
                    log.error("Sub model " + subModelName + " solved successfully, but processing result failed. Exit solving process.");
                }
            }
            log.info("Sub model " + subModelName + " is succeeded.");
            return true;
        } else {
            getTask().setFailed(true);
            return false;
        }
    }

    private void buildModel(String modelVersion) {
        if (modelVersion == null){
            modelVersion = DEFAULT_MODEL_VERSION;
        }
        // 任务单 span 批次的约束
        ConstraintTaskSpanSubtask constraintTaskSpanSubtask = new ConstraintTaskSpanSubtask(this);
        // 对optional的任务单，任务单和批次的排入状态应该一致
        ConstraintTaskSubtaskPresence constraintTaskSubtaskPresence = new ConstraintTaskSubtaskPresence(this);
        // 批次组顺序约束
        ConstraintSubtaskGroupSequence constraintSubtaskGroupSequence = new ConstraintSubtaskGroupSequence(this);

        // 小阶段活动和批次活动的关系，以及小阶段活动的顺序要求
        ConstraintStepARelations constraintStepARelations = new ConstraintStepARelations(this);
        // 非共享样件（整车）同一时间只能支持一个批次活动
        if (modelVersion.equals(MODEL_VERSION_2) || modelVersion.equals(MODEL_VERSION_3)) {
            ConstraintSampleANoOverlap constraintSampleANoOverlap = new ConstraintSampleANoOverlap(this);
        } else {
            ConstraintSampleANoOverlapV2 constraintSampleANoOverlapV2 = new ConstraintSampleANoOverlapV2(this);
        }

        // 资源活动0、1变量和interval变量的关系，资源活动和小阶段活动的关系，以及资源活动之间的关系
        if(modelVersion.equals(MODEL_VERSION_2)) {
            ConstraintResourceARelations constraintResourceARelations = new ConstraintResourceARelations(this);
        } else if (modelVersion.equals(MODEL_VERSION_3)){
            ConstraintResourceGroupAResourceARelations constraintResourceGroupAResourceARelations = new ConstraintResourceGroupAResourceARelations(this);
        } else {
            ConstraintResourceARelationsV2 constraintResourceARelations = new ConstraintResourceARelationsV2(this);
        }
        // 资源容量限制
        if (modelVersion.equals(MODEL_VERSION_2) || modelVersion.equals(MODEL_VERSION_3)) {
            ConstraintResourceCapacity constraintResourceCapacity = new ConstraintResourceCapacity(this);
        } else {
            ConstraintResourceCapacityV2 constraintResourceCapacity = new ConstraintResourceCapacityV2(this);
        }
        // 有状态要求的资源，已经排好的活动需要设置为相应的状态
        if (modelVersion.equals(MODEL_VERSION_2) || modelVersion.equals(MODEL_VERSION_3)) {
            ConstraintResourceAFixedState constraintResourceAFixedState = new ConstraintResourceAFixedState(this);
        }else {
            ConstraintResourceAFixedStateV2 constraintResourceAFixedState = new ConstraintResourceAFixedStateV2(this);
        }
        // 批次中使用的资源和小阶段使用的资源的关系，以及固定设备约束
        ConstraintResourceAssignmentRelationBetweenSubtaskAndStepA constraintResourceAssignmentRelationBetweenSubtaskAndStepA = new ConstraintResourceAssignmentRelationBetweenSubtaskAndStepA(this);
        // 任务单责任工程师和批次、小阶段的工程师约束
        ConstraintEngineerAssignmentForTaskAndSubtask constraintEngineerAssignmentForTaskAndSubtask = new ConstraintEngineerAssignmentForTaskAndSubtask(this);

        // 被 ConstraintStepARelations 包含了：
        //ConstraintSubtaskStepAPresence constraintSubtaskStepAPresence = new ConstraintSubtaskStepAPresence(this);
        //ConstraintStepASequence constraintStepASequence = new ConstraintStepASequence(this);
        //ConstraintStepAMaxGap constraintStepAMaxGap = new ConstraintStepAMaxGap(this);

        // 被 ConstraintResourceARelationships 替换了：
        // ConstraintStepAAssignResGroupRes constraintStepAAssignResGroupRes = new ConstraintStepAAssignResGroupRes(this);
        // ConstraintResourceAPresenceNotByDay constraintResourceAPresenceNotByDay = new ConstraintResourceAPresenceNotByDay(this);
        // ConstraintStepAStartBeforeResANotByDay constraintStepAStartBeforeResANotByDay = new ConstraintStepAStartBeforeResANotByDay(this);
        // ConstraintResourceAPresenceByDay constraintResourceAPresenceByDay = new ConstraintResourceAPresenceByDay(this);
        // ConstraintResASpanDays constraintResASpanDays = new ConstraintResASpanDays(this);

        // 被 ConstraintEngineerAssignmentForTaskAndStepA 包含了：
        // ConstraintTaskAssignEngineer constraintTaskAssignEngineer = new ConstraintTaskAssignEngineer(this);

         //addAllConstraints();
    }


    private void addAllConstraints() {
    }

    @Override
    protected void saveConstraintCheckResult() {

    }

    /**
     * 如果子模型成功了，update
     * main_model.[plan_t].[st].is_planned, main_model.[plan_t].[st].planned_submodel_name
     * main_model.[plan_t].[planned_st], main_model.[planned_t],
     * main_model.[planned_t].assigned_resp_engineer_id, main_model.[planned_t].possible_resp_engineer_ids
     * 如果子模型失败了，退出排程
     */

    @Override
    protected void processResults() throws IloException {

        //IloIntervalVar[] intervalVars = cp.getAllIloIntervalVars();
        //HashSet intervalVarsSet = new HashSet(Arrays.asList(intervalVars));

        // 获取相应的数据
        EngineProcessedData engineProcessedData = subModelData.getEngineProcessedData();
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getSubTaskMap();
        String planId = engineProcessedData.getDataContainer().getPlan().getPlanId();
        HashMap<String, EntityTaskPlanOutput> taskPlanOutputHashMap = engineProcessedData.getTaskPlanOutputHashMap();
        HashMap<String, EntitySubTaskPlanOutput> subTaskPlanOutputHashMap = engineProcessedData.getSubTaskPlanOutputHashMap();
        HashMap<String, EntityTaskPlanInput> taskPlanInputHashMap = engineProcessedData.getTaskPlanInputHashMap();

        int planGranularity = subModelData.getPlanGranularity();
        Date minPlanStartTime = subModelData.getMinPlanStartTime();



        // 遍历子模型中要排程的 task 和 subtask
        for (Map.Entry<String, ArrayList<EntitySubTask>> entry : subModelData.getToBePlannedSubTaskMap().entrySet()) {

            String taskNo = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            HashMap<String, EntityTask> taskHashMap = engineProcessedData.getTaskHashMap();
            EntityTask task = taskHashMap.get(taskNo);
            Integer functionId = engineProcessedData.getFunctionId();
            EntityFunction function = engineProcessedData.getFunctionHashMap().get(functionId);


            // 判断 task 是否排程成功， 设置属性 isPresence 的值
            IloIntervalVar taskVariable = (IloIntervalVar) getIvTask().getVariable(taskNo);
            if (!cp.isPresent(taskVariable)) {
                task.setIsPresence(false);
                task.setIsToBePlanned(false);
            } else {
                task.setIsPresence(true);
            }
//            else {
//                // 给task里面的这次排程的subtask赋值is planned
//                for (EntitySubTask subTask : subTasks) {
//                    IloIntervalVar subTaskVar = (IloIntervalVar) ivSubTask.getVariable(subTask.getTaskNo(), subTask.getSubTaskNo());
//                    if (cp.isPresent(subTaskVar)) {
//                        subTask.setPlanned(true);
//                    } else {
//                        // 程序内部逻辑错误，如果task被分配了，subtask应该也被分配了
//                    }
//                }
//            }
        }
        // 根据批次组再循环，去掉因为批次组前序批次排程不成功，导致后续不能排程的情况
        HashMap<String, ArrayList<EntitySubTask>> validSortedSubTaskGroupHashMap
                = engineProcessedData.getValidSortedSubTaskGroupHashMap(engineProcessedData.getSortedSubTaskGroupHashMap(),
                "排程未成功");
        engineProcessedData.setSortedSubTaskGroupHashMap(validSortedSubTaskGroupHashMap);

        // 遍历子模型中要排程的 task 和 subtask
        for (Map.Entry<String, ArrayList<EntitySubTask>> entry : subModelData.getToBePlannedSubTaskMap().entrySet()) {

            String taskNo = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            HashMap<String, EntityTask> taskHashMap = engineProcessedData.getTaskHashMap();
            EntityTask task = taskHashMap.get(taskNo);
            if (!task.getIsToBePlanned()){
                continue;
            }
            Integer functionId = engineProcessedData.getFunctionId();
//            // task内的所有的批次都被排好了，才把这个任务单加到function.plannedTasks里面
//            Boolean flag = true;
//            for (EntitySubTask toBePlannedSubTask : task.getToBePlannedSubTasks()) {
//                if (!toBePlannedSubTask.isPlanned()){
//                    flag = toBePlannedSubTask.isPlanned();
//                    break;
//                }
//            }
//            if (flag){
//                function.getPlannedTasks().add(task);
//            }

            // 创建 taskPlanOutput 对象
            IloIntervalVar taskVar = (IloIntervalVar) ivTask.getVariable(taskNo);
            int taskStart = cp.getStart(taskVar) * planGranularity;
            int taskEnd = cp.getEnd(taskVar) * planGranularity;

            // 模型中 task interval 变量中的开始时间和结束时间
            Date taskIvStartTime = DateUtil.addMinute(minPlanStartTime, taskStart);
            Date taskIvEndTime = DateUtil.addMinute(minPlanStartTime, taskEnd);

            // taskPlanOutputHashMap 中有则取出，没有则创建一个放进去再返回该对象
            EntityTaskPlanOutput taskPlanOutput = taskPlanOutputHashMap.computeIfAbsent(taskNo, key -> new EntityTaskPlanOutput());
            Date taskPlanStart = taskPlanOutput.getTaskPlanStart();
            Date taskPlanEnd = taskPlanOutput.getTaskPlanEnd();

            // 给 taskPlanOutput 的开始和结束时间赋初始值（子模型第一次遍历到这个 task）
            // 如果 task 有对应的 taskPlanInput ，则将 input 的开始时间付给 taskPlanOutput
            if (taskPlanStart == null){
                EntityTaskPlanInput taskPlanInput = taskPlanInputHashMap.get(taskNo);
                if (taskPlanInput != null){
                    taskPlanStart = taskPlanInput.getTaskPlanStart();
                    taskPlanOutput.setTaskPlanStart(taskPlanStart);
                }
            }
            // taskPlanOutput 的结束时间设置为已经 fixed 的批次的最晚结束时间
            if (taskPlanEnd == null){
                ArrayList<EntitySubTask> allSubTasks = subTaskMap.get(taskNo);
                Date maxEndTime = null;
                for (EntitySubTask subTask : allSubTasks) {
                    if (subTask.isFixed()){
                        if (maxEndTime == null || subTask.getEndTime().after(maxEndTime)){
                            maxEndTime = subTask.getEndTime();
                        }
                    }
                }
                taskPlanEnd = maxEndTime;
                taskPlanOutput.setTaskPlanEnd(maxEndTime);
            }

            // 比较并给 taskPlanOutput 的开始和结束赋值
            if (taskPlanStart == null || taskIvStartTime.before(taskPlanStart)){
                taskPlanOutput.setTaskPlanStart(taskIvStartTime);
            }
            if (taskPlanEnd == null || taskIvEndTime.after(taskPlanEnd)){
                taskPlanOutput.setTaskPlanEnd(taskIvEndTime);
            }

            taskPlanOutput.setTaskNo(task.getTaskNo());

            // 添加任务单责任人
            for (EntityStaff possibleRespEngineer : task.getPossibleRespEngineers()) {
                String staffId = possibleRespEngineer.getStaffId();
                IloIntVar taskRespEngineerVar = (IloIntVar) bvTaskRespEngineer.getVariable(taskNo, staffId);
                double varValue = 0 ;
                try {
                    varValue = cp.getValue(taskRespEngineerVar);
                } catch(Exception e){
                    varValue = 0;
                }
                if (varValue == 1.0){
                    taskPlanOutput.setRespEngineerId(staffId);
                    // 更新已被分配的责任工程师
                    task.setAssignedRespEngineer(engineProcessedData.getEngineerHashMap().get(staffId));
                    ArrayList<EntityStaff> updatedPossibleRespEngineerList = new ArrayList<>();
                    updatedPossibleRespEngineerList.add(engineProcessedData.getStaffHashMap().get(staffId));
                    task.setPossibleRespEngineers(updatedPossibleRespEngineerList);
                    break;
                }
            }

            taskPlanOutput.setFunctionId(task.getFunctionId());
            taskPlanOutput.setPlanId( planId+ "");

            taskPlanOutputHashMap.put(task.getTaskNo(), taskPlanOutput);

            // 获取决策变量结果的处理
            for (EntitySubTask subTask : subTasks) {
                //log.info("Getting result of sub task " + subTask.getSubTaskNo());
                // 已经被子模型排好程了
                subTask.setPlanned(true);
                // 设置该批次进行排程的子模型的名字
                subTask.setPlannedSubModelName(subModelName);

                // 创建 subTaskPlanOutput 对象
                String subTaskNo = subTask.getSubTaskNo();
                IloIntervalVar subTaskVar = (IloIntervalVar) ivSubTask.getVariable(taskNo, subTaskNo);

                // 从 cp 模型中取出开始和结束时间
                int subStart = cp.getStart(subTaskVar) * planGranularity;
                int subEnd = cp.getEnd(subTaskVar) * planGranularity;

                // 将模型中的时间换算成真实的时间
                Date subStartTime = DateUtil.getPreciseTime(DateUtil.addMinute(minPlanStartTime, subStart));
                Date subEndTime = DateUtil.getPreciseTime(DateUtil.addMinute(minPlanStartTime, subEnd));

                subTask.setStartTime(subStartTime);
                subTask.setEndTime(subEndTime);

                // 创建 SubTaskPlanOutput 对象
                EntitySubTaskPlanOutput subTaskPlanOutput = new EntitySubTaskPlanOutput();

                subTaskPlanOutput.setSubTaskNo(subTask.getSubTaskNo());
                subTaskPlanOutput.setSubTaskPlanStart(subStartTime);
                subTaskPlanOutput.setSubTaskPlanEnd(subEndTime);
                subTaskPlanOutput.setSubTaskStartInModel(subStartTime);
                subTaskPlanOutput.setSubTaskEndInModel(subEndTime);
                if (task.getAssignedRespEngineer() != null){
                    // 批次负责人赋值
                    String staffId = task.getAssignedRespEngineer().getStaffId();
                    // 更新已被分配的责任工程师
                    subTask.setAssignedRespEngineerId(staffId);
                    subTaskPlanOutput.setRespEngineerId(staffId);
                }
                subTaskPlanOutput.setFunctionId(functionId);
                subTaskPlanOutput.setPlanId(planId + "");
                subTaskPlanOutput.setTaskNo(taskNo);
                subTaskPlanOutput.setTaskGroup(subTask.getTaskGroup());
                subTaskPlanOutput.setTaskSeqNo(subTask.getTaskSeqNo());
                subTaskPlanOutput.setSampleGroupId(subTask.getSampleGroupId());
                subTaskPlanOutput.setSampleNo(subTask.getUniqueSampleNo());

                subTaskPlanOutputHashMap.put(subTaskPlanOutput.getSubTaskNo(), subTaskPlanOutput);

                // 将 submodel 中的 "整车" 记录到
                HashMap<String, ArrayList<EntitySubTaskPlan>> uniqueSampleSupportedSubtaskPlans = engineProcessedData.getUniqueSampleSupportedSubtaskPlans();
                String uniqueSampleNo = subTask.getUniqueSampleNo();
                if (uniqueSampleNo != null && uniqueSampleNo != ""){
                    ArrayList<EntitySubTaskPlan> subTaskPlans = uniqueSampleSupportedSubtaskPlans.computeIfAbsent(uniqueSampleNo, k -> new ArrayList<>());

                    subTaskPlans.add(subTaskPlanOutput);
                }

                // 更新step plan output的信息
                ArrayList<EntityStepActivity> stepActivities = subTask.getStepActivities();
                for (EntityStepActivity stepActivity : stepActivities) {
                    //log.info("Getting result of step " + stepActivity.getStep().getStepId());
                    EntityStep step = stepActivity.getStep();
                    EntityStepPlanOutput stepPlanOutput = new EntityStepPlanOutput();

                    Integer stepId = stepActivity.getStep().getStepId();
                    // 从模型中获取决策变量
                    IloIntervalVar stepAVar = (IloIntervalVar) ivStepA.getVariable(taskNo, subTaskNo, stepId);
                    int stepAStart;
                    // todo: production中不需要检查这个。。。
//                    if(!intervalVarsSet.contains(stepAVar)){
//                        continue;
//                    }
                    try {
                        // 尝试获得该变量
                        stepAStart = cp.getStart(stepAVar) * planGranularity;
                    } catch (Exception e){
                        continue;
                    }

                    // 获取对应的开始结束时间
//                    int stepAStart = cp.getStart(stepAVar) * planGranularity;
                    int stepAEnd = cp.getEnd(stepAVar) * planGranularity;
                    Date stepAStartTime = DateUtil.getPreciseTime(DateUtil.addMinute(minPlanStartTime, stepAStart));
                    Date stepAEndTime = DateUtil.getPreciseTime(DateUtil.addMinute(minPlanStartTime, stepAEnd));

                    String stepPlanId = subTaskNo + "|" + subTask.getBomNo() + "|" + stepId;

                    stepPlanOutput.setStepPlanId(stepPlanId);
                    stepPlanOutput.setPlanId(planId + "");
                    stepPlanOutput.setFunctionId(functionId);
                    stepPlanOutput.setStepId(stepId);
                    stepPlanOutput.setStepPlanStart(stepAStartTime);
                    stepPlanOutput.setStepPlanEnd(stepAEndTime);
                    stepPlanOutput.setSubTaskNo(subTaskNo);
                    stepPlanOutput.setBomNo(subTask.getBomNo());

                    double reqProcTime = stepActivity.getSize() * planGranularity / 60;
                    double planDuration = (double) cp.getLength(stepAVar) * planGranularity / 60;
                    double maxReqSize = stepActivity.getMaxSizeConsideredResources() * planGranularity / 60;

                    stepPlanOutput.setReqProcTime(new BigDecimal(reqProcTime));
                    stepPlanOutput.setPlanDuration(new BigDecimal(planDuration));
                    stepPlanOutput.setPlanProcTime(new BigDecimal(maxReqSize));

                    HashMap<String, EntityStepPlanOutput> stepPlanOutputHashMap = engineProcessedData.getStepPlanOutputHashMap();
                    stepPlanOutputHashMap.put(stepPlanOutput.getStepPlanId(), stepPlanOutput);

                    // todo：staff/equipment plan output
                    HashMap<String, ArrayList<Object>> possibleResources = stepActivity.getPossibleResources();
                    for (Map.Entry<String, ArrayList<Object>> resourceGroupMap : possibleResources.entrySet()) {

                        String resourceGroupName = resourceGroupMap.getKey();
                        ArrayList<Object> resources = resourceGroupMap.getValue();
                        //log.info("Getting result of resource group " + resourceGroupName);
                        // 根据资源组名获得资源组对象
                        EntityResourceGroup resourceGroup = step.getResourceGroupHashMap().get(resourceGroupName);
                        String resourceType = resourceGroup.getResourceType();
                        // 获取资源组中的工作模式
                        String dailyMode = resourceGroup.getRequestedDailyMode();
                        // 获取资源组中的请求资源天数
                        int resDayQty = resourceGroup.getRequestedDayQuantity();
                        if (step.isRepeat() &&
                                resourceGroup.getRequestedDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
                            resDayQty = resDayQty * stepActivity.getValidSampleQuantity();
                        }
                        int resourceSize = resources.size();
                        // 遍历资源
                        for (int i = 0; i < resourceSize; i++) {
                            // 资源对应实体的Id
                            String resourceId = "";
                            //创建cumul function expression的key,用resource type 和 resource Id作为key
//                            String cumulfeResourceId = "";

                            Object resource = resources.get(i);


                            EntityStaff staff = null;
                            EntityEquipment equipment = null;
                            if (RESOURCE_TYPE_ENGINEER.equals(resourceType)
                                    || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)) {
                                staff = (EntityStaff) resources.get(i);
                                resourceId = staff.getStaffId();
                          } else if (RESOURCE_TYPE_EQUIPMENT.equals(resourceType)) {
                                equipment = (EntityEquipment) resources.get(i);
                                resourceId = equipment.getEquipmentId();

                            }
                            //log.info("Getting result of resource " + resourceId +" at " + new Date());
                            if (subTaskNo.equals("LWT-STR-14689-001") && stepId.equals("1114532")
                             && resourceGroupName.equals("engineer") && resourceId.equals("167671")){
                                boolean stop = true;
                            }
                            // 更新 staff/equipment plan output的信息
                            if (RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY.equals(dailyMode)) {
                                // 从模型中获取资源变量
                                IloIntervalVar resourceAVariable = (IloIntervalVar) ivResourceANotByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId);
//                                if(!intervalVarsSet.contains(resourceAVariable)){
//                                    continue;
//                                }
                                boolean variableInCP = true;
                                try {
                                    //log.info("Getting result of resource " + resourceId +" at " + new Date().toInstant());
                                    cp.getDomain(resourceAVariable);
                                    //log.info("Getting result of resource " + resourceId +" at " + new Date().toInstant());
                                } catch(Exception e){
                                    variableInCP = false;
                                }
                                if (variableInCP) {
                                    //log.info("Getting result of resource not by day " + resourceId +" at " + new Date());
                                    updateResourcePlanOutput(resourceAVariable, planGranularity, minPlanStartTime, resourceGroupName,
                                            resource, resourceType, resourceId, engineProcessedData, planId, stepActivity,
                                            stepPlanId, functionId, equipment, resourceGroup, null);
                                }
                            // 按天资源
                            }else {

                                for (int j = 0; j < resDayQty; j++) {
                                    IloIntervalVar resourceAVariable = (IloIntervalVar) ivResourceAByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, j);
//                                    if(!intervalVarsSet.contains(resourceAVariable)){
//                                        continue;
//                                    }
                                    boolean variableInCP = true;
                                    try {
                                        //log.info("Getting result of resource " + resourceId + " by day "+ j +" at " + new Date().toInstant());
                                        cp.getDomain(resourceAVariable);
                                        //log.info("Getting result of resource " + resourceId +" by day "+ j +" at " + new Date().toInstant());
                                    } catch(Exception e){
                                        variableInCP = false;
                                    }
                                    if (variableInCP) {
                                        //log.info("Getting result of resource " + resourceId +" by day "+ j +" at " + new Date());
                                        updateResourcePlanOutput(resourceAVariable, planGranularity, minPlanStartTime, resourceGroupName,
                                                resource, resourceType, resourceId, engineProcessedData, planId, stepActivity,
                                                stepPlanId, functionId, equipment, resourceGroup, j);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 更新staff/equipment plan output的信息
     * @param resourceAVariable
     * @param planGranularity
     * @param minPlanStartTime
     * @param resourceGroupName
     * @param resource
     * @param resourceType
     * @param resourceId
     * @param engineProcessedData
     * @param planId
     * @param stepActivity
     * @param stepPlanId
     * @param functionId
     * @param equipment
     * @param resourceGroup
     */
    private void updateResourcePlanOutput(IloIntervalVar resourceAVariable,
                                          int planGranularity,
                                          Date minPlanStartTime,
                                          String resourceGroupName,
                                          Object resource,
                                          String resourceType,
                                          String resourceId,
                                          EngineProcessedData engineProcessedData,
                                          String planId,
                                          EntityStepActivity stepActivity,
                                          String stepPlanId,
                                          Integer functionId,
//                                          EntityStaff staff ,
                                          EntityEquipment equipment,
                                          EntityResourceGroup resourceGroup,
                                          Integer dayIndex) throws IloException {
        // 该资源被排入
        if (!cp.isPresent(resourceAVariable)) {
            return;
        }

        HashMap<String, ArrayList<Object>> assignedResources = stepActivity.getAssignedResources();
//            HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = stepActivity.getEquipmentEquipmentPlanOutputHashMap();
//            HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanOutputHashMap = stepActivity.getStaffStaffPlanOutputHashMap();

        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = engineProcessedData.getEquipmentEquipmentPlanOutputHashMap();
        HashMap<String, ArrayList<EntityStaffPlan>> staffStaffPlanOutputHashMap = engineProcessedData.getStaffStaffPlanOutputHashMap();


        // 获取对应的开始结束时间
        int resourceStart = cp.getStart(resourceAVariable) * planGranularity;
        int resourceEnd = cp.getEnd(resourceAVariable) * planGranularity;
        Date resourceStartTime = DateUtil.getPreciseTime(DateUtil.addMinute(minPlanStartTime, resourceStart));
        Date resourceEndTime = DateUtil.getPreciseTime(DateUtil.addMinute(minPlanStartTime, resourceEnd));

        // 更新小阶段活动中的资源分配情况
        ArrayList<Object> assingedRes = assignedResources.computeIfAbsent(resourceGroupName, k -> new ArrayList<>());
        assingedRes.add(resource);


        if (RESOURCE_TYPE_ENGINEER.equals(resourceType)
                || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)){
            EntityStaffPlanOutput staffPlanOutput = new EntityStaffPlanOutput();

            // 如果按天加个 dayIndex
            String staffPlanId = resourceId + "|" + stepPlanId + "|" + resourceGroupName ;
            if (resourceGroup.getRequestedDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
                dayIndex++;
                staffPlanId += "|" + dayIndex;
            }

            staffPlanOutput.setStaffPlanId(staffPlanId);
            staffPlanOutput.setStaffId(resourceId);

            // 如果技师有指定的技能，则设置 SkillIdSet 字段
//            ArrayList<String> skillIds = engineProcessedData.getTechnicianSkillHashMap().get(resourceId);
//            if (skillIds != null && skillIds.size() != 0){
//                String skillIdSet = "";
//                for (String skillId : skillIds) {
//                    skillIdSet += skillId;
//                }
//                staffPlanOutput.setSkillIdSet(skillIdSet);
//            }
            if (RESOURCE_TYPE_TECHNICIAN.equals(resourceType) && !resourceGroupName.equals(DEFAULT_TECHNICIAN_RESOURCE_GROUP_NAME)){
                staffPlanOutput.setSkillIdSet(resourceGroupName);
            }

            staffPlanOutput.setPlanId(planId);
            staffPlanOutput.setStepPlanId(stepPlanId);
            staffPlanOutput.setStaffPlanStart(resourceStartTime);
            staffPlanOutput.setStaffPlanEnd(resourceEndTime);
            staffPlanOutput.setStaffStartInModel(resourceStartTime);
            staffPlanOutput.setStaffEndInModel(resourceEndTime);
            if (resourceGroup.isConstraint()){
                staffPlanOutput.setIsConstraint(RESOURCE_GROUP_IS_CONSTRAINT);
            }else {
                staffPlanOutput.setIsConstraint(RESOURCE_GROUP_IS_NOT_CONSTRAINT);
            }
            staffPlanOutput.setFunctionId(functionId);

            double occupiedCapacity = engineProcessedData.calculateOccupiedCapacity(
                    staffPlanOutput.getIsConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT),
                    false,
                    (double)DEFAULT_RESOURCE_CAPACITY, (double)DEFAULT_RESOURCE_CAPACITY,
                    false, 1);
            staffPlanOutput.setOccupiedCapacity(occupiedCapacity);

            double reqWorkTime = resourceGroup.getRequestedWorkTime();
            if (stepActivity.getStep().isRepeat() &&
                    resourceGroup.getRequestedDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)){
                reqWorkTime = reqWorkTime * stepActivity.getValidSampleQuantity();
            };
            double planWorkTime = ((double)cp.getSize(resourceAVariable))* planGranularity / 60;
            staffPlanOutput.setReqWorkTime(new BigDecimal(reqWorkTime));
            staffPlanOutput.setPlanWorkTime(new BigDecimal(planWorkTime));

            engineProcessedData.getStaffPlanOutputHashMap().put(staffPlanId, staffPlanOutput);
            // 更新小阶段活动中的资源分配情况
            ArrayList<EntityStaffPlan> staffPlanInputs = staffStaffPlanOutputHashMap.computeIfAbsent(resourceId, k -> new ArrayList<>());
            staffPlanInputs.add(staffPlanOutput);

        }else {
            EntityEquipmentPlanOutput equipmentPlanOutput = new EntityEquipmentPlanOutput();

            String equipmentPlanId = resourceId + "|" + stepPlanId + "|" + resourceGroupName ;
            if (resourceGroup.getRequestedDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
                dayIndex++;
                equipmentPlanId += "|" + dayIndex ;
            }

            equipmentPlanOutput.setEquipmentPlanId(equipmentPlanId);
            equipmentPlanOutput.setEquipmentId(resourceId);
            equipmentPlanOutput.setStepPlanId(stepPlanId);
            EntityEquipmentGroupRel equipmentGroupRel = engineProcessedData.getEquipmentGroupRelByEquipmentIdHashMap().get(resourceId);
            equipmentPlanOutput.setEquipmentGroupId(equipmentGroupRel.getEquipmentGroupId());
            equipmentPlanOutput.setEquipmentPlanStart(resourceStartTime);
            equipmentPlanOutput.setEquipmentPlanEnd(resourceEndTime);
            equipmentPlanOutput.setEquipmentStartInModel(resourceStartTime);
            equipmentPlanOutput.setEquipmentEndInModel(resourceEndTime);
            int requestedCapacity = resourceGroup.getRequestedResourceCapacity();
            if (stepActivity.getStep().isExpand()) {
                requestedCapacity = requestedCapacity * stepActivity.getValidSampleQuantity();
            }
            equipmentPlanOutput.setEquipmentQty(requestedCapacity);
            if (resourceGroup.isExpandable()){
                equipmentPlanOutput.setIsExpandable(RESOURCE_GROUP_IS_EXPANDABLE);
            }else {
                equipmentPlanOutput.setIsExpandable(RESOURCE_GROUP_IS_NOT_EXPANDABLE);
            }
            if (resourceGroup.isConstraint()){
                equipmentPlanOutput.setIsConstraint(RESOURCE_GROUP_IS_CONSTRAINT);
            }else {
                equipmentPlanOutput.setIsConstraint(RESOURCE_GROUP_IS_NOT_CONSTRAINT);
            }

            equipmentPlanOutput.setOccupiedCapacity(requestedCapacity);

            equipmentPlanOutput.setEquipmentStatus(resourceGroup.getState());
            equipmentPlanOutput.setFunctionId(functionId);
            // planId
            equipmentPlanOutput.setPlanId(planId);

            double reqWorkTime = resourceGroup.getRequestedWorkTime();
            if (stepActivity.getStep().isRepeat()
                    && resourceGroup.getRequestedDailyMode().equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)){
                reqWorkTime = reqWorkTime * stepActivity.getValidSampleQuantity();
            };
            if (resourceGroup.isSameStart() && resourceGroup.isSameEnd()) {
                // synchronized的情况
                reqWorkTime = stepActivity.getSize() * planGranularity / 60;
            }
            double planWorkTime = ((double) cp.getSize(resourceAVariable)) * planGranularity / 60;
            equipmentPlanOutput.setReqWorkTime(new BigDecimal(reqWorkTime));
            equipmentPlanOutput.setPlanWorkTime(new BigDecimal(planWorkTime));

            engineProcessedData.getEquipmentPlanOutputHashMap().put(equipmentPlanId, equipmentPlanOutput);
            // 更新小阶段活动中的资源分配情况
            ArrayList<EntityEquipmentPlan> equipmentPlans = equipmentEquipmentPlanOutputHashMap.computeIfAbsent(resourceId, k -> new ArrayList<>());
            equipmentPlans.add(equipmentPlanOutput);
        }
    }

    @Override
    protected void addObjective() throws IloException {
        EntityPlan plan = subModelData.getEngineProcessedData().getPlan();
        //添加目标函数
        //添加表达式：尽量多排入的任务单
        if(exprOptionalTasks != null && plan.isEnabledObjectiveOptionalTasks()){
            cp.addKPI(exprOptionalTasks, ObjectiveOptionalTasks);
            if(objective != null){
                objective = cp.sum(objective, cp.prod(exprOptionalTasks,DEFAULT_WEIGHT_OPTIONAL_TASKS));
            }else{
                objective = cp.prod(exprOptionalTasks,DEFAULT_WEIGHT_OPTIONAL_TASKS);
            }
        }
        //添加表达式：尽量在批次“试验期望完成时间”之前完成
        if(exprLateSubtasks != null && plan.isEnabledObjectiveLateSubTasks()){
            cp.addKPI(exprLateSubtasks, ObjectiveLateSubTasks);
            if(objective != null){
                objective = cp.sum(objective, cp.prod(exprLateSubtasks,DEFAULT_WEIGHT_LATE_SUBTASKS));
            }else{
                objective = cp.prod(exprLateSubtasks,DEFAULT_WEIGHT_LATE_SUBTASKS);
            }
        }
        //添加表达式：尽量减少计划完成时间和任务单审批完成时间的差值
        if(exprLateTasks != null && plan.isEnabledObjectiveLateTasks()){
            cp.addKPI(exprLateTasks, ObjectiveLateTasks);
            if(objective != null){
                objective = cp.sum(objective, cp.prod(exprLateTasks,DEFAULT_WEIGHT_LATE_TASKS));
            }else{
                objective = cp.prod(exprLateTasks,DEFAULT_WEIGHT_LATE_TASKS);
            }
        }
        //添加表达式：尽量减少每个批次在每个资源组中使用资源的数量
        if(exprAdditionalUsedResourceQty != null && plan.isEnabledObjectiveAdditionalUsedResourceQty()){
            cp.addKPI(exprAdditionalUsedResourceQty, ObjectiveAdditionalUsedResourceQty);
            if(objective != null){
                objective = cp.sum(objective,cp.prod(exprAdditionalUsedResourceQty,DEFAULT_WEIGHT_ADDITIONAL_USED_RESOURCE_QTY));
            }else{
                objective = cp.prod(exprAdditionalUsedResourceQty,DEFAULT_WEIGHT_ADDITIONAL_USED_RESOURCE_QTY);
            }
        }
        //添加表达式 ： 人员的工作量尽量均衡
        if(exprResourceUnbalance != null && plan.isEnabledObjectiveResourceUnbalance()){
            cp.addKPI(exprResourceUnbalance, ObjectiveResourceUnbalance);
            if(objective != null){
                objective = cp.sum(objective,cp.prod(exprResourceUnbalance,DEFAULT_WEIGHT_RESOURCE_UNBALANCE));
            }else{
                objective = cp.prod(exprResourceUnbalance,DEFAULT_WEIGHT_RESOURCE_UNBALANCE);
            }
        }
        //添加表达式 ： 小阶段的size的和
        if(exprSumOfStepASize != null  && plan.isEnabledObjectiveSumOfStepASize()){
            cp.addKPI(exprSumOfStepASize, ObjectiveSumOfStepASize);
            if(objective != null){
                objective = cp.sum(objective,cp.prod(exprSumOfStepASize,DEFAULT_WEIGHT_SUM_OF_STEP_SIZE));
            }else{
                objective = cp.prod(exprSumOfStepASize,DEFAULT_WEIGHT_SUM_OF_STEP_SIZE);
            }
        }

        cp.addMinimize(objective);
    }
}
