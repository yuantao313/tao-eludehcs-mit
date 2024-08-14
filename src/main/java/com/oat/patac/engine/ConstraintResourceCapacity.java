package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.patac.entity.*;
import ilog.concert.*;
import lombok.extern.log4j.Log4j2;

import java.util.*;

import static com.oat.common.utils.ConstantUtil.*;

/**
 * 包含的约束有：
 * 资源容量限制 cResourceCapacity[res]
 */
@Log4j2
public class ConstraintResourceCapacity extends PatacConstraint{

    public ConstraintResourceCapacity(CPModel model){
        super(model);
        setConstraintName("Resource Capacity Constraint");
    }

    @Override
    public boolean apply() throws IloException {
        // 通过颗粒度计算一天的数值
        int granularity = subModelData.getPlanGranularity();

        // 子模型开始时间
        Date subModelStart = subModelData.getMinPlanStartTime();
        // 子模型结束时间
        Date subModelEnd = subModelData.getMaxPlanEndTime();

        //获得对应的二维变量
        CPModel.Var2DArray ivResourceAFixed = subModel.getIvResourceAFixed();

        //得到人员和设备资源的cumul function expression
        CPModel.Var1DArray cumulfeResource = subModel.getCumulFunctionExpression();

        //得到表达式exprResourceUsage
        CPModel.Var1DArray exprResourceUsage = subModel.getExprResourceUsage();
        //得到表达式exprResourceUnbalance
        IloNumExpr exprResourceUnbalance = subModel.getExprResourceUnbalance();

        //获得Staff_Input和Staff_Output表
        HashMap<String, ArrayList<EntityStaffPlan>> staffPlanInputHashMap = engineProcessedData.getStaffStaffPlanInputHashMap();
        HashMap<String, ArrayList<EntityStaffPlan>> staffPlanOutputHashMap = engineProcessedData.getStaffStaffPlanOutputHashMap();
        //获得Equipment_Input和Equipment_Output表
        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanInputHashMap = engineProcessedData.getEquipmentEquipmentPlanInputHashMap();
        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = engineProcessedData.getEquipmentEquipmentPlanOutputHashMap();

        //得到对应的资源（员工）列表
        HashSet<EntityStaff> possibleStaffs = subModelData.getPossibleStaffs();
        for (EntityStaff possibleStaff : possibleStaffs) {
            //得到对应的fixed的员工id
            String fixedStaffId = possibleStaff.getStaffId();
            String resTypeResourceId ;
            if(possibleStaff.getResourceType().equals( RESOURCE_TYPE_ENGINEER)){
                resTypeResourceId = RESOURCE_TYPE_ENGINEER + fixedStaffId;
            }else{
                resTypeResourceId = RESOURCE_TYPE_TECHNICIAN + fixedStaffId;
            }
            IloCumulFunctionExpr resourceCumulFE = (IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId);
            if (resourceCumulFE == null){
                // 如果资源变量的occupiedCapacity是0，可能没有cumul function，没有必要再加capacity约束了
                log.info("resource " + resTypeResourceId + " cumul function is null, no need to add capacity constraint.");
                continue;
            }

            //得到对应的员工活动计划
            ArrayList<EntityStaffPlan> staffPlanInputs = staffPlanInputHashMap.get(fixedStaffId);
            ArrayList<EntityStaffPlan> staffPlanOutputs = staffPlanOutputHashMap.get(fixedStaffId);
            if (staffPlanInputs == null){
                staffPlanInputs = new ArrayList<>();
            }
            if (staffPlanOutputs !=null) {
                staffPlanInputs.addAll(staffPlanOutputs);
            }
            //遍历对应的员工的活动计划
            for (EntityStaffPlan staffPlanInput : staffPlanInputs) {
                //得到固定人员占用的capacity
                int occupiedCapacity = (int) (staffPlanInput.getOccupiedCapacity() * DEFAULT_AMPLIFICATION_FACTOR);
                if (occupiedCapacity <= 0){
                    continue;
                }

                //得到对应的员工计划的ID
                String fixedStaffPlanId = staffPlanInput.getStaffPlanId();

                IloIntervalVar resourceAFixedVar = cp.intervalVar("ivResourceAFixed: Staff_" + fixedStaffId + "_StaffPlan_" + fixedStaffPlanId);
                ivResourceAFixed.setVariable(resTypeResourceId,fixedStaffPlanId,resourceAFixedVar);

                //从input和output表中处理后的人员的开始时间
                Date staffStartInModel = staffPlanInput.getStaffStartInModel();
                int startIntTime;
                if(staffStartInModel.compareTo(subModelStart) > 0){
                    //从input和output表中处理后的人员的开始时间最晚的开始时间时，设置该时间为startMin和StartMax
                    startIntTime = DateUtil.getDistanceIntTime(staffStartInModel, subModelStart,
                            1000 * 60 * granularity);
                }else{
                    //当模型的排程开始时间是最晚的开始时间，设置该时间为startMin和StartMax
                    startIntTime = 0;
                    //DateUtil.getDistanceIntTime(subModelStart, subModelStart, 1000 * 60 * granularity);
                }
                resourceAFixedVar.setStartMin(startIntTime);
                resourceAFixedVar.setStartMax(startIntTime);

                //从input和output表中处理后的人员的结束时间
                Date staffEndInModel = staffPlanInput.getStaffEndInModel();

                int endIntTime;
                if (staffEndInModel.compareTo(subModelEnd) < 0 ){
                    //从input和output表中处理后的人员的结束时间是最早的排程结束时间时，设置该时间为endMin和endMax
                    endIntTime = DateUtil.getDistanceIntTime(staffEndInModel, subModelStart,
                            1000 * 60 * granularity);
                }else{
                    //当模型的排程结束时间是最早的结束时间，则设置为该时间为endMin和endMax
                    endIntTime = subModelData.getEndIntTime();
                }
                resourceAFixedVar.setEndMax(endIntTime);
                resourceAFixedVar.setEndMin(endIntTime);

                //为固定的员工设置culum function
                if(cumulfeResource.getVariable(resTypeResourceId) == null){
                    cumulfeResource.setVariable(resTypeResourceId,
                            cp.pulse(resourceAFixedVar, occupiedCapacity));
                } else{
                    // 资源需要的容量要乘以放大倍数
                    cumulfeResource.setVariable(resTypeResourceId,
                            cp.sum((IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId),
                                    cp.pulse(resourceAFixedVar, occupiedCapacity)));
                }
            }
            //设置资源Capacity约束
            // 资源现有的容量要乘以放大倍数
            if(engineProcessedData.getPlan().isEnabledConstraintResourceCapacity()) {
                int capacity = (int) (possibleStaff.getCapacity() * DEFAULT_AMPLIFICATION_FACTOR);
                IloCumulFunctionExpr temp = (IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId);
                cp.add(cp.le(temp, capacity));

                log.info("ConstraintResourceCapacity:: usage of resource " + resTypeResourceId + " ALWAYS LESS THAN " + capacity);
            }

            //设置表达式：人员工作量尽量均衡
            //设置 表达式： expResourceUnbalance
            IloIntExpr resUsageExpr = (IloIntExpr) exprResourceUsage.getVariable(resTypeResourceId);
            if (resUsageExpr != null){

                Double fixedTimePerStaff = subModelData.getTotalFixedTimePerStaff().get(fixedStaffId);
                if (fixedTimePerStaff == null) {
                    fixedTimePerStaff = 0.0;
                }
                Double averageUsagePerResourceType = subModelData.getAverageExpectedUsagePerResourceType().get(possibleStaff.getResourceType());
                if (averageUsagePerResourceType == null){
                    averageUsagePerResourceType = 0.0;
                }
                double fixedOverAverage = Math.max(fixedTimePerStaff - averageUsagePerResourceType,0);
                IloNumExpr exprTemp;
                if (fixedOverAverage > 0 ){
                    //固定的部分已经超过平均值，加上新分配的一定超过平均值
                    exprTemp = resUsageExpr;
                } else {
                    //固定的部分还没有超过平均值，只惩罚加上新分配的之后，大于平均值的情况
                    exprTemp = cp.max(cp.diff(cp.sum(resUsageExpr, fixedTimePerStaff), averageUsagePerResourceType), 0);
                }
                if (exprResourceUnbalance == null){
                    exprResourceUnbalance = exprTemp;
                }else {
                    exprResourceUnbalance = cp.sum(exprResourceUnbalance, exprTemp);
                }
                subModel.setExprResourceUnbalance(exprResourceUnbalance);
            }
        }

        //得到对应的资源（设备）列表
        HashSet<EntityEquipment> possibleEquipments = subModelData.getPossibleEquipments();
        for (EntityEquipment possibleEquipment : possibleEquipments) {
            //拿到对应fixed的设备ID
            String fixedEquipmentId = possibleEquipment.getEquipmentId();
            //获得Cumul Resource Id：由resourceType和resourceId组成
            String resTypeResourceId = RESOURCE_TYPE_EQUIPMENT + fixedEquipmentId;

            IloCumulFunctionExpr resourceCumulFE = (IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId);
            if (resourceCumulFE == null) {
                // 如果资源变量的occupiedCapacity是0，可能没有cumul function, 没有必要加capacity约束了
                log.info("resource " + resTypeResourceId + " cumul function is null, no need to add capacity constraint.");
                continue;
            }
            ////得到对应的设备活动计划
            ArrayList<EntityEquipmentPlan> equipmentPlanInputs = equipmentEquipmentPlanInputHashMap.get(fixedEquipmentId);
            ArrayList<EntityEquipmentPlan> equipmentPlanOutputs = equipmentEquipmentPlanOutputHashMap.get(fixedEquipmentId);
            if (equipmentPlanInputs == null){
                equipmentPlanInputs = new ArrayList<>();
            }
            if (equipmentPlanOutputs != null) {
                equipmentPlanInputs.addAll(equipmentPlanOutputs);
            }
            //遍历对应的设备计划活动
            for (EntityEquipmentPlan equipmentPlanInput : equipmentPlanInputs) {
                //得到固定设备占用的capacity
                int occupiedCapacity = (int) (equipmentPlanInput.getOccupiedCapacity() * DEFAULT_AMPLIFICATION_FACTOR);
                if (occupiedCapacity <= 0){
                    continue;
                }

                //得到fixed的设备的plan id
                String fixedEquipmentPlanId = equipmentPlanInput.getEquipmentPlanId();
                IloIntervalVar resourceAFixedVar = cp.intervalVar("ivResourceAFixed: Equipment_" + fixedEquipmentId + "_EquipmentPlan_" + fixedEquipmentPlanId);
                ivResourceAFixed.setVariable(resTypeResourceId,fixedEquipmentPlanId,resourceAFixedVar);

                //从input和output表中处理后的设备的开始时间
                Date equipmentStartInModel = equipmentPlanInput.getEquipmentStartInModel();

                int startIntTime;
                if(equipmentStartInModel.compareTo(subModelStart) > 0){
                    //从input和output表中处理后的设备的开始时间是最晚的时间时，设置该时间为startMax和startMin
                    startIntTime = DateUtil.getDistanceIntTime(equipmentStartInModel, subModelStart,
                            1000 * 60 * granularity);
                }else{
                    //否则为子模型的开始时间为最晚的开始时间，则设置改时间为startMax和startMin
                    startIntTime = DateUtil.getDistanceIntTime(subModelStart, subModelStart,
                            1000 * 60 * granularity);
                }
                resourceAFixedVar.setStartMin(startIntTime);
                resourceAFixedVar.setStartMax(startIntTime);

                //从input和output表中处理后的设备的结束时间
                Date equipmentEndInModel = equipmentPlanInput.getEquipmentEndInModel();

                int endIntTime;
                if(equipmentEndInModel.compareTo(subModelEnd) < 0){
                    //从input和output表中处理后的设备的结束时间是最早的时间时，设置该时间为endMax和endMin
                    endIntTime = DateUtil.getDistanceIntTime(equipmentEndInModel, subModelStart,
                            1000 * 60 * granularity);
                }else{
                    //否则为子模型的开始时间为最早的结束时间，则设置该时间为endMax和endMin
                    endIntTime = DateUtil.getDistanceIntTime(subModelEnd, subModelStart,
                            1000 * 60 * granularity);
                }
                resourceAFixedVar.setEndMin(endIntTime);
                resourceAFixedVar.setEndMax(endIntTime);

                //为固定的设备设置culum function
                if (cumulfeResource.getVariable(resTypeResourceId) == null) {
                    cumulfeResource.setVariable(resTypeResourceId,
                            cp.pulse(resourceAFixedVar, occupiedCapacity));
                } else {
                    cumulfeResource.setVariable(resTypeResourceId,
                            cp.sum((IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId),
                                    cp.pulse(resourceAFixedVar, occupiedCapacity)));
                }
            }
            //设置资源Capacity约束
            if(engineProcessedData.getPlan().isEnabledConstraintResourceCapacity()){
                int capacity = (int) (possibleEquipment.getEquipmentCap() * DEFAULT_AMPLIFICATION_FACTOR);
                IloCumulFunctionExpr temp = (IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId);

                cp.add(cp.le(temp, capacity));

                log.info("ConstraintResourceCapacity:: usage of resource " + resTypeResourceId + " ALWAYS LESS THAN " + capacity);
            }
        }

        return true;
    }
}
