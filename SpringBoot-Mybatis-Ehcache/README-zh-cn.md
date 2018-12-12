## SpringBoot 使用简单的缓存 -- ehcache 
### 什么是ehCache
EhCache 是一个纯Java的进程内缓存框架，具有快速、精干等特点，是Hibernate中默认的CacheProvider。
一、具体描述    
Ehcache是一个用Java实现的使用简单，高速，实现线程安全的缓存管理类库，ehcache提供了用内存，磁盘文件存储，以及分布式存储方式等多种灵活的cache管理方案。同时ehcache作为开放源代码项目，采用限制比较宽松的Apache License V2.0作为授权方式，被广泛地用于Hibernate, Spring，Cocoon等其他开源系统。Ehcache 从 Hibernate 发展而来，逐渐涵盖了 Cahce 界的全部功能,是目前发展势头最好的一个项目。具有快速,简单,低消耗，依赖性小，扩展性强,支持对象或序列化缓存，支持缓存或元素的失效，提供 LRU、LFU 和 FIFO 缓存策略，支持内存缓存和磁盘缓存，分布式缓存机制等等特点。  

下图是 Ehcache 在应用程序中的位置：
![](https://ws1.sinaimg.cn/large/006mOQRagy1fy3yaiuxooj30lg08l0ut.jpg)

二、主要特性

快速、简单；  
多种缓存策略；  
缓存数据有两级：内存和磁盘，因此无需担心容量问题；  
缓存数据会在虚拟机重启的过程中写入磁盘； 
可以通过 RMI、可插入 API 等方式进行分布式缓存；  
具有缓存和缓存管理器的侦听接口；  
支持多缓存管理器实例，以及一个实例的多个缓存区域；  
提供 Hibernate 的缓存实现；  


配置ehcache.xml文件：  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">

    <!-- 磁盘缓存位置 -->
    <diskStore path="java.io.tmpdir"/>
    <!-- 默认缓存 -->
    <defaultCache
            maxElementsInMemory="10000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            maxElementsOnDisk="10000000"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU">
        <persistence strategy="localTempSwap"/>
    </defaultCache>

    <!-- 测试 -->
    <cache name="userCache"
           eternal="false"
           timeToIdleSeconds="120"
           timeToLiveSeconds="120"
           maxEntriesLocalHeap="10000"
           maxEntriesLocalDisk="10000000"
           diskExpiryThreadIntervalSeconds="120"
           overflowToDisk="false"
           memoryStoreEvictionPolicy="LRU">
    </cache>
</ehcache>
  <!--<diskStore>==========当内存缓存中对象数量超过maxElementsInMemory时,将缓存对象写到磁盘缓存中(需对象实现序列化接口)  -->
  <!--<diskStore path="">==用来配置磁盘缓存使用的物理路径,Ehcache磁盘缓存使用的文件后缀名是*.data和*.index  -->
  <!--name=================缓存名称,cache的唯一标识(ehcache会把这个cache放到HashMap里)  -->
  <!--maxElementsOnDisk====磁盘缓存中最多可以存放的元素数量,0表示无穷大  -->
  <!--maxElementsInMemory==内存缓存中最多可以存放的元素数量,若放入Cache中的元素超过这个数值,则有以下两种情况  -->
  <!--1)若overflowToDisk=true,则会将Cache中多出的元素放入磁盘文件中  -->
  <!--2)若overflowToDisk=false,则根据memoryStoreEvictionPolicy策略替换Cache中原有的元素  -->
  <!--eternal==============缓存中对象是否永久有效,即是否永驻内存,true时将忽略timeToIdleSeconds和timeToLiveSeconds  -->
  <!--timeToIdleSeconds====缓存数据在失效前的允许闲置时间(单位:秒),仅当eternal=false时使用,默认值是0表示可闲置时间无穷大,此为可选属性  -->
  <!--即访问这个cache中元素的最大间隔时间,若超过这个时间没有访问此Cache中的某个元素,那么此元素将被从Cache中清除  -->
  <!--timeToLiveSeconds====缓存数据在失效前的允许存活时间(单位:秒),仅当eternal=false时使用,默认值是0表示可存活时间无穷大  -->
  <!--即Cache中的某元素从创建到清楚的生存时间,也就是说从创建开始计时,当超过这个时间时,此元素将从Cache中清除  -->
  <!--overflowToDisk=======内存不足时,是否启用磁盘缓存(即内存中对象数量达到maxElementsInMemory时,Ehcache会将对象写到磁盘中)  -->
  <!--会根据标签中path值查找对应的属性值,写入磁盘的文件会放在path文件夹下,文件的名称是cache的名称,后缀名是data  -->
  <!--diskPersistent=======是否持久化磁盘缓存,当这个属性的值为true时,系统在初始化时会在磁盘中查找文件名为cache名称,后缀名为index的文件  -->
  <!--这个文件中存放了已经持久化在磁盘中的cache的index,找到后会把cache加载到内存  -->
  <!--要想把cache真正持久化到磁盘,写程序时注意执行net.sf.ehcache.Cache.put(Element element)后要调用flush()方法  -->
  <!--diskExpiryThreadIntervalSeconds==磁盘缓存的清理线程运行间隔,默认是120秒  -->
  <!--diskSpoolBufferSizeMB============设置DiskStore（磁盘缓存）的缓存区大小,默认是30MB  -->
  <!--memoryStoreEvictionPolicy========内存存储与释放策略,即达到maxElementsInMemory限制时,Ehcache会根据指定策略清理内存  -->
  <!--共有三种策略,分别为LRU(Least Recently Used 最近最少使用)、LFU(Less Frequently Used最不常用的)、FIFO(first in first out先进先出)  -->

```
在SpringBoot中指定配置：  
```properties
# 配置缓存,记住类型要选ehcache，否则ehcache配置文件中的配置将无效
spring.cache.type=ehcache
spring.cache.ehcache.config=classpath:ehcache.xml
```
接着便可以在服务类目使用缓存了。  
简单使用如下：  
```java
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * The User mapper.
     */
    @Autowired
    UserMapper userMapper;

    private final String cacheName = "userCache";

    private final String cacheKey = "'user'";

    //* @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
    //* @CacheEvict : 清除缓存。
    //* @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。

    @Override
    @Cacheable(value = cacheName, key = cacheKey)
    public List<User> selectAll() {
        log.info("未读取缓存");
        return userMapper.findAll();
    }

    @Override
    @CacheEvict(value = cacheName, key = cacheKey)
    public Boolean insertUser(User user) {
        return userMapper.insertSelective(user);
    }

    @Override
    @Cacheable(value = cacheName, key = "'user_'+ #id")
    public User selectById(long id) {
        log.info("未读取缓存");
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @CacheEvict(value = cacheName, key = cacheKey)
    public Boolean updateUser(User user) {
        return userMapper.updateByPrimaryKey(user);
    }

    @Override
    @CacheEvict(value = cacheName, key = cacheKey)
    public Boolean deleteUser(long id) {
        return userMapper.deleteByPrimaryKey(id);
    }
}
```


Demo源码已上传至GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Mybatis-Ehcache