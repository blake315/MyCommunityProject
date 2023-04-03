package com.zeeway.community.controller;

import com.zeeway.community.entity.Page;
import com.zeeway.community.entity.User;
import com.zeeway.community.service.FollowService;
import com.zeeway.community.service.UserService;
import com.zeeway.community.util.CommunityConstant;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0,"followed!");
    }

    @PostMapping(path = "/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0,"unfollowed!");
    }

    @GetMapping(path = "/followees/{userId}")
    public String getFollowee(@PathVariable("userId") int userId, Page page, Model model){
        final User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("user is not exit");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId,CommunityConstant.ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowee(userId, page.getOffset(), page.getLimit());
        if (userList != null){
            for (Map<String, Object> map: userList){
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }

        model.addAttribute("users", userList);

        return "/site/followee";

    }


    @GetMapping(path = "/followers/{userId}")
    public String getFollower(@PathVariable("userId") int userId, Page page, Model model){
        final User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("user is not exit");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollower(userId, page.getOffset(), page.getLimit());
        if (userList != null){
            for (Map<String, Object> map: userList){
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }

        model.addAttribute("users", userList);

        return "/site/follower";

    }


    private boolean hasFollowed(int userId){
        if (hostHolder.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }
}
