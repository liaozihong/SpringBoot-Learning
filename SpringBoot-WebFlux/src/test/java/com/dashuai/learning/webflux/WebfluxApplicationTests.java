package com.dashuai.learning.webflux;

import com.dashuai.learning.webflux.api.MessageApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = MessageApi.class)
public class WebfluxApplicationTests {
    @Autowired
    WebTestClient client;

    @Test
    public void contextLoads() {
        client.get().uri("/").exchange().expectStatus().isOk();
    }

    /**
     * 1.配置请求Header：Content-Type: text/event-stream，即SSE；
     * 2.这次用log()代替doOnNext(System.out::println)来查看每个元素；
     * 3.由于/times是一个无限流，这里取前10个，会导致流被取消；
     *
     * @throws InterruptedException
     */
    @Test
    public void webClientTest3() throws InterruptedException {
        WebClient webClient = WebClient.create("http://localhost:8080");
        webClient
                .get().uri("/times")
                .accept(MediaType.TEXT_EVENT_STREAM)    // 1
                .retrieve()
                .bodyToFlux(String.class)
                .log()  // 2
                .take(10)   // 3
                .blockLast();
    }

}
