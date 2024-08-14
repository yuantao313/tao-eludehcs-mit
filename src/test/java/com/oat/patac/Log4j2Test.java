package com.oat.patac;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * @author:yhl
 * @create: 2022-08-19 13:44
 * @Description: 日志功能测试
 */
public class Log4j2Test {

    // 定义日志记录器对象
    public static final Logger LOGGER = LogManager.getLogger(Log4j2Test.class);
    // 快速入门
    @Test
    public void testQuick() throws Exception {
        // 日志消息输出
        LOGGER.fatal("fatal");
        LOGGER.error("error");
        LOGGER.warn("warn");
        LOGGER.info("info");
        LOGGER.debug("debug");
        LOGGER.trace("trace");
    }

    @Test
    public void testLod() throws Exception {
        while (true) {
            LOGGER.trace("trace level");
            LOGGER.debug("debug level");
            LOGGER.info("info level");
            LOGGER.warn("warn level");
            LOGGER.error("error level");
            LOGGER.fatal("fatal level");
        }
    }

}
