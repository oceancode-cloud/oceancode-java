/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * <B>Enum Interface</B>
 * <p>This is a interface that all enums must be implements.</p>
 *
 * <p>
 * This class is a Enum Interface.
 * </p>
 *
 * @author qinjiawang
 * @since 1.0
 */
public interface TypeEnum<T> {
    /**
     * get enum value
     * <p>You can map to the corresponding enumeration through value.</p>
     * <p>You can use value to convert enumeration by TypeEnum.from().</p>
     *
     * @return value
     */
    T getValue();

    /**
     * get name is a label used to describe value
     *
     * @return the label of value
     */
    String getName();

    /**
     * get description is a description used to describe value
     *
     * @return the description of value
     */
    String getDescription();

    /**
     * get enum type
     *
     * <p>You can use this method easily to get enum type class.</p>
     *
     * @param value value
     * @param clazz target type class.
     * @param <E>   a class that implement TypeEnum Interface.
     * @return an enum type class that implement TypeEnum.
     */
    static <E extends TypeEnum<?>> E from(Object value, Class<E> clazz) {
        return from(value, clazz, null);
    }

    static <E extends TypeEnum<?>> E from(Object value, Class<E> clazz, E defaultValue) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        for (E each : clazz.getEnumConstants()) {
            if (Objects.nonNull(each.getValue()) && each.getValue().equals(value)) {
                return each;
            }
        }
        return defaultValue;
    }

    static <E extends TypeEnum<?>> String getValueTypeName(Class<E> typeClass) {
        Type[] types = typeClass.getGenericInterfaces();
        if (types.length == 0) {
            return "String";
        }
        String typeName = types[0].getTypeName();
        return typeName.substring(typeName.lastIndexOf(".") + 1, typeName.lastIndexOf(">"));
    }
}