package com.dashuai.learning.ratelimiter.api;

import com.dashuai.learning.ratelimiter.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Invoke service api
 * <p/>
 * Created in 2018.11.16
 * <p/>
 * 压测接口，也可以使用测试用例查看 {@Link com.dashuai.learning.ratelimiter.RatelimiterApplicationTests}
 * Guava的RateLimiter已经设计的很优雅了，不过你可以基于上面造轮子，如改造成利用AOP进行只标识注解就可进行限制
 *
 * @author Liaozihong
 */
@RestController
public class InvokeServiceApi {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RateLimiterService rateLimiterService;

    /**
     * Access string.
     *
     * @return the string
     */
    @RequestMapping("/access")
    public String access() {
        //尝试获取令牌
        if (rateLimiterService.rateLimiter().tryAcquire()) {
            //模拟业务执行500毫秒
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "aceess success [" + sdf.format(new Date()) + "]";
        } else {
            return "aceess limit [" + sdf.format(new Date()) + "]";
        }
    }
}
