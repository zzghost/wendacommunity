package com.example.wenda.controller;

import com.example.wenda.model.User;
import freemarker.template.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import freemarker.template.Template;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Controller
public class IndexController {
    @RequestMapping(path={"/", "/index"})
    @ResponseBody
    public String index(){
        return "hello, liuzhou";
    }


    @RequestMapping(path={"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String profile(@PathVariable("userId")int userId, @PathVariable("groupId")int groupId,
                          @RequestParam(value = "type", defaultValue = "1")int type,
                          @RequestParam(value = "key", defaultValue = "z", required = false)String key){
        return String.format("Profile page of %d, %d, type=%d, key=%s", groupId, userId, type, key);
    }

    @RequestMapping(path="/vm", method = {RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("userid","1");
        List<String> colors = Arrays.asList(new String[]{"red", "yellow", "green"});
        model.addAttribute("colors", colors);
        model.addAttribute("user", new User("LIU"));
        return "home";
    }
    @RequestMapping(path="/request", method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletRequest request, HttpServletResponse response){
        StringBuilder sb = new StringBuilder();
        sb.append(request.getCookies());
        return sb.toString();
    }
    @RequestMapping(path="/redirect/{code}", method = {RequestMethod.GET})
    @ResponseBody
    public String redirect(@PathVariable("code") int code, HttpSession httpSession){
        return "redirect:/";
    }


}
