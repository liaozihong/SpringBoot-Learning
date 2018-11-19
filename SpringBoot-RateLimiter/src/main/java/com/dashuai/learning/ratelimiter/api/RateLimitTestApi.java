package com.dashuai.learning.ratelimiter.api;

import com.dashuai.learning.ratelimiter.service.AopTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;

/**
 * 测试使用自定义注解的方式进行限流
 */
@RestController
public class RateLimitTestApi {
    @Autowired
    AopTestService aopTestService;

    /**
     * 测试限流注解
     *
     * @return
     */
    @RequestMapping("/")
    public String testApi() {
        rateLimitTest();
        rateLimitTestV2();
        return "success";
    }

    /**
     * 并发测试 1
     */
    public void rateLimitTest() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i <= 100; i++) {
            Business business = new Business(countDownLatch);
            business.start();
        }
        countDownLatch.countDown();
    }

    class Business extends Thread {
        CountDownLatch countDownLatch;

        public Business(CountDownLatch latch) {
            this.countDownLatch = latch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
                aopTestService.testRateLimiter(5.0, "大傻");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 并发测试 1
     */
    public void rateLimitTestV2() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i <= 100; i++) {
            BusinessV2 business = new BusinessV2(countDownLatch);
            business.start();
        }
        countDownLatch.countDown();
    }

    class BusinessV2 extends Thread {
        CountDownLatch countDownLatch;

        public BusinessV2(CountDownLatch latch) {
            this.countDownLatch = latch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
                aopTestService.testRateLimiterv2(10.0, "大傻");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
