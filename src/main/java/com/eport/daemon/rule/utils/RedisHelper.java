package com.eport.daemon.rule.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RedisHelper {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 通过key获取所有的field和value
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 返回满足pattern表达式的所有key
     * keys(*)
     * 返回所有的key
     *
     * @param pattern
     * @return
     */
    public Set keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

}


