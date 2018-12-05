### SpringBoot 集成 E-mail发送邮件
JDK本身有自带发送邮件api，加上SpringBoot在进行封装，使得现在使用起来十分快速简洁。
话不多说，参考纯洁的微笑博客，更改jar版本为2.0.4 开干，基本没什么坑。  
就是配置邮箱账号密码是，如果是qq邮箱，需要开启PO30和STMP服务,并且获取临时授权码。  
开启服务链接：  

    https://mail.qq.com/cgi-bin/frame_html?sid=a5ZSbreeNm9pHyl1&r=a83225170e94773c650a460c10f7a05c  
![](https://ws1.sinaimg.cn/large/006mOQRagy1fxuse0a6syj30s007x0tt.jpg)

### 与Springboot集成
**导入jar包**
```gradle
compile group: 'org.springframework.boot', name: 'spring-boot-starter-mail', version: '2.0.4.RELEASE'
compile 'org.springframework.boot:spring-boot-starter-thymeleaf:2.0.4.RELEASE'
```
配置邮箱
```
#邮箱服务器地址，各大运营商不同
spring.mail.host=smtp.qq.com
#用户名
spring.mail.username=9118542413@qq.com
#密码，如果是qq的，要申请临时授权码
spring.mail.password=faw124awfawfawg
spring.mail.default-encoding=UTF-8
#以谁来发送邮件
mail.fromMail.addr=9118542413@qq.com
```
**发送各种类型的邮件**
```java
@Service
@Slf4j
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    /**
     * The Template engine.
     */
    @Autowired
    TemplateEngine templateEngine;
    @Value("${mail.fromMail.addr}")
    private String from;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
            log.info("简单邮件已经发送。");
        } catch (Exception e) {
            log.error("发送简单邮件时发生异常！", e);
        }
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("html邮件发送成功");
        } catch (MessagingException e) {
            log.error("发送html邮件时发生异常！", e);
        }
    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            helper.addAttachment(fileName, file);

            mailSender.send(message);
            log.info("带附件的邮件已经发送。");
        } catch (MessagingException e) {
            log.error("发送带附件的邮件时发生异常！", e);
        }
    }

    @Override
    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            mailSender.send(message);
            log.info("嵌入静态资源的邮件已经发送。");
        } catch (MessagingException e) {
            log.error("发送嵌入静态资源的邮件时发生异常！", e);
        }
    }

    @Override
    public void sendTemplateMail(String to, String subject, String template, Context context) {
        String emailContent = templateEngine.process(template, context);
        sendHtmlMail("15017263512@163.com", "主题：这是模板邮件", emailContent);
    }
}
```
**测试类：**
```java
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
```
上面的邮箱和密码是我乱填的，注意自己更改。  

项目源码以上传至GitHub：https://github.com/liaozihong/SpringBoot-Learning/tree/master/SpringBoot-Email  