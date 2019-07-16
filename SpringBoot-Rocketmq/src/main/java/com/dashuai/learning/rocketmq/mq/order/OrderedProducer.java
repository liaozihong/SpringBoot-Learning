package com.dashuai.learning.rocketmq.mq.order;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Ordered producer
 * <p/>
 * Created in 2019.07.12
 * <p/>
 *
 * @author Liaozihong
 */
public class OrderedProducer {
    private DefaultMQProducer defaultMQProducer;

    /**
     * Instantiates a new Ordered producer.
     *
     * @param defaultMQProducer the default mq producer
     */
    public OrderedProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    /**
     * Send order msg string.
     *
     * @param body the body
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws InterruptedException         the interrupted exception
     * @throws RemotingException            the remoting exception
     * @throws MQClientException            the mq client exception
     * @throws MQBrokerException            the mq broker exception
     */
    public String sendOrderMsg(String body) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        String[] tags = new String[]{"TagA", "TagB", "TagC"};
        for (int i = 0; i < 100; i++) {
            int orderId = i % 10;
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTestjjj", tags[i % tags.length], "KEY" + i,
                    (body + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = defaultMQProducer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);

            System.out.println("发送消息:" + i);
            Thread.sleep(100);
        }
        return "success";
    }
}
