package com.dashuai.learning.nsq.service.impl;

import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

/**
 * Mq consumer service
 * Created in 2018.11.13
 *
 * @author Liaozihong
 */
@Service
public class MqConsumerServiceImpl {
    /**
     * The Topic.
     */
    @Value("${nsq.topic}")
    String topic;
    /**
     * The Address.
     */
    @Value("${nsq.address}")
    String address;

    /**
     * 消费者，程序初始化时启动
     */
    public void mqConsumer() {
        NSQLookup lookup = new DefaultNSQLookup();
        lookup.addLookupAddress(address, 4161);
        NSQConsumer consumer = new NSQConsumer(lookup,topic, "dakeng", (message) -> {
            System.out.println("received: " + new String(message.getMessage()));
            //now mark the message as finished.
            message.finished();

            //or you could requeue it, which indicates a failure and puts it back on the queue.
            //message.requeue();
        });

        consumer.start();
        //线程睡眠，让程序执行完
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.setExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }
}
