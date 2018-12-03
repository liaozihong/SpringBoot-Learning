package com.dashuai.learning.redislock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Redis lock application tests
 * <p/>
 * Created in 2018.12.03
 * <p/>
 * 分布式锁测试
 *
 * @author Liaozihong
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisLockApplicationTests {

    /**
     * The Redis pool.
     */
    @Autowired
    RedisPool redisPool;
    private static final int TIMEOUT = 10 * 1000;//超时时间 10s

    /**
     * 活动，特价，限量5份
     */
    static Map<String, Integer> products;//模拟商品信息表
    /**
     * The Stock.
     */
    static Map<String, Integer> stock;//模拟库存表
    /**
     * The Orders.
     */
    static Map<String, String> orders;//模拟下单成功用户表

    /**
     * Init.
     */
    @Before
    public void init() {
        /**
         * 模拟多个表，商品信息表，库存表，秒杀成功订单表
         */
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();
        products.put("123456", 5);
        stock.put("123456", 5);
    }

    /**
     * Order product mocck diff user.
     *
     * @param productId the product id
     */
//解决方法一:lock锁方法是可以解决的，但是请求会变慢,请求变慢是正常的。主要是没做到细粒度控制。比如有很多商品的秒杀，但是这个把所有商品的秒杀都锁住了。而且这个只适合单机的情况，不适合集群
    //解决方法二，基于Redis的分布式锁
    //支持分布式，可以更细粒度的控制
    //多台机器上多个线程对一个数据进行操作的互斥。
    //Redis是单线程的!!!
    public void orderProductMocckDiffUser(String productId) {
        //加锁
        String value = String.valueOf(UUID.randomUUID());
        try {
            //使用阻塞式锁，只能等当前用户操作完成才能获取到锁，这里只是模拟效果，实际应用请自行更改逻辑引用
            if (!redisPool.tryLock(productId, String.valueOf(value), 1000)) {
                log.warn("很抱歉，人太多了，换个姿势再试试~~");
                return;
            }

            //1.查询该商品库存，为0则活动结束
            int stockNum = stock.get(productId);
            if (stockNum == 0) {
                log.warn("活动结束");
            } else {
                //2.下单
                orders.put(String.valueOf(UUID.randomUUID()), productId);

                //3.减库存
                stockNum = stockNum - 1;//不做处理的话，高并发下会出现超卖的情况，下单数，大于减库存的情况。虽然这里减了，但由于并发，减的库存还没存到map中去。新的并发拿到的是原来的库存
                try {
                    Thread.sleep(100);//模拟减库存的处理时间
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stock.put(productId, stockNum);
                log.info("成功抢到!");
            }
        } finally {
//            //解锁
            redisPool.unlock(productId, value);
        }
    }

    /**
     * Test.
     */
    @Test
    public void test() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            new miaosha(countDownLatch).start();
        }
        countDownLatch.countDown();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        System.out.println(queryMap("123456"));
    }


    private String queryMap(String productId) {//模拟查询数据库
        return "国庆活动，皮蛋特教，限量"
                + products.get(productId)
                + "份,还剩:" + stock.get(productId)
                + "份,该商品成功下单用户数:"
                + orders.size() + "人";
    }

    /**
     * Miaosha
     * <p/>
     * Created in 2018.12.03
     * <p/>
     *
     * @author Liaozihong
     */
    class miaosha extends Thread {
        /**
         * The Count down latch.
         */
        CountDownLatch countDownLatch;

        /**
         * Instantiates a new Miaosha.
         *
         * @param countDownLatch the count down latch
         */
        public miaosha(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
            }
            orderProductMocckDiffUser("123456");
        }
    }
}
