package com.dashuai.learning.nsq.service.impl;

import com.dashuai.learning.nsq.model.NsqMessage;
import com.dashuai.learning.nsq.service.MqProductService;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Mq product service
 * Created in 2018.11.13
 *
 * @author Liaozihong
 */
@Slf4j
@Service
public class MqProductServiceImpl implements MqProductService {
    /**
     * The Topic.
     */
    @Value("${nsq.topic}")
    String topic;

    @Autowired
    NSQProducer nsqProducer;

    @Override
    public String sendTestMessage(String body) {
        try {
            nsqProducer.produce(topic, body.getBytes());
            log.info("消息发送成功!时间:" + new Date());
            return "Success";
        } catch (NSQException e) {
            log.error("nsq 连接异常!msg={}", e.getMessage());
            return "Error:" + e.getMessage();
        } catch (TimeoutException e) {
            log.error("nsq 发送消息超时!msg={}", e.getMessage());
            return "Error:" + e.getMessage();
        } catch (Exception e) {
            log.error("出现未知异常!", e);
            return "Error:" + e.getMessage();
        }
    }

    @Override
    public String sendTestMessageByChannel(String body) {
        try {
            NsqMessage message = new NsqMessage();
            message.setId(UUID.randomUUID().getLeastSignificantBits());
            message.setAction("testChannel");
            message.setBody(body);
            String msg = JSONParseUtils.object2JsonString(message);
            nsqProducer.produce(topic, msg.getBytes());
            log.info("消息发送特定Channel成功!时间:" + new Date());
            return "Success";
        } catch (NSQException e) {
            log.error("nsq 连接异常!msg={}", e.getMessage());
            return "Error:" + e.getMessage();
        } catch (TimeoutException e) {
            log.error("nsq 发送消息超时!msg={}", e.getMessage());
            return "Error:" + e.getMessage();
        } catch (Exception e) {
            log.error("出现未知异常!", e);
            return "Error:" + e.getMessage();
        }
    }
}
