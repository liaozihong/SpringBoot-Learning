package com.dashuai.learning.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limiter annotation
 * Created in 2020.08.06
 *
 * @author Liaozihong
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiterAnnotation {
    /**
     * 限流服务名
     *
     * @return string
     */
    String name() default "";

    /**
     * 每秒限流次数
     *
     * @return double
     */
    int count() default 500;
}
