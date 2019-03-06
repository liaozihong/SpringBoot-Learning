package com.dashuai.learning.nsq.config;

import com.github.brainlag.nsq.NSQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NsqProductConfiguration {
    @Value("${nsq.topic}")
    String topic;
    @Value("${nsq.address}")
    String nsqAddress;
    @Value("${nsq.port}")
    Integer nsqPort;

    @Bean
    public NSQProducer nsqProducer() {
        NSQProducer nsqProducer = new NSQProducer();
        nsqProducer.addAddress(nsqAddress, nsqPort);
        nsqProducer.start();
        return nsqProducer;
    }
}
