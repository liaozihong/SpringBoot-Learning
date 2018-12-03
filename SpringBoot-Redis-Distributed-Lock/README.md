## 什么是Redis？
Redis通常被称为数据结构服务器。这意味着Redis通过一组命令提供对可变数据结构的访问，这些命令使用带有TCP套接字和简单协议的服务器 - 客户端模型发送。因此，不同的进程可以以共享方式查询和修改相同的数据结构。  

Redis中实现的数据结构有一些特殊属性：  

Redis关心将它们存储在磁盘上，即使它们总是被提供并修改到服务器内存中。这意味着Redis速度很快，但这也是非易失性的。  
数据结构的实现强调内存效率，因此与使用高级编程语言建模的相同数据结构相比，Redis内部的数据结构可能使用更少的内存。  
Redis提供了许多在数据库中自然可以找到的功能，如复制，可调节的持久性级别，群集，高可用性。  
另一个很好的例子是将Redis视为memcached的更复杂版本，其中操作不仅仅是SET和GET，而是用于处理复杂数据类型（如Lists，Sets，有序数据结构等）的操作。  

## Redis 实现分布式锁
### 什么是分布式锁？
顾名思义，分布式锁肯定是用在分布式环境下。在分布式环境下，使用分布式锁的目的也是保证同一时刻只有一个线程来修改共享变量，修改共享缓存……。  

### 前景：
jdk提供的锁只能保证线程间的安全性，但分布式环境下，各节点之间的线程同步执行却得不到保障，分布式锁由此诞生。  

实现方式有以下几种：  
1. 基于数据库实现分布式锁；
1. 基于缓存（Redis等）实现分布式锁；
1. 基于Zookeeper实现分布式锁；

使用Redis做分布式锁，相对其他两种方案性能是最好的。当然也是较复杂的。
### 设计实现
实现分布式锁必须要有的可靠性保证如下：
```
互斥性：相互排斥。在任何给定时刻，只有一个客户端可以持有锁。
无死锁：最终，即使锁定资源的客户端崩溃或被分区，也始终可以获取锁定。
容错性：容错，只要大多数Redis节点启动，客户端就能够获取和释放锁。
解铃还须系铃人。加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
```
#### 与SpringBoot 集成实现Redis 分布式锁Demo

**加锁代码**
```java
    /**
     * 获取锁.
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
```
获取分布式锁，就一个方法：jedis.set(String key, String value, String nxxx, String expx, int time)，这个set()方法一共有五个形参： 
```
第一个为key，我们使用key来当锁，因为key是唯一的。  
第二个为value，传的是requestId，这里会有疑惑，有key作为锁不就够了吗，为什么还要用到value？原因就是我们在上面讲到可靠性时，分布式锁要满足第四个条件解铃还须系铃人，通过给value赋值为requestId，我们就知道这把锁是哪个请求加的了，在解锁的时候就可以有依据。requestId可以使用UUID.randomUUID().toString()方法生成或者其他方式生成的唯一标识。  
第三个为nxxx，这个参数填的是NX，意思是SET IF NOT  EXIST，即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作；
第四个为expx，这个参数传的是PX，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。  
第五个为time，与第四个参数相呼应，代表key的过期时间。  
```
也就是，判断传入的Key是否存在，不存在则添加，并设置过期时间，如果存在则不做任何操作。  

我们发现，我们的加锁代码满足我们可靠性里描述的三个条件。首先，set()加入了NX参数，可以保证如果已有key存在，则函数不会调用成功，也就是只有一个客户端能持有锁，满足互斥性。其次，由于我们对锁设置了过期时间，即使锁的持有者后续发生崩溃而没有解锁，锁也会因为到了过期时间而自动解锁（即key被删除），不会发生死锁。最后，因为我们将value赋值为requestId，代表加锁的客户端请求标识，那么在客户端在解锁的时候就可以进行校验是否是同一个客户端。由于我们只考虑Redis单机部署的场景，所以容错性我们暂不考虑。

另外根据部分场景需要可设计阻塞式的锁，简单参考如下：  
**获取分布式锁(阻塞)**
```
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
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
            }
            long time = System.currentTimeMillis() - startTime;
            //获取锁超时，避免获取不到锁出现的问题
            if (time > maxLockTimeout) {
                recycleRedis(jedis);
                return false;
            }
        }
    }
```
**解锁代码**
``` //解锁脚本
    public final static String REDIS_UNLOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    /**
     * 解锁.key和value必须两者都匹配才能删除，目的是防止误解别人的锁
     *
     * @param key       the lock 键
     * @param requestId the 对应的唯一标识
     * @return the boolean
     */
    public boolean unlock(String key, String requestId) {
        Jedis jedis = getRedis();
        Object result = jedis.eval(REDIS_UNLOCK, Collections.singletonList(key),
                Collections.singletonList(requestId));
        recycleRedis(jedis);
        return Long.valueOf(1L).equals(result);
    }
```
上面这段脚本其实很简单，首先获取锁对应的value值，检查是否与传给ARGV[1]的requestId相等，如果相等则删除锁（解锁）。  
并且eval命令执行Lua代码的时候，Lua代码将被当成一个命令去执行，并且直到eval命令执行完成，Redis才会执行其他命令。  
那么为什么要使用Lua语言来实现呢？因为要确保上述操作是原子性的。关于非原子性会带来什么问题？  
常见的错误示例：  
这种解锁代码乍一看也是没问题，与正确姿势差不多，唯一区别的是分成两条命令去执行，代码如下：  
```
public static void wrongReleaseLock2(Jedis jedis, String lockKey, String requestId) {

    // 判断加锁与解锁是不是同一个客户端
    if (requestId.equals(jedis.get(lockKey))) {
        // 若在此时，这把锁突然不是这个客户端的，则会误解锁
        jedis.del(lockKey);
    }

}
```
如代码注释，问题在于如果调用jedis.del()方法的时候，这把锁已经不属于当前客户端的时候会解除他人加的锁。那么是否真的有这种场景？答案是肯定的，比如客户端A加锁，一段时间之后客户端A解锁，在执行jedis.del()之前，锁突然过期了，
此时客户端B尝试加锁成功，然后客户端A再执行del()方法，则将客户端B的锁给解除了。  
相比而言，lua脚本执行是连贯的，在eval命令未执行完成，Redis是不会执行其他命令，所以就能解决这个问题。  

源码以上传GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Redis-Distributed-Lock  

### 补充
三种方案的比较  
上面几种方式，哪种方式都无法做到完美。就像CAP一样，在复杂性、可靠性、性能等方面无法同时满足，所以，根据不同的应用场景选择最适合自己的才是王道。  

从理解的难易程度角度（从低到高）  
数据库 > 缓存 > Zookeeper  

从实现的复杂性角度（从低到高）  
Zookeeper >= 缓存 > 数据库  

从性能角度（从高到低）  
缓存 > Zookeeper >= 数据库  

从可靠性角度（从高到低）  
Zookeeper > 缓存 > 数据库  

**参考链接**
https://redis.io/topics/distlock  
http://www.importnew.com/27477.html  
[三种方案实现分布式锁](http://www.hollischuang.com/archives/1716)  
