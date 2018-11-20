Guava是一组核心库，包括新的集合类型（例如multimap和multiset），不可变集合，图形库，函数类型，内存缓存以及用于并发，I / O，散列，基元的API /实用程序，反射，字符串处理等等！  

本示例只使用了Guava工具包的RateLimiter类，进行API的限流。  
限流简介：  
限流中的“流”字该如何解读呢？要限制的指标到底是什么？不同的场景对“流”的定义也是不同的，可以是网络流量，带宽，每秒处理的事务数 (TPS)，每秒请求数 (hits per second)，并发请求数，甚至还可能是业务上的某个指标，比如用户在某段时间内允许的最多请求短信验证码次数。 

从保证系统稳定可用的角度考量，对于微服务系统来说，最好的一个限流指标是：并发请求数。通过限制并发处理的请求数目，可以限制任何时刻都不会有过多的请求在消耗资源，比如：我们通过配置 web 容器中 servlet worker 线程数目为 200，则任何时刻最多都只有 200 个请求在处理，超过的请求都会被阻塞排队。  

假如一个接口请求量因为某些原因突然涨到之前的10倍，没多久该接口几乎不可使用，并引发连锁反应导致整个系统崩溃。如何应对这种情况呢？生活给了我们答案：比如老式电闸都安装了保险丝，一旦有人使用超大功率的设备，保险丝就会烧断以保护各个电器不被强电流给烧坏。同理我们的接口也需要安装上“保险丝”，以防止非预期的请求对系统压力过大而引起的系统瘫痪，当流量过大时，可以采取拒绝或者引流等机制。  

常见的限流算法有：  
* 漏桶算法
* 令牌桶算法

1. 漏桶算法  
漏桶算法大概意思是请求进入到漏桶中，漏桶以一定的速率漏水。当请求过多时，水直接溢出。可以看出，漏桶算法可以强制限制数据的传输速度。
![](https://ws1.sinaimg.cn/large/006mOQRagy1fx9ybxzon5j30jh08bq3i.jpg)
2. 令牌桶算法
令牌桶算法的原理是系统以一定速率向桶中放入令牌，如果有请求时，请求会从桶中取出令牌，如果能取到令牌，则可以继续完成请求，否则等待或者拒绝服务。这种算法可以应对突发程序的请求，因此比漏桶算法好。  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fx9ychi9ugj30ez05mwew.jpg)

在Wikipedia上，令牌桶算法是这么描述的：  

* 每秒会有r个令牌放入桶中，或者说，每过1/r 秒桶中增加一个令牌
* 桶中最多存放b个令牌，如果桶满了，新放入的令牌会被丢弃
* 当一个n字节的数据包到达时，消耗n个令牌，然后发送该数据包
* 如果桶中可用令牌小于n，则该数据包将被缓存或丢弃

### RateLimiter
Guava中开源出来一个令牌桶算法的工具类RateLimiter，可以轻松实现限流的工作。RateLimiter对简单的令牌桶算法做了一些工程上的优化，具体的实现是SmoothBursty。需要注意的是，RateLimiter的另一个实现SmoothWarmingUp，就不是令牌桶了，而是漏桶算法。也许是出于简单起见，RateLimiter中的时间窗口能且仅能为1S，如果想搞其他时间单位的限流，只能另外造轮子。  

RateLimiter有一个有趣的特性是[前人挖坑后人跳]，也就是说RateLimiter允许某次请求拿走了超出剩余令牌数的令牌，但是下一次请求将为此付出代价，一直等到令牌亏空补上，并且桶中有足够本次请求使用的令牌为止。这里面就涉及到一个权衡，是让前一次请求干等到令牌够用才走掉呢，还是让它走掉后面的请求等一等呢？Guava的设计者选择的是后者，先把眼前的活干了，后面的事后面再说。  
并且构建了一个自定义注解，方便松耦合，灵活的对服务进行限流。  

```java
    /**
     * The entry point of application.
     * <p>
     * RateLimiter有一个有趣的特性是[前人挖坑后人跳]，也就是说RateLimiter允许某次请求拿走了超出剩余令牌数的令牌，
     * 但是下一次请求将为此付出代价，一直等到令牌亏空补上，并且桶中有足够本次请求使用的令牌为止。
     * 这里面就涉及到一个权衡，是让前一次请求干等到令牌够用才走掉呢，还是让它走掉后面的请求等一等呢？
     * Guava的设计者选择的是后者，先把眼前的活干了，后面的事后面再说。
     */
    @Test
    public void advanceConsumerTest() {
        //每秒产生2个令牌
        RateLimiter rateLimiter = RateLimiter.create(2);
        //获取令牌，返回获取令牌所需等待的时间，获取太多，导致后面得等亏损的令牌补上才能获取到。
        System.out.println(rateLimiter.acquire(10));
        System.out.println(rateLimiter.tryAcquire(2, 2, TimeUnit.SECONDS));
        System.out.println(rateLimiter.acquire(2));
        System.out.println(rateLimiter.acquire(1));
    }
```
结果如下,可以看到，RateLimiter每秒只能产生2个令牌，而第一获取10个的话，后面的就需要用5秒的时间补上空缺。
```java
0.0
false
4.994628
0.995124
```
下面通过一个例子，测试100个并发下，限流起到的效果
```java
    @Test
    public void rateLimitTest() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i <= 100; i++) {
            Business business = new Business(countDownLatch);
            business.start();
        }
        countDownLatch.countDown();
        //等待结果处理,有只设了10个令牌，所以，只有10个请求有效。
        TimeUnit.SECONDS.sleep(10);
        System.out.println("所有模拟请求结束  at " + new Date());
    }

    class Business extends Thread {
        CountDownLatch countDownLatch;

        public Business(CountDownLatch latch) {
            this.countDownLatch = latch;
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
                if (rateLimiterService.tryAcquire()) {
                    //模拟业务
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("成功处理业务" + new Date());
                } else {
                    System.out.println("系统繁忙！请稍后再试!");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
```
运行结果,只有10个请求获取到令牌，成功执行，其他的都直接返回
```java
.....
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
系统繁忙！请稍后再试!
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
成功处理业务Tue Nov 20 23:45:23 CST 2018
所有模拟请求结束  at Tue Nov 20 23:45:30 CST 2018
```
最后，为了方便日常使用，我还特定的设计了一个自定义注解，返回简单定义达到效果，正所谓偷懒使人进步。  
这里贴出基于注解的设计：  
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiterAnnotation {
    /**
     * 限流服务名
     *
     * @return
     */
    String name();

    /**
     * 每秒限流次数
     *
     * @return
     */
    double count();
}
```
切面实现类
```java
@Aspect
@Component
public class RateLimiterAnnotationAspect {

    private ConcurrentMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    /**
     * Before.
     *
     * @param point the point
     */
    @Before("@annotation(com.dashuai.learning.ratelimiter.annotation.RateLimiterAnnotation)")
    public void before(JoinPoint point) {
        RateLimiterAnnotation rateLimiterAnnotation = this.getAnnotation(point, RateLimiterAnnotation.class);
        double rateLimitCount = rateLimiterAnnotation.count();
        String rateLimitName = rateLimiterAnnotation.name();
        if (rateLimiterMap.get(rateLimitName) == null) {
            rateLimiterMap.put(rateLimitName, RateLimiter.create(rateLimitCount));
        }
        rateLimiterMap.get(rateLimitName).acquire();
    }

    private <T extends Annotation> T getAnnotation(JoinPoint pjp, Class<T> clazz) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(clazz);
    }
}
```
使用：  
```java
@Service
public class AopTestServiceImpl implements AopTestService {
    @Override
    @RateLimiterAnnotation(name = "v1", count = 5.0)
    public String testRateLimiter(Double count, String context) {
        System.out.println(count + "   " + context);
        return "测试";
    }

    @Override
    @RateLimiterAnnotation(name = "v2", count = 7.0)
    public String testRateLimiterv2(Double count, String context) {
        System.out.println("V2版本发出:" + count + "   " + context);
        return "测试第二个";
    }
}
```

设计思路较简单，通过一个map存储各个服务的限流数，在通过AOP切面前置判断，达到一个限流效果。  

本文源码以上传github：  
https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-RateLimiter  


另外，一开源限流框架值得研究：     
https://github.com/wangzheng0822/ratelimiter4j  
参考链接：  
https://wizardforcel.gitbooks.io/guava-tutorial/    