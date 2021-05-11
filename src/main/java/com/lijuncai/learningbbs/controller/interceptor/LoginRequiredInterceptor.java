package com.lijuncai.learningbbs.controller.interceptor;

import com.lijuncai.learningbbs.annotaion.LoginRequired;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @description:
 * @author: lijuncai
 **/
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    /**
     * 在Controller处理之前判断该方法是否需要登录
     *
     * @param request  HttpServletRequest 请求对象
     * @param response HttpServletResponse 响应对象
     * @param handler  Object 被拦截的对象
     * @return boolean 处理是否完成
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断被拦截的对象是不是一个方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            //判断方法上是否有@LoginRequired注解，且用户当前未登录
            if (loginRequired != null && hostHolder.getUser() == null) {

                //需要分2种情况来处理，异步请求和普通请求

                //1.对于异步请求：返回错误信息的json字符串2.对于普通请求：重定向到登录页面
                String xRequestedWith = request.getHeader("x-requested-with");
                //异步请求
                if ("XMLHttpRequest".equals(xRequestedWith)) {
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    writer.write(LearningBbsUtil.getJSONString(2, "当前用户未登录，请先登录!"));
                } else {
                    //普通请求
                    response.sendRedirect(request.getContextPath() + "/login");
                }
                return false;
            }
        }
        return true;
    }
}
