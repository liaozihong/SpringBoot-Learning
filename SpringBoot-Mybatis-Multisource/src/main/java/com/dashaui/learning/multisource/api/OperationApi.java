package com.dashaui.learning.multisource.api;

import com.dashaui.learning.multisource.model.spring.User;
import com.dashaui.learning.multisource.model.test.People;
import com.dashaui.learning.multisource.service.PeopleService;
import com.dashaui.learning.multisource.service.UserService;
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
 * mybatis 双数据源测试
 *
 * @author Liaozihong
 */
@RestController
@Api(value = "Mybatis测试接口", tags = "OperationApi")
public class OperationApi {
    /**
     * The User service.
     */
    @Autowired
    UserService userService;

    @Autowired
    PeopleService peopleService;

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

    /**
     * Gets all.
     *
     * @return the all
     */
    @GetMapping("/getPeopleAll")
    @ApiOperation(value = "获取全部人类信息", notes = "查询全部", response = ApiResult.class)
    public ApiResult getPeopleAll() {
        return ApiResult.prepare().success(JSONParseUtils.object2JsonString(peopleService.selectAll()));
    }

    /**
     * Insert people api result.
     *
     * @param people the people
     * @return the api result
     */
    @PostMapping("/insertPeople")
    @ApiImplicitParam(name = "people", value = "人类信息", required = true, dataType = "People")
    public ApiResult insertPeople(@RequestBody People people) {
        Boolean isSuccess = peopleService.insertPeople(people);
        if (isSuccess) {
            return ApiResult.prepare().success("添加成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(people), 500, "添加失败!");
    }
}
