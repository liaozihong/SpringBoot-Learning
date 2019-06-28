package com.dashuai.learning.jwt.mapper;

import com.dashuai.learning.jwt.model.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {
    UserInfo selectUserOfRole(@Param("uid") Integer uid);

    UserInfo selectByName(@Param("username") String username);

    UserInfo selectByNameAndPassword(@Param("username") String username, @Param("password") String password);

    boolean deleteByPrimaryKey(Integer uid);

    boolean insert(UserInfo record);

    boolean insertSelective(UserInfo record);


    boolean updateByPrimaryKeySelective(UserInfo record);

    boolean updateByPrimaryKey(UserInfo record);
}