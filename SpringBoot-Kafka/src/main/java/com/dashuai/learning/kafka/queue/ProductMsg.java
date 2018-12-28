package com.dashuai.learning.kafka.queue;


import com.dashuai.learning.kafka.model.Message;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class ProductMsg {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    private Gson gson = new Gson();

    public void sendMessage() throws InterruptedException {
        Message m = new Message();
        m.setId(System.currentTimeMillis());
        m.setMsg(UUID.randomUUID().toString());
        m.setSendTime(new Date());
        System.out.println("1生产了" + m.getMsg());
        Thread.sleep(1000);
        kafkaTemplate.send("test", gson.toJson(m));
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
