package com.example.wenda.controller;

import com.example.wenda.model.EntityType;
import com.example.wenda.model.Feed;
import com.example.wenda.model.HostHolder;
import com.example.wenda.service.FeedService;
import com.example.wenda.service.FollowService;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.swing.text.html.parser.Entity;
import java.util.*;

@Controller
public class FeedController {
    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.POST, RequestMethod.GET})
    private String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0: hostHolder.getUser().getId();
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimeLineKey(localUserId), 0, 10);
        List<Feed> feeds = new ArrayList<>();
        for(String feedId: feedIds){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if(feed != null){
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.POST, RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId != 0){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

}
