package com.dashuai.learning.shiro.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Custom form authentication filter
 * <p/>
 * Created in 2018.12.14
 * <p/>
 * 自定义的验证码过滤器，需要在ShiroConfig注册到过滤器中
 *
 * @author Liaozihong
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

    //原FormAuthenticationFilter的认证方法
    @Override
    protected boolean onAccessDenied(ServletRequest request,
                                     ServletResponse response) throws Exception {
        //从session获取正确验证码
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();
        //取出session的验证码（正确的验证码）
        String validateCode = (String) session.getAttribute("_code");
        //取出页面的验证码
        //输入的验证和session中的验证进行对比
        String randomcode = httpServletRequest.getParameter("authCode");
        if (randomcode != null && validateCode != null && !randomcode.equals(validateCode)) {
            //如果校验失败，将验证码错误失败信息，通过shiroLoginFailure设置到request中
            httpServletRequest.setAttribute("shiroLoginFailure", "验证码输入错误!");
            //拒绝访问，不再校验账号和密码
            return true;
        }
        return super.onAccessDenied(request, response);
    }


}
