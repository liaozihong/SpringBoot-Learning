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
     *
     * @param user   用户信息
     * @param people 人类信息
     * @return
     * @throws Exception
     */
    Boolean insertUserAndPeople(User user, People people) throws Exception;

    /**
     * 使用编程式事务，效果跟@Transactional等同，好处，@Transactional会等整个方法处理完在释放连接，如果方法中有其他耗时长的操作，
     * 会导致连接长时间占有，可能会导致连接数过多异常。
     *
     * @param user
     * @param people
     * @return
     */
    Boolean insertUserAndPeopleV2(User user, People people);
}
