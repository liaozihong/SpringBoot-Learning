package com.dashuai.learning.nsq.config;

import com.dashuai.learning.nsq.service.impl.MqConsumerServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration
 * Created in 2018.11.14
 *
 * @author Liaozihong
 */
@Configuration
public class SpringConfiguration {
    /**
     * Mq consumer service mq consumer service.
     *
     * @return the mq consumer service
     */
    @Bean(initMethod = "mqConsumer")
    public MqConsumerServiceImpl mqConsumerService() {
        return new MqConsumerServiceImpl();
    }
}
