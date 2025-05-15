package com.oceancode.cloud.common.plugin.graph.neo4j;

import jakarta.annotation.Resource;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
@ConditionalOnClass(GraphDatabase.class)
@ConditionalOnMissingClass("org.springframework.data.neo4j.core.Neo4jClient")
public class Neo4jDriverRunner implements GraphRunner {
    @Resource
    private Driver driver;

    @Override
    public <T> T run(String sql, Map<String, Object> params, Function<Result, T> function) {
        try (Session session = driver.session()) {
            Result result = session.run(sql, params);
            return function.apply(result);
        }
    }

    @Override
    public Result run(Transaction transaction, String sql, Map<String, Object> params) {
        return transaction.run(sql, params);
    }

    @Override
    public <T> T doWithTransaction(Function<Transaction, T> function) {
        try (Session session = driver.session()) {
            Transaction transaction = session.beginTransaction();
            try {
                T apply = function.apply(transaction);
                transaction.commit();
                return apply;
            } catch (Throwable throwable) {
                transaction.rollback();
                throw throwable;
            } finally {
                transaction.close();
            }
        }
    }
}
