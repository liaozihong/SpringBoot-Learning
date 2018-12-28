package com.dashuai.learning.kafka;

import com.dashuai.learning.kafka.queue.ProductMsg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

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
        while (true) {
            ProductMsg sender = app.getBean(ProductMsg.class);
            sender.sendMessage();
//            sender.sendMessage2();
            Thread.sleep(200);
        }
    }

}

