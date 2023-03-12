package com.zeeway.community;


import com.zeeway.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;


    @Test
    public void testFilter(){
        String text = "这里可★以嫖★娼，这里★可以吸★毒嘛，这里就★是乐园！！！";
        final String filter = sensitiveFilter.Filter(text);
        System.out.println(filter);

    }


}
