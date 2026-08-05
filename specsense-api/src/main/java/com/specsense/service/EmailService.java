package com.specsense.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:info@specsense.com}")
    private String fromEmail;

    public void sendContactInquiry(String name, String email, String company, String product, String message) {
        logger.info("【邮件发送】开始发送询价邮件 - 发件人: {}, 邮箱: {}, 公司: {}", name, email, company);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(fromEmail);
            helper.setSubject("【官网询价】来自 " + name + " 的询价请求");

            String htmlContent = buildInquiryEmailContent(name, email, company, product, message);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            logger.info("【邮件发送】成功发送 HTML 格式邮件到: {}", fromEmail);
        } catch (MessagingException e) {
            logger.error("【邮件发送】HTML邮件发送失败，尝试发送纯文本邮件: {}", e.getMessage());
            // Fallback to simple text email
            sendSimpleEmail(name, email, company, product, message);
        } catch (Exception e) {
            logger.error("【邮件发送】邮件发送异常: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void sendSimpleEmail(String name, String email, String company, String product, String message) {
        try {
            SimpleMailMessage messageObj = new SimpleMailMessage();
            messageObj.setFrom(fromEmail);
            messageObj.setTo(fromEmail);
            messageObj.setSubject("【官网询价】来自 " + name + " 的询价请求");
            messageObj.setText(buildInquiryTextContent(name, email, company, product, message));
            mailSender.send(messageObj);
            logger.info("【邮件发送】成功发送纯文本格式邮件到: {}", fromEmail);
        } catch (Exception e) {
            logger.error("【邮件发送】纯文本邮件发送也失败了: {}", e.getMessage(), e);
        }
    }

    private String buildInquiryEmailContent(String name, String email, String company, String product, String message) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2 style='color: #2563eb;'>官网询价请求</h2>");
        html.append("<table style='border-collapse: collapse; width: 100%; max-width: 600px;'>");
        html.append("<tr><td style='padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f3f4f6;'>姓名</td>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(name).append("</td></tr>");
        html.append("<tr><td style='padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f3f4f6;'>邮箱</td>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'><a href='mailto:").append(email).append("'>").append(email).append("</a></td></tr>");
        html.append("<tr><td style='padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f3f4f6;'>公司</td>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(company != null ? company : "-").append("</td></tr>");
        html.append("<tr><td style='padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f3f4f6;'>感兴趣的产品</td>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(product != null ? product : "-").append("</td></tr>");
        html.append("<tr><td style='padding: 10px; border: 1px solid #ddd; font-weight: bold; background-color: #f3f4f6; vertical-align: top;'>询价内容</td>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(message).append("</td></tr>");
        html.append("</table>");
        html.append("<p style='color: #6b7280; font-size: 12px; margin-top: 20px;'>此邮件由官网询价表单自动发送</p>");
        html.append("</body></html>");
        return html.toString();
    }

    private String buildInquiryTextContent(String name, String email, String company, String product, String message) {
        StringBuilder text = new StringBuilder();
        text.append("官网询价请求\n");
        text.append("========================\n\n");
        text.append("姓名: ").append(name).append("\n");
        text.append("邮箱: ").append(email).append("\n");
        text.append("公司: ").append(company != null ? company : "-").append("\n");
        text.append("感兴趣的产品: ").append(product != null ? product : "-").append("\n");
        text.append("询价内容:\n").append(message).append("\n\n");
        text.append("此邮件由官网询价表单自动发送");
        return text.toString();
    }
}
