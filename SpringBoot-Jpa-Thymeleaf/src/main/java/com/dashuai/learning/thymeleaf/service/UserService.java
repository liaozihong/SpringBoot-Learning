package com.dashuai.learning.thymeleaf.service;


import com.dashuai.learning.thymeleaf.model.User;

import java.util.List;

/**
 * The interface User service.
 *
 * @author Liaozihong
 */
public interface UserService {
    /**
     * Gets user list.
     *
     * @return the user list
     */
    List<User> getUserList();

    /**
     * Find user by id user.
     *
     * @param id the id
     * @return the user
     */
    User findUserById(long id);

    /**
     * Save.
     *
     * @param user the user
     */
    void save(User user);

    /**
     * Edit.
     *
     * @param user the user
     */
    void edit(User user);

    /**
     * Delete.
     *
     * @param id the id
     */
    void delete(long id);
}
