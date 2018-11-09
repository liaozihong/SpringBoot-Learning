package com.dashuai.learning.mybatis.mapper;

import com.dashuai.learning.mybatis.model.User;

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

    /**
     * Find all list.
     *
     * @return the list
     */
    List<User> findAll();

    /**
     * Delete by primary key boolean.
     *
     * @param id the id
     * @return the boolean
     */
    Boolean deleteByPrimaryKey(Long id);

    /**
     * Insert boolean.
     *
     * @param record the record
     * @return the boolean
     */
    Boolean insert(User record);

    /**
     * Insert selective boolean.
     *
     * @param record the record
     * @return the boolean
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
     * Update by primary key selective boolean.
     *
     * @param record the record
     * @return the boolean
     */
    Boolean updateByPrimaryKeySelective(User record);

    /**
     * Update by primary key boolean.
     *
     * @param record the record
     * @return the boolean
     */
    Boolean updateByPrimaryKey(User record);
}