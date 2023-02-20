package com.zeeway.community.service;

import com.zeeway.community.dao.UserMapper;
import com.zeeway.community.entity.User;
import com.zeeway.community.util.CommunityConstant;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }


    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        //对输入的账号密码等注册信息进行判断
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","your account can not be blank");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","your password can not be blank");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","your email can not be blank");
            return map;
        }

        //验证邮箱和账号是否已经存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("usernameMsg" , "the account is already exist");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg", "the email is already exist");
            return map;
        }

        // 判断完成，开始正式注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //最后发送用户的激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());

        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(), "激活社区账户",content);


        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

}
