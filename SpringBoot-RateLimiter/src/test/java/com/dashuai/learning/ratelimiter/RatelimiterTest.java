package com.dashuai.learning.ratelimiter;

import com.dashuai.learning.ratelimiter.service.RateLimiterService;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RatelimiterApplication.class)
public class RatelimiterTest {

    @Autowired
    RateLimiterService rateLimiterService;

    /**
     * The entry point of application.
     * <p>
     * RateLimiter有一个有趣的特性是[前人挖坑后人跳]，也就是说RateLimiter允许某次请求拿走了超出剩余令牌数的令牌，
     * 但是下一次请求将为此付出代价，一直等到令牌亏空补上，并且桶中有足够本次请求使用的令牌为止。
     * 这里面就涉及到一个权衡，是让前一次请求干等到令牌够用才走掉呢，还是让它走掉后面的请求等一等呢？
     * Guava的设计者选择的是后者，先把眼前的活干了，后面的事后面再说。
     */
    @Test
    public void advanceConsumerTest() {
        //每秒产生2个令牌
        RateLimiter rateLimiter = RateLimiter.create(2);
        //获取令牌，返回获取令牌所需等待的时间，获取太多，导致后面得等亏损的令牌补上才能获取到。
        System.out.println(rateLimiter.acquire(10));
        System.out.println(rateLimiter.tryAcquire(2, 2, TimeUnit.SECONDS));
        System.out.println(rateLimiter.acquire(2));
        System.out.println(rateLimiter.acquire(1));
    }


    @Test
    public void rateLimitTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i <= 100; i++) {
            Business business = new Business(countDownLatch);
            business.start();
        }
        countDownLatch.countDown();
        //等待结果处理,有只设了5个令牌，所以，只有10个请求有效。
        TimeUnit.SECONDS.sleep(10);
        System.out.println("所有模拟请求结束  at " + new Date());
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
                if (rateLimiterService.rateLimiter().tryAcquire()) {
                    //模拟业务
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("成功处理业务" + new Date());
                } else {
                    System.out.println("系统繁忙！请稍后再试!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
