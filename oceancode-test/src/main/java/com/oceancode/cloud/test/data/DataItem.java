package com.oceancode.cloud.test.data;

import com.oceancode.cloud.common.entity.StringTypeMap;

public class DataItem {
    private StringTypeMap map = new StringTypeMap();
    private boolean positive;

    private DataItem(boolean positive) {
        this.positive = positive;
    }

    public static DataItem newData(boolean positive) {
        return new DataItem(positive);
    }

    public DataItem add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public StringTypeMap getAll() {
        return map;
    }

    public boolean isPositive() {
        return this.positive;
    }
}
