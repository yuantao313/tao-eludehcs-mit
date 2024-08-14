package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.cp.ConstraintViolationPlanResult;
import com.oat.patac.entity.EntitySubTask;
import com.oat.patac.entity.EntityTask;
import ilog.concert.IloException;
import ilog.concert.IloIntervalVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumExprArray;
import ilog.concert.cppimpl.IloIntArray;
import ilog.concert.IloIntExpr;
import lombok.extern.log4j.Log4j2;

import java.util.*;

/**
 * 批次组顺序约束
 */
@Log4j2
public class ConstraintSubtaskGroupSequence extends PatacConstraint {

    public ConstraintSubtaskGroupSequence(SubModel model) {
        super(model);
        setConstraintName("Sub Task Group Sequence Constraint");
    }

    @Override
    public boolean apply() throws IloException {
        //获得子模型中需要考虑的任务单和下面的批次
        HashMap<String, ArrayList<EntitySubTask>> toBePlannedSubTaskMap = subModelData.getToBePlannedSubTaskMap();
        HashSet<EntitySubTask> subTasksInSubModel = new HashSet<>();
        for (Map.Entry<String,ArrayList<EntitySubTask>> entry: toBePlannedSubTaskMap.entrySet()) {
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            subTasksInSubModel.addAll(subTasks);
        }


        //不用创建sub task的interval variable，已经在task span subtask的约束中创建，直接从sub model中获得
        CPModel.Var2DArray ivSubTask = subModel.getIvSubTask();
        //循环每个Task和Task内的SubTask，加约束

        for (Map.Entry<String,ArrayList<EntitySubTask>> entry: toBePlannedSubTaskMap.entrySet()) {
            String taskNo = entry.getKey();
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            //遍历每个批次，如果有previous的批次，则加该约束
            int size = subTasks.size();
            for (int i = 0; i < size; i++) {
                EntitySubTask subTask = subTasks.get(i);
                EntitySubTask previousSubTask = subTask.getPrevious();

                // 当前 submodel 需要考虑的subtask里面，就找那个subtask的前一个，直到找到或者为空为止
                while (!subTasksInSubModel.contains(previousSubTask) && previousSubTask != null) {
                    previousSubTask = previousSubTask.getPrevious();
                }

                // todo: 测试代码注释掉
                // 测试代码
                if (previousSubTask == null) {
                    log.info("批次 " + subTask.getSubTaskNo() + " 没有前序批次");
                } else {
                    log.info("批次 " + subTask.getSubTaskNo() + " 的前序批次是 " + previousSubTask.getSubTaskNo());
                }

                if (previousSubTask != null) {
                    if (engineProcessedData.getPlan().isEnabledConstraintSubtaskGroupSequence()) {
                        //创建批次组顺序约束
                        IloIntervalVar previousVar = (IloIntervalVar) ivSubTask.getVariable(previousSubTask.getTaskNo(), previousSubTask.getSubTaskNo());
                        IloIntervalVar var = (IloIntervalVar) ivSubTask.getVariable(taskNo, subTask.getSubTaskNo());
                        cp.add(cp.endBeforeStart(previousVar,var));

                        log.info("ConstraintSubtaskGroupSequence:: " + previousVar.getName() + " END BEFORE START " + var.getName());

                    }
                }
            }
        }
        return true;
    }

    @Override
    public Vector<ConstraintViolationPlanResult> check() {
        return null;
    }
}
