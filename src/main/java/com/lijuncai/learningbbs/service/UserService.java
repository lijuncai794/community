package com.lijuncai.learningbbs.service;

import com.lijuncai.learningbbs.dao.LoginTicketMapper;
import com.lijuncai.learningbbs.dao.UserMapper;
import com.lijuncai.learningbbs.entity.LoginTicket;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import com.lijuncai.learningbbs.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description: User的服务类
 * @author: lijuncai
 **/
@Service
public class UserService implements LearningBbsConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private HostHolder hostHolder;

    //项目域名
    @Value("${learning-bbs.path.domain}")
    private String domain;

    //项目名
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 根据用户id查询
     *
     * @param id 用户id
     * @return User对象
     */
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注册
     *
     * @param user 用户对象
     * @return 注册流程的状态信息
     */
    public Map<String, Object> register(User user) {
        // Map用于存放注册时的各类状态信息
        Map<String, Object> registerStatusMap = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            registerStatusMap.put("usernameMsg", "用户名不能为空!");
            return registerStatusMap;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            registerStatusMap.put("passwordMsg", "密码不能为空!");
            return registerStatusMap;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            registerStatusMap.put("emailMsg", "邮箱不能为空!");
            return registerStatusMap;
        }

        // 验证用户名是否已经存在
        User tempUser = userMapper.selectByName(user.getUsername());
        if (tempUser != null) {
            registerStatusMap.put("usernameMsg", "该用户名已存在!");
            return registerStatusMap;
        }
        // 验证邮箱
        tempUser = userMapper.selectByEmail(user.getEmail());
        if (tempUser != null) {
            registerStatusMap.put("emailMsg", "该邮箱已被注册!");
            return registerStatusMap;
        }

        //添加user对象的其他信息

        //生成uuid，获取前5位作为salt，再将密码+salt进行md5加密
        user.setSalt(LearningBbsUtil.generateUUID().substring(0, 5));
        user.setPassword(LearningBbsUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(LearningBbsUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //设置email参数
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        //设置url参数，模板:http://localhost:9000/learning-bbs/activation/{id}/{code}
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);

        //发送激活邮件
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "学习社区-账号激活", content);

        return registerStatusMap;
    }

    /**
     * 用户激活
     *
     * @param userId 用户id
     * @param code   激活码
     * @return 激活状态
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            //若该userId的用户不存在，则返回“激活失败”
            return ACTIVATION_FAILURE;
        } else if (user.getStatus() == 1) {
            //若该用户已经激活，则返回“重复激活”
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            //若该用户未激活且激活码正确，则返回“激活成功”
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }
        return ACTIVATION_FAILURE;
    }

    /**
     * 用户登录
     *
     * @param username       用户名
     * @param password       密码
     * @param expiredSeconds 登录有效期(秒)
     * @return 登录流程的状态信息
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "用户名不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "此用户不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "此用户未激活，请激活后再登录!");
            return map;
        }


        // 验证密码
        password = LearningBbsUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(LearningBbsUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录状态:将登录凭证设置为失效状态(status=1)
     *
     * @param ticket 登录凭证
     */
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 获取登录凭证对象
     *
     * @param ticket 登录凭证字符串
     * @return 对应的登录凭证对象
     */
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 更新用户头像
     *
     * @param userId    用户id
     * @param headerUrl 新头像的url
     * @return 受影响的行
     */
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    /**
     * 修改用户密码
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return int, 表示修改是否成功[-1代表失败，其他代表成功]
     */
    public int updatePassword(String oldPassword, String newPassword) {
        //通过hostHolder获取当前user对象
        User user = hostHolder.getUser();
        String passwordMd5 = LearningBbsUtil.md5(oldPassword + user.getSalt());
        //验证密码,若原密码不正确则返回-1
        if (!user.getPassword().equals(passwordMd5)) {
            return -1;
        }
        newPassword = LearningBbsUtil.md5(newPassword + user.getSalt());
        return userMapper.updatePassword(user.getId(), newPassword);
    }
}
