package com.dashuai.learning.rocketmq.controller;

import com.dashuai.learning.rocketmq.mq.delay.DelayProducer;
import com.dashuai.learning.rocketmq.mq.filter.FilterProducer;
import com.dashuai.learning.rocketmq.mq.order.OrderedProducer;
import com.dashuai.learning.rocketmq.mq.simple.Producer;
import com.dashuai.learning.utils.result.ApiResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

/**
 * Created in 2019.04.03
 *
 * @author Liaozihong
 */
@RestController
public class TestController {
    /**
     * The Producer.
     */
    @Autowired
    Producer producer;

    @Autowired
    DelayProducer delayProducer;

    @Autowired
    FilterProducer filterProducer;

    @Autowired
    OrderedProducer orderedProducer;

    /**
     * Test send api result.
     *
     * @param msg the msg
     * @return the api result
     */
    @RequestMapping(value = "/send")
    public ApiResult testSend(@RequestParam String msg) throws InterruptedException, RemotingException, MQClientException, UnsupportedEncodingException {
        String result = producer.sendMsg("PushTopic", "push", msg);
        return ApiResult.prepare().success(result);
    }

    @RequestMapping(value = "/sendDelay")
    public ApiResult testSendDelay(String msg) {
        String result = delayProducer.sendDelayMsg(msg);
        return ApiResult.prepare().success(result);
    }

    @RequestMapping(value = "/sendFilter")
    public ApiResult testSendFilter(String msg) {
        String result = filterProducer.sendConditionMsg(msg);
        return ApiResult.prepare().success(result);
    }

    @RequestMapping(value = "/sendOrder")
    public ApiResult testSendOrder(String msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, UnsupportedEncodingException {
        String result = orderedProducer.sendOrderMsg(msg);
        return ApiResult.prepare().success(result);
    }
}
