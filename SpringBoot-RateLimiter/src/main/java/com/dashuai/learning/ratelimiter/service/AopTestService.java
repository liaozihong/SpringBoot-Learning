package com.dashuai.learning.ratelimiter.service;

/**
 * The interface Aop test service.
 *
 * @author Liaozihong
 */
public interface AopTestService {
    /**
     * Test rate limiter string.
     * 测试自定义限流注解
     *
     * @param count   the count
     * @param context the context
     * @return the string
     */
    String testRateLimiter(Double count, String context);

    /**
     * Test rate limiterv 2 string.
     *
     * @param count   the count
     * @param context the context
     * @return the string
     */
    String testRateLimiterv2(Double count, String context);
}
