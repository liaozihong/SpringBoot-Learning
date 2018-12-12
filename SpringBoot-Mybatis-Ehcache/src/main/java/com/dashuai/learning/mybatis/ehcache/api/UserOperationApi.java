package com.dashuai.learning.mybatis.ehcache.api;

import com.dashuai.learning.mybatis.ehcache.model.User;
import com.dashuai.learning.mybatis.ehcache.service.UserService;
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

    @GetMapping("/getUserById")
    @ApiOperation(value = "根据用户id获取用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "id", value = "用户id", required = true)
    public ApiResult getUserById(Long id) {
        return ApiResult.prepare().success(userService.selectById(id));
    }

    /**
     * Insert user api result.
     *
     * @param user the user
     * @return the api result
     */
    @PostMapping("/insertUser")
    @ApiOperation(value = "添加用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User")
    public ApiResult insertUser(@RequestBody User user) {
        Boolean isSuccess = userService.insertUser(user);
        if (isSuccess) {
            return ApiResult.prepare().success("添加成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(user), 500, "添加失败!");
    }


    @PostMapping("/updateUser")
    @ApiOperation(value = "修改用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "user", value = "用户信息", required = true, dataType = "User")
    public ApiResult updateUser(@RequestBody User user) {
        Boolean isSuccess = userService.updateUser(user);
        if (isSuccess) {
            return ApiResult.prepare().success("修改成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(user), 500, "修改失败!");
    }

    @GetMapping("/deleteUser")
    @ApiOperation(value = "删除用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "id", value = "用户id", required = true)
    public ApiResult deleteUser(Long id) {
        Boolean isSuccess = userService.deleteUser(id);
        if (isSuccess) {
            return ApiResult.prepare().success("删除成功!");
        }
        return ApiResult.prepare().error(id, 500, "修改失败!");
    }
}
