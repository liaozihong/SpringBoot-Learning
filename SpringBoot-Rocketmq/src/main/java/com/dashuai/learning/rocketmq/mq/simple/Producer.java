package com.dashuai.learning.rocketmq.mq.simple;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;

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
     * 发送简单消息
     *
     * @param topic the topic
     * @param tags  the tags
     * @param body  the body
     * @return the string
     */
    public String sendMsg(String topic, String tags, String body) throws UnsupportedEncodingException, RemotingException, MQClientException, InterruptedException {
        mqProducer.setRetryTimesWhenSendAsyncFailed(0);
        for (int i = 0; i < 100; i++) {
            final int index = i;
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message(topic,
                    tags,
                    "OrderID188",
                    body.getBytes(RemotingHelper.DEFAULT_CHARSET));
            mqProducer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.printf("%-10d OK %s %n", index,
                            sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    System.out.printf("%-10d Exception %s %n", index, e);
                    e.printStackTrace();
                }
            });
        }
        return "success";
    }

}
