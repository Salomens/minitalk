package com.haust.minitalk.mapper;


import com.haust.minitalk.entity.ChatMsg;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMsgMapper {
    int deleteByPrimaryKey(String id);

    int insert(ChatMsg record);

    int insertSelective(ChatMsg record);

    ChatMsg selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ChatMsg record);

    int updateByPrimaryKey(ChatMsg record);

    // 根据接受者id 查询未签收消息列表
    List<ChatMsg> getUnReadMsgListByAcceptUid(String acceptUserId);
}