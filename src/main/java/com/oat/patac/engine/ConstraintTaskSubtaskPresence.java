package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.cp.ConstraintViolationPlanResult;
import com.oat.patac.entity.EntitySubTask;
import com.oat.patac.entity.EntityTask;
import ilog.concert.*;
import ilog.concert.cppimpl.IloIntVarArray;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Stream;

/**
 * 对optional的任务单，任务单和批次的排入状态应该一致
 */
@Log4j2
public class ConstraintTaskSubtaskPresence extends PatacConstraint {

    public ConstraintTaskSubtaskPresence(CPModel model) {
        super(model);
        setConstraintName("Task Sub Task Group Presence Constraint");
    }
    @Override
    public boolean apply() throws IloException {
        //获得子模型中需要考虑的任务单和下面的批次
        HashMap<String, ArrayList<EntitySubTask>> subTaskMap = subModelData.getToBePlannedSubTaskMap();

        HashMap<String, EntityTask> taskHashMap = engineProcessedData.getTaskHashMap();

        int optionalTaskSize = 0 ;
        IloIntExpr exprOptionalTasks = subModel.getExprOptionalTasks();

        //不用创建task和subtask的interval variable，已经在task span subtask的约束中创建，直接从sub model中获得
        CPModel.Var1DArray ivTask = subModel.getIvTask();
        CPModel.Var2DArray ivSubtask = subModel.getIvSubTask();

        //循环每个Task和Task内的SubTask，加约束
        for (Map.Entry<String,ArrayList<EntitySubTask>> entry: subTaskMap.entrySet()) {
            String taskNo = entry.getKey();
            EntityTask task = taskHashMap.get(taskNo);
            if (!task.getIsOptional()){
                continue;
            }
            ArrayList<EntitySubTask> subTasks = entry.getValue();
            IloIntervalVar taskVariable = (IloIntervalVar) ivTask.getVariable(taskNo);

            int size = subTasks.size();
            for (int i = 0; i < size; i++) {
                EntitySubTask subTask = subTasks.get(i);
                IloIntervalVar subTaskVariable = (IloIntervalVar) ivSubtask.getVariable(taskNo, subTask.getSubTaskNo());
                if (engineProcessedData.getPlan().isEnabledConstraintTaskSubtaskPresence()) {
                    cp.add(cp.eq(cp.presenceOf(taskVariable), cp.presenceOf(subTaskVariable)));

                    log.info("ConstraintTaskSubtaskPresence:: " + taskVariable.getName() + " SAME PRESENCE WITH " + subTaskVariable.getName());
                }
            }

            // 当该任务单是optional时，才计数
            optionalTaskSize = optionalTaskSize + 1;
            if (exprOptionalTasks == null){
                exprOptionalTasks =cp.presenceOf(taskVariable);
            } else {
                exprOptionalTasks = cp.sum(exprOptionalTasks, cp.presenceOf(taskVariable));
            }

        }
        // 设置表达式：尽量排入多的任务单
        if (optionalTaskSize > 0) {
            exprOptionalTasks = cp.diff(optionalTaskSize, exprOptionalTasks);
        }
        subModel.setExprOptionalTasks(exprOptionalTasks);
        return true;
    }

    @Override
    public Vector<ConstraintViolationPlanResult> check() {
        return null;
    }
}
