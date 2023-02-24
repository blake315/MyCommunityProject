package com.zeeway.community.util;

import com.zeeway.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author Thales
 * 持有用户信息的容器，用来代替session对象
 */

@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
