package com.zeeway.community.controller;


import com.zeeway.community.entity.Message;
import com.zeeway.community.entity.Page;
import com.zeeway.community.entity.User;
import com.zeeway.community.service.MessageService;
import com.zeeway.community.service.UserService;
import com.zeeway.community.util.CommunityUtil;
import com.zeeway.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page){
        final User user = hostHolder.getUser();
        //设置分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        //获取会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null){
            for (Message message :
                    conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int target = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(target));
                conversations.add(map);
            }

        }
        model.addAttribute("conversations", conversations);

        //获取未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letters != null){
            for (Message message:
                 letterList) {
                Map<String, Object> letter = new HashMap<>();
                letter.put("letter", message);
                letter.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(letter);
            }
        }
        model.addAttribute("letters", letters);

        model.addAttribute("target", getLetterTarget(conversationId));

        final List<Integer> letterIds = getLetterIds(letterList);
        if (!letterIds.isEmpty()){
            messageService.readMessage(letterIds);
        }


        return "/site/letter-detail";
    }


    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){
        Integer.valueOf("abc");
        final User target = userService.findUserByName(toName);
        if (target == null){
            return CommunityUtil.getJSONString(1,"target user is not exits");

        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    private User getLetterTarget(String conversationId){
        final String[] s = conversationId.split("_");
        int id1 = Integer.parseInt(s[0]);
        int id2 = Integer.parseInt(s[1]);

        if (hostHolder.getUser().getId() == id1){
            return userService.findUserById(id2);
        }else {
            return userService.findUserById(id1);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if (letterList != null){
            for (Message message :
                    letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
                }
        }

        return ids;
    }
}
