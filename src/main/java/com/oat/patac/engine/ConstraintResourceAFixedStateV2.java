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
public class ConstraintResourceAFixedStateV2 extends PatacConstraint{
    public ConstraintResourceAFixedStateV2(CPModel model) {
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

        // 遍历子模型中所有可能用的并带状态的设备列表
        for (Map.Entry<String, HashSet<String>> entry : equipmentsWithState.entrySet()) {
            EntityEquipment equipment = equipmentHashMap.get(entry.getKey());
            if (equipment == null){
                // 不应该到这个分支
                continue;
            }
            // 本来想实现如果总体而言只有一个状态，也不用考虑状态约束，但是这还需要一轮循环来做数据处理，所以暂时不做了
            // HashSet<String> allStates = entry.getValue(); // this is only from resource group requirements/variables, not from equipment plan inputs yet

            // 获取该设备对应输入输出表中的对象
            String resourceId = equipment.getEquipmentId();
            // 得到state function 状态函数
            IloStateFunction resourceStateF = (IloStateFunction) statefResource.getVariable(resourceId);
            if (resourceStateF == null){
                // todo: 报错。不过如果预处理正确，不会出现这种情况
                // 前提是这个约束放在ContraintResourceARelations后面
                continue;
            }
            ArrayList<EntityEquipmentPlan> equipmentPlanInputs = combineEquipmentPlanInputsAndOutputs(
                equipmentEquipmentPlanInputHashMap.get(resourceId),
                equipmentEquipmentPlanOutputHashMap.get(resourceId));

            // 遍历输入输出设备表
            for (EntityEquipmentPlan equipmentPlan : equipmentPlanInputs) {
                String equipmentStatus = equipmentPlan.getEquipmentStatus();

                if (!equipmentPlan.getIsConstraint().equals(RESOURCE_GROUP_IS_CONSTRAINT)){
                    // 需要同时is constraint = 1 才考虑该约束
                    continue;
                }
                if (equipmentStatus == null || equipmentStatus.equals("")) {
                    //todo: 可报warning
                    // 没有状态就不加这个约束
                    continue;
                }
                // 判断是否时间在子模型cover的范围内
                if (equipmentPlan.getEquipmentStartInModel().before(subModelData.getMaxPlanEndTime())
                        && equipmentPlan.getEquipmentEndInModel().after(subModelData.getMinPlanStartTime())){
                    int intStart = getStartOrEndIntTimeForFixedResourceA(equipmentPlan.getEquipmentStartInModel(),
                            subModelData.getMinPlanStartTime(), subModelData.getMaxPlanEndTime(),
                            subModelData.getPlanGranularity(), true);
                    int intEnd = getStartOrEndIntTimeForFixedResourceA(equipmentPlan.getEquipmentEndInModel(),
                            subModelData.getMinPlanStartTime(), subModelData.getMaxPlanEndTime(),
                            subModelData.getPlanGranularity(), false);

                    if (engineProcessedData.getPlan().isEnabledConstraintResourceAFixedState()) {
                        cp.add(cp.alwaysEqual(resourceStateF, intStart, intEnd,
                                equipmentStateStringToIntMap.get(equipmentStatus)));

                        log.info("ConstraintResourceAFixedState:: " + resourceId + " ALWAYS EQUAL TO resourceStateF ");
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
