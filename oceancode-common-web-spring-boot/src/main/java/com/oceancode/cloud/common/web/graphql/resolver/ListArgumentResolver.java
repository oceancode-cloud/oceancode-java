package com.oceancode.cloud.common.web.graphql.resolver;

import com.oceancode.cloud.common.list.IntegerList;
import com.oceancode.cloud.common.list.LongList;
import com.oceancode.cloud.common.list.StringList;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.function.ArgumentResolver;
import graphql.language.ArrayValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;

@Component
public class ListArgumentResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    public Object resolve(Class<?> targetClass, Object value) {
        List list = null;
        Class elementType = null;
        if (StringList.class.equals(targetClass)) {
            list = new StringList();
            elementType = String.class;
        } else if (LongList.class.equals(targetClass)) {
            list = new LongList();
            elementType = Long.class;
        } else if (IntegerList.class.equals(targetClass)) {
            list = new IntegerList();
            elementType = Integer.class;
        }
        convertList(list, elementType, value);
        return null;
    }

    private void convertList(List list, Class elementType, Object value) {
        Iterator iterator = null;
        if (value instanceof Collection) {
            iterator = ((Collection<?>) value).iterator();
        } else if (value instanceof ArrayValue) {
            iterator = ((ArrayValue) value).getValues().iterator();
        }
        if (iterator == null) {
            return;
        }
        while (iterator.hasNext()) {
            Object it = iterator.next();
            if (null == it) {
                continue;
            }
            Object targetVal = null;
            if (it instanceof IntValue) {
                targetVal = ((IntValue) it).getValue().intValue();
            } else if (it instanceof StringValue) {
                targetVal = ((StringValue) it).getValue();
            }

            if (String.class.equals(elementType)) {
                targetVal = it + "";
            } else if (Integer.class.equals(elementType)) {
                targetVal = Integer.parseInt(it + "");
            } else if (Long.class.equals(elementType)) {
                targetVal = Long.parseLong(it + "");
            } else {
                targetVal = JsonUtil.toBean(JsonUtil.toJson(it), elementType);
            }
            if (targetVal != null) {
                list.add(targetVal);
            }
        }
    }
}
