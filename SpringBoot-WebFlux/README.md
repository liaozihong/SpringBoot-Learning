## SpringBoot-WebFlux

### Spring 5 WebFlux 简介
关于 Spring 5 的 WebFlux 响应式编程，下图是传统 Spring Web MVC 结构以及Spring 5 中新增加的基于 Reactive Streams 的 Spring WebFlux 框架。
可以使用 webFlux 模块来构建 异步的、非堵塞的、事件驱动 的服务，其在 伸缩性方面 表现非常好。

![](https://ws1.sinaimg.cn/large/006mOQRagy1fxx608yme9j30en07kmy1.jpg)  

1. Spring提供了完整的支持响应式的服务端技术栈。  
如上图所示，左侧为基于spring-webmvc的技术栈，右侧为基于spring-webflux的技术栈，  

- Spring WebFlux是基于响应式流的，因此可以用来建立异步的、非阻塞的、事件驱动的服务。它采用Reactor作为首选的响应式流的实现库，不过也提供了对RxJava的支持。
- 由于响应式编程的特性，Spring WebFlux和Reactor底层需要支持异步的运行环境，比如Netty和Undertow；也可以运行在支持异步I/O的Servlet 3.1的容器之上，比如Tomcat（8.0.23及以上）和Jetty（9.0.4及以上）。
- 从图的纵向上看，spring-webflux上层支持两种开发模式： 
- 类似于Spring WebMVC的基于注解（@Controller、@RequestMapping）的开发模式；
- Java 8 lambda 风格的函数式开发模式。
- Spring WebFlux也支持响应式的Websocket服务端开发。

2. 响应式Http客户端
此外，Spring WebFlux也提供了一个响应式的Http客户端API WebClient。它可以用函数式的方式异步非阻塞地发起Http请求并处理响应。其底层也是由Netty提供的异步支持。   
我们可以把WebClient看做是响应式的RestTemplate，与后者相比，前者：  

- 是非阻塞的，可以基于少量的线程处理更高的并发；
- 可以使用Java 8 lambda表达式；
- 支持异步的同时也可以支持同步的使用方式；
- 可以通过数据流的方式与服务端进行双向通信。

另外WebFlux即兼容SpringMvc注解的风格还支持全局功能路由配置。后面用例子深入。  
### 小试牛刀
下面正式开干。
1). 创建一个SpringBoot项目   
2). 导入jar包，注意导入webflux模块的jar包，去掉web的jar包  
```pom
compile group: 'org.springframework.boot', name: 'spring-boot-starter-webflux', version: '2.0.4.RELEASE'
```
3). 创建一个基于SpringMvc的风格的路由  
```java
@RestController
@RequestMapping
public class MessageApi {

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello,World!");
    }
}
```
4). 启动应用   
```text
2018-12-06 18:05:34.266  INFO 19620 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8080
```
我们发现WebFlux默认是使用Netty做Web服务器的。
访问http://localhost:8080/hello,结果和SpringMvc实现的是一样。

5). 使用全局路由的方式配置  
注释调API，改用WebFlux的全局路由配置  
```java
@Configuration
public class RouterConfiguration{
    @Bean
    public RouterFunction<ServerResponse> routersHello() {
        return route(GET("/hello"),(ServerRequest req) ->ok().body(Mono.just("Hello World"),String.class));
    }
}
```

6). 重新启动应用  
7). 访问，发现跟SpringMvc风格的返回一毛一样。  
8). 总结：
从上边这个非常非常简单的例子中可以看出，Spring真是用心良苦，WebFlux提供了与之前WebMVC相同的一套注解来定义请求的处理，使得Spring使用者迁移到响应式开发方式的过程变得异常轻松。  

虽然我们只修改了少量的代码，但是其实这个简单的项目已经脱胎换骨了。整个技术栈从命令式的、同步阻塞的【spring-webmvc + servlet + Tomcat】变成了响应式的、异步非阻塞的【spring-webflux + Reactor + Netty】。  

Netty是一套异步的、事件驱动的网络应用程序框架和工具，能够开发高性能、高可靠性的网络服务器和客户端程序，因此与同样是异步的、事件驱动的响应式编程范式一拍即合。  


### 使用WebClient 开发响应式Http客户端
```java
    @Test
    public void webClientTest1() throws InterruptedException {
        WebClient webClient = WebClient.create("http://localhost:8080");   // 1
        Mono<String> resp = webClient
                .get().uri("/hello") // 2
                .retrieve() // 3
                .bodyToMono(String.class);  // 4
        resp.subscribe(System.out::println);    // 5
        TimeUnit.SECONDS.sleep(1);  // 6
    }
```
1. 创建WebClient对象并指定baseUrl；
1. HTTP GET；
1. 异步地获取response信息；
1. 将response body解析为字符串；
1. 打印出来；
1. 由于是异步的，我们将测试线程sleep 1秒确保拿到response，也可以像前边的例子一样用CountDownLatch。

### 初窥门径
我们可能会遇到一些需要网页与服务器端保持连接（起码看上去是保持连接）的需求，比如类似微信网页版的聊天类应用，比如需要频繁更新页面数据的监控系统页面或股票看盘页面。我们通常采用如下几种技术：  

- 短轮询：利用ajax定期向服务器请求，无论数据是否更新立马返回数据，高并发情况下可能会对服务器和带宽造成压力；
- 长轮询：利用comet不断向服务器发起请求，服务器将请求暂时挂起，直到有新的数据的时候才返回，相对短轮询减少了请求次数；
- SSE：服务端推送（Server Send Event），在客户端发起一次请求后会保持该连接，服务器端基于该连接持续向客户端发送数据，从HTML5开始加入。
- Websocket：这是也是一种保持连接的技术，并且是双向的，从HTML5开始加入，并非完全基于HTTP，适合于频繁和较大流量的双向通讯场景。

既然响应式编程是一种基于数据流的编程范式，自然在服务器推送方面得心应手，我们基于函数式方式再增加一个Endpoint /times，可以每秒推送一次时间。
```java
@Component
public class TimeHandler {
    /**
    * 在WebFlux中，请求和响应不再是WebMVC中的HttpServletRequest和HttpServletResponse，
    * 而是ServerRequest和ServerResponse。后者是在响应式编程中使用的接口，
    * 它们提供了对非阻塞和回压特性的支持，以及Http消息体与响应式类型Mono和Flux的转换方法。
    * @param serverRequest
    * @return 
    */
    public Mono<ServerResponse> sendTimePerSec(ServerRequest serverRequest) {
            return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(Flux.interval(Duration.ofSeconds(1)).
                    map(l -> new SimpleDateFormat("HH:mm:ss").format(new Date())), String.class);
    }
}
```
MediaType.TEXT_EVENT_STREAM表示Content-Type为text/event-stream，即SSE；  
利用interval生成每秒一个数据的流。  
配置全局路由   
```java
@Configuration
public class DemoRouterConfig {
    @Autowired
    private TimeHandler timeHandler;
    /**
    * RouterFunction，顾名思义，路由，相当于@RequestMapping，用来判断什么样的url映射到那个
    * 具体的HandlerFunction，输入为请求，输出为装在Mono里边的Handlerfunction：
    * @return 
    */
    @Bean
    public RouterFunction<ServerResponse> timeRouter(){
        return route(GET("/times"),timeHandler::sendTimePerSec);
    }
}
```
重启应用，访问 http://localhost:8080/times ,它将会无限制的接收后端推送的报时。  
```text
2018-12-06 18:22:45.032  INFO 43724 --- [           main] reactor.Flux.MonoFlatMapMany.1           : onSubscribe(MonoFlatMapMany.FlatMapManyMain)
2018-12-06 18:22:45.034  INFO 43724 --- [           main] reactor.Flux.MonoFlatMapMany.1           : request(unbounded)
2018-12-06 18:22:47.386  INFO 43724 --- [ctor-http-nio-4] reactor.Flux.MonoFlatMapMany.1           : onNext(18:22:47)
2018-12-06 18:22:48.302  INFO 43724 --- [ctor-http-nio-4] reactor.Flux.MonoFlatMapMany.1           : onNext(18:22:48)
2018-12-06 18:22:50.306  INFO 43724 --- [ctor-http-nio-4] reactor.Flux.MonoFlatMapMany.1           : onNext(18:22:50)
2018-12-06 18:22:56.301  INFO 43724 --- [ctor-http-nio-4] reactor.Flux.MonoFlatMapMany.1           : cancel()
```

Demo源码GitHub链接：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-WebFlux  

参考链接：  
https://blog.csdn.net/get_set/article/details/79480233
https://juejin.im/post/5b3a24386fb9a024ed75ab36#heading-10  
https://www.bysocket.com/?p=2030
