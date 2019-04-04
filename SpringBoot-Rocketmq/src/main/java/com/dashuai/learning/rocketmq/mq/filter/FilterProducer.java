package com.dashuai.learning.rocketmq.mq.filter;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public class FilterProducer {
    private DefaultMQProducer mqProducer;

    public FilterProducer(DefaultMQProducer mqProducer) {
        this.mqProducer = mqProducer;
    }

    /**
     * @param msg
     * @return
     */
    public String sendConditionMsg(String msg) {
        Message message = null;
        SendResult result = null;
        try {
            message = new Message("TopicTest2",
                    "test2",
                    ("Hello RocketMQ " + msg).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
            //设置用户属性，可以用来做条件过滤
            message.putUserProperty("a", String.valueOf(msg));
            result = mqProducer.send(message);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("发送响应：MsgId:" + Optional.ofNullable(result).map(SendResult::getMsgId).orElse(null) +
                "，发送状态:" + Optional.ofNullable(result).map(SendResult::getSendStatus).orElse(null));
        return "{\"MsgId\":\"" + Optional.ofNullable(result).map(SendResult::getMsgId).orElse(null) + "\"}";
    }
}
