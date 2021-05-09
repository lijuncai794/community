package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.Comment;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.CommentService;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @description: 评论控制类
 * @author: lijuncai
 **/

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 发表评论
     *
     * @param discussPostId int 帖子id
     * @param comment       Comment对象
     * @return String "帖子详情"的模板路径
     */
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }

    /**
     * 删除评论
     *
     * @param id int 评论id
     * @return String json字符串，代表完成状态
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDiscussPost(int id) {
        User user = hostHolder.getUser();
        int userId = commentService.selectUserIdById(id);

        if (user == null || user.getId() != userId) {
            return LearningBbsUtil.getJSONString(403, "删帖失败,没有操作权限!");
        }

        commentService.deleteCommentById(id);
        return LearningBbsUtil.getJSONString(0, "删除成功!");
    }

}
