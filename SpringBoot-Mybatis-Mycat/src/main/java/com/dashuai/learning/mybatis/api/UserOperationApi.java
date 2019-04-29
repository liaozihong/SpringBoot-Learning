package com.dashuai.learning.mybatis.api;

import com.dashuai.learning.mybatis.model.User;
import com.dashuai.learning.mybatis.service.UserService;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * User operation api
 * <p/>
 * Created in 2018.11.09
 * <p/>
 * 此例子只是简单的配置mybatis，后面会配置一个双数据源的
 *
 * @author Liaozihong
 */
@RestController
@Api(value = "Mybatis测试接口", tags = "UserOperationApi")
public class UserOperationApi {
    /**
     * The User service.
     */
    @Autowired
    UserService userService;

    /**
     * Gets all.
     *
     * @return the all
     */
    @GetMapping("/getAll")
    @ApiOperation(value = "获取全部用户信息", notes = "查询全部", response = ApiResult.class)
    public ApiResult getAll() {
        return ApiResult.prepare().success(JSONParseUtils.object2JsonString(userService.selectAll()));
    }

    /**
     * Insert user api result.
     *
     * @param user the user
     * @return the api result
     */
    @PostMapping("/insertUser")
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User")
    public ApiResult insertUser(@RequestBody User user) {
        Boolean isSuccess = userService.insertUser(user);
        if (isSuccess) {
            return ApiResult.prepare().success("添加成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(user), 500, "添加失败!");
    }
}
