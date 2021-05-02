package com.lijuncai.learningbbs.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: cookie工具类
 * @author: lijuncai
 **/
public class CookieUtil {
    /**
     * 获取cookie中key对应的value
     *
     * @param request 请求对象
     * @param name    cookie中的key
     * @return cookie中的key对应的value
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("cookie获取失败，传入参数为空!");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
