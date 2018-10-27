package com.example.wenda.model;

import org.springframework.stereotype.Component;

@Component
//这里放由ticket取出来的用户
public class HostHolder {
    //看起来是一个对象，但每个线程里都有一份拷贝，通过定义一个公共接口来访问
    private static ThreadLocal<User> users = new ThreadLocal<>();
    //当一个线程访问user时，取的是跟这个线程关联的这个user对象。每个页面都是登陆用户，每个线程都有自己的变量
    public User getUser(){
        return users.get();
    }
    public void setUser(User user){
        users.set(user);
    }
    public void clear(){
        users.remove();
    }
}
