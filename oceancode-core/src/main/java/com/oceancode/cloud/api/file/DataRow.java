package com.oceancode.cloud.api.file;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataRow {
    private int partIndex;
    private List<Map<String, Object>> values;

    public DataRow() {
    }

    public DataRow(int partIndex, List<Map<String, Object>> values) {
        this.partIndex = partIndex;
        this.values = Collections.unmodifiableList(values);
    }

    public int getPartIndex() {
        return partIndex;
    }

    public List<Map<String, Object>> getValues() {
        return values;
    }

    public String getText() {
        return null;
    }
}
