package com.zeeway.community.controller;

import com.zeeway.community.entity.DiscussPost;
import com.zeeway.community.entity.User;
import com.zeeway.community.service.DiscussPostService;
import com.zeeway.community.service.UserService;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author Thales
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = holder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403,"you didn't login yet!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "post done!");
    }


    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model){
        DiscussPost discussPostById = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", discussPostById);
        User userById = userService.findUserById(discussPostById.getUserId());
        model.addAttribute("user", userById);


        return "site/discuss-detail";
    }
}
