package com.oat.common.utils;

import junit.framework.TestCase;
import org.junit.Test;

public class IdUtilTest extends TestCase {
    @Test
    public void testGetTimeBasedId() {
        IdUtil idUtil = new IdUtil();
        System.out.println(idUtil.getTimeBasedId());
    }
}