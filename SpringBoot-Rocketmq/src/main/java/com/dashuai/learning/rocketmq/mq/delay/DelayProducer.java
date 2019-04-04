package com.dashuai.learning.rocketmq.mq.delay;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.Optional;


/**
 * Created in 2019.04.04
 *
 * @author Liaozihong
 */
public class DelayProducer {
    private DefaultMQProducer mqProducer;

    /**
     * Instantiates a new Delay producer.
     *
     * @param mqProducer the mq producer
     */
    public DelayProducer(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }


    /**
     * Send delay msg string.
     *
     * @param body the body
     * @return the string
     */
    public String sendDelayMsg(String body) {
        Message message = new Message("TestTopic", "vip", body.getBytes());
        // This message will be delivered to consumer 10 seconds later.
        message.setDelayTimeLevel(3);
        SendResult result = null;
        try {
            result = mqProducer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("发送响应：MsgId:" + Optional.ofNullable(result).map(SendResult::getMsgId).orElse(null) +
                "，发送状态:" + Optional.ofNullable(result).map(SendResult::getSendStatus).orElse(null));
        return "{\"MsgId\":\"" + Optional.ofNullable(result).map(SendResult::getMsgId).orElse(null) + "\"}";
    }
}
