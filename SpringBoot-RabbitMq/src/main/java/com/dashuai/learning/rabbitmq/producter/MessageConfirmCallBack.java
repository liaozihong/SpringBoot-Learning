package com.dashuai.learning.rabbitmq.producter;

import com.dashuai.learning.rabbitmq.constants.RabbitConstants;
import com.dashuai.learning.rabbitmq.message.MyCorrelationData;
import com.dashuai.learning.utils.json.JSONParseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Message confirm call back
 * <p/>
 * Created in 2018.11.06
 * <p/>
 *
 * @author Liaodashuai
 */
@Configuration
public class MessageConfirmCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Logger logger = LoggerFactory.getLogger(MessageConfirmCallBack.class);


    @PostConstruct
    public void afterPropertiesSet() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 消息回调确认失败处理
        if (!ack && correlationData instanceof CorrelationData) {
            MyCorrelationData correlationDataExtends = (MyCorrelationData) correlationData;

            //消息发送失败,就进行重试，重试过后还不能成功就记录到数据库
            if (correlationDataExtends.getRetryCount() < RabbitConstants.MQ_RETRY_COUNT) {
                logger.info("MQ消息发送失败，消息重发，消息ID：{}，重发次数：{}，消息体:{}", correlationDataExtends.getId(),
                        correlationDataExtends.getRetryCount(), JSONParseUtils.object2JsonString(
                                correlationDataExtends.getMessage()));
                // 将重试次数加一
                correlationDataExtends.setRetryCount(correlationDataExtends.getRetryCount() + 1);
                // 重发发消息
                rabbitTemplate.convertAndSend(correlationDataExtends.getExchange(), correlationDataExtends.getRoutingKey(),
                        correlationDataExtends.getMessage(), correlationDataExtends);
            } else {
                //消息重试发送失败,将消息放到数据库等待补发
                logger.warn("MQ消息重发失败，消息入库，消息ID：{}，消息体:{}", correlationData.getId(),
                        JSONParseUtils.object2JsonString(correlationDataExtends.getMessage()));
                // TODO 保存消息到数据库或Redis
            }
        } else {
            logger.info("消息发送成功,消息ID:{}", correlationData.getId());
        }
    }

    /**
     * 用于实现消息发送到RabbitMQ交换器，但无相应队列与交换器绑定时的回调。
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        logger.error("MQ消息发送失败，replyCode:{}, replyText:{}，exchange:{}，routingKey:{}，消息体:{}",
                replyCode, replyText, exchange, routingKey, JSONParseUtils.object2JsonString(message.getBody()));
        // TODO 可考虑做相应处理
    }


}
