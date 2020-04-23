package com.dashuai.learning.ms.support;

import com.dashuai.learning.ms.model.enums.DBTypeEnum;

import java.util.concurrent.atomic.AtomicInteger;

public class DBContextHolder {
    private static final ThreadLocal<DBTypeEnum> CONTEXT_HOLDER = new ThreadLocal<>();

    private static final AtomicInteger COUNTER = new AtomicInteger(-1);

    public static void set(DBTypeEnum dbType) {
        CONTEXT_HOLDER.set(dbType);
    }

    public static DBTypeEnum get() {
        return CONTEXT_HOLDER.get();
    }

    public static void master() {
        set(DBTypeEnum.MASTER);
        System.out.println("切换到master");
    }

    public static void slave() {
        //  轮询，如果有多个从节点，可配置轮询读取
//        int index = COUNTER.getAndIncrement() % 2;
//        if (COUNTER.get() > 9999) {
//            COUNTER.set(-1);
//        }
//        if (index == 0) {
        set(DBTypeEnum.SLAVE1);
        System.out.println("切换到slave1");
//        }
//        else {
//            set(DBTypeEnum.SLAVE2);
//            System.out.println("切换到slave2");
//        }
    }
}
