package com.dashuai.learning.nsq.service.impl;

import com.dashuai.learning.nsq.model.NsqChannelConst;
import com.dashuai.learning.nsq.model.NsqMessage;
import com.dashuai.learning.nsq.service.MqConsumerService;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

/**
 * Mq consumer service
 * Created in 2018.11.13
 *
 * @author Liaozihong
 */
@Service
@Slf4j
public class MqConsumerServiceImpl implements MqConsumerService {

    private String topic;
    private String nsqAddress;
    private Integer nsqPort;
    private Integer nsqThreadCount;

    public MqConsumerServiceImpl() {
    }

    public MqConsumerServiceImpl(String topic, String nsqAddress, Integer nsqPort, Integer nsqThreadCount) {
        this.topic = topic;
        this.nsqAddress = nsqAddress;
        this.nsqPort = nsqPort;
        this.nsqThreadCount = nsqThreadCount;
    }

    /**
     * 消费者，程序初始化时启动
     */
    @Override
    public void mqConsumer() {
        NSQLookup lookup = new DefaultNSQLookup();
        lookup.addLookupAddress(nsqAddress, nsqPort);
        NSQConsumer consumer = new NSQConsumer(lookup, topic, NsqChannelConst.DEFAULT_CHANNEL, (message) -> {
            if (message != null) {
                String msg = new String(message.getMessage());
                NsqMessage nsqMessage = null;
                try {
                    nsqMessage = JSONParseUtils.json2Object(msg, NsqMessage.class);
                } catch (Exception e) {
                    log.error("消息无法转换，存在问题");
                    message.finished();
                    return;
                }
                if (nsqMessage == null) {
                    message.finished();
                    log.error("消息为空，瞎发的消息，确认即可");
                    message.finished();
                    return;
                }
                if (!NsqChannelConst.DEFAULT_CHANNEL.equals(nsqMessage.getAction())) {
                    // 如果nsq消息体中的action不等于当前的chanel名称,说明不是当前消费者需要处理的数据,确认消费即可
                    message.finished();
                    return;
                }
                try {
                    log.info("消费特定消息: " + nsqMessage.getBody());
                    //确认消息
                    message.finished();
                    return;
                } catch (Exception e) {
                    //异常,重试
                    message.requeue();
                }
                return;
            }
            message.finished();
            return;
        });
        //默认是启动一次开启200个线程，处理200个任务，实际用不了那么多，根据需求配置就行了。
        consumer.setExecutor(Executors.newFixedThreadPool(nsqThreadCount));
        consumer.setMessagesPerBatch(nsqThreadCount);
        consumer.start();
        log.info("nsq 消费者启动成功!");
    }
}
