package com.oceancode.cloud.api.query;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class QueryMethod {
    private String method;
    private String name = "results";
    private List<Object> fields = new ArrayList<>();
    private List<Object> params = new ArrayList<>();
    private List<Object> paramsValue = new ArrayList<>();

    private QueryMethod(String method) {
        this.method = method;
    }

    public static QueryMethod method(String method) {
        return new QueryMethod(method);
    }

    public QueryMethod name(String name) {
        this.name = name;
        return this;
    }

    public QueryMethod addFields(String... fields) {
        if (fields != null && fields.length > 0) {
            for (String field : fields) {
                this.fields.add(field);
            }
        }
        return this;
    }

    public QueryMethod addParam(String paramName, Object value) {
        if (ValueUtil.isEmpty(paramName) || params.contains(paramName)) {
            return this;
        }
        this.params.add(paramName);
        this.paramsValue.add(value);
        return this;
    }

    public String getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        StringBuilder queryBuilder = new StringBuilder();
        processQuery(this.fields, queryBuilder);
        String query = queryBuilder.toString().trim();
        if (query.endsWith(",")) {
            query = queryBuilder.substring(0, query.length() - 1);
        }
        StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            Object value = paramsValue.get(i);
            if (i > 0) {
                paramBuilder.append(",");
            }
            paramBuilder.append(params.get(i)).append(":");
            if (value instanceof String) {
                value = "\\" + "\"" + value + "\\" + "\"";
            }
            paramBuilder.append(value);
        }

        if (paramBuilder.isEmpty()) {
            return getMethod() + "{" + query + "}";
        }
        return getMethod() + "(" + paramBuilder + "){" + query + "}";
    }

    private void processQuery(List<Object> fields, StringBuilder queryBuilder) {
        if (ValueUtil.isEmpty(fields)) {
            return;
        }
        Set<String> fieldList = new HashSet<>();
        for (Object field : fields) {
            if (field instanceof String) {
                if (fieldList.contains(field)) {
                    continue;
                }
                fieldList.add((String) field);
                queryBuilder.append(field).append(",");
            } else if (field instanceof QueryField) {
                QueryField queryField = (QueryField) field;
                if (ValueUtil.isEmpty(queryField.getName())) {
                    continue;
                }
                if (fieldList.contains(queryField.getName())) {
                    continue;
                }
                fieldList.add(queryField.getName());
                StringBuilder fieldBuilder = new StringBuilder();
                processQuery(queryField.getFields(), fieldBuilder);
                if (!fieldBuilder.isEmpty()) {
                    String code = fieldBuilder.toString();
                    if (code.endsWith(",")) {
                        code = code.substring(0, code.length() - 1);
                    }
                    queryBuilder.append(queryField.getName()).append("{").append(code).append("}").append(",");
                }
            }
        }
    }
}
