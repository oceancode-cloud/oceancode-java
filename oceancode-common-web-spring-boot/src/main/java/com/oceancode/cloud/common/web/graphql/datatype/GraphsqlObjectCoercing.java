package com.oceancode.cloud.common.web.graphql.datatype;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class GraphsqlObjectCoercing implements Coercing<Object, Object> {
    @Override
    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
        return dataFetcherResult;
    }

    @Override
    public Object parseValue(Object input) throws CoercingParseValueException {
        return input;
    }

    @Override
    public Object parseLiteral(Object input) throws CoercingParseLiteralException {
        return input;
    }
}
