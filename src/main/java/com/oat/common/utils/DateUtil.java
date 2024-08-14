package com.oat.common.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author OAT
 */
@Log4j2
public class DateUtil {
    //以下定义的是一些时间格式常量
    /**
     * DATETIME_PATTERN(String):yyyy-MM-dd HH:mm:ss.
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_NORMAL_PATTERN = "yyyy-MM-dd HH:mm";

    /**
     * DATE_PATTERN(String):yyyy-MM-dd.
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * MONTH_PATTERN(String):yyyy-MM.
     */
    public static final String MONTH_PATTERN = "yyyy-MM";

    public static final String TIME_ZONE = "GMT+8";

    /**
     * 设置时间的毫秒值为0
     * @param date
     * @return
     */
    public static Date getPreciseTime(Date date) {
        // 日历对象
        Calendar c = Calendar.getInstance();
        // 设置时间
        c.setTime(date);
        // 设置毫秒值为0
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 计算时间差值按颗粒度换算后的数值
     * @param cal1
     * @param cal2
     * @param planGranularity
     * @return
     */
    public static Double getDistanceIntTime(Calendar cal1, Calendar cal2, int planGranularity){
        return Math.ceil((double) (cal1.getTime().getTime() - cal2.getTime().getTime())/(1000*60)/planGranularity);
    }
    /**
     * Date 类型转换成 Calendar 类型，并设置时区
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);

        return cal;
    }
    /**
     * 返回符合格式的当前时间
     */
    public static String getCurrentTimeString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        return  dateFormat.format(new Date());
    }
    /**
     * 返回给定日期的符合格式的字符串
     */
    public static String getDateString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        return  dateFormat.format(date);
    }
    /**
     * 将给定的整数时间转换成为 Date 类型
     * @param intTime
     * @param granularity
     * @return
     */
    public static Date getDateTime(int intTime, int granularity){
        long time = intTime * (1000 * 60) * granularity;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(new Date(time));
        return cal.getTime();
    }
    /**
     * 将给定的时间按颗粒度转换成对应的整数
     * @param date
     * @param granularity 颗粒度（分钟）
     * @return
     */
    public static int getIntTime(Date date, int granularity){
        long time = date.getTime();
        return (int) Math.ceil(((double)time)/(1000*60)/granularity);
    }
    /**
     * 转换为字符串.
     */
    public static String toString(Date date) {
        String result = null;

        if (date != null) {
            result = DateFormatUtils.format(date, DATETIME_PATTERN, TimeZone.getTimeZone(TIME_ZONE));
        }

        return result;
    }

    /**
     * 转换为字符串.
     */
    public static String toString(Date date, String pattern) {
        String result = null;

        if (date != null) {
            result = DateFormatUtils.format(date, pattern, TimeZone.getTimeZone(TIME_ZONE));
        }

        return result;
    }

    /**
     * 获取当前时间的年和月
     */

    public static String getCurYearMonth() {
        int year;
        int month;
        String date;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        date = year + "年" + (month < 10 ? "0" + month : month) + "月";
        return date;
    }

    /**
     * 获取指定日期同一年的第一天.
     */
    public static Date getFirstDateOfYear(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

    /**
     * 获取指定日期同一年的最后一天.
     */
    public static Date getLastDateOfYear(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(getFirstDateOfYear(date));
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * 指定日期追加指定年数.
     */
    public static Date addYear(Date date, int year) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.add(Calendar.YEAR, year);
        return cal.getTime();
    }

    /**
     * 获取给定时间同月份的第一天.
     */
    public static Date getFirstDateOfMonth(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取给定时间同月份的第一天,且00:00:00 0.
     */
    public static Date getMonthOfDate(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取给定时间同月份的最后一天.
     */
    public static Date getLastDateOfMonth(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(getFirstDateOfMonth(date));
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    /**
     * 给指定的时间追加指定的月数.
     */
    public static Date addMonth(Date date, int month) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }

    /**
     * 获取指定日期同周的星期一.
     */
    public static Date getFirstDateOfWeek(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取指定日期同周的星期日.
     */
    public static Date getLastDateOfWeek(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    /**
     * 给指定日期追加指定的周数.
     */
    public static Date addWeek(Date date, int week) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, week);
        return cal.getTime();
    }

    /**
     * 给指定时间追加指定天数.
     */
    public static Date addSecond(Date date,int second) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(date);
//        c.add(Calendar.DATE, day);
        c.add(Calendar.SECOND,second);

        return c.getTime();
    }

    public static Date addDay(Date date, int day) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(date);
        c.add(Calendar.DATE, day);

        return c.getTime();
    }

    /**
     * 给指定时间追加指定的小时.
     */
    public static Date addHour(Date date, int hour) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hour);

        return c.getTime();
    }

    /**
     * 给指定时间追加指定的分钟数.
     */
    public static Date addMinute(Date date, int minute) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(date);
        c.add(Calendar.MINUTE, minute);

        return c.getTime();
    }

    /**
     * 获取整数小时数
     *
     * @param date 时间
     * @return 后面的整数小时数时间
     */
    public static Date getLatestHourDate(Date date) {

//        return new Date(((long) Math.ceil(((double)date.getTime()) / (1000*3600))) *1000*3600);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        // 分钟数超过 0 给小时加 1
        if (cal.get(Calendar.MINUTE) > 0 || cal.get(Calendar.SECOND) > 0){
            cal.add(Calendar.HOUR, 1);
        }
        // 时间取整，将分钟数和秒数归零
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();
    }

    /**
     * 获取整数小时数
     *
     * @param date 时间
     * @return 后面的整数半小时数时间
     */
    public static Date getLatestHalfHourDate(Date date) {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        // 分钟数超过 0 给小时变成30分钟
        if (cal.get(Calendar.MINUTE) == 0 ) {
        }
        else if ((cal.get(Calendar.MINUTE) > 0 && cal.get(Calendar.MINUTE) < 30)){
            cal.set(Calendar.MINUTE, 30);
        } else if ((cal.get(Calendar.MINUTE) >= 30)){
            cal.add(Calendar.HOUR, 1);
            cal.set(Calendar.MINUTE, 0);
        }
        // 时间取整，将分钟数和秒数归零
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();
    }

    /**
     * 判断是否是周末
     *
     * @param date 时间
     * @return 是否是周末
     */
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 计算下一个周一的开始时间
     *
     * @param date 时间
     * @return 下一个周一的开始时间
     */
    public static Date getNextMonday(Date date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        String dateString = dateFormat.format(date);
        //System.out.println(dateString);
        Date dateBegin = dateFormat.parse(dateString);
        //System.out.println(dateBegin);
        //SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //dateFormat2.setTimeZone(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        //System.out.println(dateFormat2.format(dateBegin));

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(dateBegin);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayGaps;
        if (dayOfWeek == 1) {
            dayGaps = 1;
        } else {
            dayGaps = 9-dayOfWeek;
        }
        return DateUtil.addDay(dateBegin, dayGaps);
    }

    /**
     * 返回更晚的时间
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 返回更晚的时间
     */
    public static Date getMaxTime(Date date1, Date date2) {
        if (date1 == null && date2 ==null) {
            return null;
        } else if (date1 == null && date2 !=null){
            return date2;
        } else if (date1 != null && date2 == null) {
            return date1;
        } else if (date1.compareTo(date2) > 0 ){
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * 返回更晚的时间
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return 返回更早的时间
     */
    public static Date getMinTime(Date date1, Date date2) {
        if (date1 == null && date2 ==null) {
            return null;
        } else if (date1 == null && date2 !=null){
            return date2;
        } else if (date1 != null && date2 == null) {
            return date1;
        } else if (date1.compareTo(date2) < 0 ){
            return date1;
        } else {
            return date2;
        }
    }

    /**
     * 返回时间差对应的int值
     *
     * @param date1 时间1（更晚的时间）
     * @param date2 时间2（更早的时间）
     * @param granularity 时间颗粒度，以毫秒为单位
     * @return 返回更晚的时间
     */
    public static int getDistanceIntTime(Date date1, Date date2, int granularity) {
       long timeDistance = (long) (date1.getTime() - date2.getTime());
       return (int) Math.ceil(((double)timeDistance)/granularity);
    }

    /**
     * 获取传入的两个时间的天数差
     *
     * @param date1 时间一
     * @param date2 时间二
     * @return 天数差
     */
    public static int getIntervalDateMinusDate(Date date1, Date date2) {
        return (int) ((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
    }

    /**
     * 传入的时间字符串 获取传入时间的下一天0点0分0秒
     *
     * @param date String类型
     * @return 传入时间的下一天0点0分0秒
     */
    public static Date getNextDay(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTemp = null;
        try {
            dateTemp = format.parse(date);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));

            calendar.setTime(dateTemp); //需要将date数据转移到Calender对象中操作
            calendar.add(Calendar.DATE, 1);//把日期往后增加n天.正数往后推,负数往前移动
            dateTemp = calendar.getTime();   //这个时间就是日期往后推一天的结果
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTemp;
    }

    /**
     * 获取传入时间的下一天0点0分0秒
     * @param date
     * @return
     */
    // todo：测试
    public static Date getNextDay(Date date) {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * 获取某个时间点之前的时间
     *
     * @param dateFormatStr 时间格式
     * @param dataStr       指定的时间
     * @param calendarType  Calendar类中的字段属性值   ERA = 0 、YEAR = 1、MONTH = 2、WEEK_OF_YEAR = 3、WEEK_OF_MONTH = 4.......,具体可查看   *                                          Calendar类
     * @param beforeNum     时间间隔数  根据calendarType   确定是年  月 日 等
     * @return beforeDateStr
     */
    public static String getBeforeDateStr(String dateFormatStr, String dataStr, int calendarType, int beforeNum) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        try {
            calendar.setTime(sdf.parse(dataStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(calendarType, -beforeNum);
        Date beforeDate = calendar.getTime();
        return sdf.format(beforeDate);
    }

    /**
     * 根据指定日期获得指定日期所在周的周一到周天的日期 *
     *
     * @param mdate Date类型
     * @return 所在周的周一到周天的日期
     */
    public static List<Date> dateToCurrentWeek(Date mdate) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(mdate);

        int b = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (b == 0) {
            b = 7;
        }
        Date fdate;
        List<Date> list = new ArrayList<>();
        long fTime = mdate.getTime() - (long) b * 24 * 3600000;
        for (int a = 1; a <= 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(a - 1, fdate);
        }
        return list;
    }

    /**
     * 传入日期计算自然周的开始结束时间
     * DATETIMENORMAL_PATTERN  是类里面定义的类变量的日期格式   请看该博客
     * 最开始给的类的属性
     */
    public static String[] getWeekDayBeginDateAndEndDateWithDateStr(Date date) {
        String[] beStr = new String[2];
        SimpleDateFormat formt = new SimpleDateFormat(DATETIME_NORMAL_PATTERN);
        List<Date> list = dateToCurrentWeek(date);
        beStr[0] = formt.format(list.get(0));
        beStr[1] = formt.format(list.get(6));
        return beStr;
    }

    public static long getDistanceDays(String str1, String str2) {
        DateFormat df = new SimpleDateFormat(DATE_PATTERN);   //DATE_PATTERN  是类里面定义的类变量的日期格式  请看该博客最开始给的类的属性
        Date one;
        Date two;
        long day = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day;
    }

    public static long[] getLateDistanceTimes(String beginDate, String endDate) {
        DateFormat df = new SimpleDateFormat(DATETIME_PATTERN);  //DATETIME_PATTERN  是类里面定义的类变量的日期格式  请看该博客最开始给的类的属性
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(beginDate);
            two = df.parse(endDate);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
                day = diff / (24 * 60 * 60 * 1000);
                hour = (diff / (60 * 60 * 1000) - day * 24);
                min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
                sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new long[]{day, hour, min, sec};
    }

    public static List<String> getDays(String startTime, String endTime) {
        // 返回的日期集合
        List<String> days = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);  //DATE_PATTERN  是类里面定义的类变量的日期格式  请看该博客最开始给的类的属性
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);
            Calendar tempStart = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
            tempStart.setTime(start);
            Calendar tempEnd = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(new SimpleDateFormat("MM-dd").format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * @return 通过传递时间段去获取该时间段中的每天日期
     * //     * @throws ParseException 开始时间和结束时间请传递类似于2018-12-12这样的时间-字符串
     * //     *                        DATE_PATTERN  是类里面定义的类变量的日期格式  请看该博客最开始给的类的属性
     */
    public static Date[] getDateArrays(String startPar, String endPar, int calendarType) {
        SimpleDateFormat sd = new SimpleDateFormat(DATE_PATTERN);
        Date start = null;
        Date end = null;
        try {
            start = sd.parse(startPar);
            end = sd.parse(endPar);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<Date> ret = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        assert start != null;
        calendar.setTime(start);
        Date tmpDate = calendar.getTime();
        assert end != null;
        long endTime = end.getTime();
        while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
            ret.add(calendar.getTime());
            calendar.add(calendarType, 1);
            tmpDate = calendar.getTime();
        }
        Date[] dates = new Date[ret.size()];
        return ret.toArray(dates);
    }

    /**
     * @return 通过传递时间参数，判断这天是星期几，如"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
     * 请传递类似于2018-12-12这样的时间
     */
    public static String getWeekDayByDate(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * @return 通过传递时间参数，判断这天是星期几，如"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
     * //     * @throws ParseException 请传递类似于2018-12-12这样的时间字符串
     */
    public static String getWeekDayByDateStr(String dateStr) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        SimpleDateFormat formt = new SimpleDateFormat(DATE_PATTERN);
        Date date = null;
        try {
            date = formt.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        cal.setTime(date);
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * @return 今天是星期几，如"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
     */
    public static String getWeekDay() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(new Date());
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekOfDays[w];
    }

    /**
     * @return 通过传递时间参数，判断这天是几月，如"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
     * //     * @throws ParseException 请传递类似于2018-12-12这样的时间
     */
    public static String getMonthByDate(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        String[] monthOfYear = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
        int m = cal.get(Calendar.MONTH);
        return monthOfYear[m];
    }

    /**
     * 获取传入时间的当前月
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取传入时间的当前年
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取传入时间的当月总共天数
     */
    public static int getDays(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        cal.setTime(date);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date stringToDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException ignored) {
        }
        return date;
    }

    /**
     * 获取to和from之间的月份差，例：from为2020-01，to为2020-02，所获结果为2
     *
     * @param from 开始月份
     * @param to   结束月份
     * @return 两者之差
     */
    public static int getDistanceMonths(Date from, Date to) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));

        cal.setTime(from);
        int fromYear = cal.get(Calendar.YEAR);
        int fromMonth = cal.get(Calendar.MONTH);

        cal.setTime(to);
        int toYear = cal.get(Calendar.YEAR);
        int toMonth = cal.get(Calendar.MONTH);
        return (toYear - fromYear) * 12 + (toMonth - fromMonth) + 1;
    }

    /**
     * 获取传入季度(字符串2020-Q1)的Date形式
     *
     * @param quarter 2020-Q1
     * @return date
     */
    public static Date getDateByQuarter(String quarter) {
        String year = quarter.substring(0, 4);
        int quart = Integer.parseInt(quarter.substring(6)) * 3 - 2;
        if (quart == 10) {
            year += "-" + quart;
        } else {
            year += "-" + 0 + quart;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(MONTH_PATTERN);
        Date date = null;
        try {
            date = sdf.parse(year);
        } catch (ParseException ignored) {
        }
        return date;
    }

    /**
     * @param date yyyy-02
     * @return yyyy-Q1
     */
    public static String getQuarterByDate(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) + "-Q" + (calendar.get(Calendar.MONTH) / 3 + 1);
    }

    /**
     * Description: 判断一个时间是否在一个时间段内 </br>
     *
     * @param nowTime   当前时间 </br>
     * @param beginTime 开始时间 </br>
     * @param endTime   结束时间 </br>
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        end.setTime(endTime);

        return date.after(begin) && date.before(end);

    }

    //向日期添加时分秒。
    public static Date addHourMinuteSecond(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        calendar.setTime(date);
//        calendar.add(Calendar.HOUR, -24);
//        calendar.add(Calendar.MINUTE, 59);
        calendar.add(Calendar.SECOND, -1);
//        calendar.add(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     *判断当前时间与给定时间差是否大于10分钟
     * @param date
     * @return 大于10分钟返回true,就是解锁了。
     * @throws Exception
     */
    public static boolean localTimeGivenTime(Date date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now= null;
        try {
            now = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(now.getTime()-date.getTime()>=10*60*1000){
            return true;
        }
        else{
            return false;
        }
    }

}
