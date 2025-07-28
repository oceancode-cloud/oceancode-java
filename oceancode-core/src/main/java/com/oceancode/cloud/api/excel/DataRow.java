package com.oceancode.cloud.api.excel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataRow {
    private int partIndex;
    private List<Map<String, Object>> values;
    private String name;

    public DataRow(int partIndex, String name, List<Map<String, Object>> values) {
        this.partIndex = partIndex;
        this.name = name;
        this.values = Collections.unmodifiableList(values);
    }

    public int getPartIndex() {
        return partIndex;
    }

    public List<Map<String, Object>> getValues() {
        return values;
    }

    public String getName() {
        return this.name;
    }
}
