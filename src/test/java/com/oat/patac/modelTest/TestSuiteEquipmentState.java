package com.oat.patac.modelTest;

import junit.framework.TestSuite;
import junit.framework.Test;
import lombok.extern.log4j.Log4j2;

//@RunWith(Suite.class)
//@Suite.SuiteClasses({ModelTest.class, PatacTest.class})

@Log4j2
public class TestSuiteEquipmentState {

    public static Test suite() {
        TestSuite suite = new TestSuite();

        //suite.addTestSuite(ModelTest.class);

        suite.addTest(TestSuite.createTest(ModelTest.class, "test_12"));
        suite.addTest(TestSuite.createTest(ModelTest.class, "test_12_model4"));
        suite.addTest(TestSuite.createTest(ModelTest.class, "test_13"));
        suite.addTest(TestSuite.createTest(ModelTest.class, "test_13_model4"));
        suite.addTest(TestSuite.createTest(ModelTest.class, "test_14"));
        suite.addTest(TestSuite.createTest(ModelTest.class, "test_14_model4"));
        return suite;
    }


}
