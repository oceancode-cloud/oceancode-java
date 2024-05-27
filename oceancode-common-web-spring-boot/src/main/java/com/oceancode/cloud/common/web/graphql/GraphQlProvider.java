package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.annotation.Query;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ContextUtil;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.ReflectionUtilsPredicates;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component
@ConditionalOnProperty(prefix = "oc.dsl.query", name = "package", matchIfMissing = false)
public class GraphQlProvider {
    private GraphQL graphQL;

    @Resource
    private CommonConfig commonConfig;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @PostConstruct
    public void init() throws IOException {
        File file = ResourceUtils.getFile("classpath:graphql");
        GraphQLSchema graphQLSchema = buildGraphQLSchema(file);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private void mergeSchema(TypeDefinitionRegistry registry, File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fd : files) {
                mergeSchema(registry, fd);
            }
        } else if (file.isFile()) {
            TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(file);
            registry.merge(typeDefinitionRegistry);
        }
    }

    private GraphQLSchema buildGraphQLSchema(File file) {
        TypeDefinitionRegistry registry = new TypeDefinitionRegistry();
        mergeSchema(registry, file);
        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(registry, buildWiring());
        return graphQLSchema;
    }

    private Object doDataFetcher(DataFetchingEnvironment environment, Method executeMethod, Class<?> typeClass) throws InvocationTargetException, IllegalAccessException {
        ContextUtil.set(new GraphQlContext(environment));
        try {
            return executeMethod.invoke(ComponentUtil.getBean(typeClass));
        } finally {
            ContextUtil.remove();
        }

    }

    private RuntimeWiring buildWiring() {
        String basePackage = commonConfig.getValue("oc.dsl.query.package");
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> sets = reflections.getTypesAnnotatedWith(Query.class);
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
        for (Class<?> c : sets) {
            Query query = c.getAnnotation(Query.class);
            String modelName = query.name();
            if (ValueUtil.isEmpty(modelName)) {
                modelName = c.getSimpleName();
            }

            Map<String, DataFetcher> fetcherMap = new HashMap<>();

            for (Method method : ReflectionUtils.getMethods(c, ReflectionUtilsPredicates.withAnnotation(Query.class))) {
                Query methodQuery = method.getAnnotation(Query.class);
                String methodName = methodQuery.name();
                if (ValueUtil.isEmpty(methodName)) {
                    methodName = method.getName();
                }

                DataFetcher dataFetcher = environment -> doDataFetcher(environment, method, c);
                fetcherMap.put(methodName, dataFetcher);
            }
            builder.type(TypeRuntimeWiring.newTypeWiring(modelName).dataFetchers(fetcherMap));
        }

        return builder.build();
    }
}
