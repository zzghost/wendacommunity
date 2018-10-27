package com.example.wenda.async;
import java.util.*;

public class EventModel {
    private EventType type;//触发事件类型.e.g评论
    private int actorId;//事件触发者, e.g.谁评论
    //事件触发载体：e.g.评论了什么
    private int entityType;//评论类型
    private int entityId;//评论ID
    private int entityOwnerId;

    private Map<String, String> exts = new HashMap<>();
    public EventModel(){}

    public EventModel(EventType type){
        this.type = type;
    }

    public EventModel setExt(String key, String value){
        exts.put(key, value);
        return this;
    }
    public String getExt(String key){
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
