package com.dashuai.learning.ratelimiter.annotation.aspect;

import com.dashuai.learning.ratelimiter.annotation.RateLimiterAnnotation;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Rate limit aspect
 * <p/>
 * Created in 2018.11.19
 * <p/>
 * 自定义限流注解
 *
 * @author Liaozihong
 */
@Aspect
@Component
public class RateLimiterAnnotationAspect {

    private ConcurrentMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    /**
     * Before.
     *
     * @param point the point
     */
    @Before("@annotation(com.dashuai.learning.ratelimiter.annotation.RateLimiterAnnotation)")
    public void before(JoinPoint point) {
        RateLimiterAnnotation rateLimiterAnnotation = this.getAnnotation(point, RateLimiterAnnotation.class);
        double rateLimitCount = rateLimiterAnnotation.count();
        String rateLimitName = rateLimiterAnnotation.name();
        if (rateLimiterMap.get(rateLimitName) == null) {
            rateLimiterMap.put(rateLimitName, RateLimiter.create(rateLimitCount));
        }
        rateLimiterMap.get(rateLimitName).acquire();
    }

    private <T extends Annotation> T getAnnotation(JoinPoint pjp, Class<T> clazz) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(clazz);
    }
}
