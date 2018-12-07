package com.dashuai.learning.webfluxdata.dao;

import com.dashuai.learning.webfluxdata.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * The interface User repository.
 *
 * @author Liaozihong
 */
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    /**
     * Find by username mono.
     *
     * @param username the username
     * @return the mono
     */
    Mono<User> findByUsername(String username);

    /**
     * Delete by username mono.
     *
     * @param username the username
     * @return the mono
     */
    Mono<Long> deleteByUsername(String username);

}
