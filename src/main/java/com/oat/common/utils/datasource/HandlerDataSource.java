package com.oat.common.utils.datasource;

/**
 * @author:yhl
 * @create: 2022-08-24 14:18
 * @Description: 根据当前线程来选择具体的数据源（修改数据源）
 */
public class HandlerDataSource {

    private static ThreadLocal<String> handlerThredLocal = new ThreadLocal<String>();

    /**
     * 设置当前的线程的数据源的信息
     * @param datasource 数据源名称
     */
    public static void putDataSource(String datasource) {
        handlerThredLocal. set(datasource);
    }

    /**
     *  提供给AbstractRoutingDataSource的实现类，通过key选择数据源
     * @return
     */
    public static String getDataSource() {
        return handlerThredLocal.get();
    }

    /**
     * 使用默认的数据源
     */
    public static void clear() {
        handlerThredLocal.remove();
    }

}
