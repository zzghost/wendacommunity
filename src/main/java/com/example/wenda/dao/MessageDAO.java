package com.example.wenda.dao;

import com.example.wenda.model.Message;
import com.example.wenda.model.User;
import org.apache.ibatis.annotations.*;

import javax.annotation.PreDestroy;
import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = "from_id, to_id, content, has_read, created_date, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, " (", INSERT_FIELDS, " ) values(#{fromId}, #{toId}, #{content}, #{hasRead}, #{createdDate}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId")String conversationId,
                                        @Param("offset")int offset, @Param("limit")int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where conversation_id=#{conversation_id} and has_read=0 and to_id=#{userId}"})
    int getConversationUnreadCount(@Param("conversation_id")String conversationId,
                                   @Param("userId")int userId);

    @Select({"select ", INSERT_FIELDS, " ,count(id) as id from (select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id  order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId")int userId, @Param("offset")int offset, @Param("limit")int limit);


}
