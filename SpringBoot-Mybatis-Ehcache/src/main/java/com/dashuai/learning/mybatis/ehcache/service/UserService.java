package com.dashuai.learning.mybatis.ehcache.service;

import com.dashuai.learning.mybatis.ehcache.model.User;

import java.util.List;

/**
 * The interface User service.
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
public interface UserService {
    /**
     * Select all list.
     *
     * @return the list
     */
    List<User> selectAll();

    /**
     * Select by id user.
     *
     * @param id the id
     * @return the user
     */
    User selectById(long id);


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
     * Delete user boolean.
     *
     * @param id the id
     * @return the boolean
     */
    Boolean deleteUser(long id);
}
