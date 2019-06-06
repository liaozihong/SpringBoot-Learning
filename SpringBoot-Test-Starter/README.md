SpringBoot 由众多Starter组成，随着版本的推移Starter家族成员也与日俱增，
在传统Maven项目中通常将一些层、组件拆分为模块来管理， 以便相互依赖复用，在Spring Boot项目中我们则可以创建自定义Spring Boot Starter来达成该目的。

可以认为starter是一种服务——使得使用某个功能的开发者不需要关注各种依赖库的处理，不需要具体的配置信息， 由Spring Boot自动通过classpath路径下的类发现需要的Bean，并织入相应的Bean。举个栗子，spring-boot-starter-jdbc这个starter的存在， 使得我们只需要在BookPubApplication下用@Autowired引入DataSource的bean就可以，Spring Boot会自动创建DataSource的实例。

本篇将通过一个简单的例子来演示如何编写自己的starter。

这里讲一下我们的Starter要实现的功能，很简单，提供一个Service，包含一个能够将字符串加上前后缀的方法String wrap(String word)。 而具体的前缀和后缀是通过读取SpringBoot配置文件application.yml而获取的。   

具体参照源码。  

gradle 将jar上传至本地仓库

1. apply plugin: 'maven' （引入maven插件）
1. group = 'com.user.server'（会按照group生成.m2的目录路径）
1. version = '1.0.0-RELEAS' （指定版本号）
1. def localMavenRepo（指定上传的路径）
1. uploadArchives（上传Task，Gradle会生成并上传pom.xml文件。）
1. plugin: 'maven-publish'（将源码一起打包，需要时添加）

```text
//执行打包Task
gradlew uploadArchives
```

参考链接：  
https://www.xncoding.com/2017/07/22/spring/sb-starter.html  
https://cloud.tencent.com/developer/article/1032622  