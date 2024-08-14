package com.oat.patac.dao;

import com.oat.patac.entity.EntityMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface MessageMapper {

    /**
     * 写入 message 信息
     * @param message
     * @return
     */
    int insertMessage(EntityMessage message);

    /**
     * 写入所有的 message 信息
     * @param messages
     * @return
     */
    int insertAllMessage(@Param("messages") ArrayList<EntityMessage> messages);

    /**
     * 查询所有 message 的信息(只包含：`MESSAGE_SEVERITY`, `MESSAGE_TYPE`, `MESSAGE_BODY`)
     */
    ArrayList<EntityMessage>  selectAllMessage(@Param("functionId") Integer functionId);
    /**
     * 删除指定 functionId 的 messages
     * @param functionId
     */
    void deleteMessagesByFunctionId(@Param("functionId") Integer functionId);
}
