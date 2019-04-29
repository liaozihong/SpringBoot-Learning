package com.dashuai.learning.jta.service.impl;

import com.dashuai.learning.jta.mapper.spring.UserMapper;
import com.dashuai.learning.jta.mapper.test.PeopleMapper;
import com.dashuai.learning.jta.model.spring.User;
import com.dashuai.learning.jta.model.test.People;
import com.dashuai.learning.jta.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

/**
 * People service
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class PeopleServiceImpl implements PeopleService {
    /**
     * The People mapper.
     */
    @Autowired
    PeopleMapper peopleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public List<People> selectAll() {
        return peopleMapper.findAll();
    }

    @Override
    public Boolean insertPeople(People people) {
        return peopleMapper.insertSelective(people);
    }

    @Override
    @Transactional
    public Boolean insertUserAndPeople(User user, People people) throws RuntimeException {
        peopleMapper.insert(people);
        try {
            userMapper.insertSelective(user);
        } catch (Exception e) {
            throw new RuntimeException("抛出runtime异常，导致回滚数据");
        }
        return true;
    }

    @Override
    public Boolean insertUserAndPeopleV2(User user, People people) {
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {

            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    peopleMapper.insert(people);
                    userMapper.insertSelective(user);
                } catch (Exception e) {
                    throw new RuntimeException("抛出runtime异常，导致回滚数据");
                }
                return true;
            }
        });
    }
}
