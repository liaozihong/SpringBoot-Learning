package com.dashuai.learning.nsq.config;

import com.dashuai.learning.nsq.service.impl.MqConsumerByChannelServiceImpl;
import com.dashuai.learning.nsq.service.impl.MqConsumerServiceImpl;
import org.springframework.beans.factory.annotation.Value;
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
     * The Topic.
     */
    @Value("${nsq.topic}")
    String topic;
    /**
     * The Address.
     */
    @Value("${nsq.address}")
    String nsqAddress;

    @Value("${nsq.port}")
    Integer nsqPort;
    /**
     * Mq consumer service mq consumer service.
     *
     * @return the mq consumer service
     */
    @Bean(initMethod = "mqConsumer")
    public MqConsumerServiceImpl mqConsumerService() {
        return new MqConsumerServiceImpl(topic, nsqAddress, nsqPort);
    }

    @Bean(initMethod = "mqConsumer")
    public MqConsumerByChannelServiceImpl mqConsumerByChannelService() {
        return new MqConsumerByChannelServiceImpl(topic, nsqAddress, nsqPort);
    }
}
