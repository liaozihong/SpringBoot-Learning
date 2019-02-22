package com.dashuai.learning.validator.api;

import com.dashuai.learning.utils.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Constraint violation exception api result.
     *
     * @param e the e
     * @return the api result
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public @ResponseBody
    ApiResult constraintViolationException(ConstraintViolationException e) {
        log.error("{}", e);
        return ApiResult.prepare().error(null, 400, e.getConstraintViolations().stream()
                .findFirst().get().getMessageTemplate());
    }

    /**
     * RequestBody的参数校验异常
     *
     * @param e the e
     * @return the api result
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public @ResponseBody
    ApiResult methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("{}", e);
        return ApiResult.prepare().error(null, 400, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }
}
