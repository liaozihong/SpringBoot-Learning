### 使用Mycat中间件实现分库分表  

#### What is Mycat?
* 一个彻底开源的，面向企业应用开发的大数据库集群
* 支持事务、ACID、可以替代MySQL的加强版数据库
* 一个可以视为MySQL集群的企业级数据库，用来替代昂贵的Oracle集群
* 一个融合内存缓存技术、NoSQL技术、HDFS大数据的新型SQL Server
* 结合传统数据库和新型分布式数据仓库的新一代企业级数据库产品
* 一个新颖的数据库中间件产品

#### 使用Mycat实现分库分表
搭建mycat这里就不在陈述，直接进入实现主题。   
配置schema.xml,添加一个user的表
```xml
<?xml version="1.0"?>
<!DOCTYPE mycat:schema SYSTEM "schema.dtd">
<mycat:schema xmlns:mycat="http://io.mycat/">

    <schema name="TESTDB" checkSQLschema="false" sqlMaxLimit="100">
              <table name="user" primaryKey="id" dataNode="dn1,dn2,dn3" rule="rule10" />  
    </schema>
    <dataNode name="dn1" dataHost="localhost1" database="db1" />
	<dataNode name="dn2" dataHost="localhost1" database="db2" />
	<dataNode name="dn3" dataHost="localhost1" database="db3" />
    <dataHost name="localhost1" maxCon="1000" minCon="10" balance="0"
                  writeType="0" dbType="mysql" dbDriver="native" switchType="1"  slaveThreshold="100">
            <heartbeat>select user()</heartbeat>
            <writeHost host="hostM1" url="127.0.0.1:3306" user="root"
                       password="root">
            </writeHost>
    </dataHost>
</mycat:schema>
```
接着配置rule.xml文件
在已有配置的基础上，添加
```xml
<tableRule name="rule10">
        <rule>
            <columns>id</columns>
            <algorithm>mod-long</algorithm>
        </rule>
 </tableRule>
<function name="mod-long" class="io.mycat.route.function.PartitionByMod">
    <!-- how many data nodes -->
    <property name="count">3</property>
</function>
```
默认function name为mod-long是已有的了，设置了3个分片，对应schema.xml中配置的三个数据源。  
接着进入mycat，创建user表,这里随便建了一个，忽略了主键等   
```sql
create table `user`(
    `id` bigint(20) NOT NULL,
    `age` int not null,
    `password` varchar(20) not null,
    `user_name` varchar(20) not null
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
可以通过explain看到，它分别向3个数据源都添加了表。  
![](http://ww1.sinaimg.cn/large/006mOQRagy1g2jjgjk6oaj30r50cc3z2.jpg)

接着用SpringBoot连接Mycat 查看添加效果。   
使用Mybatis进行操作，操作不变，只是mysql连接需要更新为mycat连接，
```properties
spring.datasource.open-plat-form-data.url=jdbc:mysql://127.0.0.1:8066/TESTDB?useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC
```
接着使用API添加三条数据
![](http://ww1.sinaimg.cn/large/006mOQRagy1g2jjhii1paj30mk0lh3za.jpg)  
添加成功后，三条数据分别存放在3个分片中，达到分库分表的效果。  
![](http://ww1.sinaimg.cn/large/006mOQRagy1g2jjigkur3j30j5043t8s.jpg)
![](http://ww1.sinaimg.cn/large/006mOQRagy1g2jjis77s5j30j803ht8r.jpg)
![](http://ww1.sinaimg.cn/large/006mOQRagy1g2jjj4k8lkj30ix043dfw.jpg)

同时，当你查询时，mycat会自动帮你去查询3个分片，并进行汇总返回，如下：  
![](http://ww1.sinaimg.cn/large/006mOQRagy1g2jjkx4983j30b304gq2v.jpg)  

至此，基于mycat的分库分表简单实践就结束啦，还是很简单的，但是要真正用好，还不是一件很容易的事，因为mycat也存在着很多局限，
具体可参考网上的一些对mycat进行详细分析的文章。   

上面的完整源码以上传至github:  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Mybatis-Mycat