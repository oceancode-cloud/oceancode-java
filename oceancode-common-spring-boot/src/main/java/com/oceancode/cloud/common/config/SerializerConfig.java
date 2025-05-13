package com.oceancode.cloud.common.config;

import com.oceancode.cloud.api.session.UserType;
import com.oceancode.cloud.common.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializerConfig {

    @PostConstruct
    public void init() {
        JsonUtil.registerTypeEnum(UserType.class);
    }
}
