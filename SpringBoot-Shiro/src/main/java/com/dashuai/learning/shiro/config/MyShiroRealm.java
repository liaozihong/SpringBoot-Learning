package com.dashuai.learning.shiro.config;

import com.dashuai.learning.shiro.model.SysPermission;
import com.dashuai.learning.shiro.model.SysRole;
import com.dashuai.learning.shiro.model.UserInfo;
import com.dashuai.learning.shiro.service.UserInfoService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * My shiro realm
 * <p/>
 * Created in 2018.12.12
 * <p/>
 *
 * @author Liaozihong
 */
public class MyShiroRealm extends AuthorizingRealm {
    @Resource
    private UserInfoService userInfoService;
    /**
     * The String redis template.
     */
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录次数计数  redisKey 前缀
     */
    private String SHIRO_LOGIN_COUNT = "shiro_login_count_";

    /**
     * 用户登录是否被锁定    一小时 redisKey 前缀
     */
    private String SHIRO_IS_LOCK = "shiro_is_lock_";

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();
        for (SysRole role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRole());
            for (SysPermission p : role.getPermissions()) {
                authorizationInfo.addStringPermission(p.getPermission());
            }
        }
        return authorizationInfo;
    }


    /**
     * 主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String name = token.getUsername();
        //访问一次，计数一次
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.increment(SHIRO_LOGIN_COUNT + name, 1);
        Integer count = Optional.ofNullable(opsForValue.get(SHIRO_LOGIN_COUNT + name))
                .map(x -> Integer.parseInt(x)).orElse(0);
        //计数大于5时，设置用户被锁定一小时
        if (count >= 5) {
            opsForValue.set(SHIRO_IS_LOCK + name, "LOCK");
            stringRedisTemplate.expire(SHIRO_IS_LOCK + name, 1, TimeUnit.HOURS);
        }
        if ("LOCK".equals(opsForValue.get(SHIRO_IS_LOCK + name))) {
            throw new DisabledAccountException("由于密码输入错误次数大于5次，帐号已经禁止登录！");
        }
        UserInfo userInfo = userInfoService.findByUsername(name);
        if (userInfo == null) {
            throw new UnknownAccountException("账号不存在!");
        } else if (userInfo.getState() == 1) {
            //如果用户的status为禁用。那么就抛出<code>DisabledAccountException</code>
            throw new DisabledAccountException("此帐号已经设置为禁止登录！");
        }
        String password = String.valueOf(token.getPassword());
        String newPs = new SimpleHash("MD5", password, userInfo.getCredentialsSalt(), 2).toHex();
        if (!newPs.equals(userInfo.getPassword())) {
            throw new IncorrectCredentialsException("帐号或密码不正确！");
        } else {
            //登录成功
            //更新登录时间 last login time
            userInfo.setLastLoginTime(new Date());
            userInfoService.updateById(userInfo);
            //清空登录计数
            opsForValue.set(SHIRO_LOGIN_COUNT + name, "0");
        }
        return new SimpleAuthenticationInfo(userInfo, userInfo.getPassword(),
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()), getName());
    }

}