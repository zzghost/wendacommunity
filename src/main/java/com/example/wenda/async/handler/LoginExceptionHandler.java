package com.example.wenda.async.handler;


import com.example.wenda.async.EventHandler;
import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventType;
import com.example.wenda.util.MailSender;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
        FreeMarkerConfigurer freeMarkerConfigurer = null;
        Template tpl = freeMarkerConfigurer.getConfiguration().getTemplate("mails/login_exception.html");
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登陆IP异常", tpl, map);
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.LOGIN);
    }
}
