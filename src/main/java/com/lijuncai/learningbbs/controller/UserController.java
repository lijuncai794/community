package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.annotaion.LoginRequired;
import com.lijuncai.learningbbs.entity.DiscussPost;
import com.lijuncai.learningbbs.entity.Page;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.DiscussPostService;
import com.lijuncai.learningbbs.service.FollowService;
import com.lijuncai.learningbbs.service.LikeService;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 处理用户相关请求
 * @author: lijuncai
 **/
@Controller
@RequestMapping("/user")
public class UserController implements LearningBbsConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${learning-bbs.path.upload}")
    private String uploadPath;

    @Value("${learning-bbs.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 访问"账号设置"页面
     *
     * @return String "账号设置"页面模板路径
     */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 更新用户头像
     *
     * @param headerImage MultipartFile 新的头像文件
     * @param model       Model对象
     * @return String 页面模板路径
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || !suffix.equals(".png") || !suffix.equals(".jpg")) {
            model.addAttribute("error", "图片的格式不正确，仅支持png和jpg!");
            return "/site/setting";
        }
        //生成唯一的随机文件名
        fileName = LearningBbsUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //更新当前用户的头像的路径(web访问路径)
        //http://localhost:8080/learning-bbs/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 获取用户头像
     *
     * @param fileName String 头像文件名
     * @param response HttpServletResponse对象
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int data = 0;
            while ((data = fis.read(buffer)) != -1) {
                os.write(buffer, 0, data);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     *
     * @param oldPassword String 原密码
     * @param newPassword String 新密码
     * @param model       Model对象
     * @return String 页面模板路径
     */
    @LoginRequired
    @RequestMapping(path = "/update-password", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        int status = userService.updatePassword(oldPassword, newPassword);
        if (status == -1) {
            model.addAttribute("password_error", "原密码不正确!");
            return "/site/setting";
        }
        model.addAttribute("msg", "密码修改成功!");
        model.addAttribute("target", "/index");
        return "/site/operate-result";
    }

    /**
     * 访问用户主页
     *
     * @param userId int 用户id
     * @param model  Model对象
     * @return String "用户主页"模板路径
     */
    @LoginRequired
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        //根据id查询用户
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("访问用户主页失败，该用户不存在!");
        }
        model.addAttribute("user", user);

        //获取用户被赞的次数
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        //获取关注了多少用户
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //获取粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //是否已关注此用户
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

    /**
     * 访问"我的帖子"
     *
     * @param userId int 用户id
     * @param model  Model对象
     * @return String "我的帖子"模板路径
     */
    @LoginRequired
    @RequestMapping(path = "/profile/{userId}/posts", method = RequestMethod.GET)
    public String getUserPosts(@PathVariable("userId") int userId, Model model, Page page) {
        //根据id查询用户
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("访问用户主页失败，该用户不存在!");
        }

        //设置分页信息
        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/user/profile/" + userId + "/posts");

        //获取指定用户的所有的帖子
        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (discussPostList != null) {
            for (DiscussPost discussPost : discussPostList) {
                //每个HashMap中存放的是帖子数据和帖子点赞的数据
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                //获取帖子点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        //将discussPosts添加至model
        model.addAttribute("discussPosts", discussPosts);
        //将当前主页的所属用户添加至model
        model.addAttribute("user", user);
        //将当前主页的所属用户添加至model
        model.addAttribute("postCount", discussPostList.size());

        return "/site/my-post";
    }
}
