package com.dashuai.learning.thrift.api;

import com.dashuai.learning.thrift.client.RPCThriftClient;
import com.dashuai.learning.thrift.service.Student;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "SpringBoot集成Thrift实现RPC远程调用", tags = "RPCThriftController")
public class RPCThriftController {
    private final Logger logger = LoggerFactory.getLogger(RPCThriftController.class);
    @Autowired
    RPCThriftClient rpcThriftClient;

    @GetMapping(value = "/thrift/{name}")
    @ApiOperation(value = "第一个接口Hello，World!", notes = "测试接口", response = String.class)
    public String thriftTest(@PathVariable String name) {
        try {
            rpcThriftClient.open();
            return rpcThriftClient.getRPCThriftService().getDate(name);
        } catch (Exception e) {
            logger.error("RPC调用失败", e);
            return "error";
        } finally {
            rpcThriftClient.close();
        }
    }

    @PostMapping(value = "/setStudent")
    @ApiOperation(value = "添加一个学生对象", notes = "测试结构体添加...", response = ApiResponse.class)
    @ApiImplicitParam(name = "student", value = "学生实体", required = true, dataType = "Student")
    public ApiResult setStudent(@RequestBody Student student) {
        try {
            rpcThriftClient.open();
            return ApiResult.prepare().success(rpcThriftClient.getRPCThriftService().postStudent(student));
        } catch (Exception e) {
            logger.error("RPC调用失败", e);
            return ApiResult.prepare().error(null, 500, "添加失敗");
        } finally {
            rpcThriftClient.close();
        }
    }

    @RequestMapping(value = "/getStudent", method = RequestMethod.GET)
    @ApiOperation(value = "添加一个学生对象", notes = "测试接口", response = String.class)
    @ApiImplicitParam(name = "userId", value = "获取学生信息", required = true, dataType = "String")
    public ApiResult getStudent(String userId) {
        try {
            rpcThriftClient.open();
            return ApiResult.prepare().success(rpcThriftClient.getRPCThriftService().getStudent(Integer.parseInt(userId)));
        } catch (Exception e) {
            logger.error("RPC调用失败", e);
            return ApiResult.prepare().error(null, 500, "获取失敗");
        } finally {
            rpcThriftClient.close();
        }
    }
}
