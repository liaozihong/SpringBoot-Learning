###  Java 集成 Nsq  
集成Java可基于JavaNSQClient实现，该jar包帮助我们封装了操作nsq的一些api。  
下面我们构建两个SpringBoot测试项目，分别为SpringBoot-Nsq-Consumer，SpringBoot-Nsq-Poductor，
实现基于Nsq消息中间件的简单生产消费应用。  
配置product 的 nsq信息，如果是集群版的，可请求http://ip:4161/nodes,获取可用节点，在添加进去。
```properties
nsq.topic=nimeio
nsq.address=120.79.12.138
nsq.port=4150
```
初始化jar包提供的NSQProduct，
```java
@Configuration
public class NsqProductConfiguration {
    @Value("${nsq.topic}")
    String topic;
    @Value("${nsq.address}")
    String nsqAddress;
    @Value("${nsq.port}")
    Integer nsqPort;

    @Bean
    public NSQProducer nsqProducer() {
        NSQProducer nsqProducer = new NSQProducer();
        nsqProducer.addAddress(nsqAddress, nsqPort);
        nsqProducer.start();
        return nsqProducer;
    }
}
```
由于Java Nsq Client 生产者不能选择基于特定的Channel接收消费，日常我在项目中都是自己为Topic制定特定传输格式，
在通过逻辑判断指定特定的Consumer Channel消费处理。  
如下图：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1g0susmd5rzg30bo07tmxx.gif)  

设计的消息体如下,有一个特定id，action(相当于Channel)以及消息内容body,此处只是简单示例，有特殊要求可自行改善：  
```java
public class NsqMessage implements Serializable {

    @SerializedName("id")
    @JsonProperty("id")
    private Long id;

    /**
     * 相当于nsq消费者的channel名称
     */
    @SerializedName("action")
    @JsonProperty("action")
    private String action;

    @SerializedName("body")
    @JsonProperty("body")
    private String body;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
```
接下来，测试发送消息：  
```java
    public String sendTestMessageByChannel(String body) {
        try {
            NsqMessage message = new NsqMessage();
            message.setId(UUID.randomUUID().getLeastSignificantBits());
            message.setAction("testChannel");
            message.setBody(body);
            String msg = JSONParseUtils.object2JsonString(message);
            nsqProducer.produce(topic, msg.getBytes());
            log.info("消息发送特定Channel成功!时间:" + new Date());
            return "Success";
        } catch (NSQException e) {
            log.error("nsq 连接异常!msg={}", e.getMessage());
            return "Error:" + e.getMessage();
        } catch (TimeoutException e) {
            log.error("nsq 发送消息超时!msg={}", e.getMessage());
            return "Error:" + e.getMessage();
        } catch (Exception e) {
            log.error("出现未知异常!", e);
            return "Error:" + e.getMessage();
        }
    }
```
消息生产者到这里就结束了，下面构建一个消息消费者：  
同样的，消费的配置如下,注意要与生产者的Topic相同，并且由于消费者需要通过nsqlookupd发现生产服务，
所以使用nsqlookupd的端口4161(默认的，有改的需注意更换)：  
```properties
server.port=8081
nsq.topic=nimeio
nsq.address=120.79.12.138
nsq.port=4161
```
消费消息逻辑如下，主要在于判断是否为特定Channel，如不是就确定即可。  
```java
    public void mqConsumer() {
        NSQLookup lookup = new DefaultNSQLookup();
        lookup.addLookupAddress(nsqAddress, nsqPort);
        NSQConsumer consumer = new NSQConsumer(lookup, topic, NsqChannelConst.DEFAULT_CHANNEL, (message) -> {
            if (message != null) {
                String msg = new String(message.getMessage());
                NsqMessage nsqMessage = null;
                try {
                    nsqMessage = JSONParseUtils.json2Object(msg, NsqMessage.class);
                } catch (Exception e) {
                    log.error("消息无法转换，存在问题");
                    message.finished();
                    return;
                }
                if (nsqMessage == null) {
                    message.finished();
                    log.error("消息为空，瞎发的消息，确认即可");
                    message.finished();
                    return;
                }
                if (!NsqChannelConst.DEFAULT_CHANNEL.equals(nsqMessage.getAction())) {
                    // 如果nsq消息体中的action不等于当前的chanel名称,说明不是当前消费者需要处理的数据,确认消费即可
                    message.finished();
                    return;
                }
                try {
                    log.info("消费特定消息: " + nsqMessage.getBody());
                    //确认消息
                    message.finished();
                    return;
                } catch (Exception e) {
                    //异常,重试
                    message.requeue();
                }
                return;
            }
            message.finished();
            return;
        });
        consumer.start();
        log.info("nsq 消费者启动成功!");
    }
```
具体代码以上传至GitHub：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Nsq-Consumer    
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Nsq-Producer   

参考链接：  
[NSQ官方文档](https://nsq.io/overview/features_and_guarantees.html)    
[NSQ相关curl API](https://nsq.io/components/nsqd.html)  
[JAVANSQClient](https://github.com/brainlag/JavaNSQClient#toc0)  
[https://zhuanlan.zhihu.com/p/37081073](https://zhuanlan.zhihu.com/p/37081073)   