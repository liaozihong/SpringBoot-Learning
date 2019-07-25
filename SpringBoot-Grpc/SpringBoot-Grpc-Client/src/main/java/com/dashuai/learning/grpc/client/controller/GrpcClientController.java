package com.dashuai.learning.grpc.client.controller;

import com.dashuai.learning.grpc.client.service.GreeterClientService;
import com.dashuai.learning.grpc.client.service.UserClientService;
import com.dashuai.learning.grpc.lib.proto.UserOuterClass;
import com.dashuai.learning.grpc.lib.proto.utils.ProtobufUtils;
import com.dashuai.learning.utils.result.ApiResult;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.internal.IoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Api(value = "测试Grpc 使用Protocol buffer进行远程服务调用", tags = "GrpcClientController")
public class GrpcClientController {
    @Autowired
    private GreeterClientService grpcClientService;

    @Autowired
    private UserClientService userClientService;

    @GetMapping(value = "/greeter/{name}")
    @ApiOperation(value = "hello world", notes = "hello world", response = String.class)
    @ApiImplicitParam(name = "name", value = "name", required = true, dataType = "String")
    public String printMessage(@PathVariable @RequestParam(defaultValue = "Michael") String name) {
        return grpcClientService.sendMessage(name);
    }

    @PostMapping(value = "/user", produces = "application/json;charset=UTF-8")
    public boolean saveUser(HttpServletRequest request) throws IOException {
        String json = new String(IoUtils.toByteArray(request.getInputStream()), request.getCharacterEncoding());
        return userClientService.saveUser(ProtobufUtils.jsonToPf(json, UserOuterClass.UserData.newBuilder()));
    }

    @GetMapping(value = "/user/{id}")
    @ApiOperation(value = "获取User实例", notes = "获取用户信息", response = UserOuterClass.UserData.class)
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Integer")
    public ApiResult getUser(@PathVariable Integer id) throws InvalidProtocolBufferException {
        UserOuterClass.UserData userData = userClientService.getUserData(id);
        return ApiResult.prepare().success(ProtobufUtils.pfToJson(userData));
    }
}
