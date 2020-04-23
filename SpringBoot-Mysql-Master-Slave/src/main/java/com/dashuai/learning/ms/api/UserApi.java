package com.dashuai.learning.ms.api;

import com.dashuai.learning.ms.model.User;
import com.dashuai.learning.ms.service.UserService;
import com.dashuai.learning.utils.result.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApi {

    @Autowired
    UserService userService;

    @GetMapping("/getUserAll")
    public ApiResult getUserAll() {
        return ApiResult.prepare().success(userService.getUserAll());
    }

    @GetMapping("/getUserById")
    public ApiResult getUserById(Integer userId) {
        return ApiResult.prepare().success(userService.getUserInfo(userId));
    }

    @PostMapping("/createOrUpdateUser")
    public ApiResult createUser(@RequestBody User user) {
        return ApiResult.prepare().success(userService.createOrUpdateUser(user));
    }

}
