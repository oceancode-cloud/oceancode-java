package com.oceancode.cloud.common.web.graphql.datatype;

import graphql.schema.GraphQLScalarType;

public class DslType {
    public static final GraphQLScalarType GraphQLObject = GraphQLScalarType.newScalar()
            .name("Object").description("Custom Object").coercing(new GraphsqlObjectCoercing()).build();
}
