package com.zeeway.community.controller;


import com.zeeway.community.entity.User;
import com.zeeway.community.service.LikeService;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/like")
    @ResponseBody
    public String like(int entityType, int entityId){
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0,null,map);

    }

}
