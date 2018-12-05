package com.dashuai.learning.redislockredlock.support;


/**
 * The interface Distributed locker.
 * 获取锁管理类
 *
 * @author Liaozihong
 */
public interface DistributedLocker {
    /**
     * 获取锁
     *
     * @param <T>          the type parameter
     * @param resourceName 锁的名称
     * @param worker       获取锁后的处理类
     * @return 处理完具体的业务逻辑要返回的数据 t
     * @throws Exception the exception
     */
    <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception;

    /**
     * 可重入锁
     *
     * @param <T>          the 参数类型，泛型指定返回类型
     * @param resourceName the 资源名 key
     * @param worker       the 获取资源后的操作
     * @param lockTime     the 锁过期时间
     * @return the t
     * @throws Exception the exception
     */
    <T> T tryLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception;

    /**
     * 公平锁，保证了当多个Redisson客户端线程同时请求加锁时，优先分配给先发出请求的线程
     *
     * @param <T>          the type parameter
     * @param resourceName the resource name
     * @param worker       the worker
     * @param lockTime     the lock time
     * @return the t
     * @throws Exception the exception
     */
    <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception;
}
