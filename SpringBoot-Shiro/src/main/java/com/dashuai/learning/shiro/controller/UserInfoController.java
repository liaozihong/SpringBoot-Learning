package com.dashuai.learning.shiro.controller;

import com.dashuai.learning.shiro.model.UserInfo;
import com.dashuai.learning.shiro.service.UserInfoService;
import com.dashuai.learning.utils.result.ApiResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/userInfo")
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;

    /**
     * 用户查询.
     *
     * @return
     */
    @RequestMapping("/userList")
    @RequiresPermissions("userInfo:view")//权限管理;
    public String userInfo() {
        return "userInfo";
    }

    /**
     * 用户添加页面
     *
     * @return
     */
    @RequestMapping("/userAdd")
    public String userInfoAdd() {
        return "userInfoAdd";
    }

    @RequestMapping("/ajaxVisit")
    @ResponseBody
    public ApiResult ajaxVisit() {
//        测试在被踢出后，异步访问得到吗？
        return ApiResult.prepare().success("访问成功!");
    }

    @RequestMapping("/registerUser")
    @RequiresPermissions("userInfo:add")//权限管理;
    @ResponseBody
    public ApiResult userCreate(UserInfo userInfo) {
        boolean isSuccess = userInfoService.registerUserInfo(userInfo);
        if (isSuccess) {
            return ApiResult.prepare().success("添加成功!");
        }
        return ApiResult.prepare().error(null, 500, "添加出现问题!");
    }

    /**
     * 用户删除;
     *
     * @return
     */
    @RequestMapping("/userDel")
    @RequiresPermissions("userInfo:del")//权限管理;
    public String userDel() {
        return "userInfoDel";
    }

    @RequestMapping("/getUserOnlineUser")
    @ResponseBody
    public ApiResult getUserOnlineUser() {
        return ApiResult.prepare().success(userInfoService.getUserOnlineBo());
    }

    /**
     * 显示当前在线用户
     *
     * @param model
     * @return
     */
    @RequestMapping("/onlineUser")
    public String onlineUser(Model model) {
        model.addAttribute("userList", userInfoService.getUserOnlineBo());
        return "onlineUser";
    }
}