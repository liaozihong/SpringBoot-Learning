package com.dashuai.learning.memcached.manager;

import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Opeartion memcached manager
 * <p/>
 * Created in 2018.11.15
 * <p/>
 *
 * @author Liaozihong
 */
@Slf4j
public class OpeartionMemcachedManager {
    /**
     * The Memcached ip.
     */
    @Value("${memcached.ip}")
    String memcachedIp;
    /**
     * The Memcached port.
     */
    @Value("${memcached.port}")
    Integer memcachedPort;

    /**
     * The constant DEFAULT_TIMEOUT.
     */
    public final static int DEFAULT_TIMEOUT = 5;
    /**
     * The constant timeUnitSeconds.
     */
    public final static TimeUnit timeUnitSeconds = TimeUnit.SECONDS;

    private MemcachedClient memcachedClient;

    /**
     * 初始化
     */
    public void init() {
        try {
            //只采用单机模式，如果需要配置集群模式可用AddrUtil.getAddresses(servers),
            //可参考：https://blog.csdn.net/gtuu0123/article/details/4849905
            memcachedClient = new MemcachedClient(new InetSocketAddress(memcachedIp, memcachedPort));
            log.info("++++++++++++++++++++ Memcached 连接成功,Address:{}:{} ++++++++++++++++++++++", memcachedIp, memcachedPort);
        } catch (IOException e) {
            log.info("++++++++++++++++++++ Memcached 连接异常,Address:{}:{} ++++++++++++++++++++++{}", memcachedIp, memcachedPort, e);
        }
    }

    /**
     * 设置键值
     *
     * @param key    键
     * @param expire 有效时间
     * @param value  值
     * @return boolean boolean
     */
    public Boolean set(String key, int expire, Object value) {
        OperationFuture<Boolean> result = memcachedClient.set(key, expire, value);
        return getResult(result);
    }

    /**
     * 根据键获取值
     *
     * @param key the key
     * @return the object
     */
    public Object get(String key) {
        return memcachedClient.get(key);
    }

    /**
     * 以异步的方式获取值
     *
     * @param key the key
     * @return the object
     */
    public Object ascynGet(String key) {
        Future<Object> objectFuture = memcachedClient.asyncGet(key);
        return getResult(objectFuture);
    }

    /**
     * 将对象添加到缓存
     *
     * @param key    the key
     * @param value  the value
     * @param expire the expire
     * @return the boolean
     */
    public Boolean add(String key, Object value, int expire) {
        Future<Boolean> f = memcachedClient.add(key, expire, value);
        return getResult(f);
    }

    /**
     * 替换某个键值
     *
     * @param key    the 键
     * @param value  the 值
     * @param expire the 过期时间
     * @return the boolean
     */
    public Boolean replace(String key, Object value, int expire) {
        Future<Boolean> f = memcachedClient.replace(key, expire, value);
        return getResult(f);
    }

    /**
     * 删除某个特定键
     *
     * @param key the key
     * @return the boolean
     */
    public Boolean delete(String key) {
        Future<Boolean> f = memcachedClient.delete(key);
        return getResult(f);
    }

    /**
     * 立即从所有服务器清除所有缓存,慎用。
     *
     * @return the boolean
     */
    @Deprecated
    public Boolean flush() {
        Future<Boolean> f = memcachedClient.flush();
        return getResult(f);
    }

    /**
     * 从缓存中获取多个键值。
     *
     * @param keys the 键集合
     * @return the multi
     */
    public Map<String, Object> getMulti(Collection<String> keys) {
        return memcachedClient.getBulk(keys);
    }

    /**
     * 从缓存中获取多个键值
     *
     * @param keys the 键数组
     * @return the multi
     */
    public Map<String, Object> getMulti(String[] keys) {
        return memcachedClient.getBulk(keys);
    }

    /**
     * 异步地从缓存中获取一组对象并使用它们进行解码
     *
     * @param keys the 键集合
     * @return the map
     */
    public Map<String, Object> asyncGetMulti(Collection<String> keys) {
        Map<String, Object> map = null;
        Future<Map<String, Object>> f = memcachedClient.asyncGetBulk(keys);
        try {
            map = getResult(f);
        } catch (Exception e) {
            f.cancel(false);
        }
        return map;
    }

    /**
     * 增加给定的计数器，返回新值。
     *
     * @param key          the key
     * @param by           the 增值
     * @param defaultValue the 默认值(如计时器不存在)，如该key没值，则取默认值
     * @param expire       the 过期时间
     * @return the long
     */
    public long increment(String key, int by, long defaultValue, int expire) {
        return memcachedClient.incr(key, by, defaultValue, expire);
    }

    /**
     * 以给定的数量增加给定的键。
     *
     * @param key the key
     * @param by  the 增值
     * @return the long
     */
    public long increment(String key, int by) {
        return memcachedClient.incr(key, by);
    }

    /**
     * 减量.
     *
     * @param key          the key
     * @param by           the 减量
     * @param defaultValue the 默认值(如果计数器不存在)
     * @param expire       the 过期时间
     * @return the long
     */
    public long decrement(String key, int by, long defaultValue, int expire) {
        return memcachedClient.decr(key, by, defaultValue, expire);
    }

    /**
     * 减量
     *
     * @param key the key
     * @param by  the 要减的值
     * @return the long
     */
    public long decrement(String key, int by) {
        return memcachedClient.decr(key, by);
    }

    /**
     * 异步增量，并返回当前值.
     *
     * @param key the key
     * @param by  the 要增加的值
     * @return the long
     */
    public Long asyncIncrement(String key, int by) {
        Future<Long> f = memcachedClient.asyncIncr(key, by);
        return getResult(f);
    }

    /**
     * Async decrement long.
     * 异步减量，并返回当前值
     *
     * @param key the key
     * @param by  the 要减少的值
     * @return the long
     */
    public Long asyncDecrement(String key, int by) {
        Future<Long> f = memcachedClient.asyncDecr(key, by);
        return getResult(f);
    }

    /**
     * Gets result.
     * 获取返回结果
     *
     * @param <T>    the type parameter
     * @param future the future
     * @return the result
     */
    public <T> T getResult(Future<T> future) {
        try {
            return future.get(DEFAULT_TIMEOUT,
                    timeUnitSeconds);
        } catch (Exception e) {
            log.warn("获取返回结果失败!{}", e);
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public void disConnect() {
        if (memcachedClient == null) {
            return;
        }
        memcachedClient.shutdown();
    }

}
