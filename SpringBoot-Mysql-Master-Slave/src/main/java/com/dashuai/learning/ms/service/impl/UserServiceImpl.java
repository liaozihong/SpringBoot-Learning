package com.dashuai.learning.ms.service.impl;

import com.dashuai.learning.ms.dao.UserMapper;
import com.dashuai.learning.ms.model.User;
import com.dashuai.learning.ms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created in 2020.04.22
 *
 * @author ZH ·L
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * The User mapper.
     */
    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> getUserAll() {
        return userMapper.selectAll();
    }

    @Override
    public User getUserInfo(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Boolean createOrUpdateUser(User user) {
        if (userMapper.selectByPrimaryKey(user.getId()) != null) {
            return userMapper.updateByPrimaryKey(user) > 0;
        }
        return userMapper.insertSelective(user) > 0;
    }

}
