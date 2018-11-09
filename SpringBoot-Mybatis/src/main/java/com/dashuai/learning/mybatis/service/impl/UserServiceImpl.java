package com.dashuai.learning.mybatis.service.impl;

import com.dashuai.learning.mybatis.mapper.UserMapper;
import com.dashuai.learning.mybatis.model.User;
import com.dashuai.learning.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User service
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@Service
public class UserServiceImpl implements UserService {
    /**
     * The User mapper.
     */
    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> selectAll() {
        return userMapper.findAll();
    }

    @Override
    public Boolean insertUser(User user) {
        return userMapper.insertSelective(user);
    }
}
