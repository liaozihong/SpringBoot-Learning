package com.dashuai.learning.jwt.mapper;

import com.dashuai.learning.jwt.model.RolePermission;

public interface RolePermissionMapper {
    int insert(RolePermission record);

    int insertSelective(RolePermission record);
}