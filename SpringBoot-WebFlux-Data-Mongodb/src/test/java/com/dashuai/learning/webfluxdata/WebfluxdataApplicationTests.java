package com.dashuai.learning.webfluxdata;

import com.dashuai.learning.webfluxdata.model.MyEvent;
import com.dashuai.learning.webfluxdata.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebfluxdataApplicationTests {
    /**
     * 1.创建WebClient对象并指定baseUrl；
     * 2.HTTP GET；
     * 3.异步地获取response信息；
     * 4.将response body解析为字符串；
     * 5.打印出来；
     * 6.由于是异步的，我们将测试线程sleep 1秒确保拿到response，也可以像前边的例子一样用CountDownLatch。
     *
     * @throws InterruptedException
     */
    @Test
    public void webClientTest1() throws InterruptedException {
        WebClient webClient = WebClient.create("http://localhost:8080");   // 1
        Mono<String> resp = webClient
                .get().uri("/user/hello") // 2
                .retrieve() // 3
                .bodyToMono(String.class);  // 4
        resp.subscribe(System.out::println);    // 5
        TimeUnit.SECONDS.sleep(1);  // 6
    }

    /**
     * 1.这次我们使用WebClientBuilder来构建WebClient对象；
     * 2.配置请求Header：Content-Type: application/stream+json；
     * 3.获取response信息，返回值为ClientResponse，retrive()可以看做是exchange()方法的“快捷版”；
     * 4.使用flatMap来将ClientResponse映射为Flux；
     * 5.只读地peek每个元素，然后打印出来，它并不是subscribe，所以不会触发流；
     * 6.上个例子中sleep的方式有点low，blockLast方法，顾名思义，在收到最后一个元素前会阻塞，响应式业务场景中慎用。
     */
    @Test
    public void webClientTest2() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build(); // 1
        webClient
                .get().uri("/user/delay")
                .accept(MediaType.APPLICATION_STREAM_JSON) // 2
                .exchange() // 3
                .flatMapMany(response -> response.bodyToFlux(User.class))   // 4
                .doOnNext(System.out::println)  // 5
                .blockLast();   // 6
    }

    /**
     * 1.声明速度为每秒一个MyEvent元素的数据流，不加take的话表示无限个元素的数据流；
     * 2.声明请求体的数据格式为application/stream+json；
     * 3.body方法设置请求体的数据。
     */
    @Test
    public void webClientTest4() {
        Flux<MyEvent> eventFlux = Flux.interval(Duration.ofSeconds(1))
                .map(l -> new MyEvent(System.currentTimeMillis(), "message-" + l + 10)).take(10); // 1
        WebClient webClient = WebClient.create("http://localhost:8080");
        webClient
                .post().uri("/events")
                .contentType(MediaType.APPLICATION_STREAM_JSON) // 2
                .body(eventFlux, MyEvent.class) // 3
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
