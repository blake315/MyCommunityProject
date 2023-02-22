package com.zeeway.community.controller;

import com.zeeway.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class ForgetController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @RequestMapping(path = "/forget", method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }

    @RequestMapping(path = "/code/{email}", method = RequestMethod.GET)
    public String getCheckCode(@PathVariable String email, HttpSession session, Model model){
        Map<String, Object> map = userService.getCode(email);
        if (map.containsKey("code")){
            session.setAttribute("code", map.get("code"));
            session.setAttribute("email", email);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                      try {
                          Thread.sleep(1000*60*5);
                      }catch (InterruptedException e){
                          e.printStackTrace();
                      }
                      session.removeAttribute("code");
                }
            });
        }else {
            model.addAttribute("exitMsg", map.get("exitMsg"));
        }
        return "/site/forget";
    }


    @RequestMapping(path = "/forget", method = RequestMethod.POST)
    public String forget(String email, String code, String password, HttpSession session, Model model){
        if (StringUtils.isBlank(password)){
            model.addAttribute("passwordMsg", "your reset password can not be null!");
            return "/site/forget";
        }
        String code1 = (String) session.getAttribute("code");
        String email1 = (String) session.getAttribute("email");
        if (StringUtils.isBlank(code1) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(code1)){
            model.addAttribute("codeMsg", "your Check code input error");
            return "/site/forget";
        }
        if (email1.equals(email)){
            if (userService.resetPassword(email1, password)){
                session.removeAttribute("code");
                return "forward:/logout";
            }else {
                model.addAttribute("exitMsg", "your email account are not sign up!");
                return "/site/forget";
            }

        }else {
            model.addAttribute("exitMsg", "your email account changed");
            return "/site/forget";
        }
    }

}
