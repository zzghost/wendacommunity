package com.example.wenda.dao;

import com.example.wenda.model.LoginTicket;
import com.example.wenda.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO {
    String TABLE_NAME = " login_ticket ";
    String INSERT_FIELDS = "user_id, expired, status, ticket";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into " , TABLE_NAME, " (", INSERT_FIELDS, ") values(#{userId}, #{expired}, #{status}, #{ticket})"})
    int addTicket(LoginTicket loginTicket);
    //查看用户ticket在不在表中
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    //用户登出，更改ticket的状态
    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket")String ticket, @Param("status")int status);

}
