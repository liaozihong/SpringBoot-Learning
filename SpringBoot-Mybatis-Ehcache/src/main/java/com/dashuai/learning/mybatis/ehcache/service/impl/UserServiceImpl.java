package com.dashuai.learning.mybatis.ehcache.service.impl;

import com.dashuai.learning.mybatis.ehcache.mapper.UserMapper;
import com.dashuai.learning.mybatis.ehcache.model.User;
import com.dashuai.learning.mybatis.ehcache.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * The User mapper.
     */
    @Autowired
    UserMapper userMapper;

    private final String cacheName = "userCache";

    private final String cacheKey = "'user'";

    //* @Cacheable : Spring在每次执行前都会检查Cache中是否存在相同key的缓存元素，如果存在就不再执行该方法，而是直接从缓存中获取结果进行返回，否则才会执行并将返回结果存入指定的缓存中。
    //* @CacheEvict : 清除缓存。
    //* @CachePut : @CachePut也可以声明一个方法支持缓存功能。使用@CachePut标注的方法在执行前不会去检查缓存中是否存在之前执行过的结果，而是每次都会执行该方法，并将执行结果以键值对的形式存入指定的缓存中。

    @Override
    @Cacheable(value = cacheName, key = cacheKey)
    public List<User> selectAll() {
        log.info("未读取缓存");
        return userMapper.findAll();
    }

    @Override
    @CacheEvict(value = cacheName, key = cacheKey)
    public Boolean insertUser(User user) {
        return userMapper.insertSelective(user);
    }

    @Override
    @Cacheable(value = cacheName, key = "'user_'+ #id")
    public User selectById(long id) {
        log.info("未读取缓存");
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @CacheEvict(value = cacheName, key = cacheKey)
    public Boolean updateUser(User user) {
        return userMapper.updateByPrimaryKey(user);
    }

    @Override
    @CacheEvict(value = cacheName, key = cacheKey)
    public Boolean deleteUser(long id) {
        return userMapper.deleteByPrimaryKey(id);
    }
}
