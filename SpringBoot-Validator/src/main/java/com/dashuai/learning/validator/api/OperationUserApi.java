package com.dashuai.learning.validator.api;

import com.dashuai.learning.utils.result.ApiResult;
import com.dashuai.learning.validator.model.UserInfo;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@Validated
public class OperationUserApi {
    /**
     * 请求包体参数异常会触发，MethodArgumentNotValidException，需要使用@Validated 才能生效
     *
     * @param userInfo
     * @return
     */
    @RequestMapping(value = "/fillUser")
    public ApiResult fillUser(@Validated @RequestBody UserInfo userInfo) {
        return ApiResult.prepare().success(userInfo);
    }

    /**
     * 参数异常会触发，ConstraintViolationException ，需要在类上面标识@Validated 触发校验
     *
     * @param name
     * @param desc
     * @return
     */
    @RequestMapping(value = "/validator")
    public ApiResult testValidator(@NotNull(message = "name是必须的")
                                   @Length(min = 2, max = 12, message = "name长度限制为2-12")
                                   @RequestParam("name") String name,
                                   @NotNull(message = "desc是必须的")
                                   @RequestParam("desc") String desc) {
        return ApiResult.prepare().success(name + "--------" + desc);
    }
}
