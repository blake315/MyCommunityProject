package com.zeeway.community.controller;

import com.google.code.kaptcha.Producer;
import com.zeeway.community.entity.User;
import com.zeeway.community.service.UserService;
import com.zeeway.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> register = userService.register(user);
        if (register == null || register.isEmpty()){
            model.addAttribute("msg","注册成功，已经向您的邮箱发送了一封激活邮件，请您尽快激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg",register.get("usernameMsg"));
            model.addAttribute("passwordMsg",register.get("passwordMsg"));
            model.addAttribute("emailMsg",register.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int res = userService.activation(userId, code);
        if (res == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用");
            model.addAttribute("target", "/login");
        }else if ( res == ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已激活");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg","激活失败，提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //init checkcode
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        session.setAttribute("kaptcha",text);

        response.setContentType("image/png");
        try {
            OutputStream stream = response.getOutputStream();
            ImageIO.write(image,"png",stream);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }


}
