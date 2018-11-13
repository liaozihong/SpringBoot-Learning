package com.dashuai.learning.nsq.service.impl;

import com.dashuai.learning.nsq.service.MqProductService;
import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

/**
 * Mq product service
 * Created in 2018.11.13
 *
 * @author Liaozihong
 */
@Service
public class MqProductServiceImpl implements MqProductService {
    /**
     * The Topic.
     */
    @Value("${nsq.topic}")
    String topic;
    /**
     * The Address.
     */
    @Value("${nsq.address}")
    String address;
    @Override
    public boolean mqProduct(String body) {
        boolean flag=false;
        NSQProducer producer = new NSQProducer();
        producer.addAddress(address, 4150).start();
        try {
            producer.produce(topic, body.getBytes());
            flag=true;
        } catch (NSQException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
