package com.zeeway.community.dao;

import com.zeeway.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Thales
 * selectConversations：查询当前用户的会话列表，针对每个会话只返回一条最新的私信数据
 * selectConversationCount: 查询当前用户的会话数量
 * selectLetter: 查询某个会话所包含的私信列表
 * selectLetterCount: 查询某个会话的私信数量
 * selectLetterUnreadCount: 查询未读私信数量
 */
@Mapper
public interface MessageMapper {


    List<Message> selectConversations(int userId, int offset, int limit);

    int selectConversationCount(int userId);

    List<Message> selectLetters(String conversationId, int offset, int limit);

    int selectLetterCount(String conversationId);

    int selectLetterUnreadCount(int userId, String conversationId);



}
