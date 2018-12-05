## RedLock 简介
在不同进程需要互斥地访问共享资源时，分布式锁是一种非常有用的技术手段。实现高效的分布式锁有三个属性需要考虑：
```
安全属性：互斥，不管什么时候，只有一个客户端持有锁
效率属性A:不会死锁
效率属性B：容错，只要大多数redis节点能够正常工作，客户端端都能获取和释放锁。
解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。  
```
Redlock是redis官方提出的实现分布式锁管理器的算法。这个算法会比一般的普通方法更加安全可靠。   
Redisson提供了很多种类型的分布式锁和分布式同步器，如下：  
```text
- 8.1. 可重入锁（Reentrant Lock） --例子以实现
- 8.2. 公平锁（Fair Lock）        --例子以实现
- 8.3. 联锁（MultiLock）
- 8.4. 红锁（RedLock）
- 8.5. 读写锁（ReadWriteLock）
- 8.6. 信号量（Semaphore）
- 8.7. 可过期性信号量（PermitExpirableSemaphore）
- 8.8. 闭锁（CountDownLatch）
```
详情可查看下面的参考链接  
### 分布式锁实现
```java
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
```
**配置客户端**
```java
@Configuration
public class RedissonConfiguration {

    /**
     * Gets client.
     *
     * @return the client
     */
    @Bean
    public RedissonClient redissonClient() {
        //如果是默认本地6379，则可不必配置，否则参照
        // https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95  配置
        return Redisson.create();
    }
}
```
**获取锁后的业务处理**
```java
/**
 * 获取锁后需要做的逻辑
 *
 * @param <T> the type parameter
 * @author Liaozihong
 */
public interface AcquiredLockWorker<T> {
    /**
     * Invoke after lock aquire t.
     *
     * @return the t
     * @throws Exception the exception
     */
    T invokeAfterLockAcquire() throws Exception;
}
```
**测试类**
```java
@RestController
@Slf4j
public class RedissonLockTestApi {
    /**
     * The Distributed locker.
     */
    @Autowired
    RedisLocker distributedLocker;

    /**
     * Test redlock string.
     * 并发下分布式锁测试API
     *
     * @return the string
     * @throws Exception the exception
     */
    @RequestMapping(value = "/redlock")
    public String testRedlock() throws Exception {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(5);
        // 测试5个并发
        for (int i = 0; i < 5; ++i) {
            new Thread(new Worker(startSignal, doneSignal)).start();
        }
        startSignal.countDown(); // let all threads proceed
        doneSignal.await();
        System.out.println("All processors done. Shutdown connection");
        return "redlock";
    }

    /**
     * Worker
     * <p/>
     * Created in 2018.12.05
     * <p/>
     *
     * @author Liaozihong
     */
    class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;

        /**
         * Instantiates a new Worker.
         *
         * @param startSignal the start signal
         * @param doneSignal  the done signal
         */
        Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }

        @Override
        public void run() {
            try {
                startSignal.await();
                //尝试加锁
                distributedLocker.lock("test", new AcquiredLockWorker<Object>() {

                    @Override
                    public Object invokeAfterLockAcquire() {
                        doTask();
                        return "success";
                    }

                });
            } catch (Exception e) {
                log.warn("获取锁出现异常", e);
            }
        }

        /**
         * Do task.
         */
        void doTask() {
            System.out.println(Thread.currentThread().getName() + " 抢到锁!");
            Random random = new Random();
            int _int = random.nextInt(200);
            System.out.println(Thread.currentThread().getName() + " sleep " + _int + "millis");
            try {
                Thread.sleep(_int);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 释放锁!");
            doneSignal.countDown();
        }
    }
}
```
运行结果：  
```text
Thread-26 抢到锁!
Thread-26 sleep 181millis
Thread-26 释放锁!
Thread-25 抢到锁!
Thread-25 sleep 189millis
Thread-25 释放锁!
Thread-27 抢到锁!
Thread-27 sleep 42millis
Thread-27 释放锁!
Thread-29 抢到锁!
Thread-29 sleep 97millis
Thread-29 释放锁!
Thread-28 抢到锁!
Thread-28 sleep 45millis
Thread-28 释放锁!
All processors done. Shutdown connection
```
源码 GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Redis-Distributed-Redlock   
参考链接：  
[如何用Redlock实现分布式锁](https://blog.csdn.net/forezp/article/details/70305336)  
[分布式锁和同步器](https://github.com/redisson/redisson/wiki/8.-%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%92%8C%E5%90%8C%E6%AD%A5%E5%99%A8)