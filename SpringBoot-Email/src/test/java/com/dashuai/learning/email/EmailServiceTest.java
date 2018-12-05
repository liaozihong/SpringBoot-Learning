package com.dashuai.learning.email;

import com.dashuai.learning.email.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest {
    @Autowired
    MailService mailService;

    @Test
    public void sendSimpleMailTest() {
        mailService.sendSimpleMail("15017263512@163.com","test simple mail"," hello this is simple mail");
    }

    @Test
    public void sendHtmlMailTest(){
        String content="<html>\n" +
                "<body>\n" +
                "    <h3>hello world ! 这是一封Html邮件!</h3>\n" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("15017263512@163.com","test html mail",content);
    }

    @Test
    public void sendAttachmentsMail(){
        String filePath="E:\\var\\log\\elkTest\\error\\2018-11-30.log";
        mailService.sendAttachmentsMail("15017263512@163.com", "主题：带附件的邮件", "有附件，请查收！", filePath);
    }

    @Test
    public void sendInlineResourceMail() {
        String rscId = "neo006";
        String content="<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' ></body></html>";
        String imgPath = "C:\\Users\\Admin\\Pictures\\Camera Roll\\9499189867_1476052069.jpg";

        mailService.sendInlineResourceMail("15017263512@163.com", "主题：这是有图片的邮件", content, imgPath, rscId);
    }

    @Test
    public void sendTemplateMail() {
        //创建邮件正文
        Context context = new Context();
        context.setVariable("id", "006");
        mailService.sendTemplateMail("15017263512@163.com","主题：这是模板邮件",
                "emailTemplate",context);
    }
}
