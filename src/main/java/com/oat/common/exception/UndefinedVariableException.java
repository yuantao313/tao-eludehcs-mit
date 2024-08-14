package com.oat.common.exception;

public class UndefinedVariableException extends PatacException{
    public UndefinedVariableException(Class clazz, String variableName, Object variableKey) {
        super(UndefinedVariableException.generateMessage(clazz.getSimpleName(), variableName, variableKey));
    }

    private static String generateMessage(String className, String variableName, Object variableKey) {
        return "No variable defined in Class " + className + "with variable array name " + variableName + " and variable key " + variableKey;
    }
}
