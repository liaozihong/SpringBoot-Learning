package com.dashuai.learning.rabbitmq.listener;

import com.dashuai.learning.rabbitmq.constants.RabbitConstants;
import com.dashuai.learning.rabbitmq.message.People;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Dead message listener
 * <p/>
 * Created in 2018.11.08
 * <p/>
 * 死信队列消费者
 *
 * @author Liaodashuai
 */
@Service
public class DeadMessageListener {

    private final Logger logger = LoggerFactory.getLogger(DeadMessageListener.class);

    /**
     * 死信队列消费者
     *
     * @param people  the 消息内容
     * @param channel the 信道
     * @param message the 由Header和Body组成，包括生产者添加的各种属性的集合
     * @throws Exception the exception
     */
//    @RabbitListener(queues = RabbitConstants.QUEUE_NAME_DEAD_QUEUE)
    public void process(People people, Channel channel, Message message) throws Exception {
        logger.info("[{}]处理死信队列消息队列接收数据，消息体：{}", RabbitConstants.QUEUE_NAME_DEAD_QUEUE,
                JSONParseUtils.object2JsonString(people));

        System.out.println(message.getMessageProperties().getDeliveryTag());

        try {
            // 参数校验
            Assert.notNull(people, "sendMessage 消息体不能为NULL");

            // TODO 处理消息
            // 确认消息已经消费成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // TODO 如若在出错，可记录到数据库或缓存或保存到日志文件中
            logger.error("MQ消息处理异常，消息体:{}", message.getMessageProperties().getCorrelationId(),
                    JSONParseUtils.object2JsonString(people), e);
            // 确认消息已经消费消费失败，将消息发给下一个消费者
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}