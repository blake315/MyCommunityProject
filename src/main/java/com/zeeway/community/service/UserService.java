package com.zeeway.community.service;

import com.zeeway.community.dao.LoginTicketMapper;
import com.zeeway.community.dao.UserMapper;
import com.zeeway.community.entity.LoginTicket;
import com.zeeway.community.entity.User;
import com.zeeway.community.util.CommunityConstant;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.MailClient;
import com.zeeway.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id){
        //return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null){
           user = initCache(id);
        }
        return user;
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mailClient.sendMail(user.getEmail(), "激活社区账户",content);
            }
        });
        thread.start();


        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //进行对空值特殊情况的判断
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "username can not be null!");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "password can not be null!");
            return map;
        }

        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "your username is not exist");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg", "your account is not active");
            return map;
        }
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg", "password error");
            return map;
        }

        //登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        //loginTicketMapper.insertLoginTicket(loginTicket);

        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);


        map.put("ticket", loginTicket.getTicket());

        return map;
    }


    public Map<String, Object> getCode(String email){
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(email)){
            map.put("exitMsg", "email can not be null!");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null){
            map.put("exitMsg", "your email account are not sign up!");
            return map;
        }
        String code = CommunityUtil.generateUUID().substring(0,4);
        Context context = new Context();
        context.setVariable("email" , user.getEmail());
        context.setVariable("code", code);
        String content = templateEngine.process("/mail/forget", context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mailClient.sendMail(user.getEmail(), "社区重置密码", content);
            }
        });
        thread.start();
        map.put("code", code);
        return map;
    }

    public void logout(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }

    public boolean resetPassword(String email, String password){
        User user = userMapper.selectByEmail(email);
        String salt = user.getSalt();
        password = CommunityUtil.md5(password + salt);
        int i = userMapper.updatePassword(user.getId(), password);
        return i==1;


    }

    public LoginTicket getLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }


    public int updateHeader(int userId, String headerUrl){
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }


    public Map<String, Object> updatePassword(User user, String oldPassword, String confirmPassword, String newPassword){
        Map<String, Object> map = new HashMap<>();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)){
            map.put("oldMsg", "the old password that you type in is wrong!");
            return map;
        }
        if (newPassword.length() < 8){
            map.put("newMsg", "your new password too short!");
            return map;
        }
        if (!newPassword.equals(confirmPassword)){
            map.put("confirmMsg", "different password!");
            return map;
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);
        clearCache(user.getId());
        return map;

    }

    public User findUserByName(String username){
        return userMapper.selectByName(username);
    }

    private User getCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    private User initCache(int userId){
        final User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,60, TimeUnit.MINUTES);
        return user;
    }

    private void clearCache(int userId){
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
