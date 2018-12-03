package com.dashuai.learning.redislock.config;

import com.dashuai.learning.redislock.RedisPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis configuration
 * <p/>
 * Created in 2018.12.03
 * <p/>
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
    public RedisPool redisPool() {
        return new RedisPool();
    }
}
