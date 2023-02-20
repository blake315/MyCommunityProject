package com.zeeway.community.controller;

import com.zeeway.community.service.AlphaService;
import com.zeeway.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring boot from zeeway";
    }


    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }


    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try (
                PrintWriter writer = response.getWriter();
                ){
            writer.write("<h1>nowcoder</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //处理GET请求，向服务器获取数据

    // /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit){
        System.out.println("current is " + current);
        System.out.println("limit is " + limit);
        return "some students";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println("student id is " + id);


        return "a student";
    }


    //处理post请求，向服务器提交数据
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println("name is " + name);
        System.out.println("age is " + age);
        return "保存成功";
    }


    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "jake");
        mav.addObject("age", "30");
        mav.setViewName("/demo/view");
        return mav;
    }


    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","广东药科大学");
        return "/demo/view";
    }

    //响应JSON数据 异步请求  Java对象 --> JSON字符串 --> JS对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String , Object> getEmp(){
        Map<String , Object> emp = new HashMap<>();
        emp.put("name","mile");
        emp.put("age",21);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getAllEmp(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String , Object> emp = new HashMap<>();
        emp.put("name","mike");
        emp.put("age",21);
        emp.put("salary", 8000.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name","tony");
        emp.put("age",35);
        emp.put("salary", 18000.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name","jordan");
        emp.put("age",36);
        emp.put("salary", 985200);
        list.add(emp);
        return list;
    }



    //cookie demo示例

    @RequestMapping(path = "/cookie/set" , method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        //init cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
       cookie.setPath("/community/alpha");
       cookie.setMaxAge(60*10);
       response.addCookie(cookie);

        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "already get the cookie code";
    }

    @RequestMapping(path = "/session/set" , method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        //init session
        session.setAttribute("id","101");
        session.setAttribute("name","setSession");


        return "server is already set your session";
    }

    @RequestMapping(path = "/session/get" , method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        //init session
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));


        return "we are trying to get your session";
    }



}
