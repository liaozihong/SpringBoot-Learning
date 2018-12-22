package com.dashuai.learning.jwt.api;

import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Guest controller
 * <p/>
 * Created in 2018.12.22
 * <p/>
 * 游客角色可以访问的页面
 *
 * @author Liaozihong
 */
@RestController
@RequestMapping("/guest")
@Api(value = "SpringBoot集成Shiro JWT测试接口", tags = "GuestController")
public class GuestController {

    /**
     * Login api result.
     *
     * @return the api result
     */
    @GetMapping("/welcome")
    @ApiOperation(value = "欢迎页面", notes = "全员可访问", response = ApiResult.class)
    public ApiResult login() {
        return ApiResult.prepare().success("欢迎访问游客页面！");
    }
}
