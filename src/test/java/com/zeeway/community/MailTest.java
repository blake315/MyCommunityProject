package com.zeeway.community;


import com.zeeway.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;


    @Test
    public void testTextMail(){
        mailClient.sendMail("1455975369@qq.com","TEST", "hello from zeeway");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","zeeway");
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);
        mailClient.sendMail("1455975369@qq.com","HTML", process);

    }
}
