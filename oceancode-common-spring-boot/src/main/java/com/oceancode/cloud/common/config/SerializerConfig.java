package com.oceancode.cloud.common.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.oceancode.cloud.api.mq.MessageType;
import com.oceancode.cloud.api.session.UserType;
import com.oceancode.cloud.chart.ChartMessageType;
import com.oceancode.cloud.chart.MessageLifeCycle;
import com.oceancode.cloud.common.util.JsonUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SerializerConfig {

    @PostConstruct
    public void init() {
        JsonUtil.registerTypeEnum(UserType.class);
        JsonUtil.registerTypeEnum(MessageType.class);
        JsonUtil.registerTypeEnum(ChartMessageType.class);
        JsonUtil.registerTypeEnum(MessageLifeCycle.class);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializerByType(Long.class, ToStringSerializer.instance);
        };
    }
}
