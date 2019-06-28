package com.dashuai.learning.jwt.service.impl;

import com.dashuai.learning.jwt.mapper.RoleMapper;
import com.dashuai.learning.jwt.mapper.UserInfoMapper;
import com.dashuai.learning.jwt.mapper.UserRoleMapper;
import com.dashuai.learning.jwt.model.Role;
import com.dashuai.learning.jwt.model.UserInfo;
import com.dashuai.learning.jwt.model.UserRole;
import com.dashuai.learning.jwt.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserRoleMapper userRoleMapper;

    private final Map<String, Integer> level = new HashMap<String, Integer>() {{
        put("管理员", 1);
        put("VIP", 2);
        put("游客", 3);
    }};
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    @Override
    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoMapper.selectByName(username);
    }


    @Override
    public UserInfo findByUsernameAndPassword(String username, String password) {
        System.out.println("UserInfoServiceImpl.findByUsernameAndPassword()");
        return userInfoMapper.selectByNameAndPassword(username, password);
    }

    @Override
    public boolean updateById(UserInfo userInfo) {
        return userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }

    @Override
    public boolean registerUserInfo(UserInfo userInfo) {
        if (Strings.isNotEmpty(userInfo.getUsername()) && Strings.isNotEmpty(userInfo.getPassword())) {
            UserInfo user = userInfoMapper.selectByName(userInfo.getUsername());
            if (user != null) {
                log.warn("该用户已存在");
                return false;
            }
            userInfo.setSalt(randomNumberGenerator.nextBytes().toHex());
            String newPs = new SimpleHash("MD5", userInfo.getPassword(), userInfo.getCredentialsSalt(), 2).toHex();
            userInfo.setPassword(newPs);
            userInfo.setLastLoginTime(new Date());
            Role sysRole = roleMapper.selectByPrimaryKey(level.get(userInfo.getName()));
            List<Role> roleList = new ArrayList<>();
            roleList.add(sysRole);
            boolean isSuccess = userInfoMapper.insertSelective(userInfo);
            //绑定角色，向中间表插入值
            if (isSuccess) {
                UserRole userRole = new UserRole(sysRole.getId(), userInfo.getUid());
                isSuccess = userRoleMapper.insertSelective(userRole);
            }
            return isSuccess;

        }
        log.warn("未注册成功!");
        return false;
    }

}