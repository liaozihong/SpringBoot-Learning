package com.dashuai.learning.zookeeper.config;

import com.dashuai.learning.zookeeper.lock.DistributedLockByZookeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration
 * <p/>
 * Created in 2018.11.12
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class SpringConfiguration {
    /**
     * Distributed lock by zookeeper distributed lock by zookeeper.
     *
     * @return the distributed lock by zookeeper
     */
    @Bean(initMethod = "init")
    public DistributedLockByZookeeper distributedLockByZookeeper() {
        return new DistributedLockByZookeeper();
    }
}
