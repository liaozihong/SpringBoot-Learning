## 使用阿里巴巴开源项目 -- Sentinel 进行项目的监控、降级、流控  
### 手动接入 Sentinel 以及控制台
启动控制台：  
下载sentinel 的dashboard包，该项目为springboot项目，可编译后启动。  
https://github.com/alibaba/Sentinel/tree/master/sentinel-dashboard

导包，springboot 2.1.0以下用
```
   compile 'org.springframework.cloud:spring-cloud-starter-alibaba-sentinel:0.2.2.RELEASE'
```
配置如下：  
```properties
server.port=8720
spring.application.name=TestSentinel
spring.cloud.sentinel.eager=true
spring.cloud.sentinel.transport.port=8721
spring.cloud.sentinel.transport.dashboard=127.0.0.1:8719
spring.cloud.sentinel.transport.heartbeat-interval-ms=500
```
需要为应用提供两个可使用端口，server.port为应用端口，
spring.cloud.sentinel.transport.port 端口配置会在应用对应的机器上启动一个 Http Server，
该 Server 会与 Sentinel 控制台做交互。比如 Sentinel 控制台添加了1个限流规则，
会把规则数据 push 给这个 Http Server 接收，Http Server 再将规则注册到 Sentinel 中。

接着在需要监控的API上添加注解：@SentinelResource 后，启动项目，访问相应的api，即可在dashboard上看到记录，可根据自己应用情况进行
限流、降级等处理。     


参考链接：
https://github.com/alibaba/Sentinel