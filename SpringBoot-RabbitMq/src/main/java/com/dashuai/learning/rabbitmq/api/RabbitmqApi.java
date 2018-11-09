package com.dashuai.learning.rabbitmq.api;

import com.dashuai.learning.rabbitmq.constants.RabbitConstants;
import com.dashuai.learning.rabbitmq.message.People;
import com.dashuai.learning.rabbitmq.producter.MessageSender;
import com.dashuai.learning.utils.result.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rabbitmq controller
 * <p/>
 * Created in 2018.11.08
 * <p/>
 *
 * @author Liaodashuai
 */
@RestController
@Api(value = "MQ测试接口", tags = "RabbitmqController")
public class RabbitmqApi {

    @Autowired
    private MessageSender runner;

    /**
     * Send msg object.
     *
     * @param name the name
     * @return the object
     */
    @ApiOperation(value = "发送MQ消息接口", notes = "发送mq消息", response = Object.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名称", required = false, dataType = "String", paramType = "query")})
    @PostMapping(value = "sendMsg", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApiResult sendMsg(String name) {
        People people = new People();
        people.setId(1);
        people.setName(name);
        //模拟发送20条消息
        for (int i = 0; i < 20; i++) {
            people.setAge(20 + i);
            runner.sendMessage(RabbitConstants.MQ_EXCHANGE_SEND_MESSAGE,
                    RabbitConstants.MQ_ROUTING_KEY_SEND_MESSAGE, people);
        }
        return ApiResult.prepare().success("发送成功!");
    }

}