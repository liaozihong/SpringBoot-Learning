## 什么是Memcached?
Memcached 是一个高性能的分布式内存对象缓存系统，用于动态Web应用以减轻数据库负载。它通过在内存中缓存数据和对象来减少读取数据库的次数，从而提高动态、数据库驱动网站的速度,以减少必须读取外部数据源(如数据库或API)的次数。Memcached基于一个存储键/值对的hashmap。其守护进程（daemon）是用C写的，但是客户端可以用任何语言来编写，并通过memcached协议与守护进程通信。  
Memcached的api提供了一个分布在多台机器上的非常大的哈希表。当表满时，后续插入将导致以最近最少使用的顺序清除旧数据。使用Memcached的应用程序通常在退回到较慢的备份存储(如数据库)之前，会将请求和添加到RAM中。

因为 Spring Boot 没有针对Memcached提供对应的组建包，因此需要我们自己来集成。官方推出的 Java 客户端 Spymemcached 是一个比较好的选择之一。   
## 安装  
使用Docker安装memcached：  
```
docker run --name my-memcache -d memcached memcached -m 64
```
顺带安装memadmin管理工具，该工具有php编写，需要php环境，这里直接使用docker部署。一劳永逸。  
```
Dockerfile
FROM eboraas/apache-php
RUN apt-get update && apt-get -y install php5-memcache && apt-get clean && rm -rf /var/lib/apt/lists/*
ADD https://github.com/junstor/memadmin/archive/v1.0.12.tar.gz /var/www
RUN rm -fr /var/www/html && ln -s /var/www/memadmin-1.0.12 /var/www/html && cd /var/www/ && tar xvf v1.0.12.tar.gz

构建
docker build -t memadmin .  
运行测试
docker run -it --rm -p 11080:80 memadmin
http://ip:10080/，用户名密码默认admin，可以修改/var/www/html/config.php  
```
如果想方便的修改容器里面的文件，可以把文件copy出来，然后再copy进去  
或者先整个目录copy出来，然后重新启动一个容器通过-v映射进去  
像这样  
```
$ docker cp memadmin:/var/www/html/ /usr/local/memadmin
$ docker stop memadmin
$ docker run -d --name memadmin -v /usr/local/memadmin:/var/www/html/ -p 11080:80 memadmin
```
使memcache服务器使用64兆字节进行存储。
### Spymemcached 介绍  

Spymemcached 最早由 Dustin Sallings 开发，Dustin 后来和别人一起创办了 Couchbase (原NorthScale)，职位为首席架构师。2014 加入 Google。  

Spymemcached 是一个采用 Java 开发的异步、单线程的 Memcached 客户端， 使用 NIO 实现。Spymemcached 是 Memcached 的一个流行的 Java client 库，性能表现出色，广泛应用于 Java + Memcached 项目中。  

为了方便理解，我简单写了一个Springboot集成Memcached+spymemcached的例子。
## 集成SpringBoot实例
缓存操作管理类  
```java
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
```  
测试类  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class MemcachedApplicationTests {

    @Resource
    private OpeartionMemcachedManager memcachedManager;

    @Test
    public void testSetGet() {
        Boolean result = memcachedManager.set("someKey", 10000, "666666");
        if (result) {
            System.out.println("***********  " + memcachedManager.get("someKey").toString());
            return;
        }
        System.out.println("***********  操作失败!  ***********");
    }

    @Test
    public void testAsyncGet2() {
        //获取值，如果在5秒内没有返回值，将取消
        Object myObj = null;
        Object result = memcachedManager.ascynGet("someKey");
        System.out.println(result);
    }

    @Test
    public void testReplace() {
        Boolean flag = memcachedManager.replace("someKey", "dashuai", 10000);
        if (flag) {
            System.out.println("更新替换键值成功!");
            System.out.println("最终结果为:" + memcachedManager.get("someKey").toString());
            return;
        }
        System.out.println("更新键值失败!");
    }

    @Test
    public void testAdd() {
        Boolean flag = memcachedManager.add("someKey", "dashuai", 10000);
        if (flag) {
            System.out.println("最终结果为:" + memcachedManager.get("someKey").toString());
            return;
        }
        System.out.println("添加键值失败!");
    }

    @Test
    public void delete() {
        Boolean f = memcachedManager.delete("someKey");
        System.out.println("删除" + (f ? "成功!" : "失败!"));
    }

    @Test
    public void incrementTest() {
        long result = memcachedManager.increment("increment", 5, 20, 10000);
        System.out.println(result);
    }

    @Test
    public void decrementTest() {
        long result = memcachedManager.decrement("increment", 5, 20, 10000);
        System.out.println(result);
    }

    @Test
    public void asyncIncrement() {
        Long result = memcachedManager.asyncIncrement("increment", 5);
        System.out.println(result);
    }

    @Test
    public void asyncGetMultiTest() {
        memcachedManager.set("aa", 100000, "大帅");
        memcachedManager.set("bb", 100000, "大傻");
        List<String> list = new ArrayList<>();
        list.add("aa");
        list.add("bb");
        Map map = memcachedManager.asyncGetMulti(list);
        System.out.println(JSONParseUtils.object2JsonString(map));
    }

    @Test
    public void flushTest() {
        memcachedManager.flush();
        Object result = memcachedManager.get("aa");
        System.out.println(result);
    }
}
```

项目地址：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Memcacherd  

参考链接：    
http://www.ityouknow.com/springboot/2018/09/01/spring-boot-memcached.html  
