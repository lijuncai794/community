package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.Page;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.FollowService;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @description: 关注相关的控制类
 * @author: lijuncai
 **/
@Controller
public class FollowController implements LearningBbsConstant {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 关注
     * 处理页面点击“关注”时的异步请求
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return json字符串响应，表示处理结果
     */
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return LearningBbsUtil.getJSONString(0, "已关注!");
    }

    /**
     * 取消关注
     *
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return json字符串响应，表示处理结果
     */
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return LearningBbsUtil.getJSONString(0, "已取消关注!");
    }

    /**
     * 显示用户关注的人
     *
     * @param userId 用户id
     * @param page   分页对象
     * @param model  model对象
     * @return String 用户关注列表的模板地址
     */
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        //设置分页信息
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                //在map中添加当前用户对列表中每个用户的关注状态
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    /**
     * 判断当前用户是否已关注该目标用户
     *
     * @param userId 目标用户id
     * @return boolean 是否已关注该目标用户
     */
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

    /**
     * 显示用户的粉丝列表
     *
     * @param userId 用户id
     * @param page   分页对象
     * @param model  model对象
     * @return 用户粉丝列表的模板地址
     */
    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }
}
