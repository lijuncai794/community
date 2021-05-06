package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.Message;
import com.lijuncai.learningbbs.entity.Page;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.MessageService;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.HostHolder;
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
 * @description: 消息控制类
 * @author: lijuncai
 **/
@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 获取用户所有的会话
     *
     * @param model model对象
     * @param page  分页对象
     * @return 会话列表模板的路径
     */
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //获取会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                //在会话列表页面，每个会话只显示一条最新的消息
                map.put("conversation", message);

                //查询此会话总共有多少条消息、多少条未读消息
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                //判断当前会话是谁发起的，用于确定显示哪个用户的头像
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        //查询未读消息的数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    /**
     * 用户单个会话的详情，显示会话里面的多条消息
     *
     * @param conversationId 会话id
     * @param page           分页对象
     * @param model          model对象
     * @return 会话详情模板地址
     */
    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //获取私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));

                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("curUserId", hostHolder.getUser().getId());
        //存入私信的发送者
        model.addAttribute("target", getLetterTarget(conversationId));

        //打开会话的详情页面之后，将当前页面的所有私信都设置已读状态
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 获取私信的发送者
     *
     * @param conversationId 会话id
     * @return 私信的发送者User对象
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    /**
     * 获取私信列表的id列表
     *
     * @param letterList 私信列表
     * @return 私信的id列表
     */
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                //判断会话详情页面中私信的接收者是否为当前用户，且当前私信的状态是未读
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    /**
     * 给用户发送私信
     *
     * @param toName  接收私信的用户名
     * @param content 私信内容
     * @return
     */
    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return LearningBbsUtil.getJSONString(1, "发送失败，目标用户不存在!");
        }

        //构建Message对象
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());

        //会话id由通讯双方的用户id拼成：id1_id2,默认将更小的id拼在前面,更大的id拼在后面
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());

        messageService.addMessage(message);

        return LearningBbsUtil.getJSONString(0);
    }
}
