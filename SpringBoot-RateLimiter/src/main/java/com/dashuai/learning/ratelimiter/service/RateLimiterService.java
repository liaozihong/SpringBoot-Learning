package com.dashuai.learning.ratelimiter.service;

import com.google.common.util.concurrent.RateLimiter;

/**
 * Rate limiter service
 * <p/>
 * Created in 2018.11.16
 * <p/>
 *
 * @author Liaozihong
 */
public class RateLimiterService {
    private RateLimiter rateLimiter;


    public RateLimiterService(Integer count) {
        rateLimiter = RateLimiter.create(Double.valueOf(count));
    }

    /**
     * 创建令牌桶，每秒生成10个令牌，意味着每秒10个tps
     */
    public RateLimiter rateLimiter() {
        return rateLimiter;
    }

}
