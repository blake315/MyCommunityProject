package com.zeeway.community.dao;


import com.zeeway.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author Thales
 * 在这个mapper中，使用了MyBaits的注解完成sql语句
 * 优点是简洁，不用再写多一个xml文件   缺点是代码不方便阅读和理解
 */
@Mapper
public interface LoginTicketMapper {
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);
    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);
}
