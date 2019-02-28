package com.dashuai.learning.redissentinel;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created in 2019.02.27
 *
 * @author Liaozihong
 */
@Slf4j
public class RedisSentinelPool {

    @Value("${java.redis.pool.max-total:10}")
    private Integer redisMaxTotal;
    @Value("${java.redis.pool.min-idle}")
    private Integer redisMaxIdle;
    @Value("${java.redis.pool.min-idle}")
    private Integer redisMinIdle;
    @Value("${java.redis.sentinel.nodes}")
    private String redisSentinelNodes;
    @Value("${java.redis.sentinel.password}")
    private String redisSentinelPassword;
    @Value("${java.redis.sentinel.master-name}")
    private String redisSentinelMasterName;
    @Value("${java.redis.sentinel.pool.max-total}")
    private Integer redisSentinelMaxTotal;
    @Value("${java.redis.sentinel.pool.max-idle}")
    private Integer redisSentinelMaxIdle;
    @Value("${java.redis.sentinel.pool.min-idle}")
    private Integer redisSentinelMinIdle;

    private JedisSentinelPool jedisPool;

    public void init() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisMaxTotal);
        jedisPoolConfig.setMaxIdle(redisMaxIdle);
        jedisPoolConfig.setMinIdle(redisMinIdle);
        String[] hosts = redisSentinelNodes.split(",");
        Set<String> sentinels = new HashSet<>(Arrays.asList(hosts));
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(redisSentinelMaxTotal);
        poolConfig.setMaxIdle(redisSentinelMaxIdle);
        poolConfig.setMinIdle(redisSentinelMinIdle);
        if (Strings.isNullOrEmpty(redisSentinelPassword)) {
            jedisPool = new JedisSentinelPool(redisSentinelMasterName, sentinels, jedisPoolConfig);
        } else {
            jedisPool = new JedisSentinelPool(redisSentinelMasterName, sentinels, jedisPoolConfig, redisSentinelPassword);
        }
        if (isConnectSuccess()) {
            // 初始化成功
            log.info("++++++ Init Redis Sentinel Pool SUCCESS! ++++++");
        } else {
            // 初始化失败
            log.info("++++++ Init Redis Sentinel Pool FAILURE! ++++++");
        }
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
