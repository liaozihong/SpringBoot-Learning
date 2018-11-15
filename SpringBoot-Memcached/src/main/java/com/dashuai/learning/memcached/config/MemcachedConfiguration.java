package com.dashuai.learning.memcached.config;

import com.dashuai.learning.memcached.manager.OpeartionMemcachedManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Memcached configuration
 * <p/>
 * Created in 2018.11.15
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class MemcachedConfiguration {
    /**
     * Opeartion memcached manager opeartion memcached manager.
     *
     * @return the opeartion memcached manager
     */
    @Bean(initMethod = "init", destroyMethod = "disConnect")
    public OpeartionMemcachedManager opeartionMemcachedManager() {
        return new OpeartionMemcachedManager();
    }
}
