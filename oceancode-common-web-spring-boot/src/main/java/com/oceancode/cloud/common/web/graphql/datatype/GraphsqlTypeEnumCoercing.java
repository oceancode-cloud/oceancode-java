package com.oceancode.cloud.common.web.graphql.datatype;

import com.oceancode.cloud.api.TypeEnum;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

public class GraphsqlTypeEnumCoercing implements Coercing<Object, Object> {
    @Override
    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
        if (TypeEnum.class.isInstance(dataFetcherResult)) {
            return TypeEnum.class.cast(dataFetcherResult).getValue();
        }
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
