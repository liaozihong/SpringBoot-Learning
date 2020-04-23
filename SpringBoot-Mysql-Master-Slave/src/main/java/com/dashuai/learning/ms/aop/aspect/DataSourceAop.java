package com.dashuai.learning.ms.aop.aspect;

import com.dashuai.learning.ms.support.DBContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAop {
    @Pointcut("!@annotation(com.dashuai.learning.ms.aop.Master) " +
            "&& (execution(* com.dashuai.learning.ms.dao..*.select*(..)) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.get*(..)))")
    public void readPointcut() {

    }

    @Pointcut("@annotation(com.dashuai.learning.ms.aop.Master) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.insert*(..)) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.add*(..)) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.update*(..)) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.edit*(..)) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.delete*(..)) " +
            "|| execution(* com.dashuai.learning.ms.dao..*.remove*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        DBContextHolder.slave();
    }

    @Before("writePointcut()")
    public void write() {
        DBContextHolder.master();
    }


    /**
     * 另一种写法：if...else...  判断哪些需要读从数据库，其余的走主数据库
     */
//    @Before("execution(* com.dashuai.learning.ms.dao.impl.*.*(..))")
//    public void before(JoinPoint jp) {
//        String methodName = jp.getSignature().getName();
//
//        if (StringUtils.startsWithAny(methodName, "get", "select", "find")) {
//            DBContextHolder.slave();
//        }else {
//            DBContextHolder.master();
//        }
//    }
}
