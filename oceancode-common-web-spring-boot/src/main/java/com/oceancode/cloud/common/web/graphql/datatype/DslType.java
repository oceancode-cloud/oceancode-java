package com.oceancode.cloud.common.web.graphql.datatype;

import graphql.schema.GraphQLScalarType;

public class DslType {
    public static final GraphQLScalarType GraphQLObject = GraphQLScalarType.newScalar()
            .name("Object").description("Custom Object").coercing(new GraphsqlObjectCoercing()).build();

    public static final GraphQLScalarType GraphQLLong = GraphQLScalarType.newScalar()
            .name("Long").description("Custom Int").coercing(new GraphqlLongCoercing()).build();

    public static final GraphQLScalarType GraphQLTypeEnum = GraphQLScalarType.newScalar()
            .name("TypeEnum").description("Custom TypeEnum").coercing(new GraphsqlTypeEnumCoercing()).build();
}
