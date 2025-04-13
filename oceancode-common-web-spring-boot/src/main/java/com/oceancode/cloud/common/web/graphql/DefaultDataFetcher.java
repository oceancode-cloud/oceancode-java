package com.oceancode.cloud.common.web.graphql;

import com.oceancode.cloud.common.util.SessionUtil;
import graphql.schema.AsyncDataFetcher;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DefaultDataFetcher extends AsyncDataFetcher {
    public DefaultDataFetcher(DataFetcher wrappedDataFetcher) {
        super(wrappedDataFetcher);
    }

    public DefaultDataFetcher(DataFetcher wrappedDataFetcher, Executor executor) {
        super(wrappedDataFetcher, executor);
    }

    @Override
    public CompletableFuture get(DataFetchingEnvironment environment) {
        List<Object> values = SessionUtil.getValues();
        return CompletableFuture.supplyAsync(() -> {
            try {
                SessionUtil.setValues(values);
                return getWrappedDataFetcher().get(environment);
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new RuntimeException(e);
                }
            } finally {
                SessionUtil.remove();
            }
        }, getExecutor());
    }
}
