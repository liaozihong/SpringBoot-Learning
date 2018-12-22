package com.dashuai.learning.jwt.api;

import com.dashuai.learning.jwt.mapper.UserInfoMapper;
import com.dashuai.learning.jwt.model.UserInfo;
import com.dashuai.learning.jwt.service.UserInfoService;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User controller
 * <p/>
 * Created in 2018.12.22
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@RequestMapping("/user")
@Api(value = "SpringBoot集成Shiro JWT测试接口", tags = "UserController")
public class UserController {
    /**
     * The User info service.
     */
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserInfoMapper userInfoMapper;


    /**
     * 拥有 vip, admin 角色的用户可以访问下面的页面
     *
     * @return the message
     */
    @GetMapping("/getMessage")
    @RequiresRoles(logical = Logical.OR, value = {"vip", "admin"})
    @ApiOperation(value = "获取信息接口", notes = "只有vip和admin角色可调用", response = ApiResult.class)
    public ApiResult getMessage() {
        return ApiResult.prepare().success("成功获得信息！");
    }

    /**
     * Update password api result.
     *
     * @param username    the username
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return the api result
     */
    @PostMapping("/updatePassword")
    @RequiresRoles(logical = Logical.OR, value = {"vip", "admin"})
    public ApiResult updatePassword(String username, String oldPassword, String newPassword) {
        UserInfo userInfo = userInfoMapper.selectByName(username);
        String newPs = new SimpleHash("MD5", newPassword, userInfo.getCredentialsSalt(), 2).toHex();
        if (oldPassword.equals(newPs)) {
            return ApiResult.prepare().error(null, 403, "新旧密码不能相同!");
        }
        userInfo.setPassword(newPs);
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        return ApiResult.prepare().success("成功修改密码！");
    }

    /**
     * 拥有 vip 权限可以访问该页面
     *
     * @return the vip message
     */
    @GetMapping("/getVipMessage")
    @RequiresRoles(logical = Logical.OR, value = {"vip", "admin"})
    @RequiresPermissions("userInfo:view")
    public ApiResult getVipMessage() {
        return ApiResult.prepare().success("成功获得 vip 信息！");
    }
}
