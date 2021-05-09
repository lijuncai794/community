package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.Comment;
import com.lijuncai.learningbbs.entity.DiscussPost;
import com.lijuncai.learningbbs.entity.Page;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.CommentService;
import com.lijuncai.learningbbs.service.DiscussPostService;
import com.lijuncai.learningbbs.service.LikeService;
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

import java.util.*;

/**
 * @description: 帖子控制类
 * @author: lijuncai
 **/
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements LearningBbsConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    /**
     * 新增一个帖子
     *
     * @param title   String 帖子标题
     * @param content String 帖子正文
     * @return String json字符串，代表完成状态
     */
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return LearningBbsUtil.getJSONString(403, "当前用户未登录，无法发帖!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return LearningBbsUtil.getJSONString(0, "发布成功!");
    }

    /**
     * 获取帖子的数据，如标题、正文、评论等
     *
     * @param discussPostId int 帖子id
     * @param model         Model对象
     * @param page          Page 分页对象
     * @return String "帖子详情"的模板路径
     */
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        //获取帖子对象
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        //获取帖子的发布者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //获取当前线程的用户
        model.addAttribute("curUser", hostHolder.getUser());
        //获取帖子的点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        //获取当前用户对此帖的点赞状态,若此时用户是未登录状态，则默认显示未点赞
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);

        //设置分页相关信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        //1.获取评论列表。评论: 给帖子的评论；回复: 给评论的评论
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //用于显示的评论列表。其中把userId换成用户名、用户头像来显示
        List<Map<String, Object>> commentShowList = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                //每个需要显示的评论--> 评论作者：评论信息
                Map<String, Object> commentShow = new HashMap<>();

                //评论的内容
                commentShow.put("comment", comment);
                //评论的作者
                commentShow.put("user", userService.findUserById(comment.getUserId()));
                //评论的点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentShow.put("likeCount", likeCount);
                //评论的点赞状态,若此时用户是未登录状态，则默认显示未点赞
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentShow.put("likeStatus", likeStatus);

                //2.获取回复列表,回复不做分页处理
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //用于显示的回复列表，userId转换为用户名
                List<Map<String, Object>> replyShowList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyShow = new HashMap<>();
                        //回复信息
                        replyShow.put("reply", reply);
                        //作者
                        replyShow.put("user", userService.findUserById(reply.getUserId()));

                        //回复目标(TargetId)，可以回复帖子的评论(=0)，也可以回复评论中某个人的回复(=userId)
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyShow.put("target", target);
                        replyShowList.add(replyShow);
                    }
                }
                commentShow.put("replys", replyShowList);

                //获取回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentShow.put("replyCount", replyCount);

                //将此条评论所包含的存入commentShowList
                commentShowList.add(commentShow);
            }
        }
        //将commentShowList存入model
        model.addAttribute("comments", commentShowList);
        return "/site/discuss-detail";
    }

    /**
     * 删除帖子
     *
     * @param id int 帖子id
     * @return String json字符串，代表完成状态
     */
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDiscussPost(int id) {
        User user = hostHolder.getUser();
        DiscussPost post = discussPostService.findDiscussPostById(id);

        if (user == null || user.getId() != post.getUserId()) {
            return LearningBbsUtil.getJSONString(403, "删帖失败,没有操作权限!");
        }

        discussPostService.deleteDiscussPostById(id);
        return LearningBbsUtil.getJSONString(0, "删除成功!");
    }
}