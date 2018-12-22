package com.dashuai.learning.jwt.api;

import com.dashuai.learning.jwt.mapper.UserInfoMapper;
import com.dashuai.learning.jwt.model.UserInfo;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller
 * <p/>
 * Created in 2018.12.22
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@RequestMapping("/admin")
@Api(value = "SpringBoot集成Shiro JWT测试接口", tags = "AdminController")
public class AdminController {

    /**
     * The User info mapper.
     */
    @Autowired
    UserInfoMapper userInfoMapper;

    /**
     * Gets user.
     *
     * @return the user
     */
    @GetMapping("/getUser")
    @RequiresRoles("admin")
    @ApiOperation(value = "查询全部用户", notes = "需要是admin角色", response = ApiResult.class)
    public ApiResult getUser() {
        return ApiResult.prepare().success("查看全部");
    }

    /**
     * Update password api result.
     *
     * @param username the username
     * @return the api result
     */
    @PostMapping("/banUser")
    @RequiresRoles("admin")
    @ApiOperation(value = "禁用某个用户", notes = "需要是admin角色", response = ApiResult.class)
    public ApiResult updatePassword(String username) {
        UserInfo userInfo = userInfoMapper.selectByName(username);
        if (userInfo == null || userInfo.getUsername() == null) {
            return ApiResult.prepare().error(null, 400, "该用户不存在！");
        }
        userInfo.setState((byte) 1);
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
        return ApiResult.prepare().success("成功封号！");
    }
}
