package com.zeeway.community.service;

import com.zeeway.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;


    public AlphaService(){
        System.out.println("实例化 AlphaService");
    }

    @PostConstruct
    public void init(){
        System.out.println("init AlphaService");
    }

    @PreDestroy
    public void destory(){
        System.out.println("destory AlphaService");
    }


    public String find(){
        return alphaDao.select();
    }
}
