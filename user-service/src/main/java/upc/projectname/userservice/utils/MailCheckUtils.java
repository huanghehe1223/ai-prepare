package upc.projectname.userservice.utils;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MailCheckUtils {
    @Resource
    private JavaMailSender mailSender;

    private String from="3145267275@qq.com";

    public boolean sendHtmlEmail(String subject, String checkCode, String to) {
        try {
            String content="<!DOCTYPE html>" +
                    "<html lang='zh-CN'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>邮箱验证码</title>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                    ".container { width: 100%; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }" +
                    ".header { background-color: #007BFF; color: #ffffff; padding: 10px 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
                    ".content { padding: 20px; }" +
                    ".footer { text-align: center; padding: 20px; font-size: 12px; color: #aaaaaa; border-top: 1px solid #eeeeee; }" +
                    ".verification-code { font-size: 24px; font-weight: bold; color: #333333; margin: 20px 0; text-align: center; }" +
                    ".cta-button { display: block; width: 200px; margin: 20px auto; padding: 10px; text-align: center; color: #ffffff; background-color: #28a745; text-decoration: none; border-radius: 5px; }" +
                    ".cta-button:hover { background-color: #218838; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>欢迎注册我们的网站</h1>" +
                    "</div>" +
                    "<div class='content'>" +
                    "<p>亲爱的用户，</p>" +
                    "<p>感谢您注册我们的网站。您的验证码如下：</p>" +
                    "<div class='verification-code'>" + checkCode + "</div>" +
                    "<p>请在注册页面输入此验证码以完成注册。</p>" +
                    "<p>感谢您的支持！</p>" +
                    "<a href='https://yourwebsite.com/verify' class='cta-button'>立即验证</a>" +
                    "</div>" +
                    "<div class='footer'>" +
                    "<p>&copy; 2024 快客. 保留所有权利。</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            // 创建邮件消息
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            // 设置收件人
            helper.setTo(to);
            // 设置邮件主题
            helper.setSubject(subject);
            // 设置邮件内容
            helper.setText(content, true);

            // 发送邮件
            mailSender.send(mimeMessage);

            log.info("发送邮件成功");
            return true;

        } catch (MailException e) {
            log.error("发送邮件失败: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("发送邮件时发生未知错误: {}", e.getMessage());
            return false;
        }
    }

}
