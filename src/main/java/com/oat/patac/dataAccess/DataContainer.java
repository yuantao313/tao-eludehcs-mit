package com.oat.patac.dataAccess;

import com.oat.patac.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author:yhl
 * @create: 2022-08-17 09:03
 * @Description: 所有数据的容器
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataContainer {

    private EntityPlan plan;


    private ArrayList<EntityTestArea> testAreaArrayList;
    private ArrayList<EntityLaboratory> laboratoryArrayList;
    private ArrayList<EntityFunction> functionArrayList;
    private ArrayList<EntityProcedure> procedureArrayList;
    private ArrayList<EntityBom> bomArrayList;
    private ArrayList<EntityPlanConfig> planConfigArrayList;

    // todo: check with haolei，是否为避免null，全部new好array list？
    private ArrayList<EntitySample> sampleArrayList = new ArrayList<>();
    // todo: 应该不需要
    //  private ArrayList<EntitySample> sampleOfToBePlannedSubtaskArrayList = new ArrayList<>();
    private ArrayList<EntityTask> taskArrayList;
    private ArrayList<EntitySubTask> subTaskArrayList;

    private ArrayList<EntitySubTaskPlanInput> subTaskPlanInputArrayList;
    private ArrayList<EntitySubTaskPlanInput> subTaskPlanInputOfSameSubTaskGroupArrayList;
    private ArrayList<EntitySubTaskPlanInput> subTaskPlanInputOfSameUniqueSampleNoArrayList;
    private ArrayList<EntityAuthProcedure> authProcedureArrayList;

    private ArrayList<EntityStep> stepArrayList;

    private ArrayList<EntitySpecialDay> specialDayArrayList;
    private ArrayList<EntityAuthBom> authBomArrayList;

    private ArrayList<EntityStepSkill> stepSkillArrayList;
    private ArrayList<EntityAuthStep> authStepArrayList;
    private ArrayList<EntityStepEquipmentGroup> stepEquipmentGroupArrayList;
    private ArrayList<EntityStaff> allStaffArrayList;
    private ArrayList<EntityStaff> engineerArrayList;
    private ArrayList<EntityStaff> technicianArrayList;

    private ArrayList<EntityStaffCalendar> staffCalendarArrayList;
    private ArrayList<EntitySkill> skillArrayList;
    private ArrayList<EntityTechnicianSkill> technicianSkillArrayList;
    private ArrayList<EntityCalendar> calendarArrayList;
    private ArrayList<EntityRollCalendar> rollCalendarArrayList;

    private ArrayList<EntityEquipmentGroup> equipmentGroupArrayList;
    private ArrayList<EntityEquipment> equipmentArrayList;
    private ArrayList<EntityEquipmentGroupRel> equipmentGroupRelArrayList;
    private ArrayList<EntityEquipmentCalendar> equipmentCalendarArrayList;
    private ArrayList<EntityEquipmentMaint> equipmentMaintArrayList;




    private ArrayList<EntityStepPlanInput> stepPlanInputArrayList;
    private ArrayList<EntityEquipmentPlanInput> equipmentPlanInputArrayList;
    private ArrayList<EntityStaffPlanInput> staffPlanInputArrayList;

    private ArrayList<EntityTaskPlanInput> taskPlanInputArrayList;

    /**
     * 获取各个ArrayList对应的迭代器
     */
    public Iterator<EntityTestArea> getTestAreaIterator() {
        return testAreaArrayList.iterator();
    }
    public Iterator<EntityLaboratory> getLaboratoryIterator() {
        return laboratoryArrayList.iterator();
    }
    public Iterator<EntityFunction> getFunctionIterator(){
        return functionArrayList.iterator();
    }

    public Iterator<EntityPlanConfig> getPlanConfigIterator() { return planConfigArrayList.iterator();  }
    public Iterator<EntityProcedure> getProcedureIterator() {
        return procedureArrayList.iterator();
    }
    public Iterator<EntityBom> getBOMIterator(){ return bomArrayList.iterator();    }
    public Iterator<EntitySample> getSampleIterator() { return sampleArrayList.iterator();
    }
    public Iterator<EntityStep> getStepIterator(){
        return stepArrayList.iterator();
    }

    public Iterator<EntityTask> getTaskIterator() {
        return taskArrayList.iterator();
    }
    public Iterator<EntitySubTask> getSubTaskIterator(){
        return subTaskArrayList.iterator();
    }


    public Iterator<EntityTaskPlanInput> getTaskPlanInputIterator(){
        return taskPlanInputArrayList.iterator();
    }
    public Iterator<EntitySpecialDay> getSpecialDayIterator(){
        return specialDayArrayList.iterator();
    }
    public Iterator<EntitySubTaskPlanInput> getSubTaskPlanInputIterator(){
        return subTaskPlanInputArrayList.iterator();
    }
    public Iterator<EntitySubTaskPlanInput> getSubTaskPlanInputOfSameUniqueSampleNoIterator(){
        return subTaskPlanInputOfSameUniqueSampleNoArrayList.iterator();
    }

    public Iterator<EntityAuthBom> getAuthBomIterator(){
        return authBomArrayList.iterator();
    }
    public Iterator<EntityCalendar> getCalendarIterator(){
        return calendarArrayList.iterator();
    }
    public Iterator<EntityEquipment> getEquipmentIterator(){
        return equipmentArrayList.iterator();
    }
    public Iterator<EntityEquipmentGroupRel> getEquipmentGroupRelIterator(){
        return equipmentGroupRelArrayList.iterator();
    }
    public Iterator<EntityEquipmentCalendar> getEquipmentCalendarIterator(){
        return equipmentCalendarArrayList.iterator();
    }
    public Iterator<EntityEquipmentGroup> getEquipmentGroupIterator(){
        return equipmentGroupArrayList.iterator();
    }
    public Iterator<EntityEquipmentMaint> getEquipmentMaintIterator(){
        return equipmentMaintArrayList.iterator();
    }


    public Iterator<EntityAuthProcedure> getAuthProcedureIterator(){
        return authProcedureArrayList.iterator();
    }
    public Iterator<EntityRollCalendar> getRollCalendarIterator(){
        return rollCalendarArrayList.iterator();
    }

    public Iterator<EntityStepSkill> getStepSkillIterator(){
        return stepSkillArrayList.iterator();
    }
    public Iterator<EntitySkill> getSkillIterator(){
        return skillArrayList.iterator();
    }

    public Iterator<EntityStaff> getStaffIterator(){
        return allStaffArrayList.iterator();
    }
    public Iterator<EntityStaff> getEngineerIterator(){
        return engineerArrayList.iterator();
    }
    public Iterator<EntityStaff> getTechnicianIterator(){
        return technicianArrayList.iterator();
    }
    public Iterator<EntityAuthStep> getAuthStepIterator(){
        return authStepArrayList.iterator();
    }
    public Iterator<EntityStepEquipmentGroup> getStepEquipmentIterator(){
        return stepEquipmentGroupArrayList.iterator();
    }
    public Iterator<EntityStaffCalendar> getStaffCalendarIterator(){
        return staffCalendarArrayList.iterator();
    }
    public Iterator<EntityTechnicianSkill> getTechnicianSkillIterator(){
        return technicianSkillArrayList.iterator();
    }
    public Iterator<EntityStepPlanInput> getStepPlanInputIterator(){
        return stepPlanInputArrayList.iterator();
    }
    public Iterator<EntityEquipmentPlanInput> getEquipmentPlanInputIterator(){
        return equipmentPlanInputArrayList.iterator();
    }
    public Iterator<EntityStaffPlanInput> getStaffPlanInputIterator(){
        return staffPlanInputArrayList.iterator();
    }
}
