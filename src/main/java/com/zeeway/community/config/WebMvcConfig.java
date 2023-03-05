package com.zeeway.community.config;

import com.zeeway.community.controller.interceptor.LoginRequitedInterceptor;
import com.zeeway.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Thales
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequitedInterceptor loginRequitedInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequitedInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.jpg","/**/*.jpeg");
    }


}
