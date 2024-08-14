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
public class ConstraintResourceCapacityV2 extends PatacConstraint{

    public ConstraintResourceCapacityV2(CPModel model){
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

        //得到人员和设备资源的cumul function expression
        CPModel.Var1DArray cumulfeResource = subModel.getCumulFunctionExpression();

        //得到表达式exprResourceUsage
        CPModel.Var1DArray exprResourceUsage = subModel.getExprResourceUsage();
        //得到表达式exprResourceUnbalance
        IloNumExpr exprResourceUnbalance = null;

        //获得Staff_Input和Staff_Output表
        HashMap<String, ArrayList<EntityStaffPlan>> staffPlanInputHashMap = engineProcessedData.getStaffStaffPlanInputHashMap();
        HashMap<String, ArrayList<EntityStaffPlan>> staffPlanOutputHashMap = engineProcessedData.getStaffStaffPlanOutputHashMap();
        //获得Equipment_Input和Equipment_Output表
        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanInputHashMap = engineProcessedData.getEquipmentEquipmentPlanInputHashMap();
        HashMap<String, ArrayList<EntityEquipmentPlan>> equipmentEquipmentPlanOutputHashMap = engineProcessedData.getEquipmentEquipmentPlanOutputHashMap();

        //得到对应的资源（员工）列表
        HashSet<EntityStaff> possibleStaffs = subModelData.getPossibleStaffs();
        for (EntityStaff possibleStaff : possibleStaffs) {
            //得到对应的员工id
            String staffId = possibleStaff.getStaffId();
            String resTypeResourceId ;
            if(possibleStaff.getResourceType().equals( RESOURCE_TYPE_ENGINEER)){
                resTypeResourceId = RESOURCE_TYPE_ENGINEER + staffId;
            }else{
                resTypeResourceId = RESOURCE_TYPE_TECHNICIAN + staffId;
            }
            //设置表达式：人员工作量尽量均衡
            //设置 表达式： expResourceUnbalance
            exprResourceUnbalance = calculateExpResourceUnbalance(exprResourceUnbalance, exprResourceUsage,
                    resTypeResourceId, staffId, possibleStaff);

            IloCumulFunctionExpr resourceCumulFE = (IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId);
            if (resourceCumulFE == null){
                // 如果资源变量的occupiedCapacity是0，可能没有cumul function，没有必要再加capacity约束了
                log.info("resource " + resTypeResourceId + " cumul function is null, no need to add capacity constraint.");
                continue;
            }
            log.info("resource " + resTypeResourceId + " cumul function is " + resourceCumulFE.toString() );
            // fixed资源的临时cumul function expression，用于判断是否已经超过capacity
            IloCumulFunctionExpr fixedResourceCumulFE = null;
            //得到对应的员工活动计划
            ArrayList<EntityStaffPlan> staffPlanInputs = staffPlanInputHashMap.get(staffId);
            ArrayList<EntityStaffPlan> staffPlanOutputs = staffPlanOutputHashMap.get(staffId);
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

                //从input和output表中处理后的人员的开始时间
                Date staffStartInModel = staffPlanInput.getStaffStartInModel();
                int startIntTime;
                int endIntTime;
                if (staffStartInModel.before(subModelData.getMaxPlanEndTime())
                        && staffPlanInput.getStaffEndInModel().after(subModelData.getMinPlanStartTime())) {
                    if (staffStartInModel.compareTo(subModelStart) > 0) {
                        //从input和output表中处理后的人员的开始时间最晚的开始时间时，设置该时间为startMin和StartMax
                        startIntTime = DateUtil.getDistanceIntTime(staffStartInModel, subModelStart,
                                1000 * 60 * granularity);
                    } else {
                        //当模型的排程开始时间是最晚的开始时间，设置该时间为startMin和StartMax
                        startIntTime = 0;
                        //DateUtil.getDistanceIntTime(subModelStart, subModelStart, 1000 * 60 * granularity);
                    }

                    //从input和output表中处理后的人员的结束时间
                    Date staffEndInModel = staffPlanInput.getStaffEndInModel();
                    if (staffEndInModel.compareTo(subModelEnd) < 0) {
                        //从input和output表中处理后的人员的结束时间是最早的排程结束时间时，设置该时间为endMin和endMax
                        endIntTime = DateUtil.getDistanceIntTime(staffEndInModel, subModelStart,
                                1000 * 60 * granularity);
                    } else {
                        //当模型的排程结束时间是最早的结束时间，则设置为该时间为endMin和endMax
                        endIntTime = subModelData.getEndIntTime();
                    }

                    //为固定的员工设置culum function
                    if (fixedResourceCumulFE == null) {
                        fixedResourceCumulFE = cp.pulse(startIntTime, endIntTime, occupiedCapacity);
                    } else {
                        // 资源需要的容量要乘以放大倍数
                        fixedResourceCumulFE = cp.sum(fixedResourceCumulFE,
                                cp.pulse(startIntTime, endIntTime, occupiedCapacity));
                    }
                }
            }
            //设置资源Capacity约束
            // 资源现有的容量要乘以放大倍数
            if(engineProcessedData.getPlan().isEnabledConstraintResourceCapacity()) {
                int capacity = (int) (possibleStaff.getCapacity() * DEFAULT_AMPLIFICATION_FACTOR);
                // 检查是否fixed资源占用已经超过限制
//                fixedResourceCumulFE = checkIfFixedResourceCumulFEExceedCapacity(fixedResourceCumulFE, capacity,
//                        possibleStaff, null, subModelStart, granularity);
                if (fixedResourceCumulFE != null ) {
                    resourceCumulFE = cp.sum(resourceCumulFE, fixedResourceCumulFE);
                }
                resourceCumulFE.setName("cumulFEOfStaff_" + staffId);
                cp.add(cp.le(resourceCumulFE, capacity));

                log.info("ConstraintResourceCapacity:: usage of resource " + resTypeResourceId + " ALWAYS LESS THAN " + capacity);
                log.info(" cumul function is " + resourceCumulFE);
            }


        }

        //得到对应的资源（设备）列表
        HashSet<EntityEquipment> possibleEquipments = subModelData.getPossibleEquipments();

        for (EntityEquipment possibleEquipment : possibleEquipments) {

            //拿到对应的设备ID
            String equipmentId = possibleEquipment.getEquipmentId();

            //获得Cumul Resource Id：由resourceType和resourceId组成
            String resTypeResourceId = RESOURCE_TYPE_EQUIPMENT + equipmentId;

            IloCumulFunctionExpr resourceCumulFE = (IloCumulFunctionExpr) cumulfeResource.getVariable(resTypeResourceId);
            if (resourceCumulFE == null) {
                // 如果资源变量的occupiedCapacity是0，可能没有cumul function, 没有必要加capacity约束了
                log.info("resource " + resTypeResourceId + " cumul function is null, no need to add capacity constraint.");
                continue;
            }
            log.info("resource " + resTypeResourceId + " cumul function is " + resourceCumulFE.toString() );
            // fixed资源的临时cumul function expression，用于判断是否已经超过capacity
            IloCumulFunctionExpr fixedResourceCumulFE = null;
            ////得到对应的设备活动计划
            ArrayList<EntityEquipmentPlan> equipmentPlanInputs = equipmentEquipmentPlanInputHashMap.get(equipmentId);
            ArrayList<EntityEquipmentPlan> equipmentPlanOutputs = equipmentEquipmentPlanOutputHashMap.get(equipmentId);
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

                //从input和output表中处理后的设备的开始时间
                Date equipmentStartInModel = equipmentPlanInput.getEquipmentStartInModel();

                int startIntTime;
                int endIntTime;
                if (equipmentStartInModel.before(subModelData.getMaxPlanEndTime())
                        && equipmentPlanInput.getEquipmentEndInModel().after(subModelData.getMinPlanStartTime())) {

                    if (equipmentStartInModel.compareTo(subModelStart) > 0) {
                        //从input和output表中处理后的设备的开始时间是最晚的时间时，设置该时间为startMax和startMin
                        startIntTime = DateUtil.getDistanceIntTime(equipmentStartInModel, subModelStart,
                                1000 * 60 * granularity);
                    } else {
                        //否则为子模型的开始时间为最晚的开始时间，则设置该时间为startMax和startMin
                        startIntTime = DateUtil.getDistanceIntTime(subModelStart, subModelStart,
                                1000 * 60 * granularity);
                    }

                    //从input和output表中处理后的设备的结束时间
                    Date equipmentEndInModel = equipmentPlanInput.getEquipmentEndInModel();

                    if (equipmentEndInModel.compareTo(subModelEnd) < 0) {
                        //从input和output表中处理后的设备的结束时间是最早的时间时，设置该时间为endMax和endMin
                        endIntTime = DateUtil.getDistanceIntTime(equipmentEndInModel, subModelStart,
                                1000 * 60 * granularity);
                    } else {
                        //否则为子模型的开始时间为最早的结束时间，则设置该时间为endMax和endMin
                        endIntTime = DateUtil.getDistanceIntTime(subModelEnd, subModelStart,
                                1000 * 60 * granularity);
                    }

                    //为固定的设备设置culum function
                    if(fixedResourceCumulFE == null){
                        fixedResourceCumulFE = cp.pulse(startIntTime,endIntTime, occupiedCapacity);
                        log.debug("fixed resource cumul f e: start int time " + startIntTime + ", end int time " + endIntTime );
                    } else{
                        // 资源需要的容量要乘以放大倍数
                        fixedResourceCumulFE = cp.sum(fixedResourceCumulFE,
                                cp.pulse(startIntTime,endIntTime, occupiedCapacity));
                        log.debug("fixed resource cumul f e: start int time " + startIntTime + ", end int time " + endIntTime );
                    }
                }
            }
            //设置资源Capacity约束
            if(engineProcessedData.getPlan().isEnabledConstraintResourceCapacity()){
                int capacity = (int) (possibleEquipment.getEquipmentCap() * DEFAULT_AMPLIFICATION_FACTOR);

                if (fixedResourceCumulFE != null ) {
                    resourceCumulFE = cp.sum(resourceCumulFE, fixedResourceCumulFE);
                }
                //resourceCumulFE.setName("cumulFEOfEquipment_" + equipmentId);
                cp.add(cp.le(resourceCumulFE, capacity));

                log.info("ConstraintResourceCapacity:: usage of resource " + resTypeResourceId + " ALWAYS LESS THAN " + capacity);
                log.info(" cumul function is " + resourceCumulFE.toString());
            }
        }
        subModel.setExprResourceUnbalance(exprResourceUnbalance);
        log.info(exprResourceUnbalance);
        return true;
    }

    /**
     * 设置表达式：人员工作量尽量均衡
     * 设置 表达式： expResourceUnbalance
     * @param exprResourceUnbalance
     * @param exprResourceUsage
     * @param resTypeResourceId
     * @param staffId
     * @param possibleStaff
     * @throws IloException
     */

    private IloNumExpr calculateExpResourceUnbalance(IloNumExpr exprResourceUnbalance,
                                               CPModel.Var1DArray exprResourceUsage,
                                               String resTypeResourceId, String staffId,
                                               EntityStaff possibleStaff) throws IloException {
        IloNumExpr resUsageExpr = (IloNumExpr) exprResourceUsage.getVariable(resTypeResourceId);
        if (resUsageExpr != null){
            Double fixedTimePerStaff = subModelData.getTotalFixedTimePerStaff().get(staffId);
            if (fixedTimePerStaff == null) {
                fixedTimePerStaff = 0.0;
            }
            Double averageUsagePerResourceType = subModelData.getAverageExpectedUsagePerResourceType().get(possibleStaff.getResourceType());
            if (averageUsagePerResourceType == null){
                averageUsagePerResourceType = 0.0;
            }
            Double totalRequestedWorkTime = subModelData.getTotalRequestedWorkTime().get(possibleStaff.getResourceType());
            if (totalRequestedWorkTime == null){
                totalRequestedWorkTime = 0.0;
            }

            IloNumExpr exprSurplus = null, exprSlack = null;
//                double fixedOverAverage = Math.max(fixedTimePerStaff - averageUsagePerResourceType,0);
//                if (fixedOverAverage > 0 ){
//                    //固定的部分已经超过平均值，加上新分配的一定超过平均值
//                    exprTemp = resUsageExpr;
//                } else {
//                    //固定的部分还没有超过平均值，只惩罚加上新分配的之后，大于平均值的情况
//                    exprTemp = cp.max(cp.diff(cp.sum(resUsageExpr, fixedTimePerStaff), averageUsagePerResourceType), 0);
//                }
            double upperBound = averageUsagePerResourceType * RESOURCE_UNBALANCE_RATIO_UPPER_BOUND;
            double lowerBound = averageUsagePerResourceType * RESOURCE_UNBALANCE_RATIO_LOWER_BOUND;
            IloNumExpr tempExpr = null;
            if (fixedTimePerStaff >= upperBound) {
                exprSurplus = cp.sum(resUsageExpr, fixedTimePerStaff - upperBound);
            } else if (fixedTimePerStaff+ totalRequestedWorkTime >= upperBound){
                exprSurplus = cp.max(cp.diff(cp.sum(resUsageExpr, fixedTimePerStaff), upperBound), 0);
            }
            if (fixedTimePerStaff + totalRequestedWorkTime <= lowerBound){
                exprSlack = cp.diff(lowerBound-fixedTimePerStaff, resUsageExpr);
            } else if (fixedTimePerStaff <= lowerBound ) {
                exprSlack = cp.max(cp.diff(lowerBound, cp.sum(resUsageExpr, fixedTimePerStaff)), 0);
            }
            if (exprSurplus != null) {
                exprSurplus = cp.power(exprSurplus, RESOURCE_UNBALANCE_POWER_FOR_SURPLUS);
                tempExpr = exprSurplus;
            }
            if (exprSlack != null){
                exprSlack = cp.power(exprSlack, RESOURCE_UNBALANCE_POWER_FOR_SLACK);
                if (tempExpr == null){
                    tempExpr = exprSlack;
                } else {
                    tempExpr = cp.sum(exprSurplus, exprSlack);
                }
            }

            if (tempExpr != null) {
                if (exprResourceUnbalance == null ){
                    exprResourceUnbalance = tempExpr;
                }else {
                    exprResourceUnbalance = cp.sum(exprResourceUnbalance, tempExpr);
                }
            }
        }
        return exprResourceUnbalance;
    }
}
