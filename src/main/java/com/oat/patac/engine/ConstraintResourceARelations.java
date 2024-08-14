package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.patac.entity.*;
import ilog.concert.*;

import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;
import static com.oat.common.utils.ConstantUtil.RESOURCE_TYPE_EQUIPMENT;

/**
 * 资源活动0、1变量和interval变量的关系，资源活动和小阶段活动的关系，以及资源活动之间的关系, 包含的约束有：
 *
 * 资源组级别，
 * - 给小阶段活动的每个资源组分配指定个数的资源（工程师、每个技能组的技师、每个设备组内的设备）
 *   cStepAAssignResGroupRes[plan_t][plan_st][stepA][resGroup]
 *
 * 资源级别：
 * - 对不按天和按天都包括的：
 *   - 资源分配0、1变量和interval变量的关系
 *     cResourceAPresenceNotByDay[plan_t][plan_st][stepA][resGroup_notByDay][res]
 *     cResourceAPresenceByDay[plan_t][plan_st][stepA][resGroup_byDay][res]
 *   - 小阶段活动和资源活动的关系——小阶段活动早开始，晚结束
 *     cStepAStartBeforeResANotByDay[plan_t][plan_st][stepA][resGroup_notByDay&(staff_notNeedToSameStart||(！eqptGroup_sync))][res]
 *     cStepAEndAfterResANotByDay[plan_t][plan_st][stepA][resGroup_notByDay&(staff_notNeedToSameEnd||(！eqptGroup_sync))][res]
 *     cStepAStartBeforeResAByDay[plan_t][plan_st][stepA][resGroup_byDay][res][res_day]
 *     cStepAEndAfterResAByDay[plan_t][plan_st][stepA][resGroup_byDay][res][res_day]
 *   - 资源的状态约束
 *     cResourceAStateNotByDay[plan_t][plan_st][stepA][resGroup_with_state & resGroup_notByDay][res]
 *     cResourceAStateByDay[plan_t][plan_st][stepA][resGroup_with_state & resGroup_notByDay][res][res_day]
 *
 * - 只针对不按天的约束：
 *   - 小阶段活动和资源活动的关系——一同开始，或一同结束结束
 *     cStepAStartAtResANotByDay[plan_t][plan_st][stepA][resGroup_notByDay&staff_sameStart][res]
 *     cStepAEndAtResANotByDay[plan_t][plan_st][stepA][resGroup_notByDay&staff_sameEnd][res]
 *
 * - 只针对按天的约束：
 *   - 小阶段活动和资源活动的关系——一同开始，或一同结束结束
 *     cResASpanDays[plan_t][plan_st][stepA][resGroup_byDay][res]
 *     cStepAStartAtResAByDay[plan_t][plan_st][stepA][resGroup_byDay&resGroup_sameStart][res]
 *     cStepAEndAtResAByDay[plan_t][plan_st][stepA][resGroup_byDay&resGroup_sameEnd][res]
 */
public class ConstraintResourceARelations extends PatacConstraint {
    public ConstraintResourceARelations(SubModel model) {
        super(model);
        setConstraintName("ResourceA Relations Constraint");

    }

    @Override
    public boolean apply() throws IloException {
        //获得子模型中需要考虑的任务单和下面待排程的批次
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getToBePlannedSubTaskMap();
        //得到小阶段资源组不按天的五维变量
        CPModel.Var5DArray ivResourceANotByDay = subModel.getIvResourceANotByDay();
        //得到小阶段资源组按天的六维变量
        CPModel.Var6DArray ivResourceAByDay = subModel.getIvResourceAByDay();
        //得到小阶段资源组按天的整体五维变量
        CPModel.Var5DArray ivResourceASpanDays = subModel.getIvResourceASpanDays();
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
        IloIntExpr resourceUsage = null;

        //生成intensity function
        ArrayList<Double> stepFunctionTime = new ArrayList<>();
        ArrayList<Double> stepFunctionValue = new ArrayList<>();


        // 通过颗粒度计算一天的数值
        int planGranularity = subModelData.getPlanGranularity();
        int oneDay = 24 * 60 / planGranularity;

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
                        // 获取资源组中的请求资源工作时间
                        int workTime = (int) resourceGroup.getRequestedWorkTime();

                        int resourceSize = resources.size();
                        IloIntExpr sumOfbvStepAResGroupResExpr = cp.intExpr();

                        // 遍历资源
                        for (int i = 0; i < resourceSize; i++) {
                            // 资源对应实体的Id
                            String resourceId = "";
                            //创建cumul function expression的key,用resource type 和 resource Id作为key
                            String cumulfeResourceId = "";
                            if (RESOURCE_TYPE_ENGINEER.equals(resourceType)
                                    || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)) {
                                EntityStaff staff = (EntityStaff) resources.get(i);
                                resourceId = staff.getStaffId();
                                cumulfeResourceId = resourceType + resourceId;
                                stepFunctionTime = subModelData.getStepFunctionStaffTime().get(resourceId);
                                stepFunctionValue = subModelData.getStepFunctionStaffValue().get(resourceId);
                            } else if (RESOURCE_TYPE_EQUIPMENT.equals(resourceType)) {
                                EntityEquipment equipment = (EntityEquipment) resources.get(i);
                                resourceId = equipment.getEquipmentId();
                                cumulfeResourceId = resourceType + resourceId;
                                stepFunctionTime = subModelData.getStepFunctionEquipmentTime().get(resourceId);
                                stepFunctionValue = subModelData.getStepFunctionEquipmentValue().get(resourceId);

                            }

                            //创建单个资源的binary变量
                            IloIntVar stepAResGroupResVar = cp.boolVar("bvStepAResGroupRes: subTask_" + subTaskNo + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId);
                            bvStepAResGroupRes.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, stepAResGroupResVar);
                            sumOfbvStepAResGroupResExpr = cp.sum(sumOfbvStepAResGroupResExpr,stepAResGroupResVar);

                            // 获得是否被分配的变量
                            //IloIntVar bvStepAResGroupResVar = (IloIntVar) bvStepAResGroupRes.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId);

                            // 生成stepFunction
                            int sizeTime = stepFunctionTime.size();
                            IloNumToNumStepFunction stepFResourceCalendar = cp.numToNumStepFunction();
                            for (int j = 0; j < sizeTime - 1; j++) {
                                stepFResourceCalendar.setValue(stepFunctionTime.get(j), stepFunctionTime.get(j + 1), stepFunctionValue.get(j));
                            }

                            // 有状态要求的资源需要在相应活动时在要求的状态中
                            // 获得状态函数

                            IloStateFunction resourceStateF = (IloStateFunction) statefResource.getVariable(resourceId);
                            if (resourceGroup.getState() != null && resourceStateF == null) {
                                //如果没有创建过，就创建变量
                                resourceStateF = cp.stateFunction("statefResource_" + resourceId);
                                statefResource.setVariable(resourceId, resourceStateF);
                            }

                            if (RESOURCE_GROUP_DAILY_MODE_NOT_BY_DAY.equals(dailyMode)) {
                                //不按天模式开始
                                //创建对应的决策变量
                                IloIntervalVar resourceANotByDayVar = cp.intervalVar("ivResourceANotByDay: subtask_" + subTaskNo
                                        + "_step_" + stepId + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId);
                                //设置对应的属性
                                //设置对应的optional属性
                                resourceANotByDayVar.setOptional();
                                //设置开始和结束时间,其中startMin设置为小阶段活动变量的开始时间，endMax设置为小阶段活动变量的结束时间
                                resourceANotByDayVar.setStartMin(stepAStartMin);
                                resourceANotByDayVar.setEndMax(stepAEndMax);

                                //设置变量的size
                                int variableSize = (int) Math.ceil(((double) workTime * stepActivity.getValidSampleQuantity() * 60) / planGranularity);
                                resourceANotByDayVar.setSizeMin(variableSize);
                                resourceANotByDayVar.setSizeMax(variableSize);
                                //设置stepFunction
                                resourceANotByDayVar.setIntensity(stepFResourceCalendar);
                                if (engineProcessedData.getPlan().isEnabledConstraintResourceAForbidStartEnd()) {
                                    cp.add(cp.forbidStart(resourceANotByDayVar, stepFResourceCalendar));
                                    cp.add(cp.forbidEnd(resourceANotByDayVar, stepFResourceCalendar));
                                }

                                ivResourceANotByDay.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resourceANotByDayVar);

                                if (engineProcessedData.getPlan().isEnabledConstraintResourceAPresenceNotByDay()) {
                                    cp.add(cp.eq(cp.presenceOf((IloIntervalVar) ivResourceANotByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId)),
                                            stepAResGroupResVar));
                                }

                                if (resourceGroup.isSameStart()) {
                                    // 要求同时开始
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAStartAtResANotByDay()) {
                                        cp.add(cp.startAtStart(stepAVar, resourceANotByDayVar));
                                    }
                                } else {
                                    // 不要求同时开始, 小阶段活动比相关的资源活动早开始
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAStartBeforeResANotByDay()) {
                                        cp.add(cp.startBeforeStart(stepAVar, resourceANotByDayVar));
                                    }
                                }
                                if (resourceGroup.isSameEnd()) {
                                    // 要求同时结束
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAEndAtResANotByDay()) {
                                        cp.add(cp.endAtEnd(stepAVar, resourceANotByDayVar));
                                    }
                                } else {
                                    // 不要求同时结束,小阶段活动比相关的设备活动晚结束
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAEndAfterResANotByDay()) {
                                        cp.add(cp.endBeforeEnd(resourceANotByDayVar, stepAVar));
                                    }
                                }

                                // 有状态要求的资源需要在相应活动时在要求的状态中，针对不按天的资源活动
                                if (resourceGroup.getState() != null) {
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationResourceAStateNotByDay()) {
                                        cp.add(cp.alwaysEqual(resourceStateF, resourceANotByDayVar,
                                                equipmentStateStringToIntMap.get(resourceGroup.getState())));
                                    }
                                }

                                // 设置资源组不按天的cumul function
                                // 根据类型是人员还是设备分别设置cumulfe
                                // 可以先统一获得资源需要的容量occupiedCapacity，需要乘以放大倍数
                                int occupiedCapacity = (int) (stepActivity.getOccupiedCapacity().get(resourceGroupName + "_" + resourceId)
                                        * DEFAULT_AMPLIFICATION_FACTOR);

                                if (cumulfeResource.getVariable(cumulfeResourceId) == null) {
                                    cumulfeResource.setVariable(cumulfeResourceId,
                                        cp.pulse((IloIntervalVar) ivResourceANotByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId),
                                            occupiedCapacity));
                                } else {
                                    cumulfeResource.setVariable(cumulfeResourceId,
                                        cp.sum((IloCumulFunctionExpr) cumulfeResource.getVariable(cumulfeResourceId),
                                            cp.pulse((IloIntervalVar) ivResourceANotByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId),
                                                occupiedCapacity)));
                                }

                                //设置表达式exprResourceUsage
                                if((RESOURCE_TYPE_ENGINEER.equals(resourceType)
                                        || RESOURCE_TYPE_TECHNICIAN.equals(resourceType))
                                        && resourceGroup.isConstraint()){
                                    // todo: 逻辑有问题，需要从exprResourceUsage里面get回来累加
                                    resourceUsage = (IloIntExpr) cp.sum(resourceUsage,cp.prod(cp.presenceOf((IloIntervalVar) ivResourceANotByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId)),resourceGroup.getRequestedWorkTime()));
                                }
                            } // 不按天模式结束
                            else if (RESOURCE_GROUP_DAILY_MODE_BY_DAY.equals(dailyMode)) {
                                //按天模式开始
                                // haolei todo: 没有考虑时区；也没有考虑模型的开始时间，所以得到的cal时间不对；也不应该上来就加一天，因为第一天如果时间够是可以安排的
                                // 第一天
                                Date date = DateUtil.getDateTime(stepAStartMin, planGranularity);
                                Date tempDate = DateUtil.getNextDay(date);

                                // 将时间转换成int
                                int tempTime = DateUtil.getIntTime(tempDate, planGranularity);

                                // res_day 变量总数
                                int resDaysSize;
                                if (tempTime > stepAEndMax) {
                                    resDaysSize = 1;
                                } else {
                                    resDaysSize = (int) Math.ceil((double) (stepAEndMax - tempTime) / oneDay) + 1;
                                }
                                stepActivity.setResDaysSize(resDaysSize);
                                //创建资源活动的变量的数组
                                IloIntervalVar[] ivArrayResourceAByDay = new IloIntervalVar[resDaysSize];
                                IloIntExpr sumOfResAByDayPresence = cp.intExpr();

                                // 遍历时间范围创建资源按天的变量
                                // haolei todo：可进一步优化，结合calendar，如果当天的工作时间小于要求的时间，则不需要建这天的变量
                                for (int k = 0; k < resDaysSize; k++) {
                                    // 创建小阶段资源活动的决策变量
                                    int resDay = k + 1;
                                    IloIntervalVar resAByDayVar = cp.intervalVar("ivResourceAByDay: subTask_" + subTaskNo + "_step_" + stepId
                                            + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId + "_day_" + resDay);
                                    int startMin;
                                    int endMax;
                                    // 第一天的开始时间单独设置
                                    if (k == 0) {
                                        startMin = stepAStartMin;
                                        endMax = tempTime;
                                    } else {
                                        startMin = tempTime;
                                        // 当到最后一天时，结束时间设置为 stepAVar 的结束时间
                                        if (k == resDaysSize - 1) {
                                            endMax = stepAEndMax;
                                        } else {
                                            endMax = tempTime + oneDay;
                                        }

                                        tempTime += oneDay;
                                    }

                                    // 设置资源的optional 属性
                                    // 设置开始和结束时间
                                    resAByDayVar.setOptional();
                                    resAByDayVar.setStartMin(startMin);
                                    resAByDayVar.setEndMax(endMax);
                                    // 设置size
                                    resAByDayVar.setSizeMax(workTime);
                                    resAByDayVar.setSizeMin(workTime);
                                    // 设置 Intensity
                                    resAByDayVar.setIntensity(stepFResourceCalendar);
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceAForbidStartEnd()) {
                                        cp.add(cp.forbidStart(resAByDayVar, stepFResourceCalendar));
                                        cp.add(cp.forbidEnd(resAByDayVar, stepFResourceCalendar));
                                    }

//                                    System.out.println("k:" + k +",startMin: " + startMin + ", endMax: "+ endMax);

                                    ivResourceAByDay.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resDay, resAByDayVar);
                                    ivArrayResourceAByDay[k] = resAByDayVar;
                                    sumOfResAByDayPresence = cp.sum(sumOfResAByDayPresence, cp.presenceOf(resAByDayVar));

                                    // 小阶段活动比相关的资源活动早开始
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAStartBeforeResAByDay()) {
                                        cp.add(cp.startBeforeStart(stepAVar, resAByDayVar));
                                    }
                                    // 小阶段活动比相关的设备活动晚结束
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAEndAfterResAByDay()) {
                                        cp.add(cp.endBeforeEnd(resAByDayVar, stepAVar));
                                    }

                                    // 有状态要求的资源需要在相应活动时在要求的状态中，针对按天的资源活动
                                    if (resourceGroup.getState() != null) {
                                        if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationResourceAStateByDay()) {
                                            cp.add(cp.alwaysEqual(resourceStateF, resAByDayVar,
                                                    equipmentStateStringToIntMap.get(resourceGroup.getState())));
                                        }
                                    }

                                    // 设置资源按天的cumul function
                                    // 可以先统一获得资源需要的容量occupiedCapacity，需要乘以放大倍数
                                    int occupiedCapacity = (int) (stepActivity.getOccupiedCapacity().get(resourceGroupName + "_" + resourceId)
                                            * DEFAULT_AMPLIFICATION_FACTOR);

                                    if (cumulfeResource.getVariable(cumulfeResourceId) == null) {
                                        cumulfeResource.setVariable(cumulfeResourceId,
                                                cp.pulse((IloIntervalVar) ivResourceAByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resDay),
                                                        occupiedCapacity));
                                    } else {
                                        cumulfeResource.setVariable(cumulfeResourceId,
                                                cp.sum((IloCumulFunctionExpr) cumulfeResource.getVariable(cumulfeResourceId), cp.pulse((IloIntervalVar) ivResourceAByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resDay),
                                                        occupiedCapacity)));
                                    }

                                    //设置表达式exprResourceUsage
                                    if((RESOURCE_TYPE_ENGINEER.equals(resourceType)
                                            || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)) ){
                                            //&& resourceGroup.isConstraint()){
                                        // todo: 逻辑有问题，需要从exprResourceUsage里面get回来累加
                                        resourceUsage = (IloIntExpr) cp.sum(resourceUsage,cp.prod(cp.presenceOf((IloIntervalVar) ivResourceAByDay.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resDay)),resourceGroup.getRequestedWorkTime()));
                                    }
                                }//按天循环结束

                                // 添加约束cResourceAPresenceByDay[plan_t][plan_st][stepAVar][resGroup_byDay][res]
                                if (engineProcessedData.getPlan().isEnabledConstraintResourceAPresenceByDay()) {
                                    cp.add(cp.eq(cp.prod(stepAResGroupResVar, resDayQty * stepActivity.getValidSampleQuantity()), sumOfResAByDayPresence));
                                }

                                //创建 对一个小阶段的活动的决策变量 (该变量会span每天的变量）
                                IloIntervalVar resourceASpanDayVar = cp.intervalVar("ivResourceASpanDays: subTask_" + subTaskNo + "_step_" + stepId + "_resourceGroupName_"
                                        + resourceGroupName + "_resource_" + resourceId+"_spanDays");

                                ivResourceASpanDays.setVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId, resourceASpanDayVar);
                                // 设置决策变量的属性
                                resourceASpanDayVar.setOptional();
                                // haolei todo：可进一步优化为根据每天判断的最早开始的时间和最晚结束的时间界
                                resourceASpanDayVar.setStartMin(stepAVar.getStartMin());
                                resourceASpanDayVar.setEndMax(stepAVar.getEndMax());

                                // 小阶段活动和相关的资源活动一同开始
                                if (resourceGroup.isSameStart()) {
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAStartAtResAByDay()) {
                                        cp.add(cp.startAtStart(stepAVar, resourceASpanDayVar));
                                    }
                                }

                                // 小阶段活动和相关的资源活动一同结束
                                if (resourceGroup.isSameEnd()) {
                                    if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAEndAtResAByDay()) {
                                        cp.add(cp.endAtEnd(stepAVar, resourceASpanDayVar));
                                    }
                                }
                                // 资源针对小阶段的跨多天活动需要span内部的多天活动。
                                if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationResASpanDays()) {
                                    cp.add(cp.span(resourceASpanDayVar, ivArrayResourceAByDay));
                                }
                            } // 按天模式结束

                            exprResourceUsage.setVariable(cumulfeResourceId,resourceUsage);
                        }// 资源循环结束
                        // 资源组内资源分配数量约束
                        // 获取资源组中的请求资源数量;
                        int resourceQuantity = resourceGroup.getRequestedResourceQuantity();
                        //小阶段的0，1变量和对应资源组里资源的数量的乘积
                        if (engineProcessedData.getPlan().isEnabledConstraintResourceARelationStepAAssignResGroupRes()) {
                            IloIntervalVar stepVar = (IloIntervalVar) ivStepA.getVariable(taskNo, subTaskNo, stepId);
                            if (stepVar.isPresent()) {
                                //对资源分配0、1变量在资源组的维度上求和，并且等于上述的小阶段的0，1变量和对应资源组里资源的数量的乘积
                                cp.add(cp.eq(sumOfbvStepAResGroupResExpr, resourceQuantity));
                            } else {
                                IloIntExpr stepAResourceQuantity = cp.prod(cp.presenceOf(stepVar), resourceQuantity);

                                //对资源分配0、1变量在资源组的维度上求和，并且等于上述的小阶段的0，1变量和对应资源组里资源的数量的乘积
                                cp.add(cp.eq(sumOfbvStepAResGroupResExpr, stepAResourceQuantity));
                            }
                        }
                    }//资源组循环结束
                }//小阶段活动循环结束
            } //批次循环结束
        }//任务单循环结束
        //设置表达式：每个人员的工作量
        subModel.setExprResourceUsage(exprResourceUsage);
        return true;
    }
}
