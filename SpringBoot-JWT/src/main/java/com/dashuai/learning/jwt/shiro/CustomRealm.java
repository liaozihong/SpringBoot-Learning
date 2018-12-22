package com.dashuai.learning.jwt.shiro;

import com.dashuai.learning.jwt.mapper.RoleMapper;
import com.dashuai.learning.jwt.mapper.UserInfoMapper;
import com.dashuai.learning.jwt.model.Permission;
import com.dashuai.learning.jwt.model.Role;
import com.dashuai.learning.jwt.model.UserInfo;
import com.dashuai.learning.jwt.utils.JWTUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Custom realm
 * <p/>
 * Created in 2018.12.22
 * <p/>
 *
 * @author Liaozihong
 */
public class CustomRealm extends AuthorizingRealm {
    /**
     * The User info mapper.
     */
    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * The Role mapper.
     */
    @Autowired
    RoleMapper roleMapper;

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("————身份认证方法————");
        String token = (String) authenticationToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getUsername(token);
        if (username == null || !JWTUtil.verify(token, username)) {
            throw new AuthenticationException("token认证失败！");
        }
        UserInfo userInfo = userInfoMapper.selectByName(username);
        if (userInfo == null) {
            throw new AuthenticationException("该用户不存在！");
        }
        if (userInfo.getState() == 1) {
            throw new AuthenticationException("该用户已被封号！");
        }
        return new SimpleAuthenticationInfo(token, token, "MyRealm");
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("————权限认证————");
        String username = JWTUtil.getUsername(principals.toString());
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 此处最好使用缓存提升速度
        UserInfo userInfo = userInfoMapper.selectByName(username);
        userInfo = userInfoMapper.selectUserOfRole(userInfo.getUid());
        if (userInfo == null || userInfo.getRoleList().isEmpty()) {
            return authorizationInfo;
        }
        for (Role role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
            role = roleMapper.selectRoleOfPerm(role.getId());
            if (role == null || role.getPermissions().isEmpty()) {
                continue;
            }
            for (Permission p : role.getPermissions()) {
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }
}
