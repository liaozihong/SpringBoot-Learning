### 学习SpringBoot所记

### 项目构建
```
git clone https://github.com/liaozihong/SpringBoot-Learning.git 
cd SpringBoot-Learning
# 进入setting.gradle , 找到自己想要运行的模块，取消注释，构建即可
gradle clean build -x test
```
### 项目介绍

#### SpringBoot-ActiveMq(ActiveMq 消息队列)
集成ActiveMq，实现点到点发送、广播订阅功能，简单的小实例，未用到Springboot封装的ActiveTemplate，后续打算在写一个利用SpringBoot的ActiveTemplate在写一个demo。  

#### SpringBoot-Dubbo-Provider (基于Dubbo的服务提供者)
集成Dubbo的服务提供者，依赖zookeeper。  

#### SpringBoot-Dubbo-Consumer(基于Dubbo的服务实现者)
集成Dubbo的服务消费者。

#### SpringBoot-Elasticsearch(Java 操作Es进行CURD)
集成Elasticsearch实现增删改查、分页例子。  
[SpringBoot集成Elasticsearch实现增删改查](https://blog.csdn.net/qq_39211866/article/details/84980416)

#### SpringBoot-Elasticsearch-Query(Es的高级搜索示例)
集成Elasticsearch实现中文、拼音分词，以及繁简体过滤，实现可以使用中文、拼音、繁体搜索，并且提供众多搜索类型，如：高亮、聚合。并且提供查看分词结果接口。  
[SpringBoot集成Elasticsearch 进阶，实现 中文、拼音、繁简体转换 搜索](https://blog.csdn.net/qq_39211866/article/details/85178707)

#### SpringBoot-Email(使用JAVA进行发邮件)
集成Email发送各种类型的邮件。  
[SpringBoot 集成 E-mail发送各种类型的邮件
](https://blog.csdn.net/qq_39211866/article/details/84843977)

#### SpringBoot-FastDFS(集成使用FastDFS分布式文件系统)
连接FastDFS分布式文件系统，上传文件远程服务器。(包含FastDFS文件系统的搭建)  

#### SpringBoot-Jpa-Thymeleaf(使用Thymeleaf模板，进行前端分离)
集成Jpa 做Crud，并使用Thymeleaf模板显示数据，提供了前后端分离的思路。  
[SpringBoot 集成 Thymeleaf 实现增删改查，实现前后端分离做法](https://blog.csdn.net/qq_39211866/article/details/84452874)

#### SpringBoot-Memcached(使用Memcached缓存中间件)
集成memcach`ed，内置了一个通用的操作管理类，可快速操作缓存。  
[https://blog.csdn.net/qq_39211866/article/details/84312170](https://blog.csdn.net/qq_39211866/article/details/84312170)   

#### SpringBoot-Mongodb (实现基于Mongodb 的CURD)
集成较火的nosql:Mongodb,使用泛型设计了一个通用的crud管理了。写起crud来方便快捷。  

#### SpringBoot-Mybatis(SpringBoot 使用 Druid 连接池示例)
集成Mybatis，并使用druid做连接池，配置了druid的监控功能。  

#### Spring-Mybatis-Multisource (SpringBoot 配置Mybatis多数据源示例)
集成Mybatis，并配置了双数据源，也可直接扩展多数据源。  
[SpringBoot集成Mybatis实现多数据源
](https://blog.csdn.net/qq_39211866/article/details/84843885)

#### SpringBoot-RabbitMq(集成RabbitMq消息中间件，实现延时队列)
集成RabbitMq，配置了延时队列，死信队列，并且提供了重试机制的设计思想，可做参考。  

#### SpringBoot-RateLimiter(基于Guava实现api限流功能)
集成Guava，利用RateLimiter API，实现限流功能。并且设计了有一个限流注解，能快速对服务进行限流配置。  
[基于Guava的RateLimiter设计常用接口限流功能
](https://blog.csdn.net/qq_39211866/article/details/84312116)

#### SpringBoot-Redis-Distributed-Lock(使用Redis+Lua实现分布式锁示例)
使用Redis+Lua实现分布式锁，附带测试实例。  
[SpringBoot 集成 Redis 实现分布式锁
](https://blog.csdn.net/qq_39211866/article/details/84843861)

#### SpringBoot-Redis-Distributed-RedLock(使用Redis官方推荐的Redisson演示分布式锁使用示例 )
使用Redis官方推荐的Redisson实现分布式锁。   
[SpringBoot 集成Redisson实现分布式锁
](https://blog.csdn.net/qq_39211866/article/details/84843849)

#### SpringBoot-WebSocket(SpringBoot + WebSocket实现双向通讯)
集成WebSocket打造一个聊天室。  
[SpringBoot集成WebSocket 打造聊天室
](https://blog.csdn.net/qq_39211866/article/details/84843923)  

#### SpringBoot-Zookeeper-Distributed-Lock(基于Zookeeper的Watch机制实现分布式锁)
使用Zookeeper实现分布式锁。  
[Zookeeper分布式锁](https://blog.csdn.net/qq_39211866/article/details/84844005)

#### SpringBoot-Shiro(集成Shiro安全框架)
使用Shiro实现  
```
添加用户注册，用户注销，当前用户显示;  
添加动态验证码验证，记住密码功能;
添加用户权限获取添加Redis缓存管理器，避免每次验证都查询数据库;
添加用户登录错误次数记录，到达特定次数将暂时停用改账号，避免暴力破解，基于redis;
添加登录限制，限定同一时间只能在一处登录，并踢出前一个登录;
添加异步单点登录，同上一条相似;
添加当前在线人数显示，可自己造轮子，做在线人数踢出功能，思路已给出。
```

#### SpringBoot-JWT(使用Shiro + JWT 对接口调用者进行认证和鉴权)
使用Jwt标准+Shiro安全框架，对请求进行认证和鉴权。  
[SpringBoot集成Shiro、JWT 进行请求认证和鉴权](https://blog.csdn.net/qq_39211866/article/details/85222852)

#### SpringBoot-Kafka  (基于kafka进行消息传递与消费)
使用SpringBoot集成kafka实现消息生产与消费，日常使用可使项目部分业务逻辑解耦，使项目具有更高的可扩展性，可恢复性，提高性能。   
[SpringBoot 搭建并使用Kafka消息中间件](https://blog.csdn.net/qq_39211866/article/details/85332030)

#### SpringBoot-Validator(基于Hibernate 的Validator进行参数校验)
基于validator对参数和实体进行校验，不符合约束条件将会抛出错误，在全局异常处处理。  

#### SpringBoot-Redis-Sentinel(Redis哨兵搭建及SpringBoot连接使用Redis集群)
使用SpringBoot连接Redis哨兵集群，配置连接哨兵，由哨兵给出可连接的redis ip，实现高可用。  
[SpringBoot 2.0 连接 Redis 主从、哨兵集群](https://blog.csdn.net/qq_39211866/article/details/88067664)

#### SpringBoot-JTA-Atomikos(多数据源下基于Atomikos实现分布式事务)
通常在项目中，我用会用到不止一种数据源，也就是多数据源，这时内置的Spring事务管理器并不能对多数据源进行事务处理，所以我们就要使用Atomikos事务管理器来跨多个XA资源的实现分布式JTA事务。   
[SpringBoot 2.0 集成Atomikos、Durid 实现多数据源、分布式事务](https://blog.csdn.net/qq_39211866/article/details/86547674)

#### SpringBoot-Nsq-Producer(Nsq消息生产者)
SpringBoot 使用Nsq消息中间件，此项目为消息生产者例子。  
[Springboot 2 集成 Nsq 消息中间件实现生产消费应用](https://blog.csdn.net/qq_39211866/article/details/88360559)  

#### SpringBoot-Nsq-Consumer(Nsq消息消费者)
SpringBoot 使用Nsq消息中间件，此项目为消息消费者例子。  
[Springboot 2 集成 Nsq 消息中间件实现生产消费应用](https://blog.csdn.net/qq_39211866/article/details/88360559)  

#### SpringBoot-Mybatis-Mycat(使用中间件Mycat进行分库分表，并使用应用连接测试)
使用Mycat数据库中间件，对Mysql进行数据分片存放，主从复制(待实现)

#### SpringBoot-Nacos-Config(基于阿里巴巴的Nacos实现公共配置中心)
使用Nacos注册中心实现动态配置

#### SpringBoot-SpringSecurity(使用Spring Security 进行资源访问权限控制)
SpringBoot 基于Spring-Security实现资源的权限控制  
[SpringBoot2 集成SpringSecurity对资源访问认证](https://blog.csdn.net/qq_39211866/article/details/90273681)

#### SpringBoot-Oauth2(演示Oauth2 授权协议的简化、密码、客户端模式)
SpringBoot 基于Oauth2授权协议实现简化、密码、客户端模式.

#### SpringBoot-Oauth2-Code(演示Oauth2 授权协议的授权码登录流程)
SpringBoot 基于Oauth2授权协议实现完整的授权码登录流程.

#### SpringBoot-Test-Starter(自定义测试模块，揭秘Springboot自动配置的神秘面纱)
SpringBoot 自动配置例子，了解SpringBoot的Starter原理。

#### SpringBoot-Builder-Docker(SpringBoot 配置Gradle构建Docker镜像例子)
SpringBoot 使用Gradle做依赖管理时，配置打包docker镜像的DEMO   

#### SpringBoot-Thrift-Server(Thrift  实现远程服务调用 服务端)
[SpringBoot 结合 Thrift 构建远程服务调用](https://blog.csdn.net/qq_39211866/article/details/95205938)

#### SpringBoot-Thrift-Client(Thrift  实现远程服务调用 客戶端)
SpringBoot基于Thrift实现RPC客户端

#### SpringBoot-Mybatis-Plus
SpringBoot 集成Mybatis Plus，内置乐观锁，crud，自动填充示例

#### SpringBoot-Alibaba-Sentinel
SpringBoot 使用 Alibaba开源监控平台 sentinel 示例

#### SpringBoot-Utils 
各项目的依赖模块，存放一些工具类
