package com.dashuai.learning.webfluxdata.service;

import com.dashuai.learning.webfluxdata.dao.UserRepository;
import com.dashuai.learning.webfluxdata.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * User service
 * <p/>
 * Created in 2018.12.06
 * <p/>
 *
 * @author Liaozihong
 */
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
