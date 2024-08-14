package com.oat.patac.engine;

import com.oat.cp.CPModel;
import com.oat.patac.entity.*;
import ilog.concert.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import static com.oat.common.utils.ConstantUtil.*;
import static com.oat.common.utils.ConstantUtil.RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY;

/**
 *
 * 资源活动0、1变量和interval变量的关系，资源活动和小阶段活动的关系，以及资源活动之间的关系, 包含的约束有：
 *
 * 不按天的资源组，
 * - 不按天资源组活动interval变量： ivResourceGroupANotByDay
 *   - 资源组活动和小阶段活动的排入状态应该一致：ConstraintResourceGroupANotByDayPresence
 *   - 资源组活动应该在小阶段活动的时间范围内：ConstraintResourceGroupANotByDayInStepARange （包括startBeforeStart, endBeforeEnd, startAtStart, endAtEnd)
 *
 * 按天的资源组：
 * - 按天的资源组每天的范围interval变量： ivResourceGroupADayRange
 *   - 资源组每天范围变量和小阶段活动的排入状态应该一致：ConstraintResourceGroupADayRangePresence
 *   - 资源组每天范围变量应按照顺序执行： ConstraintResourceGroupADayRangeSequence
 *   - 资源组每天范围变量应该在小阶段时间范围之内： ConstraintResourceGroupADayRangeInStepARange （包括startBeforeStart, endBeforeEnd, startAtStart, endAtEnd)
 *   - 资源组每天范围变量只能从0点开始，24点结束： ConstraintResourceGroupADayRangeForbidStartEnd（第一天不限制0点开始，最后一天不限制24点结束）
 * - 按天的资源组活动interval变量： ivResourceGroupAByDay
 *   - 资源组每天活动变量和范围变量的排入状态应该一致：ConstraintResourceGroupAByDayPresence
 *   - 资源组每天活动变量应该在范围变量的时间范围之内：ConstraintResourceGroupAByDayInResourceGroupADayRange（包括startBeforeStart, endBeforeEnd, startAtStart, endAtEnd)
 *
 * 资源：
 * - 资源活动interval变量：ivResourceANotByDay、ivResourceAByDay
 *   - 从资源组内的资源活动中选择指定个数的活动执行试验：ConstraintResourceGroupAAlternativeResource
 *   - 资源活动不应该在不工作的时候开始或结束：ConstraintResourceAForbidStartEnd
 *   - 资源的状态约束：ConstraintResourceAState
 *   - 资源分配0、1变量：bvStepAResGroupRes， 和interval变量的关系：ConstraintResourceAPresenceNotByDay、ConstraintResourceAPresenceByDay
 */
@Log4j2
public class ConstraintResourceARelationsV2 extends PatacConstraint{

    public ConstraintResourceARelationsV2(SubModel model) {
        super(model);
        setConstraintName("ResourceGroupA ResourceA Relations Constraint");
    }

    boolean isPrintConstraintLog = false;

    @Override
    public boolean apply() throws IloException {
        //获得子模型中需要考虑的任务单和下面待排程的批次
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getToBePlannedSubTaskMap();
        //得到小阶段资源组不按天的四维变量
        //CPModel.Var4DArray ivResourceGroupANotByDay = subModel.getIvResourceGroupANotByDay();
        //得到小阶段资源组按天的每工作天范围变量
        CPModel.Var5DArray ivResourceGroupADayRange = subModel.getIvResourceGroupADayRange();
        //得到小阶段资源组按天的每工作天变量
        //CPModel.Var5DArray ivResourceGroupAByDay = subModel.getIvResourceGroupAByDay();
        //得到小阶段资源组不按天的五维变量
        CPModel.Var5DArray ivResourceANotByDay = subModel.getIvResourceANotByDay();
        //得到小阶段资源组按天的六维变量
        CPModel.Var6DArray ivResourceAByDay = subModel.getIvResourceAByDay();

        //得到对应小阶段资源组资源的binary变量
        CPModel.Var5DArray bvStepAResGroupRes = subModel.getBvStepAResGroupRes();
        //得到对应小阶段活动的变量
        CPModel.Var3DArray ivStepA = subModel.getIvStepA();
        //得到state function
        CPModel.Var1DArray statefResource = subModel.getStatefResource();
        HashMap<String, Integer> equipmentStateStringToIntMap = engineProcessedData.getEquipmentStateStringToIntMap();
        //得到 Cumul Function Expression
        CPModel.Var1DArray cumulfeResource = subModel.getCumulFunctionExpression();
        //得到exprResourceUsage
        CPModel.Var1DArray exprResourceUsage = subModel.getExprResourceUsage();

        IloIntExpr exprSumOfStepASize = subModel.getExprSumOfStepASize();

        // 通过颗粒度计算一天的数值
        int planGranularity = subModelData.getPlanGranularity();

        ArrayList<Double> stepFunctionDayStartTime = subModelData.getStepFunctionDayStartTime();
        ArrayList<Double> stepFunctionDayStartValue = subModelData.getStepFunctionDayStartValue();
        ArrayList<Double> stepFunctionDayEndTime = subModelData.getStepFunctionDayEndTime();
        ArrayList<Double> stepFunctionDayEndValue = subModelData.getStepFunctionDayEndValue();

        IloNumToNumStepFunction stepFDayStart = cp.numToNumStepFunction();
        int sizeTime = stepFunctionDayStartTime.size();

        for (int j = 0; j < sizeTime - 1; j++) {
            stepFDayStart.setValue(stepFunctionDayStartTime.get(j), stepFunctionDayStartTime.get(j + 1), stepFunctionDayStartValue.get(j));
        }

        IloNumToNumStepFunction stepFDayEnd = cp.numToNumStepFunction();
        sizeTime = stepFunctionDayEndTime.size();
        for (int j = 0; j < sizeTime - 1; j++) {
            stepFDayEnd.setValue(stepFunctionDayEndTime.get(j), stepFunctionDayEndTime.get(j + 1), stepFunctionDayEndValue.get(j));
        }

        // 循环每个Task
        for (Map.Entry<String, ArrayList<EntitySubTask>> entry : subTaskMap.entrySet()) {
            //得到对应的任务单单号，以及任务单实体和对应的批次list
            String taskNo = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            //遍历批次
            for (EntitySubTask subTask : subTasks) {
                //得到对应的批次号和小阶段的list
                String subTaskNo = subTask.getSubTaskNo();
                ArrayList<EntityStepActivity> stepActivities = subTask.getStepActivities();
                HashSet<String> resourceGroupsFixingResource = subTask.getResourceGroupsFixingResource();
                //遍历小阶段活动
                for (EntityStepActivity stepActivity : stepActivities) {
                    //获得对应的小阶段和小阶段id
                    EntityStep step = stepActivity.getStep();
                    Integer stepId = step.getStepId();
                    // 获取 stepActivity 的 interval 决策变量
                    IloIntervalVar stepAVar = (IloIntervalVar) ivStepA.getVariable(taskNo, subTaskNo, stepId);
                    // 获取小阶段活动的可能时间范围内
                    int stepAStartMin = stepAVar.getStartMin();
                    int stepAEndMax = stepAVar.getEndMax();
                    // 该小阶段活动的资源组和资源组内可能用的资源列表
                    HashMap<String, ArrayList<Object>> possibleResources = stepActivity.getPossibleResources();
                    // 遍历资源列表
                    for (Map.Entry<String, ArrayList<Object>> resourceGroupMap : possibleResources.entrySet()) {
                        //获得资源组的名称和对应的列表
                        String resourceGroupName = resourceGroupMap.getKey();
                        ArrayList<Object> resources = resourceGroupMap.getValue();
                        // 根据资源组名获得资源组对象
                        EntityResourceGroup resourceGroup = step.getResourceGroupHashMap().get(resourceGroupName);
                        String resourceType = resourceGroup.getResourceType();
                        // 获取资源组中的工作模式
                        String dailyMode = resourceGroup.getRequestedDailyMode();
                        // 获取资源组中的请求资源天数
                        int resDayQty = resourceGroup.getRequestedDayQuantity();
                        boolean isRepeat = false;
                        if (step.isRepeat()) {
                            isRepeat = true;
                            if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)){
                                // 按天，倍乘天数
                                resDayQty = resDayQty * stepActivity.getValidSampleQuantity();
                            }
                        }
                        // 获取资源组中的请求资源工作时间
                        double workTime = resourceGroup.getRequestedWorkTime();
                        // 获取是否要求固定设备
                        boolean isFixingResource = false;
                        if (resourceGroupsFixingResource.contains(resourceGroupName)){
                            isFixingResource = true;
                        }

                        // 处理资源组不按天的情况
                        if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                            addResourceANotByDayConstraints(taskNo, subTaskNo, stepId, isRepeat, resourceGroupName,
                                    resourceType, resourceGroup, workTime, stepActivity, planGranularity,
                                    stepAVar, stepAStartMin, stepAEndMax,
                                    ivResourceANotByDay,
                                    statefResource, cumulfeResource, exprResourceUsage, bvStepAResGroupRes,
                                    resources, equipmentStateStringToIntMap);
                        } else {
                            // 处理资源组按天的情况
                            addResourceAByDayConstraints(taskNo, subTaskNo,
                                    stepId, isRepeat, resourceGroupName,
                                    resourceType, resourceGroup, workTime, resDayQty, isFixingResource,
                                    stepActivity, planGranularity,
                                    stepAVar, stepAStartMin, stepAEndMax,
                                    ivResourceGroupADayRange, ivResourceAByDay,
                                    statefResource, cumulfeResource, exprResourceUsage, bvStepAResGroupRes,
                                    resources, equipmentStateStringToIntMap,
                                    stepFDayStart, stepFDayEnd);
                        }
                    }// 资源组循环结束
                    // 小阶段活动考虑资源组之后的size
                    IloIntExpr stepASizeDiff = cp.diff(cp.sizeOf(stepAVar),(int) stepActivity.getMaxSizeConsideredResources());
                    if (exprSumOfStepASize == null){
                        exprSumOfStepASize = stepASizeDiff;
                    } else{
                        exprSumOfStepASize = cp.sum(exprSumOfStepASize, stepASizeDiff);
                    }

                }//小阶段活动循环结束
            } //批次循环结束
        }//任务单循环结束
        //设置表达式：每个人员的工作量
        subModel.setExprResourceUsage(exprResourceUsage);
        // 变大的size和实际size的差
        subModel.setExprSumOfStepASize(exprSumOfStepASize);
        return true;
    }

    private void addResourceAByDayConstraints(String taskNo, String subTaskNo,
                                              Integer stepId, boolean isRepeat,
                                                            String resourceGroupName, String resourceType,
                                                            EntityResourceGroup resourceGroup, double workTime, int resDayQty, boolean isFixingResource,
                                                            EntityStepActivity stepActivity, int planGranularity,
                                                            IloIntervalVar stepAVar, int stepAStartMin, int stepAEndMax,
                                                            CPModel.Var5DArray ivResourceGroupADayRange,
                                                            CPModel.Var6DArray ivResourceAByDay,
                                                            CPModel.Var1DArray statefResource,
                                                            CPModel.Var1DArray cumulfeResource,
                                                            CPModel.Var1DArray exprResourceUsage,
                                                            CPModel.Var5DArray bvStepAResGroupRes,
                                                            ArrayList<Object> resources,
                                                            HashMap<String, Integer> equipmentStateStringToIntMap,
                                                            IloNumToNumStepFunction stepFDayStart, IloNumToNumStepFunction stepFDayEnd)
            throws IloException {

        int oneDaySize = 24 *60 /planGranularity;
        IloIntervalVar previousDayRangeVar = null;
        HashMap<String, IloIntExpr[]> resourceAExprArrayMap = new HashMap<>();

        // 按天工作的，更新stepA变量的max length
        int resourceGroupSize;
        if (resDayQty == 1 ){
            resourceGroupSize = (int) Math.ceil(workTime * 60/planGranularity);
        } else {
            resourceGroupSize = (resDayQty - 2) * oneDaySize + 2 * (int) Math.ceil(workTime * 60/planGranularity);
        }
        if (resourceGroupSize > stepActivity.getMaxSizeConsideredResources()) {
            stepActivity.setMaxSizeConsideredResources(resourceGroupSize);

            if (engineProcessedData.getPlan().isEnabledLengthMaxOfStepA()) {
                subModelData.setIntervalVarMaxLength(resourceGroupSize, stepAVar);
            }
        }
        for (int i = 0; i< resDayQty; i++){
            // 创建按天的资源组每天范围的变量
            IloIntervalVar resourceGroupADayRangeVar= cp.intervalVar("ivResourceGroupADayRange: subTask_" + subTaskNo
                    + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName+"_day_"+ i);
            //todo: 是否不给变量命名
            //IloIntervalVar resourceGroupADayRangeVar= cp.intervalVar();
            if (!stepAVar.isPresent()){
                resourceGroupADayRangeVar.setOptional();
            }


            resourceGroupADayRangeVar.setStartMin(stepAStartMin);
            resourceGroupADayRangeVar.setEndMax(stepAEndMax);
            ivResourceGroupADayRange.setVariable(taskNo,subTaskNo,stepId, resourceGroupName, i, resourceGroupADayRangeVar);
            if (i==0 || i== resDayQty-1){
                resourceGroupADayRangeVar.setSizeMin(0);
            }else {
                resourceGroupADayRangeVar.setSizeMin(oneDaySize);
            }
            resourceGroupADayRangeVar.setSizeMax(oneDaySize);

            // 添加cStepAResourceGroupADayRangePresence约束
            if (!stepAVar.isPresent()
                    && engineProcessedData.getPlan().isEnabledConstraintResourceGroupADayRangePresence()) {
                cp.add(cp.eq(cp.presenceOf(stepAVar), cp.presenceOf(resourceGroupADayRangeVar)));
                if (isPrintConstraintLog) {
                    log.info("ConstraintResourceGroupADayRangePresence:: " + stepAVar.getName() + " SAME PRESENCE WITH " + resourceGroupADayRangeVar.getName());
                }
            }
            if (i >= 1 ){
                // 添加cResourceGroupADayRangeSequence约束
                if (engineProcessedData.getPlan().isEnabledConstraintResourceGroupADayRangeSequence()){
                    cp.add(cp.endBeforeStart(previousDayRangeVar, resourceGroupADayRangeVar));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceGroupADayRangeSequence:: " + previousDayRangeVar.getName() + " END BEFORE START " + resourceGroupADayRangeVar.getName());
                    }
                }
            }
            previousDayRangeVar = resourceGroupADayRangeVar;
            // 添加 cResourceGroupADayRangeInStepARange 约束
            if (engineProcessedData.getPlan().isEnabledConstraintResourceGroupADayRangeInStepARange()) {
                if (i == 0) {
                    if (resourceGroup.isSameStart()) {
                        cp.add(cp.startAtStart(stepAVar, resourceGroupADayRangeVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceGroupADayRangeInStepARange:: " + stepAVar.getName() + " START AT START " + resourceGroupADayRangeVar.getName());
                        }
                    } else {
                        cp.add(cp.startBeforeStart(stepAVar, resourceGroupADayRangeVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceGroupADayRangeInStepARange:: " + stepAVar.getName() + " START BEFORE START " + resourceGroupADayRangeVar.getName());
                        }
                    }
                }
                if (i == resDayQty -1){
                    if (resourceGroup.isSameEnd()){
                        cp.add(cp.endAtEnd(stepAVar,resourceGroupADayRangeVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceGroupADayRangeInStepARange:: " + stepAVar.getName() + " END AT END " + resourceGroupADayRangeVar.getName());
                        }
                    } else {
                        cp.add(cp.endBeforeEnd(resourceGroupADayRangeVar, stepAVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceGroupADayRangeInStepARange:: " + resourceGroupADayRangeVar.getName() + " END BEFORE END " + stepAVar.getName());
                        }
                    }
                }
            }
            // 添加 cResourceAGroupADayRangeForbidStartEnd 约束
            if (engineProcessedData.getPlan().isEnabledConstraintResourceGroupADayRangeForbidStartEnd()){
                if (i >0){ // 第一天不设置必须从0点开始
                    cp.add(cp.forbidStart(resourceGroupADayRangeVar, stepFDayStart));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceGroupADayRangeForbidStartEnd:: " + resourceGroupADayRangeVar.getName() + " FORBID START AT stepFDayStart ");
                    }
                }
                if (i <resDayQty-1){ // 最后一天不设置必须在24点结束
                    cp.add(cp.forbidEnd(resourceGroupADayRangeVar, stepFDayEnd));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceGroupADayRangeForbidStartEnd:: " + resourceGroupADayRangeVar.getName() + " FORBID END AT stepFDayEnd ");
                    }
                }
            }

            // 创建资源组内资源的活动变量
            addResourceAConstraints(RESOURCE_GROUP_DAILY_MODE_BY_DAY, taskNo, subTaskNo, stepId, isRepeat,
                    resourceGroupName,resourceType,resourceGroup, workTime, isFixingResource,
                    stepActivity, planGranularity,
                    stepAStartMin, stepAEndMax,
                    null, ivResourceAByDay,
                    statefResource, cumulfeResource, exprResourceUsage,
                    bvStepAResGroupRes, stepAVar, resourceAExprArrayMap,
                    resources, equipmentStateStringToIntMap, resourceGroupADayRangeVar, i, resDayQty);


        }// 每天循环结束

        // 添加资源是否被分配的0、1变量和资源活动interval变量的关系
        addConstraintResourceAPresenceByDay(resources, resourceType,
                taskNo, subTaskNo, stepId, resourceGroupName, isFixingResource,
                bvStepAResGroupRes, resourceAExprArrayMap);
    }


    private void addConstraintResourceAPresenceByDay(ArrayList<Object> resources, String resourceType,
                                                     String taskNo, String subTaskNo, int stepId, String resourceGroupName, boolean isFixingResource,
                                                     CPModel.Var5DArray bvStepAResGroupRes,
                                                     HashMap<String, IloIntExpr[]> resourceAExprArrayMap) throws IloException {
        int resourceSize = resources.size();

        // 遍历资源
        for (int i = 0; i < resourceSize; i++) {
            // 资源对应实体的Id
            String resourceId;
            // 用resource type 和 resource Id作为key
            String resourceTypeResourceId;
            if (RESOURCE_TYPE_ENGINEER.equals(resourceType)
                    || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)) {
                EntityStaff staff = (EntityStaff) resources.get(i);
                resourceId = staff.getStaffId();
            } else //if (RESOURCE_TYPE_EQUIPMENT.equals(resourceType))
            {
                EntityEquipment equipment = (EntityEquipment) resources.get(i);
                resourceId = equipment.getEquipmentId();
            }
            resourceTypeResourceId = resourceType + resourceId;
            IloIntExpr[] resourceAExprArray = resourceAExprArrayMap.get(resourceTypeResourceId);
            // 为按天的情况，添加资源是否被安排的0、1变量和资源活动interval变量的关系
            if (engineProcessedData.getPlan().isEnabledConstraintResourceAPresenceByDay()) {
                IloIntVar var = (IloIntVar) bvStepAResGroupRes.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId);
                if (!isFixingResource) {
                    cp.add(cp.eq(var, cp.max(resourceAExprArray)));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceAPresenceByDay:: " + var.getName() + " EQUALS TO MAX OF ");
                        Stream.of(resourceAExprArray).forEach(v -> log.info("ConstraintResourceAPresenceByDay:: " + v.toString()));
                    }
                }
            }
        }
    }

    private void addResourceANotByDayConstraints(String taskNo, String subTaskNo, Integer stepId, boolean isRepeat,
                                                 String resourceGroupName, String resourceType, EntityResourceGroup resourceGroup,
                                                 double workTime,
                                                 EntityStepActivity stepActivity, int planGranularity,
                                                 IloIntervalVar stepAVar, int stepAStartMin, int stepAEndMax,
                                                 CPModel.Var5DArray ivResourceANotByDay,
                                                 CPModel.Var1DArray statefResource,
                                                 CPModel.Var1DArray cumulfeResource,
                                                 CPModel.Var1DArray exprResourceUsage,
                                                 CPModel.Var5DArray bvStepAResGroupRes,
                                                 ArrayList<Object> resources,
                                                 HashMap<String, Integer> equipmentStateStringToIntMap) throws IloException {


        // 更新stepA考虑了资源组后的最大size, 如果<4小时，lengthMax设置为8小时
        if (!(resourceGroup.isSameStart() && resourceGroup.isSameEnd())) {

            int resourceGroupVariableSize = (int) Math.ceil((workTime * 60) / planGranularity);
            if (isRepeat){
                resourceGroupVariableSize = (int) Math.ceil((workTime * stepActivity.getValidSampleQuantity() * 60) / planGranularity);
            }
            if (resourceGroupVariableSize > stepActivity.getMaxSizeConsideredResources()) {
                stepActivity.setMaxSizeConsideredResources(resourceGroupVariableSize);
                if (engineProcessedData.getPlan().isEnabledLengthMaxOfStepA()) {
                    subModelData.setIntervalVarMaxLength(resourceGroupVariableSize, stepAVar);
                }
            }
        }




        addResourceAConstraints(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY, taskNo, subTaskNo, stepId, isRepeat,
                resourceGroupName,resourceType,resourceGroup, workTime, false,
                stepActivity, planGranularity,
                stepAStartMin, stepAEndMax,
                ivResourceANotByDay, null,
                statefResource, cumulfeResource, exprResourceUsage,
                bvStepAResGroupRes, stepAVar, null,
                resources, equipmentStateStringToIntMap, null,0, 1);


    }// 资源组循环结束

    private void addResourceAConstraints(String dailyMode, String taskNo, String subTaskNo, int stepId, boolean isRepeat,
                                         String resourceGroupName,String resourceType,EntityResourceGroup resourceGroup,
                                         double workTime, boolean isFixingResource,
                                         EntityStepActivity stepActivity, int planGranularity,
                                         int stepAStartMin, int stepAEndMax,
                                         CPModel.Var5DArray ivResourceANotByDay,
                                         CPModel.Var6DArray ivResourceAByDay,
                                         CPModel.Var1DArray statefResource,
                                         CPModel.Var1DArray cumulfeResource,
                                         CPModel.Var1DArray exprResourceUsage,
                                         CPModel.Var5DArray bvStepAResGroupRes,
                                         IloIntervalVar stepAVar,
                                         HashMap<String, IloIntExpr[]> resourceAExprArrayMap,
                                         ArrayList<Object> resources,
                                         HashMap<String, Integer> equipmentStateStringToIntMap,
                                         IloIntervalVar resourceGroupADayRangeVar,
                                         int dayIndex, int resDayQty) throws IloException {

        int resourceSize = resources.size();
        IloIntervalVar[] resourceAVarArray = new IloIntervalVar[resourceSize];
        IloIntExpr[] resourceAVarPresenceArray = new IloIntExpr[resourceSize];

        //intensity function
        ArrayList<Double> stepFunctionTime;
        ArrayList<Double> stepFunctionValue;

        // 遍历资源
        for (int i = 0; i < resourceSize; i++) {
            // 资源对应实体的Id
            String resourceId;
            //创建cumul function expression的key,用resource type 和 resource Id作为key
            String resourceTypeResourceId;
            if (RESOURCE_TYPE_ENGINEER.equals(resourceType)
                    || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)) {
                EntityStaff staff = (EntityStaff) resources.get(i);
                resourceId = staff.getStaffId();
                stepFunctionTime = subModelData.getStepFunctionStaffTime().get(resourceId);
                stepFunctionValue = subModelData.getStepFunctionStaffValue().get(resourceId);
            } else //if (RESOURCE_TYPE_EQUIPMENT.equals(resourceType))
            {
                EntityEquipment equipment = (EntityEquipment) resources.get(i);
                resourceId = equipment.getEquipmentId();
                stepFunctionTime = subModelData.getStepFunctionEquipmentTime().get(resourceId);
                stepFunctionValue = subModelData.getStepFunctionEquipmentValue().get(resourceId);
            }
            resourceTypeResourceId = resourceType + resourceId;

            // 生成stepFunction
            int sizeTime = stepFunctionTime.size();
            IloNumToNumStepFunction stepFResourceCalendar = cp.numToNumStepFunction();
            // for debug
//            log.info("FOR DEBUG: subtask_" + subTaskNo
//                    + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId);
//            log.info("stepFunctionTime: " + stepFunctionTime.size() + ". Details: " + stepFunctionTime);
//            log.info("stepFunctionValue: " + stepFunctionValue.size() + ". Details: " + stepFunctionValue);
            for (int j = 0; j < sizeTime - 1; j++) {
                stepFResourceCalendar.setValue(stepFunctionTime.get(j), stepFunctionTime.get(j + 1), stepFunctionValue.get(j));
            }

            //创建对应的决策变量
            IloIntervalVar resourceAVar;
            if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {

                resourceAVar = cp.intervalVar("ivResourceANotByDay: subtask_" + subTaskNo
                        + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId);
                //todo: 是否不给变量命名
                //resourceAVar = cp.intervalVar();
            } else {
                resourceAVar = cp.intervalVar("ivResourceAByDay: subtask_" + subTaskNo
                        + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId
                        +"_day_" + dayIndex);
                //todo: 是否不给变量命名
                //resourceAVar = cp.intervalVar();
            }
            //设置对应的属性
            //设置对应的optional属性
            resourceAVar.setOptional();
            //设置开始和结束时间,其中startMin设置为小阶段活动变量的开始时间，endMax设置为小阶段活动变量的结束时间
            resourceAVar.setStartMin(stepAStartMin);
            resourceAVar.setEndMax(stepAEndMax);

            //设置变量的size
            if (!(dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY) &&
                    resourceGroup.isSameStart() && resourceGroup.isSameEnd())) {
                // 不按天的时候，如果同时开始和结束，代表是synchronized，不需要设置size
                // 否则设置size
                int variableSize= (int) Math.ceil(( workTime * 60) / planGranularity);
                if (isRepeat && dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                    // 不按天，倍乘时长; 按天，已经倍乘天数
                    variableSize = (int) Math.ceil(( workTime * stepActivity.getValidSampleQuantity() * 60) / planGranularity);
                }
                resourceAVar.setSizeMin(variableSize);
                resourceAVar.setSizeMax(variableSize);
                // 如果size小于6小时，不要跨天
                if (engineProcessedData.getPlan().isEnabledLengthMaxOfResourceA()) {
                    subModelData.setIntervalVarMaxLength(variableSize, resourceAVar);
                }
            }


            //设置stepFunction
            resourceAVar.setIntensity(stepFResourceCalendar);
            if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                ivResourceANotByDay.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resourceAVar);
            } else {
                ivResourceAByDay.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, dayIndex, resourceAVar);
            }
            resourceAVarArray[i] = resourceAVar;
            resourceAVarPresenceArray[i] = cp.presenceOf(resourceAVar);

            if (engineProcessedData.getPlan().isEnabledConstraintResourceAInStepARange()) {
                if (resourceGroup.isSameStart()) {
                    // 要求同时开始
                    if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                        cp.add(cp.startAtStart(stepAVar, resourceAVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceANotByDayInStepARange:: " + stepAVar.getName() + " START AT START " + resourceAVar.getName());
                        }
                    } else{
                        if (dayIndex == 0) {
                            cp.add(cp.startAtStart(resourceGroupADayRangeVar, resourceAVar));
                            if (isPrintConstraintLog) {
                                log.info("ConstraintResourceAByDayInResourceGroupADayRange:: " + resourceGroupADayRangeVar.getName() + " START AT START " + resourceAVar.getName());
                            }
                        }
                    }

                } else {
                    // 不要求同时开始, 小阶段活动比相关的资源活动早开始
                    if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                        cp.add(cp.startBeforeStart(stepAVar, resourceAVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceANotByDayInStepARange:: " + stepAVar.getName() + " START BEFORE START " + resourceAVar.getName());
                        }
                    }
                }
                if (resourceGroup.isSameEnd()) {
                    // 要求同时结束
                    if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                        cp.add(cp.endAtEnd(stepAVar, resourceAVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceANotByDayInStepARange:: " + stepAVar.getName() + " END AT END " + resourceAVar.getName());
                        }
                    } else{
                        if (dayIndex == resDayQty -1) {
                            cp.add(cp.endAtEnd(resourceGroupADayRangeVar, resourceAVar));
                            if (isPrintConstraintLog) {
                                log.info("ConstraintResourceAByDayInResourceGroupADayRange:: " + resourceGroupADayRangeVar.getName() + " END AT END " + resourceAVar.getName());
                            }
                        }
                    }
                } else {
                    // 不要求同时结束,小阶段活动比相关的设备活动晚结束
                    if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY)) {
                        cp.add(cp.endBeforeEnd(resourceAVar, stepAVar));
                        if (isPrintConstraintLog) {
                            log.info("ConstraintResourceANotByDayInStepARange:: " + stepAVar.getName() + " END BEFORE END " + resourceAVar.getName());
                        }
                    }
                }
                if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_BY_DAY)) {
                    cp.add(cp.startBeforeStart(resourceGroupADayRangeVar, resourceAVar));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceAByDayInResourceGroupADayRange:: " + resourceGroupADayRangeVar.getName() + " START BEFORE START " + resourceAVar.getName());
                    }
                    cp.add(cp.endBeforeEnd(resourceAVar, resourceGroupADayRangeVar));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceAByDayInResourceGroupADayRange:: " + resourceAVar.getName() + " END BEFORE END " + resourceGroupADayRangeVar.getName());
                    }
                }

            }

            // 添加休息时间不能开始和结束活动的约束
            if (engineProcessedData.getPlan().isEnabledConstraintResourceAForbidStartEnd()) {
                cp.add(cp.forbidStart(resourceAVar, stepFResourceCalendar));
                cp.add(cp.forbidEnd(resourceAVar, stepFResourceCalendar));
                if (isPrintConstraintLog) {
                    log.info("ConstraintResourceAForbidStartEnd:: " + resourceAVar.getName() + " FORBID START END AT stepFResourceCalendar ");
                }
            }


            // 有状态要求的资源需要在相应活动时在要求的状态中
            if (resourceGroup.getState() != null && resourceGroup.isConstraint()) {
                // 有状态要求的资源需要在相应活动时在要求的状态中
                // 获得状态函数
                IloStateFunction resourceStateF = (IloStateFunction) statefResource.getVariable(resourceId);
                if (resourceStateF == null) {
                    //如果没有创建过，就创建变量
                    resourceStateF = cp.stateFunction("statefResource_" + resourceId);
                    statefResource.setVariable(resourceId, resourceStateF);
                }
                if (engineProcessedData.getPlan().isEnabledConstraintResourceAState()) {
                    cp.add(cp.alwaysEqual(resourceStateF, resourceAVar,
                            equipmentStateStringToIntMap.get(resourceGroup.getState())));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceAState:: " + resourceAVar.getName() + " ALWAYS EQUAL TO resourceStateF ");
                    }
                }
            }

            // 设置cumul function
            // 可以先统一获得资源需要的容量occupiedCapacity，需要乘以放大倍数
            int occupiedCapacity = (int) (stepActivity.getOccupiedCapacity().get(resourceGroupName + "_" + resourceId)
                    * DEFAULT_AMPLIFICATION_FACTOR);

            if (occupiedCapacity > 0) {
                IloCumulFunctionExpr pulseFunctionExpr = cp.pulse(resourceAVar, occupiedCapacity);
                log.info("resourceA variable " + resourceAVar.getName() + " occupied capacity is " + occupiedCapacity);
                pulseFunctionExpr.setName("cumulFEOf_"+resourceAVar.getName());
                IloCumulFunctionExpr totalFunctionExpr = (IloCumulFunctionExpr)cumulfeResource.getVariable(resourceTypeResourceId);
                if (totalFunctionExpr == null) {
                    cumulfeResource.setVariable(resourceTypeResourceId,pulseFunctionExpr);
                } else {
                    cumulfeResource.setVariable(resourceTypeResourceId,
                            cp.sum(totalFunctionExpr, pulseFunctionExpr));
                }
            }

            //设置表达式exprResourceUsage
            if((RESOURCE_TYPE_ENGINEER.equals(resourceType)
                    || RESOURCE_TYPE_TECHNICIAN.equals(resourceType))
                    // 不管是否是constraint都算工作量
                    //&& resourceGroup.isConstraint()
            ) {
                IloNumExpr resourceUsage = (IloNumExpr) exprResourceUsage.getVariable(resourceTypeResourceId);
                double realWorkTime = ( workTime * 60) / planGranularity;
                if (isRepeat){
                    realWorkTime = realWorkTime * stepActivity.getValidSampleQuantity();
                }
                if (!resourceGroup.isConstraint()){
                    realWorkTime = realWorkTime * ENGINEER_NON_CONSTRAINT_USAGE_DISCOUNT_RATIO;
                }
                if (resourceUsage == null) {
                    resourceUsage = cp.prod(cp.presenceOf(resourceAVar), realWorkTime);
                } else {
                    resourceUsage = cp.sum(resourceUsage, cp.prod(cp.presenceOf(resourceAVar), realWorkTime));
                }
                exprResourceUsage.setVariable(resourceTypeResourceId, resourceUsage);
            }

            //创建单个资源的binary变量
            IloIntVar stepAResGroupResVar = (IloIntVar) bvStepAResGroupRes.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId);
            if ( stepAResGroupResVar == null) {
                stepAResGroupResVar = cp.boolVar("bvStepAResGroupRes: subTask_" + subTaskNo + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId);
                bvStepAResGroupRes.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, stepAResGroupResVar);
            }

            // 为不按天的情况，添加资源是否被安排的0、1变量和资源活动interval变量的关系
            if (dailyMode.equals(RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY) ){
                if (engineProcessedData.getPlan().isEnabledConstraintResourceAPresenceNotByDay()){
                    cp.add(cp.eq(stepAResGroupResVar, cp.presenceOf(resourceAVar)));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceAPresenceNotByDay:: " + stepAResGroupResVar.getName() + " SAME PRESENCE WITH " + resourceAVar.getName());
                    }
                }
            } else {
                if (isFixingResource && engineProcessedData.getPlan().isEnabledConstraintResourceAPresenceByDay()) {
                    cp.add(cp.le(stepAResGroupResVar, cp.presenceOf(resourceAVar)));
                    if (isPrintConstraintLog) {
                        log.info("ConstraintResourceAPresenceByDay:: " + stepAResGroupResVar.getName() + " LESS OR EQUALS TO PRESENCE OF " + resourceAVar.getName());
                    }
                } else {
                    // 为按天的情况，添加 resource 被安排的在每天中的最大值
                    IloIntExpr[] resourceAExprArray = resourceAExprArrayMap.get(resourceTypeResourceId);
                    if (resourceAExprArray == null) {
                        resourceAExprArray = new IloIntExpr[resDayQty];
                    }
                    resourceAExprArray[dayIndex] = cp.presenceOf(resourceAVar);
                    resourceAExprArrayMap.put(resourceTypeResourceId, resourceAExprArray);
                }
            }

        }// 资源循环结束

        // 添加资源组和资源活动的关系
        if (engineProcessedData.getPlan().isEnabledConstraintResourceABeAssignedQuantity()){
            if( resourceAVarPresenceArray.length< resourceGroup.getRequestedResourceQuantity()){
                // 前面已经有数据检查报错，所以这里应该不再报错了
                log.error("!!!ERROR: subTask " + subTaskNo
                        + " step " + stepId + " resourceGroup " + resourceGroupName + " assigned resources quantity infeasible!!!");
            } else {
                cp.add(cp.eq(cp.sum(resourceAVarPresenceArray), resourceGroup.getRequestedResourceQuantity()));
                if (isPrintConstraintLog) {
                log.info("ConstraintResourceABeAssignedQuantity:: subTask " + subTaskNo
                        + " step " + stepId + " resourceGroup " + resourceGroupName  + " ASSIGNED "
                        + resourceGroup.getRequestedResourceQuantity() + " from resources:");
                Stream.of(resourceAVarArray).forEach(v -> log.info("ConstraintResourceABeAssignedQuantity:: " + v.getName()));
                }
            }
        }
    }
}

