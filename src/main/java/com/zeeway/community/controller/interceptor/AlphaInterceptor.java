package com.zeeway.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Thales
 * preHandle:在调用controller之前执行
 * postHandle:在调用controller之后执行
 */
@Component
public class AlphaInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AlphaInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.debug("preHandle: " + handler.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOGGER.debug("postHandle: " + handler.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LOGGER.debug("afterCompletion: "+ handler.toString());
    }
}
