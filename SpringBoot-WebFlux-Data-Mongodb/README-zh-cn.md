## SpringBoot WebFlux Data Mongodb 实战
前面我已经简单了解和入门WebFlux了，不了解的可先查看下我前面总结的WebFlux文章。  
本篇主要内容是使用WebFlux连接Mongodb，实现crud并且体验Reactive 的响应式编程。  

下面使用集成例子探讨：  
1). 导入jar包
```text
compile group: 'org.springframework.boot', name: 'spring-boot-starter-webflux', version: '2.0.4.RELEASE'
compile group: 'org.springframework.boot', name: 'spring-boot-starter-data-mongodb-reactive', version: '2.0.4.RELEASE'
```
注意mongodb对于的jar包是reactive类型的了。  
2). 配置数据源，SpringBoot2.0配置mongodb变得十分简单，只需一句即可。  
```text
# 2.0以上mongodb配置，格式mongodb://username:password@hostname:port/dbname
spring.data.mongodb.uri=mongodb://127.0.0.1:27017/test
```
3). 配置实体类
MongoDB是文档型的NoSQL数据库，因此，我们使用@Document注解User类：
```java
    @Data
    @AllArgsConstructor
    @Document
    public class User {
        @Id
        private String id;      // 注解属性id为ID
        @Indexed(unique = true) // 注解属性username为索引，并且不能重复
        private String username;
        private String name;
        private String phone;
        private Date birthday;
    }
```
与非响应式Spring Data的CrudReposity对应的，响应式的Spring Data也提供了相应的Repository库：ReactiveCrudReposity，当然，我们也可以使用它的子接口ReactiveMongoRepository。  
我们增加UserRepository：  
```java
    public interface UserRepository extends ReactiveCrudRepository<User, String> {  // 1
        Mono<User> findByUsername(String username);     // 2
        Mono<Long> deleteByUsername(String username);
    }
```
同样的，ReactiveCrudRepository的泛型分别是User和ID的类型；  
ReactiveCrudRepository已经提供了基本的增删改查的方法，根据业务需要，我们增加四个方法（在此膜拜一下Spring团队的牛人们，使得我们仅需按照规则定义接口方法名即可完成DAO层逻辑的开发，牛~）  
由于基本没什么业务，直接简单调用Dao层代码即可。  
4). 实现CRUD操作  
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * 保存或更新。
     * 如果传入的user没有id属性，由于username是unique的，在重复的情况下有可能报错，
     * 这时找到以保存的user记录用传入的user更新它。
     *
     * @param user the user
     * @return the mono
     */
    public Mono<User> save(User user) {
        return userRepository.save(user)
                //onErrorResume进行错误处理；
                .onErrorResume(e ->
                        //找到username重复的记录；
                        userRepository.findByUsername(user.getUsername())
                                //由于函数式为User -> Publisher，所以用flatMap
                                .flatMap(originalUser -> {
                                    user.setId(originalUser.getId());
                                    //拿到ID从而进行更新而不是创建；
                                    return userRepository.save(user);
                                }));
    }

    /**
     * Delete by username mono.
     *
     * @param username the username
     * @return the mono
     */
    public Mono<Long> deleteByUsername(String username) {
        return userRepository.deleteByUsername(username);
    }

    /**
     * Find by username mono.
     *
     * @param username the username
     * @return the mono
     */
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Find all flux.
     *
     * @return the flux
     */
    public Flux<User> findAll() {
        return userRepository.findAll();
    }
}
```
5). Controller层，调用service方法
```java
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Save mono.
     *
     * @param user the user
     * @return the mono
     */
    @PostMapping("")
    public Mono<User> save(User user) {
        return this.userService.save(user);
    }

    /**
     * Delete by username mono.
     *
     * @param username the username
     * @return the mono
     */
    @DeleteMapping("/{username}")
    public Mono<Long> deleteByUsername(@PathVariable String username) {
        return this.userService.deleteByUsername(username);
    }

    /**
     * Find by username mono.
     *
     * @param username the username
     * @return the mono
     */
    @GetMapping("/{username}")
    public Mono<User> findByUsername(@PathVariable String username) {
        return this.userService.findByUsername(username);
    }

    /**
     * Find all flux.
     *
     * @return the flux
     */
    @GetMapping("")
    public Flux<User> findAll() {
        return this.userService.findAll().log();
    }

    /**
     * Find all delay flux.
     * 查询全部，每条记录延时2秒发送,如果不加produces=MediaType.APPLICATION_STREAM_JSON_VALUE
     * 它将会等所有记录的延时的时间过来在一同发送出去，就没有响应式的2秒发一条
     *
     * @return the flux
     */
    @GetMapping(value = "/delay", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> findAllDelay() {
        return this.userService.findAll().delayElements(Duration.ofSeconds(2));
    }

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello World!");
    }
}
```
接着使用Restlet Client或Postman 测试调用即可。  
另外WebFlux是异步非阻塞的，怎么查看效果呢！ 为此，上面专门设置了一个延时API/delay，同上一篇的/times类似，我们也加一个MediaType，
不过由于这里返回的是JSON，因此不能使用TEXT_EVENT_STREAM，而是使用APPLICATION_STREAM_JSON，即application/stream+json格式。  
请求此接口，你会发现数据是每隔2秒才会打印一条。  
6). 总结  
如果有Spring Data开发经验的话，切换到Spring Data Reactive的难度并不高。跟Spring WebFlux类似：
原来返回User的话，那现在就返回Mono<User>；原来返回List<User>的话，那现在就返回Flux<User>。  

对于稍微复杂的业务逻辑或一些必要的异常处理，比如上边的save方法，请一定采用响应式的编程方式来定义，
从而一切都是异步非阻塞的。如下图所示，从HttpServer（如Netty或Servlet3.1以上的Servlet容器）到
ServerAdapter（Spring WebFlux框架提供的针对不同server的适配器），到我们编写的Controller和DAO，
以及异步数据库驱动，构成了一个完整的异步非阻塞的管道，里边流动的就是响应式流。  


参考链接：  
https://blog.csdn.net/get_set/article/details/79480233