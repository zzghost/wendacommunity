package com.example.wenda.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.example.wenda.async.EventHandler;
import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventType;
import com.example.wenda.model.EntityType;
import com.example.wenda.model.Feed;
import com.example.wenda.model.Question;
import com.example.wenda.model.User;
import com.example.wenda.service.FeedService;
import com.example.wenda.service.FollowService;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class FeedHandler implements EventHandler {
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;
    @Autowired
    FeedService feedService;

    public String buildFeedData(EventModel model){
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(model.getActorId());
        if(actor == null){
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("uesrName", actor.getName());

        if(model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION)){
            Question question = questionService.selectById(model.getEntityId());
            if(question == null){
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandle(EventModel model) throws IOException {
        Random r = new Random();
        model.setActorId(1 + r.nextInt(10));

        //构造一个新鲜事
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setData(buildFeedData(model));
        feed.setId(model.getActorId());
        feed.setType(model.getType().getValue());
        if(feed.getData() == null){
            return ;
        }
        feedService.addFeed(feed);

        //获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);
        followers.add(0);
        for(int follower: followers){
            String timelineKey = RedisKeyUtil.getTimeLineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }

    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}
