package com.dashuai.learning.shiro.controller;

import com.dashuai.learning.shiro.utils.vcode.Captcha;
import com.dashuai.learning.shiro.utils.vcode.GitCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @RequestMapping({"/", "/index"})
    public String index() {
        return "/index";
    }

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Map<String, Object> map) {
        // 登录失败从request中获取shiro处理的异常信息。
        // shiroLoginFailure:就是shiro异常类的全类名.
        String exception = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("exception=" + exception);
        String msg = "";
        if (exception != null) {
            if (UnknownAccountException.class.getName().equals(exception)) {
                System.out.println("UnknownAccountException -- > 账号不存在：");
                msg = "UnknownAccountException -- > 账号不存在：";
            } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
                System.out.println("IncorrectCredentialsException -- > 密码不正确：");
                msg = "IncorrectCredentialsException -- > 密码不正确：";
            } else if ("kaptchaValidateFailed".equals(exception)) {
                System.out.println("kaptchaValidateFailed -- > 验证码错误");
                msg = "kaptchaValidateFailed -- > 验证码错误";
            } else if (DisabledAccountException.class.getName().equals(exception)) {
                System.out.println("账号已被禁用");
                msg = "当前账号已被禁用!";
            } else {
                msg = "else >> " + exception;
                System.out.println("else -- >" + exception);
            }
        }
        map.put("msg", msg);
        // 此方法不处理登录成功,由shiro进行处理
        return "/login";
    }

    @RequestMapping(value = "/ajaxLogin")
    @ResponseBody
    public Object ajaxLogin(@RequestParam String username, @RequestParam String password,
                            @RequestParam String authCode, @RequestParam boolean rememberMe, HttpServletRequest request) {
        String ret = "";
        Subject currentUser = SecurityUtils.getSubject();
        String vcode = (String) request.getSession().getAttribute("_code");
        System.out.println(vcode);
        if (!vcode.equals(authCode)) {
            ret = "{success:false,message:'验证码错误!'}";
        }
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(username,
                    password);
            token.setRememberMe(rememberMe);
            try {
                currentUser.login(token);
                ret = "{success:true,message:'登陆成功'}";
            } catch (UnknownAccountException ex) {
                ret = "{success:false,message:'账号错误'}";
                log.info(ret);
            } catch (IncorrectCredentialsException ex) {
                ret = "{success:false,message:'密码错误'}";
                log.info(ret);
            } catch (LockedAccountException ex) {
                ret = "{success:false,message:'账号已被锁定，请与管理员联系'}";
                log.info(ret);
            } catch (AuthenticationException ex) {
                ret = "{success:false,message:'您没有授权'}";
                log.info(ret);
            }
        }
        // 返回json数据
        return ret;
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("------没有权限-------");
        return "403";
    }

    @RequestMapping("/kickout")
    public String kickout() {
        System.out.println("------您已在其他地方登录-------");
        return "kickout";
    }

    @RequestMapping(value = "/getGifCode", method = RequestMethod.GET)
    public void getGifCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");
            //session需要在输出response前定义
            HttpSession session = request.getSession(true);
            /**
             * gif格式动画验证码
             * 宽，高，位数。
             */
            Captcha captcha = new GitCaptcha(146, 33, 4);
            //输出
            captcha.out(response.getOutputStream());
            //存入Session
            session.setAttribute("_code", captcha.text().toLowerCase());
        } catch (Exception e) {
            System.err.println("获取验证码异常：" + e.getMessage());
        }
    }

}