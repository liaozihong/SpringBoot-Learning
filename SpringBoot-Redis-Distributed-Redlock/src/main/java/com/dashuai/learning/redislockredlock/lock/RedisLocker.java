package com.dashuai.learning.redislockredlock.lock;

import com.dashuai.learning.redislockredlock.exception.UnableToAcquireLockException;
import com.dashuai.learning.redislockredlock.support.AcquiredLockWorker;
import com.dashuai.learning.redislockredlock.support.DistributedLocker;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis locker
 * <p/>
 * Created in 2018.12.05
 * <p/>
 *
 * @author Liaozihong
 */
@Component
public class RedisLocker implements DistributedLocker {

    private final static String LOCKER_PREFIX = "lock:";

    /**
     * The Redisson client.
     */
    @Autowired
    RedissonClient redissonClient;

    @Override
    public <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception {
        return fairLock(resourceName, worker, 100);
    }

    @Override
    public <T> T tryLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception {
        RLock lock = redissonClient.getLock(LOCKER_PREFIX + resourceName);
        // (可重入锁)最多等待100秒，锁定后经过lockTime秒后自动解锁
        boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return worker.invokeAfterLockAcquire();
            } finally {
                lock.unlock();
            }
        }
        throw new UnableToAcquireLockException();
    }

    @Override
    public <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception {
        RLock lock = redissonClient.getFairLock(LOCKER_PREFIX + resourceName);
        // (公平锁)最多等待100秒，锁定后经过lockTime秒后自动解锁
        boolean success = lock.tryLock(100, lockTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return worker.invokeAfterLockAcquire();
            } finally {
                lock.unlock();
            }
        }
        throw new UnableToAcquireLockException();
    }

}
