package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.LikeService;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 点赞控制类
 * @author: lijuncai
 **/
@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 对实体进行点赞
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     *                   //     * @param entityUserId 实体所属用户id
     */
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId) {
        //获取当前用户
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        //获取实体的点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //获取用户对此实体的点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //返回json字符串
        return LearningBbsUtil.getJSONString(0, null, map);
    }
}
