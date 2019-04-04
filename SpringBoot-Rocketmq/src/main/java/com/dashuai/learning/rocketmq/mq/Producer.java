package com.dashuai.learning.rocketmq.mq;


import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.util.StopWatch;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Created in 2019.04.03
 *
 * @author Liaozihong
 */
public class Producer {
    private DefaultMQProducer mqProducer;

    /**
     * Instantiates a new Producer.
     *
     * @param mqProducer the mq producer
     */
    public Producer(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    /**
     * Send msg string.
     *
     * @param topic the topic
     * @param tags  the tags
     * @param body  the body
     * @return the string
     */
    public String sendMsg(String topic, String tags, String body) {
        Message message = null;
        try {
            message = new Message(topic, tags, body.getBytes(RemotingHelper.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StopWatch stop = new StopWatch();
        stop.start();
        SendResult result = null;
        try {
            result = mqProducer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发送响应：MsgId:" + Optional.ofNullable(result).map(SendResult::getMsgId).orElse(null) +
                "，发送状态:" + Optional.ofNullable(result).map(SendResult::getSendStatus).orElse(null));
        stop.stop();
        return "{\"MsgId\":\"" + Optional.ofNullable(result).map(SendResult::getMsgId).orElse(null) + "\"}";
    }

}
