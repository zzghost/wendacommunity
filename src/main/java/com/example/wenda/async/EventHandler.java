package com.example.wenda.async;

import java.io.IOException;
import java.util.List;

//每个handler应该做哪些事情
public interface EventHandler {
    //来做处理
    void doHandle(EventModel model) throws IOException;
    //注册自己，让别人知道自己是关注哪些event的
    List<EventType> getSupportEventType();
}
