package com.dashuai.learning.redislock;

import com.dashuai.learning.redislock.constant.ApplicationConfigConst;
import com.dashuai.learning.redislock.shell.RedisShell;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis pool
 * <p/>
 * Created in 2018.11.30
 * <p/>
 * redis 连接池
 *
 * @author Liaozihong
 */
@Slf4j
public class RedisPool {
    private static JedisPool jedisPool;

    @Value(ApplicationConfigConst.REDIS_IP)
    private String ip;

    @Value(ApplicationConfigConst.REDIS_PORT)
    private Integer port;

    @Value(ApplicationConfigConst.REDIS_PASSWORD)
    private String password;

    @Value(ApplicationConfigConst.REDIS_TIMEOUT)
    private Integer timeout;

    @Value(ApplicationConfigConst.REDIS_MAX_TOTAL)
    private Integer maxTotal;

    @Value(ApplicationConfigConst.REDIS_MAX_IDLE)
    private Integer maxIdle;

    @Value(ApplicationConfigConst.REDIS_MAX_WAIT_MILLIS)
    private Long maxWaitMillis;

    @Value(ApplicationConfigConst.REDIS_TEST_ON_BORROW)
    private Boolean testOnBorrow;

    @Value(ApplicationConfigConst.REDIS_TEST_ON_RETURN)
    private Boolean testOnReturn;

    @Value(ApplicationConfigConst.REDIS_LOCK_TIMEOUT)
    private Long maxLockTimeout;

    /**
     * Init.
     */
    public void init() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Optional.ofNullable(maxTotal).orElse(1024));
        config.setMaxIdle(Optional.ofNullable(maxIdle).orElse(1000));
        config.setMaxWaitMillis(Optional.ofNullable(maxWaitMillis).orElse(2000L));
        config.setTestOnBorrow(Optional.ofNullable(testOnBorrow).orElse(true));
        config.setTestOnReturn(Optional.ofNullable(testOnReturn).orElse(true));
        if (Strings.isNullOrEmpty(password)) {
            jedisPool = new JedisPool(config,
                    Optional.ofNullable(ip).orElse("localhost"),
                    Optional.ofNullable(port).orElse(6379),
                    Optional.ofNullable(timeout).orElse(100000));
        } else {
            jedisPool = new JedisPool(config,
                    Optional.ofNullable(ip).orElse("localhost"),
                    Optional.ofNullable(port).orElse(6379),
                    Optional.ofNullable(timeout).orElse(100000), password);
        }
        if (isConnectSuccess()) {
            // 初始化成功
            log.info("++++++ Init Redis Pool SUCCESS! Redis IP={} Port={} ++++++", ip, port);
        } else {
            // 初始化失败
            log.info("++++++ Init Redis Pool FAILURE! Redis IP={} Port={} ++++++", ip, port);
        }
    }

    /**
     * 加锁.
     *
     * @param key                  the lock 键
     * @param requestId            the 随机唯一标识
     * @param expireMillionSeconds the 过期时间
     * @return the boolean
     */
    public boolean lock(String key, String requestId, int expireMillionSeconds) {
        //获取redis资源
        Jedis jedis = getRedis();
        String result = jedis.set(key, requestId, "NX", "PX", expireMillionSeconds);
        //释放
        recycleRedis(jedis);
        return "OK".equals(result);
    }

    /**
     * 尝试获取锁（阻塞式实现）.
     *
     * @param key                  the lock键
     * @param requestId            the 随机生成的唯一标识，
     * @param expireMillionSeconds the 该锁的过期时间，避免redis宕了出现死锁
     * @return the boolean
     */
    public boolean tryLock(String key, String requestId, int expireMillionSeconds) {
        Jedis jedis = getRedis();
        long startTime = System.currentTimeMillis();
        while (true) {
            String result = jedis.set(key, requestId, "NX", "PX", expireMillionSeconds);
            if ("OK".equals(result)) {
                recycleRedis(jedis);
                return true;
            }
            try {
                //视情况而定，避免无效循环过多
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
            }
            long time = System.currentTimeMillis() - startTime;
            //获取锁超时，避免获取不到锁出现的问题
            if (time > maxLockTimeout) {
                recycleRedis(jedis);
                return false;
            }
        }
    }

    /**
     * 解锁.key和value必须两者都匹配才能删除，目的是防止误解别人的锁
     *
     * @param key       the lock 键
     * @param requestId the 对应的唯一标识
     * @return the boolean
     */
    public boolean unlock(String key, String requestId) {
        Jedis jedis = getRedis();
        Object result = jedis.eval(RedisShell.REDIS_UNLOCK, Collections.singletonList(key),
                Collections.singletonList(requestId));
        recycleRedis(jedis);
        return Long.valueOf(1L).equals(result);
    }

    /**
     * Del long.
     *
     * @param key the key
     * @return the long
     */
    public long del(String key) {
        Jedis jedis = getRedis();
        long result = jedis.del(key);
        recycleRedis(jedis);
        return result;
    }

    /**
     * 获取当前key的生成时长
     *
     * @param key the key
     * @return the long
     */
    public long ttl(String key) {
        Jedis jedis = getRedis();
        long result = jedis.ttl(key);
        recycleRedis(jedis);
        return result;
    }

    /**
     * Get string.
     *
     * @param key the key
     * @return the string
     */
    public String get(String key) {
        Jedis jedis = getRedis();
        String result = jedis.get(key);
        recycleRedis(jedis);
        return result;
    }

    /**
     * Sets .
     *
     * @param key           the key
     * @param value         the value
     * @param expireSeconds the expire seconds
     * @return the
     */
    public String setex(String key, String value, int expireSeconds) {
        Jedis jedis = getRedis();
        String result = jedis.setex(key, expireSeconds, value);
        recycleRedis(jedis);
        return result;
    }

    /**
     * 重新初始化连接池
     */
    public void reload() {
        close();
        init();
    }

    /**
     * 销毁Redis连接池
     */
    public void close() {
        if (null != jedisPool) {
            jedisPool.close();
        }
    }

    /**
     * 资源回收
     *
     * @param jedis jedis实例
     */
    private void recycleRedis(Jedis jedis) {
        jedis.close();
    }

    /**
     * @return 连接池中的Jedis实例
     */
    private Jedis getRedis() {
        return jedisPool.getResource();
    }

    /**
     * @return 是否成功连接上redis
     */
    private boolean isConnectSuccess() {
        Jedis jedis = getRedis();
        String ping = jedis.ping();
        recycleRedis(jedis);
        return ping.equals("PONG");
    }
}
