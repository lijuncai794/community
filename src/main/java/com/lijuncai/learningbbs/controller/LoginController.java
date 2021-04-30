package com.lijuncai.learningbbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description: 登录控制类
 * @author: lijuncai
 **/
@Controller
public class LoginController {

    /**
     * 访问注册页面
     *
     * @return 注册页面模板地址
     */
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage() {
        return "/site/register";
    }
}
