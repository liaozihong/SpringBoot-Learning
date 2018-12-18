## SpringBoot 2.0使用做Shiro安全认证
### 什么是Shiro？
shiro是apache的一个开源框架，是一个权限管理的框架，实现 用户认证、用户授权。  

spring中有spring security (原名Acegi)，是一个权限框架，它和spring依赖过于紧密，没有shiro使用简单。  
shiro不依赖于spring，shiro不仅可以实现 web应用的权限管理，还可以实现c/s系统，分布式系统权限管理，shiro属于轻量框架，越来越多企业项目开始使用shiro。   
Shiro架构图：  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fyazmow3hij30m80ezn8q.jpg)  
* subject：主体，可以是用户也可以是程序，主体要访问系统，系统需要对主体进行认证、授权。
* securityManager：安全管理器，主体进行认证和授权都 是通过securityManager进行。
* authenticator：认证器，主体进行认证最终通过authenticator进行的。
* authorizer：授权器，主体进行授权最终通过authorizer进行的。
* sessionManager：web应用中一般是用web容器对session进行管理，shiro也提供一套session管理的方式。
* SessionDao： 通过SessionDao管理session数据，针对个性化的session数据存储需要使用sessionDao。
* cache Manager：缓存管理器，主要对session和授权数据进行缓存，比如将授权数据通过cacheManager进行缓存管理，和ehcache整合对缓存数据进行管理。
* realm：域，领域，相当于数据源，通过realm存取认证、授权相关数据。
* cryptography：密码管理，提供了一套加密/解密的组件，方便开发。比如提供常用的散列、加/解密等功能。
比如md5散列算法。

### 为什么要使用Shiro？
我们在使用URL拦截的时候，要将所有的URL都配置起来，繁琐、不易维护。  
而我们的Shiro实现系统的权限管理，有效提高开发效率，从而降低开发成本。

使用纯洁的微笑的博文例子为脚手架进行扩展：  
http://www.ityouknow.com/springboot/2017/06/26/springboot-shiro.html  

更改项目环境：SpringBoot 2.0.4 ,shiro-spring 1.4.0  

主要扩展有：  
```text
添加用户注册，用户注销，当前用户显示;  
添加动态验证码验证，记住密码功能;
添加用户权限获取添加Redis缓存管理器，避免每次验证都查询数据库;
添加用户登录错误次数记录，到达特定次数将暂时停用改账号，避免暴力破解，基于redis;
添加单点登录，限定同一时间只能在一处登录，并踢出前一个登录;
添加异步单点登录，同上一条相似;
添加当前在线人数显示，可自己造轮子，做在线人数踢出功能，思路已给出。
```

### 实现用户授权登录

导入jar包，此处使用gradle例子：  
```text
 compile 'org.apache.shiro:shiro-spring:1.4.0'
 compile 'org.springframework.boot:spring-boot-starter-thymeleaf:2.0.4.RELEASE'
 compile 'org.springframework.boot:spring-boot-starter-data-jpa:2.0.4.RELEASE'
  //此jar包针对shiro，对thymeleaf做了部分标签，方便在开发中使用。
 compile group: 'com.github.theborakompanioni', name: 'thymeleaf-extras-shiro', version: '2.0.0'
 compile 'mysql:mysql-connector-java:6.0.6'
```


