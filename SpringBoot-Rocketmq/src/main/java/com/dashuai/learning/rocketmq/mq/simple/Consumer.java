package com.dashuai.learning.rocketmq.mq.simple;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class Consumer implements CommandLineRunner {
    /**
     * 消费者
     */
    @Value("${apache.rocketmq.consumer.pushConsumer}")
    private String pushConsumer;

    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;


    /**
     * 初始化RocketMq的监听信息，渠道信息
     */
    public void messageListener() {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(pushConsumer);

        consumer.setNamesrvAddr(namesrvAddr);
        try {

            // 订阅PushTopic下Tag为push的消息,都订阅消息
            consumer.subscribe("PushTopic", "push");

            // 程序第一次启动从消息队列头获取数据
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //可以修改每次消费消息的数量，默认设置是每次消费一条
            consumer.setConsumeMessageBatchMaxSize(1);

            //在此监听中消费信息，并返回消费的状态信息
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {

                // 会把不同的消息分别放置到不同的队列中
                for (MessageExt message : msgs) {
                    System.out.println("Receive message[msgId=" + message.getMsgId() + "] , 发送时间:"
                            + (new Date(message.getStoreTimestamp())) + "消息体："
                            + new String(message.getBody(), StandardCharsets.UTF_8) + ",以消费!");
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            consumer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) {
        this.messageListener();
    }
}
