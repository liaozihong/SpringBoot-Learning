## 什么是Zookeeper？  
Zookeeper 是一个开放源码的分布式应用程序协调服务，它包含一个简单的原语集，分布式应用程序可以基于它实现同步服务、配置维护和命名服务等等。  
采用下图描述zookeeper协调服务。  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fwfww2q6mcj312y0b6n73.jpg)  
### Zookeeper特性  
ZooKeeper非常快速且非常简单。但是，由于其目标是构建更复杂的服务（如同步）的基础，因此它提供了一系列保证。这些是：  
* 顺序一致性 - 客户端的更新将按发送顺序应用。  
* 原子性 - 更新成功或失败。没有部分结果。  
* 单系统映像 - 无论服务器连接到哪个服务器，客户端都将看到相同的服务视图。  
* 可靠性 - 一旦应用了更新，它将从那时起持续到客户端覆盖更新。  
* 及时性 - 系统的客户视图保证在特定时间范围内是最新的。  
### 什么是分布式锁  
顾名思义，分布式锁肯定是用在分布式环境下。在分布式环境下，使用分布式锁的目的也是保证同一时刻只有一个线程来修改共享变量，修改共享缓存……。

前景：    
jdk提供的锁只能保证线程间的安全性，但分布式环境下，各节点之间的线程同步执行却得不到保障，分布式锁由此诞生。  

实现方式有以下几种：  
1. 基于数据库实现分布式锁； 
1. 基于缓存（Redis等）实现分布式锁； 
1. 基于Zookeeper实现分布式锁；


本示例利用zookeeper实现分布式锁。   
### 实现思路
zookeeper 可以根据有序节点+watch实现，实现思路，如：  
为每个线程生成一个有序的临时节点，为确保有序性，在排序一次全部节点，获取全部节点，每个线程判断自己是否最小，如果是的话，获得锁，执行操作，操作完删除自身节点。如果不是第一个的节点则监听它的前一个节点，当它的前一个节点被删除时，则它会获得锁，以此类推。 
### 与SpringBoot集成使用Demo
```java
    /**
     * 获取分布式锁
     *
     * @param path the path
     */
    public void acquireDistributedLock(String path) {
        String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        while (true) {
            try {
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(keyPath);
                log.info("success to acquire lock for path:{}", keyPath);
                break;
            } catch (Exception e) {
                //抢不到锁，进入此处!
                log.info("failed to acquire lock for path:{}", keyPath);
                log.info("while try again .......");
                try {
                    if (countDownLatch.getCount() <= 0) {
                        countDownLatch = new CountDownLatch(1);
                    }
                    //避免请求获取不到锁，重复的while，浪费CPU资源
                    countDownLatch.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
```
**解锁代码**
```java
/**
     * 释放分布式锁
     *
     * @param path the  节点路径
     * @return the boolean
     */
    public boolean releaseDistributedLock(String path) {
        try {
            String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
            if (curatorFramework.checkExists().forPath(keyPath) != null) {
                curatorFramework.delete().forPath(keyPath);
            }
        } catch (Exception e) {
            log.error("failed to release lock,{}", e);
            return false;
        }
        return true;
    }
```
**监听节点删除代码**
```java
 private void addWatcher(String path) {
        String keyPath;
        if (path.equals(ROOT_PATH_LOCK)) {
            keyPath = "/" + path;
        } else {
            keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        }
        try {
            final PathChildrenCache cache = new PathChildrenCache(curatorFramework, keyPath, false);
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener((client, event) -> {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    String oldPath = event.getData().getPath();
                    log.info("上一个节点 " + oldPath + " 已经被断开");
                    if (oldPath.contains(path)) {
                        //释放计数器，让当前的请求获取锁
                        countDownLatch.countDown();
                    }
                }
            });
        } catch (Exception e) {
            log.info("监听是否锁失败!{}", e);
        }
    }
```
源码已上传至GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Zookeeper-Distributed-Lock

### 使用Zookeeper实现分布式锁的优点    
有效的解决单点问题，不可重入问题，非阻塞问题以及锁无法释放的问题。实现起来较为简单。  

### 使用Zookeeper实现分布式锁的缺点  
性能上不如使用缓存实现分布式锁。因为需要频繁的创建删除节点。并且需要对ZK的原理有所了解。  
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

参考链接：  
[三种方案实现分布式锁](http://www.hollischuang.com/archives/1716)