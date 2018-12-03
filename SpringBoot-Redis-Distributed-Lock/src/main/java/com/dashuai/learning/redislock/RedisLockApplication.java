package com.dashuai.learning.redislock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Redis lock application
 * <p/>
 * Created in 2018.12.03
 * <p/>
 *
 * @author Liaozihong
 */
@SpringBootApplication
public class RedisLockApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RedisLockApplication.class, args);
    }
}
