package com.dashuai.learning.mongodb.api;

import com.dashuai.learning.mongodb.model.People;
import com.dashuai.learning.mongodb.service.OperationService;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Operation api
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
public class OperationApi {
    /**
     * The Operation service.
     */
    @Autowired
    OperationService operationService;

    /**
     * Gets all.
     *
     * @return the all
     */
    @ApiOperation(value = "查询全部People信息", notes = "获取全部People信息", response = ApiResult.class)
    @GetMapping("/getAll")
    public ApiResult getAll() {
        return ApiResult.prepare().success(operationService.selectAllPeople());
    }

    /**
     * Insert people api result.
     *
     * @param people the people
     * @return the api result
     */
    @ApiOperation(value = "添加people实例", notes = "添加用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "people", value = "用户信息", required = true, dataType = "People")
    @PostMapping("/insertPeople")
    public ApiResult insertPeople(@RequestBody People people) {
        Boolean isSuccess = operationService.insertPeople(people);
        if (isSuccess) {
            return ApiResult.prepare().success("添加成功!");
        }
        return ApiResult.prepare().success("添加失败!");
    }

    /**
     * Update people api result.
     *
     * @param people the people
     * @return the api result
     */
    @ApiOperation(value = "修改people实例", notes = "修改用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "people", value = "用户信息", required = true, dataType = "People")
    @PostMapping("/updatePeople")
    public ApiResult updatePeople(@RequestBody People people) {
        Boolean isSuccess = operationService.uploadPeople(people);
        if (isSuccess) {
            return ApiResult.prepare().success("修改成功!");
        }
        return ApiResult.prepare().success("修改失败!");
    }

    /**
     * Delete people api result.
     *
     * @param name the name
     * @return the api result
     */
    @ApiOperation(value = "删除people实例", notes = "删除用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "name", value = "用户名称", required = false, dataType = "String", paramType = "query")
    @GetMapping("/deletePeople")
    public ApiResult deletePeople(String name) {
        Boolean isSuccess = operationService.deletePeople(name);
        if (isSuccess) {
            return ApiResult.prepare().success("刪除成功!");
        }
        return ApiResult.prepare().success("刪除失败!");
    }

    /**
     * Gets people by name.
     *
     * @param name the name
     * @return the people by name
     */
    @ApiOperation(value = "查询特定people实例", notes = "查询用户信息", response = ApiResult.class)
    @ApiImplicitParam(name = "name", value = "用户名称", required = false, dataType = "String", paramType = "query")
    @GetMapping("/getPeopleByName")
    public ApiResult getPeopleByName(String name) {
        People people = new People();
        people.setName(name);
        return ApiResult.prepare().success(operationService.selectPeople(people));
    }
}
