package com.oat.common.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * @author:yhl
 * @create: 2022-08-22 14:41
 * @Description: 日志打印工具类
 */
@Log4j2
public class LogUtil {

    /**
     * 判断异常类型，打印日志
     * @param messageSeverity 信息严重程度
     * @param messageBody
     */
    public static void log(String messageSeverity, String messageBody){
        if (StringUtils.equals(messageSeverity, ConstantUtil.MESSAGE_SEVERITY_WARNNING)){
            log.warn(messageBody);
        } else if (messageSeverity.equals(ConstantUtil.MESSAGE_SEVERITY_ERROR)) {
            log.error(messageBody);
        } else {
            log.info(messageBody);
        }
    }
}
