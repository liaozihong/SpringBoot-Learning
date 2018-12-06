package com.dashuai.learning.webflux.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Time handler
 * <p/>
 * Created in 2018.12.06
 * <p/>
 *
 * @author Liaozihong
 */
@Component
public class TimeHandler {

    public Mono<ServerResponse> hello(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Hello World"), String.class);
    }

    /**
     * 在WebFlux中，请求和响应不再是WebMVC中的HttpServletRequest和HttpServletResponse，
     * 而是ServerRequest和ServerResponse。后者是在响应式编程中使用的接口，
     * 它们提供了对非阻塞和回压特性的支持，以及Http消息体与响应式类型Mono和Flux的转换方法。
     *
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> getTime(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Now is " + new SimpleDateFormat("HH:mm:ss").format(new Date())), String.class);
    }

    /**
     * Gets date.
     *
     * @param serverRequest the server request
     * @return the date
     */
    public Mono<ServerResponse> getDate(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_PLAIN).body(Mono.just("Today is " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())), String.class);
    }

    /**
     * Send time per sec mono.
     * 服务器推送，每一秒发送一次时间
     * MediaType.TEXT_EVENT_STREAM表示Content-Type为text/event-stream，即SSE；
     * 利用interval生成每秒一个数据的流,无限流。
     *
     * @param serverRequest the server request
     * @return the mono
     */
    public Mono<ServerResponse> sendTimePerSec(ServerRequest serverRequest) {
        return ok().contentType(MediaType.TEXT_EVENT_STREAM).body(Flux.interval(Duration.ofSeconds(1)).
                map(l -> new SimpleDateFormat("HH:mm:ss").format(new Date())), String.class);
    }
}
