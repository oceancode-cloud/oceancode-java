package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.function.Context;
import graphql.schema.DataFetchingEnvironment;

public class GraphQlContext implements Context {
    private DataFetchingEnvironment environment;

    public GraphQlContext(DataFetchingEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public boolean hasSelectionFields(String field, String... fields) {
        return this.environment.getSelectionSet().containsAllOf(field, fields);
    }
}
