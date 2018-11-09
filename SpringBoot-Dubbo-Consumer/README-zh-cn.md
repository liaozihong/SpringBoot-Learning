## Springboot 集成Dubbo  简单Demo
Dubbo架构  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fx1t7bxbevj30ci08cdg2.jpg)  
节点角色说明  

 节点 | 角色说明 
 ----- | ----- 
Provider|	暴露服务的服务提供方
Consumer|	调用远程服务的服务消费方
Registry|	服务注册与发现的注册中心
Monitor|	统计服务的调用次数和调用时间的监控中心
Container|	服务运行容器
调用关系说明  
1. 服务容器负责启动，加载，运行服务提供者。
2. 服务提供者在启动时，向注册中心注册自己提供的服务。
3. 服务消费者在启动时，向注册中心订阅自己所需的服务。
4. 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
5. 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
6. 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。
Dubbo 架构具有以下几个特点，分别是连通性、健壮性、伸缩性、以及向未来架构的升级性。  


值的注意的是，Dubbo要注册的Service类provider端和Consumer端需要在包名相同的位置，不然Dubbo会认为这是
不同服务的提供者和消费者，故而使他们之间无法产生通信，另外，序列化传递的实体类也需在同一包名下。否则
会产生CastClassException。
  
  
参考链接：  
[Dubbo官网](http://dubbo.apache.org/zh-cn/docs/user/preface/background.html)  
### Dubbo 的一些配置信息
```
# 扫描包路径
<br>spring.dubbo.scan=org.<br>spring.<br>springboot.dubbo

## Dubbo 应用配置
// 应用名称
<br>spring.dubbo.application.name=xxx

// 模块版本
<br>spring.dubbo.application.version=xxx

// 应用负责人
<br>spring.dubbo.application.owner=xxx

// 组织名(BU或部门)
<br>spring.dubbo.application.organization=xxx

// 分层
<br>spring.dubbo.application.architecture=xxx

// 环境，如：dev/test/run
<br>spring.dubbo.application.environment=xxx

// Java代码编译器
<br>spring.dubbo.application.compiler=xxx

// 日志输出方式
<br>spring.dubbo.application.logger=xxx

// 注册中心 0
<br>spring.dubbo.application.registries[0].address=zookeeper://127.0.0.1:2181=xxx
// 注册中心 1
<br>spring.dubbo.application.registries[1].address=zookeeper://127.0.0.1:2181=xxx

// 服务监控
<br>spring.dubbo.application.monitor.address=xxx

## Dubbo 注册中心配置类
// 注册中心地址
<br>spring.dubbo.application.registries.address=xxx

// 注册中心登录用户名
<br>spring.dubbo.application.registries.username=xxx

// 注册中心登录密码
<br>spring.dubbo.application.registries.password=xxx

// 注册中心缺省端口
<br>spring.dubbo.application.registries.port=xxx

// 注册中心协议
<br>spring.dubbo.application.registries.protocol=xxx

// 客户端实现
<br>spring.dubbo.application.registries.transporter=xxx

<br>spring.dubbo.application.registries.server=xxx

<br>spring.dubbo.application.registries.client=xxx

<br>spring.dubbo.application.registries.cluster=xxx

<br>spring.dubbo.application.registries.group=xxx

<br>spring.dubbo.application.registries.version=xxx

// 注册中心请求超时时间(毫秒)
<br>spring.dubbo.application.registries.timeout=xxx

// 注册中心会话超时时间(毫秒)
<br>spring.dubbo.application.registries.session=xxx

// 动态注册中心列表存储文件
<br>spring.dubbo.application.registries.file=xxx

// 停止时等候完成通知时间
<br>spring.dubbo.application.registries.wait=xxx

// 启动时检查注册中心是否存在
<br>spring.dubbo.application.registries.check=xxx

// 在该注册中心上注册是动态的还是静态的服务
<br>spring.dubbo.application.registries.dynamic=xxx

// 在该注册中心上服务是否暴露
<br>spring.dubbo.application.registries.register=xxx

// 在该注册中心上服务是否引用
<br>spring.dubbo.application.registries.subscribe=xxx


## Dubbo 服务协议配置


// 服务协议
<br>spring.dubbo.application.protocol.name=xxx

// 服务IP地址(多网卡时使用)
<br>spring.dubbo.application.protocol.host=xxx

// 服务端口
<br>spring.dubbo.application.protocol.port=xxx

// 上下文路径
<br>spring.dubbo.application.protocol.contextpath=xxx

// 线程池类型
<br>spring.dubbo.application.protocol.threadpool=xxx

// 线程池大小(固定大小)
<br>spring.dubbo.application.protocol.threads=xxx

// IO线程池大小(固定大小)
<br>spring.dubbo.application.protocol.iothreads=xxx

// 线程池队列大小
<br>spring.dubbo.application.protocol.queues=xxx

// 最大接收连接数
<br>spring.dubbo.application.protocol.accepts=xxx

// 协议编码
<br>spring.dubbo.application.protocol.codec=xxx

// 序列化方式
<br>spring.dubbo.application.protocol.serialization=xxx

// 字符集
<br>spring.dubbo.application.protocol.charset=xxx

// 最大请求数据长度
<br>spring.dubbo.application.protocol.payload=xxx

// 缓存区大小
<br>spring.dubbo.application.protocol.buffer=xxx

// 心跳间隔
<br>spring.dubbo.application.protocol.heartbeat=xxx

// 访问日志
<br>spring.dubbo.application.protocol.accesslog=xxx

// 网络传输方式
<br>spring.dubbo.application.protocol.transporter=xxx

// 信息交换方式
<br>spring.dubbo.application.protocol.exchanger=xxx

// 信息线程模型派发方式
<br>spring.dubbo.application.protocol.dispatcher=xxx

// 对称网络组网方式
<br>spring.dubbo.application.protocol.networker=xxx

// 服务器端实现
<br>spring.dubbo.application.protocol.server=xxx

// 客户端实现
<br>spring.dubbo.application.protocol.client=xxx

// 支持的telnet命令，多个命令用逗号分隔
<br>spring.dubbo.application.protocol.telnet=xxx

// 命令行提示符
<br>spring.dubbo.application.protocol.prompt=xxx

// status检查
<br>spring.dubbo.application.protocol.status=xxx

// 是否注册
<br>spring.dubbo.application.protocol.status=xxx
```