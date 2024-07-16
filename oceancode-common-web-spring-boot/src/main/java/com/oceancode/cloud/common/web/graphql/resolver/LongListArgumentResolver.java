package com.oceancode.cloud.common.web.graphql.resolver;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.list.LongList;
import com.oceancode.cloud.function.ArgumentResolver;
import com.sun.org.apache.xpath.internal.operations.String;
import graphql.language.ArrayValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class LongListArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type) {
        return LongList.class.equals(type);
    }

    @Override
    public Object resolve(Class<?> targetClass, Object value) {
        LongList list = new LongList();
        if (value instanceof IntValue) {
            list.add(((IntValue) value).getValue().longValue());
        } else if (value instanceof ArrayValue) {
            ArrayValue arrayValue = (ArrayValue) value;
            List<Value> values = arrayValue.getValues();
            for (Value v : values) {
                if (Objects.isNull(v)) {
                    continue;
                }
                if (v instanceof IntValue) {
                    list.add(((IntValue) v).getValue().longValue());
                } else if (v instanceof StringValue) {
                    list.add(Long.parseLong(((StringValue) v).getValue()));
                } else {
                    throw new BusinessRuntimeException(CommonErrorCode.ERROR, value.toString() + " can not convert to Long");
                }
            }
        } else {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, value.toString() + " can not convert to Long");
        }
        return list;
    }
}
