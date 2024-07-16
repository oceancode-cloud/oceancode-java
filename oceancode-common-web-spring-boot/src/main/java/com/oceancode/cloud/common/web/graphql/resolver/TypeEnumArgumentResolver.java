package com.oceancode.cloud.common.web.graphql.resolver;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.list.StringList;
import com.oceancode.cloud.function.ArgumentResolver;
import graphql.language.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class TypeEnumArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type) {
        return TypeEnum.class.isAssignableFrom(type);
    }

    @Override
    public Object resolve(Class<?> targetClass, Object value) {
        Class<TypeEnum<?>> typeClass = (Class<TypeEnum<?>>) targetClass;
        Object v = value;
        if (value instanceof IntValue) {
            v = ((IntValue) value).getValue();
        } else if (value instanceof StringValue) {
            v = ((StringValue) value).getValue();
        }
        return TypeEnum.from(v, typeClass);
    }
}
