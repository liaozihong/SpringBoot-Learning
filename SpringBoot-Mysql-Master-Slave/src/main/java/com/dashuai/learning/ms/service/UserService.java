package com.dashuai.learning.ms.service;

import com.dashuai.learning.ms.model.User;

import java.util.List;

/**
 * Created in 2020.04.22
 *
 * @author ZH ·L
 */
public interface UserService {
    /**
     * Gets user all.
     *
     * @return the user all
     */
    List<User> getUserAll();

    /**
     * Gets user info.
     *
     * @param userId the user id
     * @return the user info
     */
    User getUserInfo(Integer userId);

    /**
     * Create user boolean.
     *
     * @param user the user
     * @return the boolean
     */
    Boolean createOrUpdateUser(User user);

}
