package com.dashuai.learning.rabbitmq.producter;

import com.dashuai.learning.rabbitmq.message.MyCorrelationData;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.UUID;

@Service
public class MessageSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    private final static Logger logger = LoggerFactory.getLogger(MessageSender.class);


    /**
     * 发送MQ消息
     *
     * @param exchangeName 交换机名称
     * @param routingKey   路由名称
     * @param message      发送消息体
     */
    public void sendMessage(String exchangeName, String routingKey, Object message) {
        Assert.notNull(message, "message 消息体不能为NULL");
        Assert.notNull(exchangeName, "exchangeName 不能为NULL");
        Assert.notNull(routingKey, "routingKey 不能NULL");

        // 获取CorrelationData对象
        MyCorrelationData correlationData = this.correlationData(message);
        correlationData.setExchange(exchangeName);
        correlationData.setRoutingKey(routingKey);
        correlationData.setMessage(message);

        logger.info("发送MQ消息，消息ID：{}，消息体:{}, exchangeName:{}, routingKey:{}",
                correlationData.getId(), JSONParseUtils.object2JsonString(message), exchangeName, routingKey);
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message, correlationData);
    }

    /**
     * 消息相关数据（消息ID）
     *
     * @param message
     * @return
     */
    private MyCorrelationData correlationData(Object message) {

        return new MyCorrelationData(UUID.randomUUID().toString(), message);
    }
}
