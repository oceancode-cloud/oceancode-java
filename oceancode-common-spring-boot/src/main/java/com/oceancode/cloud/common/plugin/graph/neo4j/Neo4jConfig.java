package com.oceancode.cloud.common.plugin.graph.neo4j;


import com.oceancode.cloud.api.graph.GraphService;
import com.oceancode.cloud.common.config.CommonConfig;
import jakarta.annotation.Resource;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass(GraphDatabase.class)
public class Neo4jConfig {

    @Resource
    private CommonConfig commonConfig;

    @Bean
    @ConditionalOnClass(GraphDatabase.class)
    public Driver driver() {
        Driver driver = GraphDatabase.driver(commonConfig.getValue("spring.data.neo4j.uri", true),
                AuthTokens.basic(commonConfig.getValue("spring.data.neo4j.username", true), commonConfig.getValue("spring.data.neo4j.password", true)));
        return driver;
    }

    @Bean
    @ConditionalOnBean(Driver.class)
    public GraphService graphService() {
        return new Neo4jGraphServiceImpl();
    }
}
