package com.example.wenda.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.*;


@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    public JedisPool pool;
    public Jedis getJedis(){
        return pool.getResource();
    }
    public Transaction multi(Jedis jedis){
        try{
            return jedis.multi();
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {

        }
        return null;
    }
    public List<Object> exec(Transaction tx, Jedis jedis){
        try{
            return tx.exec();
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally{
            if(tx != null){
                try{
                    tx.close();
                }catch (IOException ioe){
                    logger.error("发生异常" + ioe.getMessage());
                }
            }
        }
        return null;
    }

    /*
    public static void print(int t, Object obj){
        System.out.println(String.format("%d, %s", t, obj.toString()));

    }


    public static void main(String[] args){
        Jedis jedis = new Jedis("redis://localhost:6379/9");
        jedis.flushDB();

        //get and set
        jedis.sadd("friends", "1");
        jedis.sadd("friends", "2");
        print(1, jedis.smembers("friends"));
        jedis.rename("friends", "newFriends");


        //incr
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));

        //show keys with pattern
        print(3, jedis.keys("*"));

        //**list**
        String listName = "list";
        jedis.del(listName);
        for(int i = 0; i < 10; i++)
            jedis.rpush(listName, "a" + String.valueOf(i));
        print(4, jedis.lrange(listName, 0, -1));
        print(5, jedis.llen(listName));
        print(6, jedis.lpop(listName));
        print(7, jedis.lrange(listName, 0, -1));
        print(8, jedis.lindex(listName, 3));
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "1"));
        print(4, jedis.lrange(listName, 0, -1));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "2"));
        print(4, jedis.lrange(listName, 0, -1));


        //**hash**主要使用在用户表之类的
        String userKey = "userxx";
        //优点:随意增加和删除字段
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "tel", "187");
        print(11, jedis.hget(userKey, "name"));
        print(12, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "tel");
        print(12, jedis.hgetAll(userKey));
        print(13, jedis.hexists(userKey, "tel"));
        //若不存在，则设置；若存在，则不设置
        jedis.hsetnx(userKey, "school", "pku");
        print(14, jedis.hexists(userKey,"school"));


        //set，很多人给你点赞，统计赞数
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for(int i = 0; i < 10; ++i){
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }
        print(15, jedis.smembers(likeKey1));
        print(15, jedis.smembers(likeKey2));
        //求交和并(微博的共同好友)
        print(16, jedis.sunion(likeKey1, likeKey2));
        print(16, jedis.sdiff(likeKey1, likeKey2));//我有你没有
        print(16, jedis.sinter(likeKey1, likeKey2));
        //查询
        print(17, jedis.sismember(likeKey1, "18"));
        print(17, jedis.sismember(likeKey2, "64"));
        //删除
        jedis.srem(likeKey1, "5");
        print(18, jedis.smembers(likeKey1));
        //移动likekey2的25到likekey1
        jedis.smove(likeKey2, likeKey1, "25");
        print(19, jedis.smembers(likeKey1));
        print(20, jedis.scard(likeKey1));


        //sorted set(zset),在数据结构中就是优先队列
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "jim");
        jedis.zadd(rankKey, 80, "ben");
        jedis.zadd(rankKey, 60, "lilei");
        jedis.zadd(rankKey, 50, "han");
        print(30, jedis.zcard(rankKey));
        //查看上下限里有多少人
        print(31, jedis.zcount(rankKey, 61, 100));
        //查看lilei有多少分
        print(32, jedis.zscore(rankKey, "lilei"));
        //调整lilei分数，也可以调整不存在的人的分数
        jedis.zincrby(rankKey, 2, "lilei");
        print(33, jedis.zscore(rankKey, "lilei"));
        jedis.zincrby(rankKey, 2, "li");
        print(33, jedis.zscore(rankKey, "li"));
        //查看某一范围内的人
        print(34, jedis.zrange(rankKey, 0, -1));
        print(35, jedis.zrange(rankKey, 3,5));
        //查看逆序里某一范围内的人
        print(36, jedis.zrevrange(rankKey, 1, 3));
        //打印60-100分内的用户
        for(Tuple tuple: jedis.zrangeByScoreWithScores(rankKey, "60", "100")){
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        //查看排名和反过来的排名
        print(38, jedis.zrank(rankKey, "lilei"));
        print(38, jedis.zrevrank(rankKey, "lilei"));


        //当所有人的分值都一样时，自动按照某个顺序排序
        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");
        //-负无穷,+正无穷
        print(40, jedis.zlexcount(setKey, "-", "+"));
        //(是包含，[是不包含
        print(40, jedis.zlexcount(setKey, "(b", "[e"));
        //删除
        jedis.zrem(setKey, "b");
        print(41, jedis.zrange(setKey, 0, -1));
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(41, jedis.zrange(setKey, 0, -1));

        //连接池

        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; ++i) {
            Jedis j = pool.getResource();
            print(45, j.get("pv"));
            j.close();
        }

        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
       // print(46, JSONObject.toJSONString(user));
        jedis.set("user1", JSONObject.toJSONString(user));

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value, User.class);
        print(47, user2);


    }*/

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/10");
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }
    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);

        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally{
            if(jedis != null)
                jedis.close();
        }
        return false;
    }

    public long lpush(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public List<String> brpop(int timeout, String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);

        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally{

            if(jedis != null)
                jedis.close();
        }
        return null;
    }

    public long zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public long zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public Set<String> zrevrange(String key, int start, int end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    public Double zscore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
    public List<String> lrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
        }finally {
            if(jedis != null)
                jedis.close();
        }
        return null;
    }

}
