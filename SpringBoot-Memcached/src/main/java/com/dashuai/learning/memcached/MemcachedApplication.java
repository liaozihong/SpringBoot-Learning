package com.dashuai.learning.memcached;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Memcached application
 * <p/>
 * Created in 2018.11.15
 * <p/>
 *
 * @author Liaozihong
 */
@SpringBootApplication
public class MemcachedApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MemcachedApplication.class, args);
    }
}
