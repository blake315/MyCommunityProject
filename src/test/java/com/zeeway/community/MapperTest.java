package com.zeeway.community;

import com.zeeway.community.dao.DiscussPostMapper;
import com.zeeway.community.dao.LoginTicketMapper;
import com.zeeway.community.dao.UserMapper;
import com.zeeway.community.entity.DiscussPost;
import com.zeeway.community.entity.LoginTicket;
import com.zeeway.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("zhangfei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder102@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("zeeway");
        user.setPassword("wsmzw31532");
        user.setSalt("abc");
        user.setEmail("zeeway@163.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateStatus(165, 1);
        System.out.println(rows);
        rows = userMapper.updateHeader(165,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
        rows = userMapper.updatePassword(165,"wsmzw");
        System.out.println(rows);
    }
    
    
    @Test
    public void testSelectPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(165, 0, 10);
        for (DiscussPost post: discussPosts
             ) {
            System.out.println(post);
        }

        int i = discussPostMapper.selectDiscussPostRows(165);
        System.out.println(i);
    }

    @Test
    public void testLoginTicketTest(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("zeeway");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectAndUpdateLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("zeeway");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("zeeway",1);
        loginTicket = loginTicketMapper.selectByTicket("zeeway");
        System.out.println(loginTicket);
    }


}
