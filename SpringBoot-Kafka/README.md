## SpringBoot 搭建并使用Kafka消息中间件

### 简介
Kafka 是一种高吞吐的分布式发布订阅消息系统，能够替代传统的消息队列用于解耦合数据处理，缓存未处理消息等，同时具有更高的吞吐率，支持分区、多副本、冗余，因此被广泛用于大规模消息数据处理应用。Kafka 支持Java 及多种其它语言客户端，可与Hadoop、Storm、Spark等其它大数据工具结合使用。

### 使用Docker安装kafka和可视化管理界面
这里我为了方便使用，将其整理成了一个docker-compose.yml，基于本地安装，如果需要如下的ip地址可自行更改。  
请先确保已有docker和docker-compose环境  
```yaml
version: '2'
services:
  zookeeper-kafka:
    image: zookeeper:3.5
    ports:
      - "2181:2181"
    networks:
      - front
  kafka:
    image: wurstmeister/kafka       ## 镜像
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1   ## 修改:宿主机IP
      KAFKA_ZOOKEEPER_CONNECT: zookeeper-kafka:2181       ## 卡夫卡运行是基于zookeeper的
    networks:
      - front
    depends_on:
      - zookeeper-kafka
  kafka-manager:  
    image: sheepkiller/kafka-manager                ## 镜像：开源的web管理kafka集群的界面
    environment:
        ZK_HOSTS: zookeeper-kafka:2181                   ## 修改:宿主机IP
    ports:  
      - "9000:9000"           ## 暴露端口
    networks:
      - front
    depends_on:
      - zookeeper-kafka
networks:
    front:
```
安装启动命令:  
```text
docker-compose up -d
```
访问http://localhost:9000,查看kafka的管理界面:  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fymlrokn5bj30rr0bomxv.jpg)  
添加zookeeper集群：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fymlsok6afj30ru0erjsf.jpg)  
没问题点击保存即可。  

> 注意，我是本地搭建的，使用的是docker的内部zookeeper虚拟域名:zookeeper-kafka:2181,而不是127.0.0.1:2181

### 使用SpringBoot集成Kafka，实现消息的生产和消费。

1. 导入Jar包，
```pom
    compile 'org.springframework.kafka:spring-kafka:2.1.5.RELEASE'
```

2. 配置Kafka
```properties
# 此处集群可配置多个
spring.kafka.bootstrap-servers=127.0.0.1:9092
# 生产者配置，指定生产者处理消息的序列化类
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
# 消费者配置，指定消费者处理消息的序列化类，指定组id
spring.kafka.consumer.group-id=myGroup
spring.kafka.consumer.enable-auto-commit=true
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

3. 配置生产者

Springboot以帮我们封装了对kafka的操作，我们只想使用KafkaTemplate就能简单轻松的操作kafka。  

```java
@Component
public class ProductMsg {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    private Gson gson = new Gson();
    /**
     * 发送向一个主题为test的队列发送消息 
     * @throws InterruptedException
     */
    public void sendMessage() throws InterruptedException {
        Message m = new Message();
        m.setId(System.currentTimeMillis());
        m.setMsg(UUID.randomUUID().toString());
        m.setSendTime(new Date());
        System.out.println("1生产了" + m.getMsg());
        Thread.sleep(1000);
        kafkaTemplate.send("test", gson.toJson(m));
    }
}
```
4. 配置消费者  
监听主题为test的队列，topics可配置对个主题
```java
@Component
public class ConsumerMsg {
    private Gson gson = new Gson();

    @KafkaListener(topics = {"test"})
    public void processMessage(String content) {
        Message m = gson.fromJson(content, Message.class);
        System.out.println("test1--消费消息:" + m.getMsg());
    }
}
```
测试消费：  
```java
@SpringBootApplication
public class KafkaApplication {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext app = SpringApplication.run(KafkaApplication.class, args);
        while (true) {
            ProductMsg sender = app.getBean(ProductMsg.class);
            sender.sendMessage();
            sender.sendMessage2();
            Thread.sleep(200);
        }
    }
}
```
测试结果：  
```txt
1生产了2648feb0-12d7-419d-a9e1-1b7773d02546
test1--消费消息:2648feb0-12d7-419d-a9e1-1b7773d02546
1生产了2463ea26-2d93-491f-b1f6-4c936700d9b7
test1--消费消息:2463ea26-2d93-491f-b1f6-4c936700d9b7
1生产了9e3fe5a5-58d7-4ca1-9574-431f67766ba9
test1--消费消息:9e3fe5a5-58d7-4ca1-9574-431f67766ba9
1生产了8951ffe8-587e-4956-85e5-7d1a6d453c1a
1生产了11f2a378-b2ff-4faf-87cd-3ab36f368757
test1--消费消息:8951ffe8-587e-4956-85e5-7d1a6d453c1a
test1--消费消息:11f2a378-b2ff-4faf-87cd-3ab36f368757
```

登录kafka可视化界面，可看到创建的主题情况：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fymllbvfi1j310f0gygmv.jpg)  

本例子简单的使用和搭建了kafka。如果文中有什么错误的，请联系我，谢谢。  

本例代码以上传GITHUB：   
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Kafka  