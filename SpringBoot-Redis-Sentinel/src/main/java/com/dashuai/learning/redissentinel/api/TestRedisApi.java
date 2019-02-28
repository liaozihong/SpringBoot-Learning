package com.dashuai.learning.redissentinel.api;

import com.dashuai.learning.redissentinel.RedisSentinelPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRedisApi {

    @Autowired
    RedisSentinelPool redisSentinelPool;

    @RequestMapping("/setKey")
    public String setKey(String key, String value) {
        return redisSentinelPool.setex(key, value, 60);
    }

    @RequestMapping("/getKey")
    public String getKey(String key) {
        return redisSentinelPool.get(key);
    }
}
