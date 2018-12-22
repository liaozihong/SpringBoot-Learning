package com.dashuai.learning.jwt.mapper;

import com.dashuai.learning.jwt.model.Role;

public interface RoleMapper {
    Role selectRoleOfPerm(Integer id);

    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
}