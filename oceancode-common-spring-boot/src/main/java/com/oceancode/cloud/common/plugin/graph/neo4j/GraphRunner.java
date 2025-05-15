package com.oceancode.cloud.common.plugin.graph.neo4j;

import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;

import java.util.Map;
import java.util.function.Function;

public interface GraphRunner {
    <T> T run(String sql, Map<String, Object> params, Function<Result, T> function);

    Result run(Transaction transaction, String sql, Map<String, Object> params);

    <T> T doWithTransaction(Function<Transaction, T> function);
}
