package com.oat.patac.engine;


import com.oat.cp.CPModel;
import com.oat.cp.ConstraintViolationPlanResult;
import com.oat.patac.entity.EntityEquipment;
import com.oat.patac.entity.EntityEquipmentPlan;
import ilog.concert.IloException;
import ilog.concert.IloIntervalVar;
import ilog.concert.IloStateFunction;
import lombok.extern.log4j.Log4j2;

import java.util.*;

import static com.oat.common.utils.ConstantUtil.RESOURCE_GROUP_IS_CONSTRAINT;
import static com.oat.common.utils.ConstantUtil.RESOURCE_TYPE_EQUIPMENT;

/**
 * @author:yhl
 * @create: 2022-09-30 11:57
 * @Description:
 * 包含的约束：
 * 有状态要求的资源，已经排好的活动需要设置为相应的状态
 * cResourceAFixedState[resGroup_with_state][res_planned]
 */
@Log4j2
public class ConstraintResourceAFixedState extends PatacConstraint{
    public ConstraintResourceAFixedState(CPModel model) {
        super(model);
        setConstraintName("ResourceA Fixed State Constraint");
    }

    @Override
    public boolean apply() throws IloException {
        HashMap<String, HashSet<String>> equipmentsWithState = subModelData.getEquipmentsWithState();
        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanInputHashMap = engineProcessedData.getEquipmentEquipmentPlanInputHashMap();
        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = engineProcessedData.getEquipmentEquipmentPlanOutputHashMap();
        HashMap<String, EntityEquipment> equipmentHashMap = engineProcessedData.getEquipmentHashMap();
        //得到state function
        CPModel.Var1DArray statefResource = subModel.getStatefResource();
        HashMap<String, Integer> equipmentStateStringToIntMap = engineProcessedData.getEquipmentStateStringToIntMap();

        CPModel.Var2DArray ivResourceAFixed = subModel.getIvResourceAFixed();
        // 遍历子模型中所有可能用的并带状态的设备列表
        for (Map.Entry<String, HashSet<String>> entry : equipmentsWithState.entrySet()) {
            EntityEquipment equipment = equipmentHashMap.get(entry.getKey());
            if (equipment == null){
                // 不应该到这个分支
                continue;
            }
            // 获取该设备对应输入输出表中的对象
            String resourceId = equipment.getEquipmentId();
            // 得到state function 状态函数
            IloStateFunction resourceStateF = (IloStateFunction) statefResource.getVariable(resourceId);
            if (resourceStateF == null){
                // todo: 报错。不过如果预处理正确，不会出现这种情况
                // 前提是这个约束放在ContraintResourceARelations后面
                continue;
            }
            ArrayList<EntityEquipmentPlan> equipmentPlanInputs = equipmentEquipmentPlanInputHashMap.get(resourceId);
            ArrayList<EntityEquipmentPlan> equipmentPlanOutputs = equipmentEquipmentPlanOutputHashMap.get(resourceId);

            if (equipmentPlanInputs != null || equipmentPlanOutputs != null){
                if (equipmentPlanOutputs != null){
                    equipmentPlanInputs.addAll(equipmentPlanOutputs);
                }
//                else {
//                    equipmentPlanInputs = equipmentPlanOutputs;
//                }
                // 遍历输入输出设备表
                for (EntityEquipmentPlan equipmentPlan : equipmentPlanInputs) {
                    String equipmentPlanId = equipmentPlan.getEquipmentPlanId();
                    String equipmentStatus = equipmentPlan.getEquipmentStatus();

                    // 判断是否时间在子模型cover的范围内
                    if (equipmentPlan.getEquipmentStartInModel().before(subModelData.getMaxPlanEndTime())
                            && equipmentPlan.getEquipmentEndInModel().after(subModelData.getMinPlanStartTime())){
                        // 需要capacity constraint放在前面
                        IloIntervalVar resAFixedVar = (IloIntervalVar) ivResourceAFixed.getVariable(RESOURCE_TYPE_EQUIPMENT+resourceId, equipmentPlanId);
                        if (resAFixedVar == null) {
                            //todo: 报错。如果约束顺序对，不会报这个错误
                        }
                        if (!equipmentPlan.getIsConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT)){
                            // 需要同时is constraint = 1 才考虑该约束
                            continue;
                        }
                        if (equipmentStatus == null) {
                            //todo: 可报warning
                            // 没有状态就不加这个约束
                            continue;
                        }

                        if (engineProcessedData.getPlan().isEnabledConstraintResourceAFixedState()) {
                            cp.add(cp.alwaysEqual(resourceStateF, resAFixedVar,
                                    equipmentStateStringToIntMap.get(equipmentStatus)));

                            log.info("ConstraintResourceAFixedState:: " + resAFixedVar.getName() + " ALWAYS EQUAL TO resourceStateF ");
                        }
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
