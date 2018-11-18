## 什么是Memcached?
Memcached 是一个高性能的分布式内存对象缓存系统，用于动态Web应用以减轻数据库负载。它通过在内存中缓存数据和对象来减少读取数据库的次数，从而提高动态、数据库驱动网站的速度,以减少必须读取外部数据源(如数据库或API)的次数。Memcached基于一个存储键/值对的hashmap。其守护进程（daemon）是用C写的，但是客户端可以用任何语言来编写，并通过memcached协议与守护进程通信。  
Memcached的api提供了一个分布在多台机器上的非常大的哈希表。当表满时，后续插入将导致以最近最少使用的顺序清除旧数据。使用Memcached的应用程序通常在退回到较慢的备份存储(如数据库)之前，会将请求和添加到RAM中。

因为 Spring Boot 没有针对Memcached提供对应的组建包，因此需要我们自己来集成。官方推出的 Java 客户端 Spymemcached 是一个比较好的选择之一。   
## 安装  
使用Docker安装memcached：  
```
docker run --name my-memcache -d memcached memcached -m 64
```
顺带安装memadmin管理工具，该工具有php编写，需要php环境，这里直接使用docker部署。一劳永逸。  
```
Dockerfile
FROM eboraas/apache-php
RUN apt-get update && apt-get -y install php5-memcache && apt-get clean && rm -rf /var/lib/apt/lists/*
ADD https://github.com/junstor/memadmin/archive/v1.0.12.tar.gz /var/www
RUN rm -fr /var/www/html && ln -s /var/www/memadmin-1.0.12 /var/www/html && cd /var/www/ && tar xvf v1.0.12.tar.gz

构建
docker build -t memadmin .  
运行测试
docker run -it --rm -p 11080:80 memadmin
http://ip:10080/，用户名密码默认admin，可以修改/var/www/html/config.php  
```
如果想方便的修改容器里面的文件，可以把文件copy出来，然后再copy进去  
或者先整个目录copy出来，然后重新启动一个容器通过-v映射进去  
像这样  
```
$ docker cp memadmin:/var/www/html/ /usr/local/memadmin
$ docker stop memadmin
$ docker run -d --name memadmin -v /usr/local/memadmin:/var/www/html/ -p 11080:80 memadmin
```
使memcache服务器使用64兆字节进行存储。
### Spymemcached 介绍  

Spymemcached 最早由 Dustin Sallings 开发，Dustin 后来和别人一起创办了 Couchbase (原NorthScale)，职位为首席架构师。2014 加入 Google。  

Spymemcached 是一个采用 Java 开发的异步、单线程的 Memcached 客户端， 使用 NIO 实现。Spymemcached 是 Memcached 的一个流行的 Java client 库，性能表现出色，广泛应用于 Java + Memcached 项目中。  

为了方便理解，我简单写了一个Springboot集成Memcached+spymemcached的例子。  

项目地址：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Memcacherd  