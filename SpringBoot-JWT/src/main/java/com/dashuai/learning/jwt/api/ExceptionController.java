package com.dashuai.learning.jwt.api;

import com.dashuai.learning.utils.result.ApiResult;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * Exception controller
 * <p/>
 * Created in 2018.12.22
 * <p/>
 *
 * @author Liaozihong
 */
@RestControllerAdvice
public class ExceptionController {

    /**
     * Handle 401 api result.
     * 捕捉shiro的异常
     *
     * @return the api result
     */
    @ExceptionHandler(ShiroException.class)
    public ApiResult handle401() {
        return ApiResult.prepare().error(null, 401, "您没有权限访问！");
    }

    /**
     * Global exception api result.
     * 捕捉其他所有异常
     *
     * @param request the request
     * @param ex      the ex
     * @return the api result
     */
    @ExceptionHandler(Exception.class)
    public ApiResult globalException(HttpServletRequest request, Throwable ex) {
        ex.printStackTrace();
        return ApiResult.prepare().error(null, 401, "访问出错，无法访问: " + ex.getMessage());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
