package com.dashuai.learning.jta.service;


import com.dashuai.learning.jta.model.spring.User;
import com.dashuai.learning.jta.model.test.People;

import java.util.List;

/**
 * The interface People service.
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
public interface PeopleService {
    /**
     * Select all list.
     *
     * @return the list
     */
    List<People> selectAll();

    /**
     * Insert people boolean.
     *
     * @param people the people
     * @return the boolean
     */
    Boolean insertPeople(People people);

    /**
     * 同时添加两表，测试事务
     * @param user 用户信息
     * @param people 人类信息
     * @return
     * @throws Exception
     */
    Boolean insertUserAndPeople(User user, People people) throws Exception;
}
