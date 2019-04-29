package com.dashuai.learning.jta.api;

import com.dashuai.learning.jta.model.spring.User;
import com.dashuai.learning.jta.model.test.People;
import com.dashuai.learning.jta.service.PeopleService;
import com.dashuai.learning.jta.service.UserService;
import com.dashuai.learning.utils.json.JSONParseUtils;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    /**
     * The People service.
     */
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
     * 同时操作两个数据源，并且试一个数据源操作失败，测试回滚.
     *
     * @param peopleName the people name
     * @param userName   the user name
     * @return the api result
     * @throws Exception the exception
     */
    @PostMapping(value = "/insertPeopleAndUser", produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "添加两个表", notes = "测试分布式事务", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "peopleName", value = "人名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "用户信息", required = true, dataType = "String")
    })
    public ApiResult insertPeopleAndUser(String peopleName, String userName) throws Exception {
        User user = new User();
        user.setUserName(userName);
        user.setPassword("15251251");
        user.setAge(22);
        People people = new People();
        people.setName(peopleName);
        people.setAge(20);
        people.setSex("男");
        Boolean isSuccess = peopleService.insertUserAndPeople(user, people);
        if (isSuccess) {
            return ApiResult.prepare().success("同时添加两表成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(people), 500, "添加失败，全部回滚");
    }

    @PostMapping(value = "/insertPeopleAndUserV2", produces = "application/json;charset=UTF-8")
    @ApiOperation(value = "添加两个表V2", notes = "测试分布式事务", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "peopleName", value = "人名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userName", value = "用户信息", required = true, dataType = "String")
    })
    public ApiResult insertPeopleAndUserV2(String peopleName, String userName) throws Exception {
        User user = new User();
        user.setUserName(userName);
        user.setPassword("15251251");
        user.setAge(22);
        People people = new People();
        people.setName(peopleName);
        people.setAge(20);
        people.setSex("男");
        Boolean isSuccess = peopleService.insertUserAndPeopleV2(user, people);
        if (isSuccess) {
            return ApiResult.prepare().success("同时添加两表成功!");
        }
        return ApiResult.prepare().error(JSONParseUtils.object2JsonString(people), 500, "添加失败，全部回滚");
    }
}
