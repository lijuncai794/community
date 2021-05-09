package com.lijuncai.learningbbs.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @description: 项目工具类
 * @author: lijuncai
 **/
public class LearningBbsUtil {

    /**
     * 生成随机的UUID,其中的横线不需要
     *
     * @return String UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对密码进行md5加密
     *
     * @param key String 明文密码
     * @return String 经过md5加密后的密码
     */
    public static String md5(String key) {
        //密码若为空，则不执行加密，直接返回null
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 将数据转成JSON格式
     *
     * @param code int 编号
     * @param msg  String 提示信息
     * @param map  Map<String, Object> 业务数据
     * @return String json字符串
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    /**
     * 将数据转成JSON格式
     *
     * @param code int 编号
     * @param msg  String 提示信息
     * @return String json字符串
     */
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    /**
     * 将数据转成JSON格式
     *
     * @param code int 编号
     * @return String json字符串
     */
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

//    public static void main(String[] args) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("name", "zhangsan");
//        map.put("age", 25);
//        System.out.println(getJSONString(0, "ok", map));
//    }
}
