package com.oat.common.utils;

import com.oat.patac.engine.EngineProcessedData;
import com.oat.patac.entity.EntityMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * @author:yhl
 * @create: 2022-08-19 11:15
 * @Description: 数据验证工具类
 * 方法的返回值代表是否异常，有异常为 false，没有异常为 true
 */
@Log4j2
public class CheckDataUtil {

    /**
     * 封装异常信息，打印日志
     * @param msgSeverity 信息严重程度
     * @param msgType 信息类型
     * @param msgBody 信息内容
     */
    public static void packageAndLog(String msgSeverity, String msgType, String msgBody){
        // 封装异常信息到 message 中
        EntityMessage message = new EntityMessage(msgSeverity, msgType, msgBody);
        // 将 message 信息存储到 list 中
        EngineProcessedData.messages.add(message);
        // 打印日志
        LogUtil.log(msgSeverity, msgBody);
    }

    /**
     * b 是 false ，则封装异常信息
     * @param b
     * @param msgSeverity
     * @param msgType
     * @param msgBody
     */
    public static void isFalse(Boolean b, String msgSeverity, String msgType, String msgBody){
        if (!b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }
    }

    /**
     * b 是 true ，则封装异常信息
     * @param b
     * @param msgSeverity
     * @param msgType
     * @param msgBody
     */
    public static void isTure(Boolean b, String msgSeverity, String msgType, String msgBody){
        if (b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }
    }

    /**
     * 比较整数 n1 和 n2 的大小， 如果 n1 大于 n2 则封装异常信息
     * @param n1
     * @param n2
     * @param msgSeverity
     * @param msgType
     * @param msgBody
     * @return true 没有异常 false 有异常
     */
    public static Boolean numCompare(Integer n1, Integer n2, String msgSeverity, String msgType, String msgBody){
        boolean b = n1 > n2;
        if (b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }
        return !b;
    }

    /**
     * 比较时间 d1 和 d2 的大小，如果 d1 大于 d2 则封装异常信息 (大多影响排程，几个不影响的需要特殊处理)
     * @param d1
     * @param d2
     * @param msgSeverity
     * @param msgBody
     * @return true 没有异常 false 有异常
     */
    public static Boolean timeCompare(Date d1, Date d2, String msgSeverity, String msgBody){
        long time1 = d1.getTime();
        long time2 = d2.getTime();
        boolean b = time1 > time2;
        if (b){
            packageAndLog(msgSeverity, ConstantUtil.MESSAGE_TYPE_TIME_CONFICT, msgBody);
        }

        return !b;
    }

    /**
     * 对应的id在表中不存在，封装异常信息
     * @param msgSeverity
     * @param msgBody
     * @return true 没有异常 false 有异常
     */
    public static <K, V> Boolean notExistTable(HashMap<K, V> hashMap, K key, String msgSeverity, String msgType, String msgBody){
        boolean b = !hashMap.containsKey(key);
        if (b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return !b;
    }

    /**
     * 检查  param 是否为空，空则封装异常信息
     * @param param
     * @param msgSeverity
     * @param msgType
     * @param msgBody
     * @param <T> 要判断数的类型
     * @return true 没有异常 false 有异常
     */
    public static <T> Boolean notBlank(T param, String msgSeverity, String msgType, String msgBody){
        boolean b = (param == null);
        if (b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return !b;
    }
    /**
     * 检查 list 集合类型是否为空，空则封装异常信息
     * @param list 需要判断是否为空的参数
     * @return true 没有异常 false 有异常
     */
    public static Boolean notBlank(ArrayList<?> list, String msgSeverity, String msgType, String msgBody) {
        boolean b = (list == null || list.size() == 0);
        if (b) {
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return !b;
    }

    /**
     * 判断字符串 param1 和 param2 是否相等，不相等则封装异常信息
     * @param param1
     * @param param2
     * @param msgSeverity
     * @param msgBody
     * @return true 相等 false 不相等
     */
    public static  Boolean isNotEquals(String param1, String param2,  String msgSeverity, String msgType, String msgBody){
        boolean b = StringUtils.equals(param1, param2);
        if (!b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return b;
    }
    /**
     * 判断字符串 param1 和 param2 是否相等，相等则封装异常信息
     * @param param1
     * @param param2
     * @param msgSeverity
     * @param msgBody
     * @return true 相等 false 不相等
     */
    public static  Boolean isEquals(String param1, String param2,  String msgSeverity, String msgType, String msgBody){
        boolean b = StringUtils.equals(param1, param2);
        if (b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return b;
    }
    /**
     * 判断基本数据类型 param1 和 param2 是否相等，不相等则封装异常信息
     * @param param1
     * @param param2
     * @param msgSeverity
     * @param msgBody
     * @return true 相等 false 不相等
     */
    public static  <T> Boolean isNotEquals(T param1, T param2,  String msgSeverity, String msgType, String msgBody){
        boolean b = (param1 == param2);
        if (!b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return b;
    }

    /**
     * 判断参数 param 是否在 list 中， 不在则封装异常信息
     * @param param
     * @param list
     * @param msgSeverity
     * @param msgType
     * @param msgBody
     * @return true 没有异常 false 有异常
     */
    public static <T> Boolean isNotInList(ArrayList<T> list, T param, String msgSeverity, String msgType, String msgBody){
        Boolean b ;
        if (list == null){
            b = true;
        }else {
            b  = !list.contains(param);
        }

        if (b){
            packageAndLog(msgSeverity, msgType, msgBody);
        }

        return !b;
    }

}
