package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.annotation.Query;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.graphql.datatype.DslType;
import com.oceancode.cloud.common.web.util.ContextUtil;
import com.oceancode.cloud.function.ArgumentResolver;
import com.oceancode.cloud.function.QueryFunction;
import graphql.GraphQL;
import graphql.Scalars;
import graphql.language.*;
import graphql.schema.*;
import graphql.schema.idl.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class GraphQlProvider {
    private GraphQL graphQL;

    private CommonConfig commonConfig;

    private volatile List<ArgumentResolver> resolvers;

    private QueryFunction queryFunction;

    public GraphQlProvider(QueryFunction queryFunction, CommonConfig commonConfig) {
        this.queryFunction = queryFunction;
        this.commonConfig = commonConfig;
        try {
            init();
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    public GraphQL graphQL() {
        return graphQL;
    }

    private List<ArgumentResolver> resolvers() {
        if (Objects.nonNull(resolvers)) {
            return resolvers;
        }
        synchronized (this) {
            if (Objects.nonNull(resolvers)) {
                return resolvers;
            }

            resolvers = new ArrayList<>(ComponentUtil.getBeans(ArgumentResolver.class).values());
        }

        return resolvers;
    }

    private void init() throws IOException {
        GraphQLSchema graphQLSchema = createSchema();
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private void getMethods(List<Method> methods, Class classType) {
        for (Method method : classType.getDeclaredMethods()) {
            methods.add(method);
        }
        if (!classType.getSuperclass().equals(Object.class)) {
            getMethods(methods, classType.getSuperclass());
        }
    }

    private GraphQLSchema createSchema() {
//        String basePackage = commonConfig.getValue("oc.dsl.query.package");
//        Reflections reflections = new Reflections(basePackage);
//        Set<Class<?>> sets = reflections.getTypesAnnotatedWith(Query.class);
        GraphQLObjectType.Builder queryBuilder = GraphQLObjectType.newObject()
                .name(queryFunction.getClass().getSimpleName());
        Map<String, GraphQLOutputType> typeMapping = new HashMap<>();


        List<Method> methods = new ArrayList<>();
        getMethods(methods, queryFunction.getClass());
        for (Method method : methods) {
            Query methodQuery = method.getAnnotation(Query.class);
            if (Objects.isNull(methodQuery)) {
                continue;
            }
            String methodName = methodQuery.name();
            if (ValueUtil.isEmpty(methodName)) {
                methodName = method.getName();
            }

            DataFetcher dataFetcher = environment -> doDataFetcher(environment, method, queryFunction);
            boolean isList = Collection.class.isAssignableFrom(method.getReturnType());
            Class returnType = methodQuery.returnType();
            if (!isList) {
                returnType = method.getReturnType();
            }

            GraphQLFieldDefinition.Builder methodBuilder = GraphQLFieldDefinition.newFieldDefinition();
            methodBuilder.name(methodName);
            if (method.getParameterCount() > 0) {
                for (Parameter parameter : method.getParameters()) {
                    methodBuilder.argument(GraphQLArgument.newArgument().name(parameter.getName()).type(convertQLType(parameter.getType())).build());
                }
            }

            String key = returnType.getName();
            GraphQLOutputType graphQLOutputType = typeMapping.get(key);
            if (graphQLOutputType == null) {
                graphQLOutputType = createOutField(returnType);
                typeMapping.put(key, graphQLOutputType);
            }
            methodBuilder.type(isList ? new GraphQLList(graphQLOutputType) : graphQLOutputType);
            methodBuilder.dataFetcher(dataFetcher);
            queryBuilder.field(methodBuilder.build());
        }
//        for (Class<?> c : sets) {
//            Query query = c.getAnnotation(Query.class);
//            String modelName = query.name();
//            if (ValueUtil.isEmpty(modelName)) {
//                modelName = c.getSimpleName();
//            }
//
//            for (Method method : ReflectionUtils.getMethods(c, ReflectionUtilsPredicates.withAnnotation(Query.class))) {
//
//            }
//        }

        return GraphQLSchema.newSchema().query(queryBuilder.build()).build();
    }

    public GraphQLOutputType createOutField(Class type) {
        GraphQLObjectType.Builder builder = GraphQLObjectType.newObject().name(type.getSimpleName());
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            builder.field(GraphQLFieldDefinition.newFieldDefinition().name(field.getName()).type(convertQLType(field.getType())).build());
        }

        return builder.build();
    }

    private GraphQLScalarType convertQLType(Class type) {
        if (Integer.class.equals(type)) {
            return Scalars.GraphQLInt;
        } else if (Long.class.equals(type)) {
            return DslType.GraphQLLong;
        } else if (Boolean.class.equals(type)) {
            return Scalars.GraphQLBoolean;
        } else if (String.class.equals(type)) {
            return Scalars.GraphQLString;
        }

        return DslType.GraphQLObject;
    }

    private Object[] buildArgs(Method method, DataFetchingEnvironment environment) {
        if (method.getParameterCount() == 0) {
            return null;
        }
        Object[] args = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            Parameter parameter = method.getParameters()[i];
            Object value = environment.getArgument(parameter.getName());
            if (null == value) {
                continue;
            }

            Class type = parameter.getType();
            Object argValue = value;
            if (Long.class.equals(type)) {
                argValue = Long.parseLong(value + "");
            } else if (Integer.class.equals(type)) {
                argValue = Integer.parseInt(value + "");
            } else if (String.class.equals(type)) {
                argValue = value + "";
            } else {
                Optional<ArgumentResolver> optional = resolvers().stream().filter(e -> e.support(type)).findFirst();
                if (optional.isPresent()) {
                    argValue = optional.get().resolve(type, value);
                } else {
                    argValue = processJson(type, value);
                }
            }

            args[i] = argValue;
        }

        return args;
    }

    private Object processJson(Class type, Object value) {
        if (value instanceof ObjectValue) {
            ObjectValue objectValue = (ObjectValue) value;
            List<ObjectField> objectFields = objectValue.getObjectFields();
            Map<String, Object> valueMap = new HashMap<>(objectFields.size());
            for (ObjectField objectField : objectFields) {
                Object fieldValue = objectField.getValue();
                Object javaTypeValue = fieldValue;
                if (fieldValue instanceof BooleanValue) {
                    javaTypeValue = ((BooleanValue) fieldValue).isValue();
                } else if (fieldValue instanceof IntValue) {
                    javaTypeValue = ((IntValue) fieldValue).getValue().intValue();
                } else if (fieldValue instanceof StringValue) {
                    javaTypeValue = ((StringValue) fieldValue).getValue();
                }
                valueMap.put(objectField.getName(), javaTypeValue);
            }
            return JsonUtil.mapToBean(valueMap, type);
        }

        if (List.class.isAssignableFrom(type)) {
            return JsonUtil.toList(JsonUtil.toJson(value), type);
        } else {
            if (Map.class.isAssignableFrom(value.getClass())) {
                return JsonUtil.mapToBean((Map) value, type);
            }
            return JsonUtil.toBean(JsonUtil.toJson(value), type);
        }
    }

//    private void mergeSchema(TypeDefinitionRegistry registry, File file) {
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            for (File fd : files) {
//                mergeSchema(registry, fd);
//            }
//        } else if (file.isFile()) {
//            TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(file);
//            registry.merge(typeDefinitionRegistry);
//        }
//    }

//    private GraphQLSchema buildGraphQLSchema(File file) {
//        TypeDefinitionRegistry registry = new TypeDefinitionRegistry();
//        mergeSchema(registry, file);
//        GraphQLSchema graphQLSchema = new SchemaGenerator().makeExecutableSchema(registry, buildWiring());
//        return graphQLSchema;
//    }

    private Object doDataFetcher(DataFetchingEnvironment environment, Method executeMethod, Object instance) throws InvocationTargetException, IllegalAccessException {
        ContextUtil.set(new GraphQlContext(environment));
        try {
            return executeMethod.invoke(instance, buildArgs(executeMethod, environment));
        } finally {
            ContextUtil.remove();
        }

    }

//    private RuntimeWiring buildWiring() {
//        String basePackage = commonConfig.getValue("oc.dsl.query.package");
//        Reflections reflections = new Reflections(basePackage);
//        Set<Class<?>> sets = reflections.getTypesAnnotatedWith(Query.class);
//        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();
//        for (Class<?> c : sets) {
//            Query query = c.getAnnotation(Query.class);
//            String modelName = query.name();
//            if (ValueUtil.isEmpty(modelName)) {
//                modelName = c.getSimpleName();
//            }
//
//            Map<String, DataFetcher> fetcherMap = new HashMap<>();
//
//            for (Method method : ReflectionUtils.getMethods(c, ReflectionUtilsPredicates.withAnnotation(Query.class))) {
//                Query methodQuery = method.getAnnotation(Query.class);
//                String methodName = methodQuery.name();
//                if (ValueUtil.isEmpty(methodName)) {
//                    methodName = method.getName();
//                }
//
//                DataFetcher dataFetcher = environment -> doDataFetcher(environment, method, c);
//                fetcherMap.put(methodName, dataFetcher);
//            }
//            builder.type(TypeRuntimeWiring.newTypeWiring(modelName).dataFetchers(fetcherMap));
//        }
//
//        return builder.build();
//    }
}
