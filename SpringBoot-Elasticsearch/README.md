[TOC]
### Elasticsearch简介
Elasticsearch是一个基于Lucene的搜索服务器。
它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，可靠，快速，安装使用方便。     

ES安装x-pack后，默认的账户有三个，如下：

账户名 | 默认密码 |	权限
---|---|---
elastic	| changeme |	
kibana	| changeme	|
logstash_system	| changeme |

注意登录Kibana时，最好使用 Elastic 的账号。
您需要使用Elastic用户/密码登录才能创建新用户。Kibana用户仅用于连接和与ES通信。  

需要知道的：  
9300端口： ES节点之间通讯使用  
9200端口： ES节点 和 外部 通讯使用  

另外elasticsearch.yml的配置需要添加
```
network.host: 0.0.0.0
```
让其可以外网访问。   

另外，注意JAVA SpringBoot集成Elaskicsearch时，需要注意版本相对应，

spring data elasticsearch |	elasticsearch
---|---
3.1.x | 6.2.2
3.0.x | 5.5.0
2.1.x | 2.4.0
2.0.x | 2.2.0
1.3.x |	1.5.2

### 与SpringBoot集成  
添加JAR包依赖：  
```
compile group: 'org.springframework.boot', name: 'spring-boot-starter-data-elasticsearch', version: '2.0.6.RELEASE'
compile 'org.elasticsearch.client:x-pack-transport:5.5.0'
```
由于使用了X-PACK插件，需要做密码验证。  

所以需要引入x-pack-transport包。  

需要密码验证，所以yml配置不了，需要配置Bean注入，如下：  
```
 @Bean
    public TransportClient transportClient() throws UnknownHostException {
        TransportClient client = new PreBuiltXPackTransportClient(Settings.builder()
                .put("cluster.name", "docker-cluster")
                .put("xpack.security.user", "elastic:changeme")
                .build())
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }
```
使用上面的Bean注入，便可通过验证，连接到elasticsearch。  

部分注解介绍：  
#### @Document
@Document注解里面的几个属性，类似Mysql的:
```
index –> DB   
type –> Table   
Document –> row
```
加上@Id注解后，在Elasticsearch里对应的该列就是主键了，在查询时就可以直接用主键查询。其实和mysql非常类似，基本就是一个数据库。  
```
@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Document {
    String indexName();//索引库的名称，个人建议以项目的名称命名
    String type() default "";//类型，个人建议以实体的名称命名
    short shards() default 5;//默认分区数
    short replicas() default 1;//每个分区默认的备份数
    String refreshInterval() default "1s";//刷新间隔
    String indexStoreType() default "fs";//索引文件存储类型
}
```
#### @Field  
加上了@Document注解之后，默认情况下这个实体中所有的属性都会被建立索引、并且分词。 

通过@Field注解来进行详细的指定，如果没有特殊需求，那么只需要添加@Document即可。
```
@Field注解的定义如下：  

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Inherited
public @interface Field {

    FieldType type() default FieldType.Auto;#自动检测属性的类型
    FieldIndex index() default FieldIndex.analyzed;#默认情况下分词
    DateFormat format() default DateFormat.none;
    String pattern() default "";
    boolean store() default false;#默认情况下不存储原文
    String searchAnalyzer() default "";#指定字段搜索时使用的分词器
    String indexAnalyzer() default "";#指定字段建立索引时指定的分词器
    String[] ignoreFields() default {};#如果某个字段需要被忽略
    boolean includeInParent() default false;
}
```
集成Demo可查看源码，地址：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Elasticsearch  

参考链接：  
https://github.com/spring-projects/spring-data-elasticsearch   
https://segmentfault.com/a/1190000015568618#911