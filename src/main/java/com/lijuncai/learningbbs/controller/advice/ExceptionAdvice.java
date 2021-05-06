package com.lijuncai.learningbbs.controller.advice;

import com.lijuncai.learningbbs.controller.LoginController;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description: 异常通知类
 * @author: lijuncai
 **/
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * 处理所有异常
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        //对于普通请求：重定向到500页面，对于异步请求：返回错误信息的json字符串
        String xRequestedWith = request.getHeader("x-requested-with");

        //1.异步请求
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(LearningBbsUtil.getJSONString(1, "服务器异常!"));
        } else {
            //2，普通请求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
