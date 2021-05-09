package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.DiscussPost;
import com.lijuncai.learningbbs.entity.Page;
import com.lijuncai.learningbbs.service.DiscussPostService;
import com.lijuncai.learningbbs.service.LikeService;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 搜索控制类
 * @author: lijuncai
 **/
@Controller
public class SearchController implements LearningBbsConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    /**
     * 根据关键词搜索帖子
     * 访问路径的格式: /search?keyword=xxx
     *
     * @param keyword String 关键词
     * @param page    Page 分页对象
     * @param model   Model对象
     * @return String "搜索结果"页面模板路径
     */
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        //搜索帖子
        List<DiscussPost> searchResult = discussPostService.search(keyword, page.getOffset(), page.getLimit());
        //聚合数据,包含帖子数据、作者信息、获赞数量
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                //帖子数据
                map.put("post", post);
                //作者信息
                map.put("user", userService.findUserById(post.getUserId()));
                //获赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }

        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        //设置分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : discussPostService.selectSearchTotal(keyword));

        return "/site/search";
    }
}
