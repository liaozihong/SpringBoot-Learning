package com.dashuai.learning.utils.result;

import java.util.HashMap;

/**
 * Api result
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaodashuai
 */
public class ApiResult extends HashMap<String, Object> {

    /**
     * 获取实例
     *
     * @return 实例 api result
     */
    public static ApiResult prepare() {
        return new ApiResult();
    }

    /**
     * Instantiates a new Api result.
     */
    public ApiResult() {
    }

    /**
     * 调用成功返回结果
     *
     * @param result 调用结果数据
     * @return 响应实体 api result
     */
    public ApiResult success(Object result) {
        this.put("is_success", true);
        this.put("result", result);
        this.put("code", 200);
        this.put("msg", "");
        return this;
    }

    /**
     * 调用成功返回结果
     *
     * @param result 调用结果数据
     * @param code   说明码
     * @param msg    其他信息
     * @return 响应实体 api result
     */
    public ApiResult success(Object result, int code, String msg) {
        this.put("is_success", true);
        this.put("result", result);
        this.put("code", code);
        this.put("msg", msg);
        return this;
    }

    /**
     * 调用失败返回结果
     *
     * @param result 调用失败为null或者其他错误信息
     * @param code   错误码
     * @param msg    其他信息
     * @return 响应实体 api result
     */
    public ApiResult error(Object result, int code, String msg) {
        this.put("is_success", false);
        this.put("result", result);
        this.put("code", code);
        this.put("msg", msg);
        return this;
    }
}