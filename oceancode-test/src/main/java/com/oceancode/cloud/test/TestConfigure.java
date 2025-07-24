package com.oceancode.cloud.test;

import com.oceancode.cloud.common.util.ComponentUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class TestConfigure {
    @Autowired
    private Environment environment;
    @Autowired
    private ApplicationContext applicationContext;

    static {
        System.setProperty("spring.profiles.active", "test");
    }

    @PostConstruct
    public void init(){
        ComponentUtil.setApplicationContext(applicationContext);
    }
}
