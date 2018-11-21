### 什么是FastDFS
百度百科：FastDFS是一个开源的轻量级分布式文件系统，它对文件进行管理，功能包括：文件存储、文件同步、文件访问（文件上传、文件下载）等，解决了大容量存储和负载均衡的问题。特别适合以文件为载体的在线服务，如相册网站、视频网站等等。
FastDFS为互联网量身定制，充分考虑了冗余备份、负载均衡、线性扩容等机制，并注重高可用、高性能等指标，使用FastDFS很容易搭建一套高性能的文件服务器集群提供文件上传、下载等服务。

FastDFS是由余庆所开发的开源、免费的分布式文件系统。  
### FastDFS结构组成  
FastDFS有三个角色：跟踪服务器(Tracker server)、存储服务器(Storage Server)和客户端(Client)。  
* Tracker Server: 跟踪服务器，主要做调度工作，起到均衡的作用；负责管理所有的 storage server
和 group，每个 storage 在启动后会连接 Tracker，告知自己所属 group 等信息，并保持周期性心跳。
* Storage Server：存储服务器，主要提供容量和备份服务；以 group 为单位，每个 group 内可以有多台 storage server，数据互为备份。
* Client:客户端，上传下载数据的服务器，也就是我们自己的项目所部署在的服务器。
 
FastDFS的架构图，说明了三次角色的职责及关系  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fxfqeyepfpj30rr0istho.jpg)   

上述架构优点：1.高可靠性：无单点故障 2.高吞吐性：只要 Group 足够多，数据流量将足够分散。

### 基于Docker搭建  
使用此博客教程搭建：https://blog.csdn.net/alinyua/article/details/82464496  
经测试可用。  

### 与SpringBoot集成  
参考纯洁的微笑写的Demo：http://www.ityouknow.com/springboot/2018/01/16/spring-boot-fastdfs.html  


参考链接：  
https://blog.csdn.net/alinyua/article/details/82464496  
https://www.jianshu.com/p/206b342cc689  
https://github.com/ityouknow/spring-boot-examples/blob/master/spring-boot-fastDFS/src/main/java/com/neo/controller/UploadController.java   
[使用Spring Boot集成FastDFS](http://www.ityouknow.com/springboot/2018/01/16/spring-boot-fastdfs.html)  
[fastdfs-client-java](https://github.com/happyfish100/fastdfs-client-java)