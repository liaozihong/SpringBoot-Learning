package com.dashuai.learning.rabbitmq.listener;

import com.dashuai.learning.rabbitmq.constants.RabbitConstants;
import com.dashuai.learning.rabbitmq.message.People;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Random;

/**
 * Send message listener
 * <p/>
 * Created in 2018.11.08
 * <p/>
 * 队列消息消费者
 *
 * @author Liaodashuai
 */
@Service
public class SendMessageListener {

    private final Logger logger = LoggerFactory.getLogger(SendMessageListener.class);

    /**
     * 如果不使用Send_QUEUE，而直接使用DEAD_QUEUE并且没有其他Send_QUEUE的消费者的话，就可以实现延时队列
     * 因为Config已经设置SEND_QUEUE的消息过期时间，这便是使用死信队列实现延时队列。其实这时的发送队列相当于
     * 一个缓冲队列而已，真正处理是使用死信队列进行处理的。
     *
     * @param people  the MessageConverter会自动转换内容
     * @param channel the channel 信道
     * @param message the message 由Header和Body组成，包括生产者添加的各种属性的集合
     * @throws Exception the exception
     */
    @RabbitListener(queues = RabbitConstants.QUEUE_NAME_DEAD_QUEUE)
//    @RabbitListener(queues = RabbitConstants.QUEUE_NAME_SEND_COUPON)
    public void process(People people, Channel channel, Message message) throws Exception {
        logger.info("[{}]处理延迟队列消息队列接收数据，消息体：{}", RabbitConstants.MQ_EXCHANGE_SEND_MESSAGE,
                JSONParseUtils.object2JsonString(people));

        System.out.println(message.getMessageProperties().getDeliveryTag());

        try {
            // 参数校验
            Assert.notNull(people, "people 消息体不能为NULL");

            // TODO 处理消息
            if (new Random().nextInt(10) == 6) {
                throw new IOException("随机异常触发： 未消费!");
            }
            // 确认消息已经消费成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error("MQ消息处理异常，消息体:{}", message.getMessageProperties().getCorrelationId(),
                    JSONParseUtils.object2JsonString(people), e);
            // TODO 容错处理，可保存消息到数据库或者利用死信队列在尝试重试
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }

    }
}