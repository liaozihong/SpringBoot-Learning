package com.dashuai.learning.jwt.api;

import com.dashuai.learning.jwt.mapper.UserInfoMapper;
import com.dashuai.learning.jwt.model.UserInfo;
import com.dashuai.learning.jwt.service.UserInfoService;
import com.dashuai.learning.jwt.utils.JWTUtil;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Login controller
 * <p/>
 * Created in 2018.12.22
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@Api(value = "SpringBoot集成Shiro JWT测试接口", tags = "LoginController")
public class LoginController {
    /**
     * The User info mapper.
     */
    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserInfoService userInfoService;

    /**
     * Login api result.
     *
     * @param username the username
     * @param password the password
     * @return the api result
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录接口", notes = "成功会返回JWT token，用于接口调用校验", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    public ApiResult login(@RequestParam("username") String username,
                           @RequestParam("password") String password) {
        UserInfo userInfo = userInfoService.findByUsername(username);
        String newPs = new SimpleHash("MD5", password, userInfo.getCredentialsSalt(), 2).toHex();
        if (userInfo == null || userInfo.getName() == null) {
            return ApiResult.prepare().error(null, 401, "用户名错误");
        } else if (!newPs.equals(userInfo.getPassword())) {
            return ApiResult.prepare().error(null, 401, "密码错误");
        } else {
            return ApiResult.prepare().success(JWTUtil.createToken(username));
        }
    }

    /**
     * Unauthorized api result.
     *
     * @param message the message
     * @return the api result
     */
    @GetMapping(path = "/unauthorized/{message}")
    public ApiResult unauthorized(@PathVariable String message) {
        return ApiResult.prepare().error(null, 401, message);
    }
}
