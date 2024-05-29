package com.oceancode.cloud.common.web.graphql.datatype;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.math.BigDecimal;


public class GraphqlLongCoercing implements Coercing<Long, Long> {
    private Long convertImpl(Object input) {
        if (input instanceof Long) {
            return (Long) input;
        } else if (input instanceof Number || input instanceof String) {
            BigDecimal value;
            try {
                value = new BigDecimal(input.toString());
            } catch (NumberFormatException e) {
                return null;
            }
            try {
                return value.longValueExact();
            } catch (ArithmeticException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    static String typeName(Object input) {
        if (input == null) {
            return "null";
        }

        return input.getClass().getSimpleName();
    }

    @Override
    public Long serialize(Object input) throws CoercingSerializeException {
        Long result = convertImpl(input);
        if (result == null) {
            throw new CoercingSerializeException(
                    "Expected type 'Long' but was '" + typeName(input) + "'."
            );
        }
        return result;
    }

    @Override
    public Long parseValue(Object input) throws CoercingParseValueException {
        Long result = convertImpl(input);
        if (result == null) {
            throw new CoercingParseValueException(
                    "Expected type 'Long' but was '" + typeName(input) + "'."
            );
        }
        return result;
    }

    @Override
    public Long parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            return Long.parseLong(((StringValue) input).getValue());
        } else if (input instanceof IntValue) {
            return ((IntValue) input).getValue().longValue();
        }

        return null;
    }
}
