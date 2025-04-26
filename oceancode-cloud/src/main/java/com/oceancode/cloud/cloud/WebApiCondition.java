package com.oceancode.cloud.cloud;


import com.oceancode.cloud.api.ApiClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

public class WebApiCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (metadata.getAnnotations().isPresent(LoadBalanced.class)) {
            try {
                WebClient.Builder bean = context.getBeanFactory().getBean(ApiClient.SERVICE_CLIENT_NAME, WebClient.Builder.class);
                if (Objects.nonNull(bean)) {
                    return false;
                }
            } catch (Throwable throwable) {
                //ignore
            }
        }

        try {
            WebClient.Builder bean = context.getBeanFactory().getBean(WebClient.Builder.class);
            if (Objects.nonNull(bean)) {
                return false;
            }
        } catch (Throwable throwable) {
            //ignore
        }
        return true;
    }
}
