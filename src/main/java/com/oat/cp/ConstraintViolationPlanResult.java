package com.oat.cp;

public class ConstraintViolationPlanResult  {

    private Long planId;
    private String constraintName;
    private String violationMessage;
    private String warningLevel;

    //public static String FATAL = "FATAL";
    public static String ERROR = "ERROR";
    public static String WARNING = "WARNING";

    public ConstraintViolationPlanResult() {
    }

    //TODO: - by Sophia
    public ConstraintViolationPlanResult(String violationMessage, String warningLevel, String constraintName) {
        this.constraintName = constraintName;
        this.violationMessage = violationMessage;
        this.warningLevel = warningLevel;
    }

}
