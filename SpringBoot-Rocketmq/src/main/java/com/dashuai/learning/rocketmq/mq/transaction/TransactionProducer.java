package com.dashuai.learning.rocketmq.mq.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;

/**
 * Created in 2019.04.04
 *
 * @author Liaozihong
 */
public class TransactionProducer {

    private DefaultMQProducer defaultMQProducer;

    public TransactionProducer(DefaultMQProducer defaultMQProducer) {
        this.defaultMQProducer = defaultMQProducer;
    }

    /**
     * Send transaction msg string.
     *
     * @return the string
     * @throws InterruptedException the interrupted exception
     * @throws MQClientException    the mq client exception
     */
    public boolean sendTransaction(String body)  {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        try {
            for (int i = 0; i < tags.length; i++) {
                Message msg =
                        new Message("TopicTest1234", tags[i], "KEY" + i,
                                ("Hello RocketMQ, " + body).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = defaultMQProducer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);
                Thread.sleep(10);
            }
        } catch (MQClientException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
