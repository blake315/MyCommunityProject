package com.zeeway.community.controller;

import com.zeeway.community.entity.Comment;
import com.zeeway.community.entity.DiscussPost;
import com.zeeway.community.entity.Page;
import com.zeeway.community.entity.User;
import com.zeeway.community.service.CommentService;
import com.zeeway.community.service.DiscussPostService;
import com.zeeway.community.service.LikeService;
import com.zeeway.community.service.UserService;
import com.zeeway.community.util.CommunityConstant;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author Thales
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

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
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page){
        //帖子
        DiscussPost discussPostById = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", discussPostById);
        //作者
        User userById = userService.findUserById(discussPostById.getUserId());
        model.addAttribute("user", userById);
        //点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", likeCount);
        //点赞状态
        int likeStatus = holder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(holder.getUser().getId(), ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);

        //分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(discussPostById.getCommentCount());

        /**
         * 这一段分别处理帖子的评论还有对于评论的回复
         *
         */

        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPostById.getId(), page.getFrom(), page.getLimit());

        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (commentList != null){
            for (Comment comment :
                    commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //评论的点赞数和点赞状态
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                likeStatus = holder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(holder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus);


                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply :
                            replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        User targetUser = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", targetUser);
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        likeStatus = holder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(holder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                //获取回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "site/discuss-detail";
    }
}
