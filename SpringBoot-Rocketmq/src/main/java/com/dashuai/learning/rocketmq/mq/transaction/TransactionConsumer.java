package com.dashuai.learning.rocketmq.mq.transaction;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TransactionConsumer implements CommandLineRunner {
    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    public void filterConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");
        consumer.setNamesrvAddr(namesrvAddr);
        try {
            consumer.subscribe("TopicTest1234", "*");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt message : msgs) {
                System.out.println("Receive transaction message[msgId=" + message.getMsgId() + "] , 发送时间:"
                        + (new Date(message.getStoreTimestamp())) + "消息体："
                        + new String(message.getBody(), StandardCharsets.UTF_8) + ",以消费!");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) {
        filterConsumer();
    }
}
