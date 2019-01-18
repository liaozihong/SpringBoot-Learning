package com.dashuai.learning.jta.mapper.spring;


import com.dashuai.learning.jta.model.spring.User;

import java.util.List;

/**
 * The interface User mapper.
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
public interface UserMapper {
    List<User> findAll();

    /**
     * Delete by primary key int.
     *
     * @param id the id
     * @return the int
     */
    Boolean deleteByPrimaryKey(Long id);

    /**
     * Insert int.
     *
     * @param record the record
     * @return the int
     */
    Boolean insert(User record);

    /**
     * Insert selective int.
     *
     * @param record the record
     * @return the int
     */
    Boolean insertSelective(User record);

    /**
     * Select by primary key user.
     *
     * @param id the id
     * @return the user
     */
    User selectByPrimaryKey(Long id);

    /**
     * Update by primary key selective int.
     *
     * @param record the record
     * @return the int
     */
    Boolean updateByPrimaryKeySelective(User record);

    /**
     * Update by primary key int.
     *
     * @param record the record
     * @return the int
     */
    Boolean updateByPrimaryKey(User record);
}