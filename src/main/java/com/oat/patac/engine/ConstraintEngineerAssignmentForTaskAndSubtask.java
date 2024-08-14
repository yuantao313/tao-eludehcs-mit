package com.oat.patac.engine;

import com.oat.cp.CPModel;
import com.oat.patac.entity.*;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.oat.common.utils.ConstantUtil.REPORTING_PHASE_NAME;
import static com.oat.common.utils.ConstantUtil.RESOURCE_TYPE_ENGINEER;

/**
 * 任务单责任工程师和批次、小阶段的工程师约束
 * 包含的约束有：
 * - 每个待排程任务单应该分配一个责任工程师约束
 *   cTaskAssignEngineer[plan_t]
 * - 如果小阶段的大阶段是“报告撰写”，则该小阶段的工程师和任务单责任工程师为一个人；
 *   如果批次内没有“报告撰写”大阶段，则至少一个小阶段活动的工程师和任务单责任工程师是一个人
 *   cTaskAndStepAAssignEngineer[plan_t][possible_resp_engineer]
 */
@Log4j2
public class ConstraintEngineerAssignmentForTaskAndSubtask extends PatacConstraint{

    public ConstraintEngineerAssignmentForTaskAndSubtask(SubModel model){
        super(model);
        setConstraintName("Engineer Assignment For Task And StepA Engineer");
    }

    @Override
    public boolean apply() throws IloException {

        //获得子模型需要考虑的任务单和下面的批次
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getToBePlannedSubTaskMap();
        HashMap<String, EntityTask> taskHashMap = engineProcessedData.getTaskHashMap();

        //获得五维的变量数组
        CPModel.Var5DArray bvStepAResGroupRes = subModel.getBvStepAResGroupRes();
        //获得任务单对应的责任工程师的二维变量
        CPModel.Var2DArray bvTaskRespEngineer = subModel.getBvTaskRespEngineer();

        //循环每个task，得到下面的subTask列表;
        for (Map.Entry<String,ArrayList<EntitySubTask>> entry: subTaskMap.entrySet()) {
            //获得任务单No和对应的批次的集合
            String taskNo = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            //获得任务单以及任务单对应的任务单负责人
            EntityTask task = taskHashMap.get(taskNo);
            ArrayList<EntityStaff> engineers = task.getPossibleRespEngineers();

            int size = engineers.size();
            //创建可能的engineer 的0/1变量的数组
            IloIntVar[] ivArrayEngineer = new IloIntVar[size];

            for (int i = 0; i < size; i++) {
                //循环可能的工程师
                EntityStaff engineer = engineers.get(i);
                String engineerId = engineer.getStaffId();
                //创建每个待排程任务单和可分配的责任工程师为维度的0/1决策变量
                IloIntVar taskBoolVar = cp.boolVar("bvTaskRespEngineer: task_" + taskNo + "_engineer_" + engineerId);
                bvTaskRespEngineer.setVariable(taskNo, engineerId, taskBoolVar);
                ivArrayEngineer[i] = taskBoolVar;
                //bvTaskRespEngineer.getVariable(taskNo, engineerId);

                //循环每个批次
                for (EntitySubTask subTask : subTasks) {
                    //获得对应的批次的No和对应的小阶段的集合
                    String subTaskNo = subTask.getSubTaskNo();
                    ArrayList<EntityStepActivity> stepActivities = subTask.getStepActivities();
                    //获得对应批次的是否有报告的阶段
                    boolean hasReportingStep = subTask.isHasReportingStep();
                    //设置各批次内小阶段对该工程师的分配的变量和的expression
                    ArrayList<IloIntVar> bvListStepAAssignEngineerPerSubtask = new ArrayList<>();
                    //循环小阶段，给对应的资源组加约束
                    for (EntityStepActivity stepActivity : stepActivities) {
                        //获得对应的小阶段
                        EntityStep step = stepActivity.getStep();
                        //得到对应的小阶段的No
                        int stepActivityNo = step.getStepId();
                        //对应小阶段活动的大阶段名称Test_Phase
                        String stepATestPhase = step.getTestPhase();
                        //循环下面每个资源组
                        HashMap<String, ArrayList<Object>> resourceGroupMap = stepActivity.getPossibleResources();
                        for (Map.Entry<String, ArrayList<Object>> resourceGroup : resourceGroupMap.entrySet()) {
                            //获得每个资源组的名称
                            String resourceGroupName = resourceGroup.getKey();
                            //根据资源组名称获得资源组对象、类型和资源组内的资源数量
                            EntityResourceGroup resGroupResource = step.getResourceGroupHashMap().get(resourceGroupName);
                            String resourceType = resGroupResource.getResourceType();
                            //仅针对工程师资源组
                            if (!resourceType.equals(RESOURCE_TYPE_ENGINEER)) {
                                continue;
                            }
                            //循环每个资源组的资源，并且创立对应的资源的binary的变量
                            ArrayList<Object> possibleResources = resourceGroup.getValue();
                            //资源不需要循环，因为需要找和第二层循环的engineer是同一个engineer的变量进行设置
                            IloIntVar stepBoolVar = (IloIntVar) bvStepAResGroupRes.getVariable(taskNo, subTaskNo, stepActivityNo, resourceGroupName, engineerId);
                            if (hasReportingStep) {
                                if (REPORTING_PHASE_NAME.equals(stepATestPhase)) {
                                    if (!possibleResources.contains(engineer)) {
                                        //报错 todo
                                        //如果预处理逻辑正确，不应该出现该错误
                                        log.error("task " + taskNo + "的可能的责任工程师不在其下面的批次" + subTaskNo + "的报告撰写大阶段可以分配的工程师例表中。");
                                    }
                                    // 约束：如果小阶段的大阶段是“报告撰写”，则这些小阶段每个小阶段中，任务单责任工程师必然也是小阶段的工程师之一
                                    if (engineProcessedData.getPlan().isEnabledConstraintEngineerAssignmentForReportingPhase()) {
                                        cp.add(cp.ge(stepBoolVar,taskBoolVar));

                                        log.info("ConstraintEngineerAssignmentForReportingPhase:: " + stepBoolVar.getName() + " GREATER THAN OR EQUALS TO " + taskBoolVar.getName());
                                    }
                                }
                            } else {
                                if (possibleResources.contains(engineer)) {
                                    bvListStepAAssignEngineerPerSubtask.add(stepBoolVar);
                                }

                            }
                        }//资源组循环结束
                    }//小阶段循环结束
                    if (!hasReportingStep) {
                        if (bvListStepAAssignEngineerPerSubtask.size() == 0) {
                            //报错 todo
                            //如果预处理逻辑正确，不应该出现该错误
                            log.error("task " + taskNo + "的可能的责任工程师不在其下面的批次" + subTaskNo + "的任何小阶段的可以分配的工程师例表中。");
                        }
                        IloIntExpr sumOfBvStepAAssignEngineerPerSubtask = cp.sum(arrayFromList(bvListStepAAssignEngineerPerSubtask));
                        //约束：如果批次内没有“报告撰写”大阶段，则每个批次内至少一个小阶段活动的工程师和任务单责任工程师是一个人
                        if (engineProcessedData.getPlan().isEnabledConstraintEngineerAssignmentForTaskAndStepA()) {
                            cp.add(cp.ge(sumOfBvStepAAssignEngineerPerSubtask,taskBoolVar));

                            log.info("ConstraintEngineerAssignmentForTaskAndStepA:: " + taskBoolVar.getName() + " LESS THAN OR EQUAL TO SUM OF " + sumOfBvStepAAssignEngineerPerSubtask.toString());
                        }
                    }
                }//批次循环结束
            }//工程师循环结束
            // 约束：每个待排程任务单应该分配一个责任工程师
            IloIntExpr sumOfEngineerVariable = cp.sum(ivArrayEngineer);
            //创建task span subtask的约束
            if (engineProcessedData.getPlan().isEnabledConstraintEngineerAssignmentForTask()){
                cp.add(cp.eq(sumOfEngineerVariable, 1));

                log.info("ConstraintEngineerAssignmentForTask:: 1 EQUALS TO SUM OF " + sumOfEngineerVariable.toString());

            }
        }//任务单循环结束
        return true;
    }

    static IloIntVar[] arrayFromList(List<IloIntVar> list) {
        return list.toArray(new IloIntVar[list.size()]);
    }
}
