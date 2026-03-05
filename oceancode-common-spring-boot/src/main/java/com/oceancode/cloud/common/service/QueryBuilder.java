package com.oceancode.cloud.common.service;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.entity.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class QueryBuilder {
    private List<Method> methods = new ArrayList<>();

    public String build() {
        StringJoiner sj = new StringJoiner(",");
        for (Method method : methods) {
            sj.add(method.build());
        }
        return sj.toString();
    }

    public static QueryBuilder builder() {
        return new QueryBuilder();
    }

    public Method method(String methodName, String outputId) {
        Method method = new Method(methodName, outputId);
        methods.add(method);
        return method;
    }

    public Method method(String methodName) {
        return method(methodName, "data");
    }

    public static class Method {
        private List<Tuple2<String, Object>> params = new ArrayList<>();
        private String name;
        private String outputId;
        private Set<String> outputs = new HashSet<>();

        private Method(String methodName, String outputId) {
            this.name = methodName;
            this.outputId = outputId;
        }

        public Method outputs(Collection<String> fields) {
            this.outputs.addAll(fields);
            return this;
        }

        public Method outputs(String... fields) {
            this.outputs.addAll(Arrays.asList(fields));
            return this;
        }

        private String build() {
            String code = outputId + ":" + name;

            StringJoiner returnSj = new StringJoiner(",");
            for (String output : outputs) {
                returnSj.add(output);
            }
            if (params.isEmpty()) {
                return code + "{" + returnSj + "}";
            }

            StringJoiner arg = new StringJoiner(",");
            for (Tuple2<String, Object> param : params) {
                Object value = param.getSecond();
                if (value instanceof Number) {
                    // nothing
                } else if (value instanceof TypeEnum<?>) {
                    value = ((TypeEnum<?>) value).getValue();
                } else {
                    value = JsonUtil.toJson(value);
                }
                if (Objects.isNull(value)) {
                    value = "";
                }
                if (value instanceof String) {
                    value = "\"" + value + "\"";
                }
                arg.add(param.getFirst() + ":" + value);
            }
            return code + "(" + arg + "){" + returnSj + "}";
        }

        public Method param(String id, Object value) {
            params.add(new Tuple2<>(id, value));
            return this;
        }

        public String getName() {
            return name;
        }

        public String getOutputId() {
            return outputId;
        }
    }
}
