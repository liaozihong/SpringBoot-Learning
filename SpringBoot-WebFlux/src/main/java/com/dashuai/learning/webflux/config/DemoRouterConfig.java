package com.dashuai.learning.webflux.config;

import com.dashuai.learning.webflux.handler.TimeHandler;
import com.dashuai.learning.webflux.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


/**
 * Demo router config
 * <p/>
 * Created in 2018.12.03
 * <p/>
 * 配置全局路由
 * RouterFunctionMapping 将 RouterFunction 里的 全局路径 和 请求处理 进行了映射和绑定。
 *
 * @author Liaozihong
 */
@Configuration
public class DemoRouterConfig {
    @Autowired
    private TimeHandler timeHandler;

    @Bean
    public RouterFunction<ServerResponse> routersHello() {
        return route(GET("/hello"),(ServerRequest req) ->ok().body(Mono.just("Hello World"),String.class));
    }
    /**
     * Routes router function.
     * RouterFunction，顾名思义，路由，相当于@RequestMapping，用来判断什么样的url映射到那个
     * 具体的HandlerFunction，输入为请求，输出为装在Mono里边的Handlerfunction：
     * @return the router function
     */
    @Bean
    public RouterFunction<ServerResponse> routersTest() {
        return route(GET("/"), (ServerRequest req) -> ok()
                .body(
                        BodyInserters.fromObject(
                                Arrays.asList(
                                        Message.builder().body("hello Spring 5").build(),
                                        Message.builder().body("hello Spring Boot 2").build()
                                )
                        )
                )
        );
    }
    @Bean
    public RouterFunction<ServerResponse> timeRouter(){
        return route(GET("/time"), req -> timeHandler.getTime(req))
                .andRoute(GET("/date"), timeHandler::getDate)
                .andRoute(GET("/times"),timeHandler::sendTimePerSec);
    }
}