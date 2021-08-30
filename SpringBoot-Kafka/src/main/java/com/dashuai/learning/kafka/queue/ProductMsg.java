package com.dashuai.learning.kafka.queue;


import com.dashuai.learning.kafka.model.Message;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProductMsg {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    private AtomicInteger atomicInteger = new AtomicInteger(1);
    public void sendMessage() throws InterruptedException {
        Message m = new Message();
        m.setId(atomicInteger.getAndIncrement());
        m.setMsg(UUID.randomUUID().toString());
        m.setSendTime(new Date());
        Thread.sleep(1000);
        String content = JSONParseUtils.object2JsonString(m);
        System.out.println("生产了" + content);
        kafkaTemplate.send("test_ch",content);
    }


//    public void sendMessage2() throws InterruptedException {
//        Message m = new Message();
//        m.setId(System.currentTimeMillis());
//        m.setMsg(UUID.randomUUID().toString());
//        m.setSendTime(new Date());
//        System.out.println("2生产了" + m.getMsg());
//        Thread.sleep(1000);
//        kafkaTemplate.send("test2", gson.toJson(m));
//    }
}
