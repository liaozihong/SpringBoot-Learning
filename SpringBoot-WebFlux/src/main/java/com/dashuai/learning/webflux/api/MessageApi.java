package com.dashuai.learning.webflux.api;

import com.dashuai.learning.webflux.model.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Message api
 * <p/>
 * Created in 2018.12.03
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@RequestMapping
public class MessageApi {
    /**
     * All message flux.
     *
     * @return the flux
     */
    @GetMapping("/")
    public Flux<Message> allMessage() {
        return Flux.just(
                Message.builder().body("hello Spring 5").build(),
                Message.builder().body("hello Spring Boot 2").build()
        );
    }

//    @GetMapping("/hello")
//    public Mono<String> hello() {
//        return Mono.just("Hello,World!");
//    }
}