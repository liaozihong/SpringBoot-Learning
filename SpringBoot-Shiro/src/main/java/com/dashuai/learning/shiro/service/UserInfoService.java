package com.dashuai.learning.shiro.service;


import com.dashuai.learning.shiro.model.UserInfo;
import com.dashuai.learning.shiro.model.UserOnlineBo;

import java.util.List;

public interface UserInfoService {
    /**
     * 通过username查找用户信息;
     */
    UserInfo findByUsername(String username);

    UserInfo findByUsernameAndPassword(String username, String password);

    UserInfo updateById(UserInfo userInfo);

    /**
     * 注册用户
     *
     * @param userInfo
     * @return
     */
    boolean registerUserInfo(UserInfo userInfo);

    /**
     * 获取当前在线用户
     *
     * @return
     */
    List<UserOnlineBo> getUserOnlineBo();
}