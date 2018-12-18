package com.dashuai.learning.shiro.dao;

import com.dashuai.learning.shiro.model.UserInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * The interface User info dao.
 *
 * @author Liaozihong
 */
public interface UserInfoDao extends CrudRepository<UserInfo, Long> {
    /**
     * 通过username查找用户信息; @param username the username
     *
     * @param username the username
     * @return the user info
     */
    UserInfo findByUsername(String username);

    /**
     * Find by username and password user info.
     * 校验账号密码
     *
     * @param username the username
     * @param password the password
     * @return the user info
     */
    UserInfo findByUsernameAndPassword(String username, String password);

}