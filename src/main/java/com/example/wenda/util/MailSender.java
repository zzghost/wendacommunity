package com.example.wenda.util;

import com.example.wenda.model.HostHolder;
import com.sun.javafx.tools.ant.DeployFXTask;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Service
public class MailSender implements InitializingBean{
    private static Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;
    private FreeMarkerConfigurer freeMarkerConfigurer=null;
    @Autowired
    HostHolder hostHolder;

    public boolean sendWithHTMLTemplate(String to, String subject, Template template, Map<String, Object> model){
        try{
            String nick = MimeUtility.encodeText("知否");
            InternetAddress from = new InternetAddress(nick + "<zhou.liu@pku.edu.cn>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(result, true);
            mailSender.send(mimeMessage);
            return true;
        }catch(Exception e){
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("dsmylv@163.com");
        mailSender.setPassword("mirage1992712!");
        mailSender.setHost("smtp.163.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
