package com.dashuai.learning.alipay.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AlipayService {
    /**
     * 付款
     *
     * @return
     */
    String alipay();

    /**
     * 付款同步返回地址
     *
     * @param request
     * @return
     */
    String synchronous(HttpServletRequest request);

    /**
     * 付款异步通知地址
     *
     * @param request
     * @param response
     */
    void notify(HttpServletRequest request, HttpServletResponse response);

    boolean rsaCheckV1(HttpServletRequest request);
}
