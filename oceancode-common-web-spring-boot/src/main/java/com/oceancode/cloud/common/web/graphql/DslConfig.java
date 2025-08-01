package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.function.QueryFunction;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DslConfig {

    @Autowired(required = false)
    private DataFetcherProvider dataFetcherProvider;

    @Bean
    @ConditionalOnBean(QueryFunction.class)
    public GraphQL provider(QueryFunction queryFunction, CommonConfig commonConfig) {
        GraphQlProvider graphQlProvider = new GraphQlProvider(queryFunction, commonConfig, dataFetcherProvider);
        return graphQlProvider.graphQL();
    }
}
