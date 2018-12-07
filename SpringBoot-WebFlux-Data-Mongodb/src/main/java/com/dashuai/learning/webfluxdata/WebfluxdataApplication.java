package com.dashuai.learning.webfluxdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Webfluxdata application
 * <p/>
 * Created in 2018.12.06
 * <p/>
 *
 * @author Liaozihong
 */
@SpringBootApplication
@EnableMongoRepositories
public class WebfluxdataApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WebfluxdataApplication.class, args);
    }
}
