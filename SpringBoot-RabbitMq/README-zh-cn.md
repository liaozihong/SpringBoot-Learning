### 什么是RabbitMq?
RabbitMQ是流行的开源消息队列系统。RabbitMQ是AMQP（高级消息队列协议）的标准实现。支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP等，支持AJAX，持久化。用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。是使用Erlang编写的一个开源的消息队列，本身支持很多的协议：AMQP，XMPP, SMTP, STOMP，也正是如此，使的它变的非常重量级，更适合于企业级的开发。同时实现了一个Broker构架，这意味着消息在发送给客户端时先在中心队列排队。对路由(Routing)，负载均衡(Load balance)或者数据持久化都有很好的支持。其主要特点如下:  
* 可靠性
* 灵活的路由
* 扩展性
* 高可用性
* 多种协议
* 多语言客户端
* 管理界面
* 插件机制  

### 概念
RabbitMQ从整体上来看是一个典型的生产者消费者模型，主要负责接收、存储和转发消息。其整体模型架构如下图所示:  
![image](http://p1.pstatp.com/large/pgc-image/153794151125810e350b0bb)
### 安装
基于Docker安装  

    docker run -d --name myrabbitmq -p 5672:5672 -p 15672:15672 docker.io/rabbitmq:3-management

windows上简单安装，需要先安装erlang，在安装rabbitmq，默认监听的端口是5672，客户端管理工具访问端口是15672.  
具体安装步骤可参照下面的参考链接。   

### 延时队列
所谓延时消息就是指当消息被发送以后，并不想让消费者立即拿到消息，而是等待指定时间后，消费者才拿到这个消息进行消费。  
RabbitMQ队列本身是没有直接实现支持延迟队列的功能，但可以通过它的Time-To-Live Extensions 与 Dead Letter Exchange 的特性模拟出延迟队列的功能。
原理图如下：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fx0sqzx5zsj30m808qab6.jpg)  

使用SpringBoot集成RabbitMq的Demo来了解，具体参照源码：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-RabbitMq  


参考链接：  
[RabbitMQ延迟队列](https://segmentfault.com/a/1190000015369917#articleHeader0)  
[分布式系统消息中间件——RabbitMQ的使用基础篇](https://www.toutiao.com/a6605409550738653703/?tt_from=mobile_qq&utm_campaign=client_share&timestamp=1537965508&app=news_article&utm_source=mobile_qq&iid=44236874386&utm_medium=toutiao_android&group_id=6605409550738653703)
[RabbitMQ基本概念](https://www.jianshu.com/p/25816ae3d8db)      
[Rabbitmq在windows上的安装](http://www.cnblogs.com/ericli-ericli/p/5902270.html)  
