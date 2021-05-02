package com.lijuncai.learningbbs;

import com.lijuncai.learningbbs.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @description: MailClient的测试类
 * @author: lijuncai
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = LearningBbsApplication.class)
public class MailClientTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendTextMail() {
        mailClient.sendMail("ef9rnt1j@meantinc.com", "hello spring mail", "test mail");
    }

    @Test
    public void testSendHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("ijfyq4gt@chapedia.org", "HTML Mail", content);
    }
}
