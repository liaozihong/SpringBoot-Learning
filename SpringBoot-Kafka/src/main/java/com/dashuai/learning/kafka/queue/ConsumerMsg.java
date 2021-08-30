package com.dashuai.learning.kafka.queue;

import com.dashuai.learning.kafka.model.Message;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerMsg {

//    @KafkaListener(topics = {"test_ch"})
    public void processMessage(String content) {
        System.out.println(content);
        Message m = JSONParseUtils.json2Object(content, Message.class);
        System.out.println("test1--消费消息:" + JSONParseUtils.object2JsonString(m));
    }

//    @KafkaListener(topics = {"test2"})
//    public void processMessage2(String content) {
//        Message m = gson.fromJson(content, Message.class);
//        System.out.println("test2--消费消息:" + m.getMsg());
//    }
}
