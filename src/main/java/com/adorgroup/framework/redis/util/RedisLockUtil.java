package com.adorgroup.framework.redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collections;

public class RedisLockUtil {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 获取锁
     * @param jedisPool Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean getLock(JedisPool jedisPool, String lockKey, String requestId, int expireTime) {
        Jedis jedis =  jedisPool.getResource();
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        jedis.close();
        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }


    /**
     * 释放锁
     * @param jedisPool Redis客户端
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseLock(JedisPool jedisPool, String lockKey, String requestId) {
        Jedis jedis =  jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        jedis.close();
        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }


}
