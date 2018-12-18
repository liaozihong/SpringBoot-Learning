package com.dashuai.learning.shiro.service.impl;

import com.dashuai.learning.shiro.dao.SysRoleDao;
import com.dashuai.learning.shiro.dao.UserInfoDao;
import com.dashuai.learning.shiro.model.SysRole;
import com.dashuai.learning.shiro.model.UserInfo;
import com.dashuai.learning.shiro.model.UserOnlineBo;
import com.dashuai.learning.shiro.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * User info service
 * <p/>
 * Created in 2018.12.12
 * <p/>
 *
 * @author Liaozihong
 */
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    UserInfoDao userInfoDao;
    @Autowired
    SysRoleDao sysRoleDao;
    @Autowired
    RedisSessionDAO redisSessionDAO;

    private final Map<String, Integer> level = new HashMap<String, Integer>() {{
        put("管理员", 1);
        put("VIP", 2);
        put("游客", 3);
    }};
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

    @Override
    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoDao.findByUsername(username);
    }


    @Override
    public UserInfo findByUsernameAndPassword(String username, String password) {
        System.out.println("UserInfoServiceImpl.findByUsernameAndPassword()");
        return userInfoDao.findByUsernameAndPassword(username, password);
    }

    @Override
    public UserInfo updateById(UserInfo userInfo) {
        return userInfoDao.save(userInfo);
    }

    @Override
    public boolean registerUserInfo(UserInfo userInfo) {
        if (Strings.isNotEmpty(userInfo.getUsername()) && Strings.isNotEmpty(userInfo.getPassword())) {
            UserInfo user = userInfoDao.findByUsername(userInfo.getUsername());
            if (user != null) {
                log.warn("该用户已存在");
                return false;
            }
            userInfo.setSalt(randomNumberGenerator.nextBytes().toHex());
            /*
             * MD5加密：
             * 使用SimpleHash类对原始密码进行加密。
             * 第一个参数代表使用MD5方式加密
             * 第二个参数为原始密码
             * 第三个参数为盐值，即用户名
             * 第四个参数为加密次数
             * 最后用toHex()方法将加密后的密码转成String
             * */
            String newPs = new SimpleHash("MD5", userInfo.getPassword(), userInfo.getCredentialsSalt(), 2).toHex();
            userInfo.setPassword(newPs);
            userInfo.setLastLoginTime(new Date());
            Optional<SysRole> sysRole = sysRoleDao.findById(level.get(userInfo.getName()));
            List<SysRole> roleList = new ArrayList<>();
            roleList.add(sysRole.get());
            //绑定角色，向中间表插入值
            userInfo.setRoleList(roleList);
            userInfo = userInfoDao.save(userInfo);
        }
        if (userInfo != null) {
            return true;
        }
        log.warn("未注册成功!");
        return false;
    }

    @Override
    public List<UserOnlineBo> getUserOnlineBo() {
        // 因为我们是用redis实现了shiro的session的Dao,而且是采用了shiro+redis这个插件
        // 所以从spring容器中获取redisSessionDAO
        // 来获取session列表.
        Collection<Session> sessions = redisSessionDAO.getActiveSessions();
        Iterator<Session> it = sessions.iterator();
        List<UserOnlineBo> onlineUserList = new ArrayList<>();
        // 遍历session
        while (it.hasNext()) {
            // 这是shiro已经存入session的
            // 现在直接取就是了
            Session session = it.next();
            // 如果被标记为踢出就不显示 ,这里可以扩展踢出功能，就是在线用户后台也可管理踢出。
            Object obj = session.getAttribute("kickout");
            if (!(Boolean) obj) {
                continue;
            }
            UserOnlineBo onlineUser = getSessionBo(session);
            onlineUserList.add(onlineUser);
        }

        return onlineUserList;
    }

    //从session中获取UserOnline对象
    private UserOnlineBo getSessionBo(Session session) {
        //获取session登录信息。
        Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (null == obj) {
            return null;
        }
        //确保是 SimplePrincipalCollection对象。
        if (obj instanceof SimplePrincipalCollection) {
            SimplePrincipalCollection spc = (SimplePrincipalCollection) obj;
            /**
             * 获取用户登录的，@link SampleRealm.doGetAuthenticationInfo(...)方法中
             * return new SimpleAuthenticationInfo(user,user.getPswd(), getName());的user 对象。
             */
            obj = spc.getPrimaryPrincipal();
            if (null != obj && obj instanceof UserInfo) {
                //存储session + user 综合信息
                UserOnlineBo userBo = new UserOnlineBo((UserInfo) obj);
                //最后一次和系统交互的时间
                userBo.setLastAccess(session.getLastAccessTime());
                //主机的ip地址
                userBo.setHost(session.getHost());
                //session ID
                userBo.setSessionId(session.getId().toString());
                //session最后一次与系统交互的时间
                userBo.setLastLoginTime(session.getLastAccessTime());
                //回话到期 ttl(ms)
                userBo.setTimeout(session.getTimeout());
                //session创建时间
                userBo.setStartTime(session.getStartTimestamp());
                //是否踢出
                userBo.setSessionStatus(false);
                return userBo;
            }
        }
        return null;
    }

}