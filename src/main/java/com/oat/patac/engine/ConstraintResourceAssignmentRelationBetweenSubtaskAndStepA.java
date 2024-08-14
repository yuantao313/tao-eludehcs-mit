package com.oat.patac.engine;

import com.oat.cp.CPModel;
import com.oat.patac.entity.*;
import ilog.concert.*;
import ilog.concert.cppimpl.IloBoolVar;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * 批次中使用的资源和小阶段使用的资源的关系，以及固定设备约束
 * 包含的约束有：
 * - 批次中任何一个小阶段中使用的资源作为批次使用的资源
 * cSubtaskStepAResRelation[plan_t][plan_st][res_group][res]
 * - 批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多。
 * cSubtaskAssignSameRes[plan_t][plan_st][resGroup_fixing_res][res]
 */
@Log4j2
public class ConstraintResourceAssignmentRelationBetweenSubtaskAndStepA extends PatacConstraint{
    public ConstraintResourceAssignmentRelationBetweenSubtaskAndStepA(CPModel model){
        super(model);
        setConstraintName("Resource Assignment Relation Between Subtask And StepA Constraint");
    }

    boolean isPrintConstraintLog = false;

    @Override
    public boolean apply() throws IloException {
        //获得子模型中需要考虑的任务单和下面待排程的批次
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getToBePlannedSubTaskMap();
        // 获得变量
        CPModel.Var4DArray bvSubtaskResource = subModel.getBvSubtaskResource();
        CPModel.Var5DArray bvStepAResGroupRes = subModel.getBvStepAResGroupRes();
        //获得exprAdditionalUsedResourceQty
        IloIntExpr exprAdditionalUsedResourceQty = subModel.getExprAdditionalUsedResourceQty();


        for(Map.Entry<String, ArrayList<EntitySubTask>> entry : subTaskMap.entrySet()){
            // 获得对应的任务单单号和任务单
            String taskNo = entry.getKey();
            // 获得下面全部的批次的列表
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            for (EntitySubTask subTask : subTasks) {
                String subTaskNo = subTask.getSubTaskNo();
                //得到每个批次下的资源组列表
                HashMap<String, HashSet<Object>> possibleResources = subTask.getPossibleResources();
                HashMap<String, String> resourceGroupType = subTask.getResourcesGroupTypeHashMap();
                HashMap<String, Integer> maxRequestedResourceQuantity = subTask.getMaxRequestedResourceQuantity();
                HashMap<String, Integer> minRequestedResourceQuantity = subTask.getMinRequestedResourceQuantity();
                HashSet<String> resourceGroupsFixingResource = subTask.getResourceGroupsFixingResource();

                for(Map.Entry<String, HashSet<Object>> resourceGroupMap : possibleResources.entrySet()){
                    //获得资源组的名称和对应的列表
                    String resourceGroupName = resourceGroupMap.getKey();

                    HashSet<Object> resources = resourceGroupMap.getValue();
                    String resourceType = resourceGroupType.get(resourceGroupName);
                    int maxRequestedResQty = maxRequestedResourceQuantity.get(resourceGroupName);
                    int minRequestedResQty = minRequestedResourceQuantity.get(resourceGroupName);
                    boolean isFixingResource = false;
                    if (resourceGroupsFixingResource.contains(resourceGroupName)) {
                        isFixingResource = true;
                    }
                    // 资源组下用的资源个数的和
                    IloIntVar[] bvSubtaskResourceArray = new IloIntVar[resources.size()];
                    int i = -1;
                    for (Object resource : resources) {
                        i++;
                        String resourceId = "";
                        if (RESOURCE_TYPE_ENGINEER.equals(resourceType)
                                || RESOURCE_TYPE_TECHNICIAN.equals(resourceType)) {
                            EntityStaff staff = (EntityStaff) resource;
                            resourceId = staff.getStaffId();
                        } else if (RESOURCE_TYPE_EQUIPMENT.equals(resourceType)) {
                            EntityEquipment equipment = (EntityEquipment) resource;
                            resourceId = equipment.getEquipmentId();
                        }
                        IloIntVar subtaskResourceVar = cp.boolVar("bvSubtaskResource: subtask_" + subTaskNo
                                + "_resourceGroupName_" + resourceGroupName + "_resource_" + resourceId);
                        bvSubtaskResource.setVariable(taskNo, subTaskNo, resourceGroupName, resourceId, subtaskResourceVar);
                        bvSubtaskResourceArray[i] = subtaskResourceVar;

                        //遍历小阶段，得到对应的小阶段的资源变量
                        ArrayList<EntityStepActivity> stepActivities = subTask.getStepActivities();
                        ArrayList<IloIntVar> bvStepAResGroupResArrayList = new ArrayList<>();
                        for (EntityStepActivity stepActivity : stepActivities) {
                            Integer stepId = stepActivity.getStep().getStepId();
                            IloBoolVar bvStepAResGroupResVar
                                    = (IloBoolVar) bvStepAResGroupRes.getVariable(taskNo, subTaskNo, stepId, resourceGroupName, resourceId);
                            if (bvStepAResGroupResVar != null) {
                                //如果取得到这个变量就加入
                                bvStepAResGroupResArrayList.add(bvStepAResGroupResVar);
                                //添加使用固定资源的批次和小阶段活动和资源的关系 ： 批次是否使用某个资源 小于等于 批次中任何一个小阶段中是否使用该资源
                                if (isFixingResource && engineProcessedData.getPlan().isEnabledConstraintResourceAssignmentRelationSubtaskStepA()) {
                                    cp.add(cp.le(subtaskResourceVar, bvStepAResGroupResVar));
                                    if (isPrintConstraintLog) {
                                        log.info("ConstraintResourceAssignmentRelationSubtaskStepA:: " + subtaskResourceVar.getName()
                                                + " LESS THAN OR EQUALS TO " + bvStepAResGroupResVar.getName());
                                    }
                                }
                            }
                        }
                        if (!isFixingResource) {
                            IloIntVar[] bvStepAResGroupResArray
                                = bvStepAResGroupResArrayList.toArray(new IloIntVar[bvStepAResGroupResArrayList.size()]);
                            //添加批次和小阶段活动和资源的关系 ： 批次中任何一个小阶段中使用的资源作为批次使用的资源
                            if (engineProcessedData.getPlan().isEnabledConstraintResourceAssignmentRelationSubtaskStepA()) {
                                cp.add(cp.eq(subtaskResourceVar, cp.max(bvStepAResGroupResArray)));
                                if (isPrintConstraintLog) {
                                    log.info("ConstraintResourceAssignmentRelationSubtaskStepA:: " + subtaskResourceVar.getName() + " EQUALS TO MAX OF ");
                                    Stream.of(bvStepAResGroupResArray).forEach(v -> log.info("ConstraintResourceAssignmentRelationSubtaskStepA(:: " + v.getName()));
                                }
                            }
                        }

                    }//资源循环结束

                    // 添加资源分配的约束 ： 批次中要求使用固定设备的设备组中，一共被使用的设备数量和每个小阶段要求使用的最大的数字一样多。
                    if (isFixingResource) {
                        //仅针对批次中要求使用固定设备的设备组
                        if (engineProcessedData.getPlan().isEnabledConstraintResourceAssignmentRelationSubtaskAssignSameRes()) {
//                            cp.add(cp.eq(cp.sum(bvSubtaskResourceArray), maxRequestedResQty));
//                            if (isPrintConstraintLog) {
//                                log.info("ConstraintResourceAssignmentRelationSubtaskAssignSameRes:: " + maxRequestedResQty + " EQUALS TO SUM OF ");
//                                Stream.of(bvSubtaskResourceArray).forEach(v -> log.info("ConstraintResourceAssignmentRelationSubtaskAssignSameRes:: " + v.getName()));
//                            }
                            cp.add(cp.ge(cp.sum(bvSubtaskResourceArray), minRequestedResQty));
                            if (isPrintConstraintLog) {
                                log.info("ConstraintResourceAssignmentRelationSubtaskAssignSameRes:: " + minRequestedResQty + " LESS THAN OR EQUALS TO SUM OF ");
                                Stream.of(bvSubtaskResourceArray).forEach(v -> log.info("ConstraintResourceAssignmentRelationSubtaskAssignSameRes:: " + v.getName()));
                            }
                        }
                    } else {
                        IloIntExpr additionalQty = cp.max(0, cp.diff(cp.sum(bvSubtaskResourceArray), maxRequestedResQty));
                        if (exprAdditionalUsedResourceQty == null) {
                            exprAdditionalUsedResourceQty = additionalQty;
                        } else {
                            exprAdditionalUsedResourceQty = cp.sum(exprAdditionalUsedResourceQty, additionalQty);
                        }
                    }
                }//资源组循环结束
            }
        }
        //设置表达式：尽量减少每个批次在每个资源组中使用资源的数量
        subModel.setExprAdditionalUsedResourceQty(exprAdditionalUsedResourceQty);
        return true;
    }
}
