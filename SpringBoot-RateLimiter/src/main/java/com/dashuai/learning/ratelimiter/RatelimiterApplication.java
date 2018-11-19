package com.dashuai.learning.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ratelimiter application
 * <p/>
 * Created in 2018.11.19
 * <p/>
 *
 * @author Liaozihong
 */
@SpringBootApplication
public class RatelimiterApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RatelimiterApplication.class, args);
    }
}
