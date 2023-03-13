package com.zeeway.community.service;


import com.zeeway.community.dao.DiscussPostMapper;
import com.zeeway.community.entity.DiscussPost;
import com.zeeway.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;


    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }


    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
            throw new IllegalArgumentException("discussPost is null!");

        }
        /**
         * 第一步：对帖子的标题和内容中可能出现的特殊字符（如html字符）进行转义，保证可以正常显示
         * 第二步：使用上一次编写的敏感词过滤器，对标题和内容中的敏感词汇进行过滤
         */
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        discussPost.setTitle(sensitiveFilter.Filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.Filter(discussPost.getContent()));



        return discussPostMapper.insertDiscussPost(discussPost);
    }



    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }



}
