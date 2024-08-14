package com.oat.cp;

import ilog.concert.IloException;

import java.util.Vector;

public abstract class Constraint {

    private CPModel cpModel;
    private String constraintName;
    public Constraint(CPModel model)
    {
        this.cpModel =model;
        cpModel.addConstraint(this);
    }

    public CPModel getModel(){ return this.cpModel;}

    public  abstract boolean  apply() throws IloException;
    public abstract Vector<ConstraintViolationPlanResult> check();
    public void setConstraintName(String name){constraintName = name;}
    public String getConstraintName(){return constraintName;}

}
