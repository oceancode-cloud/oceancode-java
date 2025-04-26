package com.oceancode.cloud.api.query;

import java.util.ArrayList;
import java.util.List;

public class QueryField {
    private String name;
    private List<Object> fields = new ArrayList<>();

    private QueryField(String name) {
        this.name = name;
    }

    public static QueryField name(String name) {
        return new QueryField(name);
    }

    public QueryField addFields(String... fields) {
        if (fields != null) {
            for (String field : fields) {
                this.fields.add(field);
            }
        }
        return this;
    }

    public QueryField addFields(QueryField... fields) {
        if (fields != null) {
            for (QueryField field : fields) {
                this.fields.add(field);
            }
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public List<Object> getFields() {
        return fields;
    }
}
