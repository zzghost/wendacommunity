package com.example.wenda.service;

import com.example.wenda.dao.LoginTicketDAO;
import com.example.wenda.dao.UserDAO;
import com.example.wenda.model.LoginTicket;
import com.example.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.wenda.util.WendaUtil;

import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;


    public User getUser(int id){
        return userDAO.selectById(id);
    }
    public User selectByName(String name){return userDAO.selectByName(name);}

    //用户注册
    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isEmpty(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg", "密码不能为空");
            return map;

        }
        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("msg", "用户名已被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        Random random = new Random();
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));

        userDAO.addUser(user);
        //注册时也要自动登陆，下发给用户一个ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    //用户登陆
    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isEmpty(username)){
            map.put("msg", "用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("msg", "密码不能为空");
            return map;

        }
        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg", "用户名不存在");
            return map;
        }
        if(!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "密码错误");
            return map;
        }

        //表示用户登陆成功，给用户下发Ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public String addLoginTicket(int userId){
        Map<String, String> map = new HashMap<>();
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        //当前时间后移100天
        Date now = new Date();
        now.setTime(3600 * 24 * 100 + now.getTime());
        loginTicket.setExpired(now);
        //0为有效
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);


        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
