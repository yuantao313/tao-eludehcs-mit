package com.oat.patac.engine;

import com.oat.common.utils.DateUtil;
import com.oat.cp.CPModel;
import com.oat.cp.Constraint;
import com.oat.cp.ConstraintViolationPlanResult;
import com.oat.patac.entity.EntityEquipmentPlan;
import ilog.concert.IloException;
import ilog.cp.IloCP;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

public abstract class PatacConstraint extends Constraint {

    protected IloCP cp;
    protected SubModel subModel;
    protected EngineProcessedData engineProcessedData;
    protected SubModelData subModelData;

    public PatacConstraint(CPModel model) {
        super(model);
        cp = model.getCp();
        subModel = (SubModel) this.getModel();
        engineProcessedData = ((PatacSchedulingTask) subModel.getTask()).getEngineProcessedData();
        subModelData = subModel.getSubModelData();
    }

    @Override
    public boolean apply() throws IloException {
        return true;
    }

    @Override
    public Vector<ConstraintViolationPlanResult> check() {
        return null;
    }

    public ArrayList<EntityEquipmentPlan> combineEquipmentPlanInputsAndOutputs(
            ArrayList<EntityEquipmentPlan> equipmentPlanInputs,ArrayList<EntityEquipmentPlan> equipmentPlanOutputs) {

    //得到对应的设备活动计划
        if(equipmentPlanInputs ==null) {
            equipmentPlanInputs = new ArrayList<>();
        }
        if(equipmentPlanOutputs !=null) {
            equipmentPlanInputs.addAll(equipmentPlanOutputs);
        }
        return equipmentPlanInputs;
    }

    public Integer getStartOrEndIntTimeForFixedResourceA(Date dateInModel,
                                                 Date subModelStart, Date subModelEnd,
                                                 int granularity, boolean isStartDate){
        //从input和output表中处理后的设备的开始时间
        Integer intTime = null;
        if (isStartDate) {
            if (dateInModel.after(subModelEnd)) {
                return intTime;
            } else {
                if (dateInModel.after(subModelStart)) {
                    //从input和output表中处理后的开始时间是最晚的时间时，设置该时间为startMax和startMin
                    intTime = DateUtil.getDistanceIntTime(dateInModel, subModelStart,
                            1000 * 60 * granularity);
                } else {
                    //否则为子模型的开始时间为最晚的开始时间，则设置该时间为startMax和startMin
                    intTime = 0;
                }
                return intTime;
            }
        } else {
            // 结束时间
            if (dateInModel.before(subModelStart)){
                return intTime;
            } else {
                if(dateInModel.before(subModelEnd)){
                    //从input和output表中处理后的设备的结束时间是最早的时间时，设置该时间为endMax和endMin
                    intTime = DateUtil.getDistanceIntTime(dateInModel, subModelStart,
                            1000 * 60 * granularity);
                }else{
                    //否则为子模型的开始时间为最早的结束时间，则设置该时间为endMax和endMin
                    intTime = DateUtil.getDistanceIntTime(subModelEnd, subModelStart,
                            1000 * 60 * granularity);
                }
                return intTime;
            }
        }

    }
}
