package com.oceancode.cloud.common.web.graphql.datatype;

import com.oceancode.cloud.common.entity.StringTypeMap;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.util.Collections;
import java.util.Map;

public class GraphsqlMapCoercing implements Coercing<Map, Map> {
    @Override
    public Map serialize(Object dataFetcherResult) throws CoercingSerializeException {
        if (!(dataFetcherResult instanceof Map<?, ?>)) {
            return Collections.emptyMap();
        }
        return Coercing.super.serialize(dataFetcherResult);
    }

    @Override
    public Map parseValue(Object input) throws CoercingParseValueException {
        if (input instanceof Map<?, ?>) {
            return new StringTypeMap((Map<String, Object>) input);
        }
        return Collections.emptyMap();
    }


    @Override
    public Map parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof Map<?, ?>) {
            return new StringTypeMap((Map<String, Object>) input);
        }
        return Collections.emptyMap();
    }
}
