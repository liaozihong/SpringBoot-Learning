package com.dashuai.learning.thymeleaf.service;

import com.dashuai.learning.thymeleaf.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The interface User repository.
 *
 * @author <a href="https://github.com/liaozihong" style="background: #55a7e3;">Liaozihong</a>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find by id user.
     *
     * @param id the id
     * @return the user
     */
    User findById(long id);

}
