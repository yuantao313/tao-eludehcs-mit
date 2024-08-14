package com.oat.patac.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * @author:yhl
 * @create: 2022-08-12 17:30
 * @Description: 信息
 */
@Data
@NoArgsConstructor
@Log4j2
public class EntityMessage {

    /**
     * 信息ID
     */
    private Integer messageId ;
    /**
     * plan ID
     */
    private String planId ;
    /**
     * 功能块ID
     */
    private Integer functionId ;
    /**
     * 信息严重程度
     */
    private String messageSeverity ;
    /**
     *  信息类型
     */
    private String messageType ;
    /**
     *  信息日期
     */
    private Date messageDate ;
    /**
     * 信息内容
     */
    private String messageBody ;

    /**
     * messageId 数据库自动生成，functionId 统一添加
     * @param messageSeverity
     * @param messageType
     * @param messageBody
     */
    public EntityMessage(String messageSeverity, String messageType, String messageBody){
        this.messageBody = messageBody;
        // 初始化日期 （测试时先不初始化）
//        this.messageDate = new Date();
        this.messageType = messageType;
        this.messageSeverity = messageSeverity;
    }

    public void printMessage(){
        log.info(messageSeverity + " | " + messageType + " | " + messageBody);
    }
}
