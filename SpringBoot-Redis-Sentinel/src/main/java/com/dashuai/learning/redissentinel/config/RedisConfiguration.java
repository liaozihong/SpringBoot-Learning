package com.dashuai.learning.redissentinel.config;

import com.dashuai.learning.redissentinel.RedisSentinelPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created in 2019.02.27
 *
 * @author Liaozihong
 */
@Configuration
public class RedisConfiguration {
    /**
     * Redis pool redis pool.
     *
     * @return the redis pool
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    public RedisSentinelPool redisPool() {
        return new RedisSentinelPool();
    }
}
