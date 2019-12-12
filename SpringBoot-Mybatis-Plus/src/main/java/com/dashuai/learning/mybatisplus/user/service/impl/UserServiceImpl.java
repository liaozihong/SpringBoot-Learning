package com.dashuai.learning.mybatisplus.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dashuai.learning.mybatisplus.user.entity.User;
import com.dashuai.learning.mybatisplus.user.mapper.UserMapper;
import com.dashuai.learning.mybatisplus.user.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zoey
 * @since 2019-12-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
