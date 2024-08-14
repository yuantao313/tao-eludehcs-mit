package com.oat.common.utils;

import com.oat.patac.entity.EntityTask;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author:yhl
 * @create: 2022-09-23 15:32
 * @Description: 非功能测试
 */
@Log4j2
public class NonFunctionalTest {

    public static <K, V> void notExistTable(HashMap<K, V> hashMap, K key){
        if (!hashMap.containsKey(key)){
            log.info(key + "不在 map 中！");
        }
    }

    @Test
    public void testCase01(){
        String num = "01";
        int i = 01;
        System.out.println(Integer.parseInt(num));
    }

//    @Test
//    public void test19090(){
//        double i = 1.1;
//        BigDecimal b = new BigDecimal(i);
//        System.out.println(b);
//
//    }

    @Test
    public void test4(){
        Date date = new Date();
        Calendar tempTime1 = DateUtil.dateToCalendar(date);
        tempTime1.set(Calendar.HOUR_OF_DAY, 7);
        tempTime1.set(Calendar.MINUTE, 0);
        tempTime1.set(Calendar.SECOND, 0);
        tempTime1.set(Calendar.MILLISECOND, 0);

        System.out.println(tempTime1.getTime());

    }

    @Test
    public void test1(){
        String s = "12";
        String s1 = "12";
        System.out.println(s.compareTo(s1));

        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.TIME_ZONE));
        Date date = new Date();
        cal1.setTime(date);

        Calendar cal2 = DateUtil.dateToCalendar(cal1.getTime());
        Calendar cal3 = DateUtil.dateToCalendar(cal1.getTime());

        System.out.println(cal2.equals(cal3));
    }

    @Test
    public void test(){
//        Integer i1 = 1;
//        Integer i2 = 2;
//        log.info(i1.compareTo(i2));

        ArrayList<Integer> list = new ArrayList<>();
        System.out.println(list.size());
    }

    @Test
    public void testDate(){
//        Date date = new Date();
//        System.out.println(date);
//        System.out.println(date.getDay());
//        System.out.println(date.getYear());
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        log.info(cal.get(Calendar.DAY_OF_WEEK));
//        //2019-01-10
//        Date date1 = new Date(2019 - 1900, 2 - 1,10);
//        System.out.println("date1:" + date1);
//        LocalDate localDate = LocalDate.now();
//        System.out.println(localDate);

//        log.info(1 == 2 || 1 == 2 && 1 == 1);

        HashMap<Integer, EntityTask> hashMap = new HashMap<>();
        EntityTask task = hashMap.computeIfAbsent(1, key -> new EntityTask());
        log.info(hashMap);
    }

    @Test
    public void testThreadSafe(){

//        ArrayList<Integer> list = new ArrayList<>();

        // 多线程写时线程安全
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                list.add(1);
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                list.add(1);
            }
        }).start();

        try {
            TimeUnit.SECONDS.sleep(10);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("list.size() = " + list.size());

    }

    @Test
    public void setAddTest(){
//        HashMap<String, Integer> map = new HashMap<>();
//        map.put("1", 1);
//        notExistTable(map, "1");
//        notExistTable(map, "2");

        HashSet<HashSet<Integer>> set = new HashSet<>();
//        ArrayList<Integer> list1 = new ArrayList<>();
//        ArrayList<Integer> list2 = new ArrayList<>();
//        ArrayList<Integer> list3 = new ArrayList<>();
//        ArrayList<Integer> list4 = new ArrayList<>();

        HashSet<Integer> list1 = new HashSet<>();
        HashSet<Integer> list2 = new HashSet<>();
        list1.add(1);
        list1.add(2);

        list2.add(2);
        list2.add(1);

        set.add(list1);
        set.add(list2);
//        set.add(list3);
        log.info(set);
    }

    @Test
    public void listMethodTest(){
        ArrayList<Integer> list1 = new ArrayList<>();
        ArrayList<Integer> list2 = new ArrayList<>();

        list1.add(1);
        list1.add(2);
        list1.add(3);

        list2.add(2);
        list2.add(3);
        list2.add(1);

        Assert.assertTrue(list1.equals(list2));


        ArrayList<Integer> list3 = (ArrayList<Integer>) list1.clone();

        list3.retainAll(list2);
        list2.removeAll(list3);
        list1.removeAll(list3);

        log.info(list1);
        log.info(list2);

    }

    @Test
    public void test3(){
        double d1 = 1.2;
        double d2 = 1.23;
        log.info(d1 + d2);
    }
}
