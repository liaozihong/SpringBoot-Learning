package com.dashuai.learning.redislockredlock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson configuration
 * <p/>
 * Created in 2018.12.05
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class RedissonConfiguration {

    /**
     * Gets client.
     *
     * @return the client
     */
    @Bean
    public RedissonClient redissonClient() {
        //如果是默认本地6379，则可不必配置，否则参照
        // https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95  配置
        return Redisson.create();
    }
}
