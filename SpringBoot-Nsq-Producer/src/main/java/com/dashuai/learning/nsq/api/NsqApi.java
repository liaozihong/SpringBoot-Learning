package com.dashuai.learning.nsq.api;

import com.dashuai.learning.nsq.service.MqProductService;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nsq api
 * Created in 2018.11.13
 *
 * @author Liaozihong
 */
@RestController
@Api(value = "NSQ测试接口", tags = "NsqApi")
public class NsqApi {

    /**
     * The Mq product service.
     */
    @Autowired
    MqProductService mqProductService;

    /**
     * Mq product api result.
     *
     * @param message the message
     * @return the api result
     */
    @ApiOperation(value = "发送MQ消息接口", notes = "发送mq消息", response = ApiResult.class)
    @ApiImplicitParam(name = "message", value = "队列消息", required = true, dataType = "String", paramType = "query")
    @GetMapping("/sendMsg")
    public ApiResult mqProduct(String message){
        String msg = mqProductService.sendTestMessage(message);
        return new ApiResult().success(msg);
    }

    /**
     * Mq product api result.
     *
     * @param message the message
     * @return the api result
     */
    @ApiOperation(value = "发送MQ消息至特定Channel接口", notes = "发送mq消息", response = ApiResult.class)
    @ApiImplicitParam(name = "message", value = "队列消息", required = true, dataType = "String", paramType = "query")
    @GetMapping("/sendMsgByChannel")
    public ApiResult mqProductByChannel(String message) {
        String msg = mqProductService.sendTestMessageByChannel(message);
        return new ApiResult().success(msg);
    }

}
