### SpringBoot2.0 集成Spring-Security实现OAuth2授权模式

使用OAuth2做授权保护应用，可以简易的分为3个步骤:  
* 配置资源服务器
* 配置认证服务器
* 配置spring security

前两点是oauth2的主体内容，spring security oauth2是建立在spring security基础之上的，所以有一些体系是公用的。  

OAuth2 授权使用根据场景不同，分成4种模式：

* 简化模式(implicit)
* 密码模式(resource owner password credentials)
* 客户端模式(client credentials)
* 授权码模式(authorization code)

本文主要实现 密码模式和客户端模式 进行用户资源的认证请求 

首先配置资源服务器和认证服务器

在根据需要配置用户名和密码  

具体描述暂时未总结，可先参照程序员DD写的文章进行理解。  

参考链接：https://github.com/lexburner/oauth2-demo