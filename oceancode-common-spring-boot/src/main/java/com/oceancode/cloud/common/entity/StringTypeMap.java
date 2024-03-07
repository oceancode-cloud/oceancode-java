/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.entity;

import java.util.Map;

public class StringTypeMap extends TypeMap<String> {

    public StringTypeMap() {
        super();
    }

    public StringTypeMap(Map<String, Object> data) {
        super(data);
    }

    public static StringTypeMap of() {
        return new StringTypeMap();
    }

    public StringTypeMap add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public StringTypeMap addAll(Map<String, ?> map) {
        super.putAll(map);
        return this;
    }
}