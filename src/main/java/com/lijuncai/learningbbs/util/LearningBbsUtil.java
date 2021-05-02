package com.lijuncai.learningbbs.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @description: 项目工具类
 * @author: lijuncai
 **/
public class LearningBbsUtil {

    /**
     * 生成随机的UUID,其中的横线不需要
     *
     * @return UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对密码进行md5加密
     *
     * @param key 明文密码
     * @return md5加密后的字符串
     */
    public static String md5(String key) {
        //密码若为空，则不执行加密，直接返回null
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
