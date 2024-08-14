package com.oat.patac.modelTest;

public class SuiteRunner {
    @org.junit.Test
    public void testEquipmentState(){
        junit.textui.TestRunner.run(TestSuiteEquipmentState.suite());

//        Result result = JUnitCore.runClasses(TestSuite1.class);
//        for (Failure failure : result.getFailures())
//        {
//            System.out.println(failure.toString());
//        }
    }

}
