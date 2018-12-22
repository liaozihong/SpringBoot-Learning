package com.dashuai.learning.jwt.mapper;

import com.dashuai.learning.jwt.model.UserRole;

public interface UserRoleMapper {
    boolean insert(UserRole record);

    boolean insertSelective(UserRole record);
}