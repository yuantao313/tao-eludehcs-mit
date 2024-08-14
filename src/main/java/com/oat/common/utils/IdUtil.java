package com.oat.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class IdUtil {

    public static long getTimeBasedId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sdf.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        String result = sdf.format(new Date());
        long id = Long.parseLong(result);
        return id;
    }

}
