package com.dashuai.learning.kafka;

import com.dashuai.learning.kafka.queue.ProductMsg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Kafka application
 * <p/>
 * Created in 2018.12.25
 * <p/>
 *
 * @author Liaozihong
 */
@SpringBootApplication
public class KafkaApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(KafkaApplication.class, args);
        Executor executor = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100000; i++) {
            executor.execute(()->{
                ProductMsg sender = app.getBean(ProductMsg.class);
                try {
                    sender.sendMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}

