package com.dashuai.learning.ratelimiter.service;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

/**
 * Rate limiter service
 * <p/>
 * Created in 2018.11.16
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class RateLimiterService {

    /**
     * 创建令牌桶，每秒生成10个令牌，意味着每秒10个tps
     */
    RateLimiter rateLimiter = RateLimiter.create(10);

    /**
     * 尝试获取令牌，获取到返回true
     *
     * @return boolean
     */
    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}
