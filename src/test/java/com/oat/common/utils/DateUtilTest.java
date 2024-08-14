package com.oat.common.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class DateUtilTest {
    @Test
    public void test_time() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        String dateString = "2022-10-31 00:00:00";
        Date date = dateFormat.parse(dateString);
        dateString = "2022-10-30 09:00:00";
        Date minPlanStartTime = dateFormat.parse(dateString);
        int planGranularity = 30;
        double modeTime = Math.floor((double) (date.getTime() - minPlanStartTime.getTime())/(1000*60)/planGranularity);
        System.out.println(modeTime);
    }
    //周一的case
    @Test
    public void getNextMon_case1() throws ParseException {
        String dateString = "2022-09-19 10:42:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        Date date = dateFormat.parse(dateString);
        System.out.println(dateFormat.format(date));
        Date nextMonday = DateUtil.getNextMonday(date);
        System.out.println(dateFormat.format(nextMonday));
        assertEquals("2022-09-26 00:00:00", dateFormat.format(nextMonday));
    }

    //周日的case
    @Test
    public void getNextMon_case2() throws ParseException {
        String dateString = "2022-09-18 10:42:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        Date date = dateFormat.parse(dateString);
        System.out.println(dateFormat.format(date));
        Date nextMonday = DateUtil.getNextMonday(date);
        System.out.println(dateFormat.format(nextMonday));
        assertEquals("2022-09-19 00:00:00", dateFormat.format(nextMonday));
    }

    //周六的case
    @Test
    public void getNextMon_case3() throws ParseException {
        String dateString = "2022-09-17 10:42:00";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        Date date = dateFormat.parse(dateString);
        System.out.println(dateFormat.format(date));
        Date nextMonday = DateUtil.getNextMonday(date);
        System.out.println(dateFormat.format(nextMonday));
        assertEquals("2022-09-19 00:00:00", dateFormat.format(nextMonday));
    }
}