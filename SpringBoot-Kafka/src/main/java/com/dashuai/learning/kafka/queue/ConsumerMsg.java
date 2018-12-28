package com.dashuai.learning.kafka.queue;

import com.dashuai.learning.kafka.model.Message;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerMsg {
    private Gson gson = new Gson();

    @KafkaListener(topics = {"test"})
    public void processMessage(String content) {
        Message m = gson.fromJson(content, Message.class);
        System.out.println("test1--消费消息:" + m.getMsg());
    }

//    @KafkaListener(topics = {"test2"})
//    public void processMessage2(String content) {
//        Message m = gson.fromJson(content, Message.class);
//        System.out.println("test2--消费消息:" + m.getMsg());
//    }
}
