### SpringBoot2 集成 Redis 主从、哨兵

上一篇文章中我使用docker-compose搭建了redis的主从复制，并启动3个哨兵容器相互监控。  

>

现在，我要使用SpringBoot来连接redis集群，
由于使用了哨兵模式，显然，不能向之前配置单节点那样配置连接池了，
节点经过故障转移后，主从结构已经发生了改变且主已经死亡，如果还按照之前那样写死IP的方式连接Redis的话，势必会出现错误。
可以想到，在Sentinel结构下，你必须向哨兵询问来获取谁是Master。  

更改后的自定义哨兵模式连接池与配置如下：  
由于是用docker搭建的redis主从、哨兵集群，所以访问哨兵在推举master时，给的master ip 为内网地址，所以需要将项目一同编排进
docker-compose.yml中，方可使用。  
```properties
java.redis.pool.max-total=10
java.redis.pool.max-idle=5
java.redis.pool.min-idle=5
# 多个用逗号隔开，host:port,host2:port2
java.redis.sentinel.nodes=sentinel1:26379,sentinel2:26380,sentinel3:26381
java.redis.sentinel.password=123456
java.redis.sentinel.master-name=mymaster
java.redis.sentinel.pool.max-total=10
java.redis.sentinel.pool.max-idle=5
java.redis.sentinel.pool.min-idle=5
```
连接池使用JedisSentinelPool。
```java
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
           jedisPool = new JedisSentinelPool(redisSentinelMasterName, sentinels, jedisPoolConfig, redisSentinelPassword);
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
```
配置完后，添加两个测试接口： 
```java
@RestController
public class TestRedisApi {

    @Autowired
    RedisSentinelPool redisSentinelPool;

    @RequestMapping("/setKey")
    public String setKey(String key, String value) {
        return redisSentinelPool.setex(key, value, 60);
    }

    @RequestMapping("/getKey")
    public String getKey(String key) {
        return redisSentinelPool.get(key);
    }
}
``` 
使用gradle构建Docker镜像,测试项目子模块过多，建议构建时，删剩当前的子模块即可,在Springboot-Learning目录下构建：
```bash
gradle clean build -x test
gradle SpringBoot-Redis-Sentinel:docker
```
接着更改哨兵集群的docker-compose.yml，加入该测试镜像的资源编排：  
```yaml
version: '3'
services:
  sentinel1:
    image: redis       ## 镜像
    container_name: redis-sentinel-1
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    ports:
    - "26379:26379"           ## 暴露端口
    volumes:
    - "./sentinel1.conf:/usr/local/etc/redis/sentinel.conf"
  sentinel2:
    image: redis                ## 镜像
    container_name: redis-sentinel-2
    ports:
    - "26380:26379"           ## 暴露端口
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
    - "./sentinel2.conf:/usr/local/etc/redis/sentinel.conf"
  sentinel3:
    image: redis                ## 镜像
    container_name: redis-sentinel-3
    ports:
    - "26381:26379"           ## 暴露端口
    command: redis-sentinel /usr/local/etc/redis/sentinel.conf
    volumes:
    - ./sentinel3.conf:/usr/local/etc/redis/sentinel.conf
  java-demo:
    image: redis-docker/springboot-redis-sentinel:1.0.0
    ports:
    - "8080:8080"
    depends_on:
    - sentinel1
    - sentinel2
    - sentinel3
networks:
  default:
    external:
      name: redis_sentinel-master
```
重启启动下哨兵集群即可。  
测试：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g0m9vr4z76j30l6057t8w.jpg)    
![](https://ws1.sinaimg.cn/large/006mOQRagy1g0m9wlykpoj30fh03jwek.jpg)  
可以看到，成功接入redis集群。  

> 学习本就逆流而上，不进则退。

源码已上传GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Redis-Sentinel
