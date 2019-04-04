package com.dashuai.learning.rocketmq.mq.delay;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Created in 2019.04.04
 *
 * @author Liaozihong
 */
@Component
public class DelayConsumer implements CommandLineRunner {
    /**
     * NameServer 地址
     */
    @Value("${apache.rocketmq.namesrvAddr}")
    private String namesrvAddr;

    /**
     * Delay mq consumer.
     *
     * @throws MQClientException the mq client exception
     */
    public void delayMqConsumer() throws MQClientException {
        // Instantiate message consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ExampleConsumer");

        consumer.setNamesrvAddr(namesrvAddr);
        // Subscribe topics
        consumer.subscribe("TestTopic", "*");
        // Register message listener
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    System.out.println("Receive message[msgId=" + message.getMsgId() + "] , 发送时间:"
                            + (new Date(message.getStoreTimestamp())) + "消息体："
                            + new String(message.getBody(), StandardCharsets.UTF_8) + ",以消费!");
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }

    @Override
    public void run(String... args) throws Exception {
        delayMqConsumer();
    }
}
