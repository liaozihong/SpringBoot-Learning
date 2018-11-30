package com.dashuai.learning.elasticsearch.service;

import com.dashuai.learning.elasticsearch.model.User;

import java.util.List;

/**
 * The interface User search service.
 *
 * @author Liaozihong
 */
public interface UserSearchService {
    /**
     * Gets user all.
     *
     * @return the user all
     */
    List<User> getUserAll();

    /**
     * Gets user limit.
     *
     * @param pageNum the page num
     * @param size    the size
     * @return the user limit
     */
    List<User> getUserLimit(int pageNum, int size);

    /**
     * Gets user by name.
     *
     * @param name    the name
     * @param pageNum the page num
     * @param size    the size
     * @return the user by name
     */
    List<User> getUserByName(String name, int pageNum, int size);

    /**
     * Gets user by name.
     *
     * @param name the name
     * @return the user by name
     */
    List<User> getUserByName(String name);

    /**
     * Insert user boolean.
     *
     * @param user the user
     * @return the boolean
     */
    Boolean insertUser(User user);

    /**
     * Update user boolean.
     *
     * @param user the user
     * @return the boolean
     */
    Boolean updateUser(User user);

    /**
     * Drop user boolean.
     *
     * @param id the id
     * @return the boolean
     */
    Boolean dropUser(Integer id);
}
