package com.lijuncai.learningbbs.config;

import com.lijuncai.learningbbs.controller.interceptor.HelloInterceptor;
import com.lijuncai.learningbbs.controller.interceptor.LoginInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: 拦截器配置类
 * @author: lijuncai
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private HelloInterceptor helloInterceptor;
    @Autowired
    private LoginInfoInterceptor loginInfoInterceptor;

    /**
     * 添加拦截器
     *
     * @param registry registry对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(helloInterceptor)
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
//                .addPathPatterns("/register", "/login");
        registry.addInterceptor(loginInfoInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    }
}
