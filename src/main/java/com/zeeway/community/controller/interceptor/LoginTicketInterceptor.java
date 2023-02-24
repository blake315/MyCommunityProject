package com.zeeway.community.controller.interceptor;

import com.zeeway.community.entity.LoginTicket;
import com.zeeway.community.entity.User;
import com.zeeway.community.service.UserService;
import com.zeeway.community.util.CookieUtils;
import com.zeeway.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Thales
 * preHandle:在请求首页之前获取登录状态。先通过CookieUtil工具类获取ticket凭证对象。
 * 之后查询凭证的有效性，根据凭证中的信息获取到用户对象，
 * 同时将用户对象保存在HostHolder中，用来持有用户信息，取代session
 *
 * postHandle：取出Hostholder中保存的用户，将其添加到模板引擎中。
 *
 * afterCompletion:请求结束之后，将HostHolder对象中的用户对象清空。
 *
 *
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtils.getValue(request, "ticket");
        if (ticket != null){
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
            }

        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
       User user = hostHolder.getUser();
       if (user != null && modelAndView != null){
           modelAndView.addObject("loginUser", user);
       }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
