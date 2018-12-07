package com.dashuai.learning.webfluxdata.api;

import com.dashuai.learning.webfluxdata.model.User;
import com.dashuai.learning.webfluxdata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * User controller
 * <p/>
 * Created in 2018.12.06
 * <p/>
 * webflux暂时不支持swagger
 *
 * @author Liaozihong
 */
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
