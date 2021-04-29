package com.lijuncai.learningbbs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * @description:
 * @author: lijuncai
 **/
@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "hello springboot";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest res, HttpServletResponse rep) {
        //获取请求数据
        System.out.println(res.getMethod());
        System.out.println(res.getServletPath());

        Enumeration<String> enumeration = res.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = res.getHeader(name);
            System.out.println(name + ":" + value);
        }

        //返回响应数据
        rep.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer = rep.getWriter();
        ) {
            writer.write("<h1>学习社区</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //GET请求
    //需求：分页获取用户列表 /users?cur=2&limit=20
    //使用@RequestParam可以参数进一步设置
    @ResponseBody
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public String getUsers(
            @RequestParam(name = "cur", required = false, defaultValue = "1") int cur,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {

        System.out.println(cur);
        System.out.println(limit);
        return "students:";
    }

    //需求：获取单个用户
    //这里把id拼接到路径中，再使用@PathVariable进行获取
    @ResponseBody
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable("id") int id) {
        System.out.println("id:" + id);
        return "a user:";
    }

    //POST请求
    @ResponseBody
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(String userName, String userPsw) {
        System.out.println(userName);
        System.out.println(userPsw);
        return "register success";
    }


    //响应HTML数据,这里不设置@ResponseBody，则默认返回html类型
    @RequestMapping(path = "/admin", method = RequestMethod.GET)
    public ModelAndView getAdmin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", "20");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    //另一种响应方式,方法加参数：Model model，返回只需要返回视图的路径
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "清华大学");
        model.addAttribute("age", "110");
        return "/demo/view";
    }

    //响应JSON数据；(一般是异步请求)，比如注册时判断用户名是否已存在
    //Java对象 --> JSON字符串 --> JS对象
    @RequestMapping(path = "/employee", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmployee() {
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put("name", "张三");
        employeeMap.put("age", "25");
        employeeMap.put("salary", "80000.0");

        //返回时，DispatcherServlet会将map自动转成JSON格式
        return employeeMap;
    }

    @RequestMapping(path = "/employees", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getAllEmployee() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> employeeMap1 = new HashMap<>();
        employeeMap1.put("name", "张三");
        employeeMap1.put("age", "20");
        employeeMap1.put("salary", "8000.0");

        list.add(employeeMap1);

        Map<String, Object> employeeMap2 = new HashMap<>();
        employeeMap2.put("name", "李四");
        employeeMap2.put("age", "25");
        employeeMap2.put("salary", "30000.0");
        list.add(employeeMap2);

        //返回时，DispatcherServlet会将map自动转成JSON格式
        return list;
    }
}
