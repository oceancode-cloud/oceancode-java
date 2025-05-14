package com.oceancode.cloud.common.plugin.graph.neo4j;

import com.oceancode.cloud.api.graph.GraphCell;
import com.oceancode.cloud.api.graph.GraphEdge;
import com.oceancode.cloud.api.graph.GraphNode;
import com.oceancode.cloud.api.graph.GraphService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Neo4jGraphServiceImpl implements GraphService {
    @Resource
    private Driver driver;

    @Override
    public GraphNode findById(String id) {
        String cql = "MATCH(n:Node) WHERE n.id=$id return n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return run(cql, params, result -> {
            GraphNode graphNode = null;
            if (result.hasNext()) {
                graphNode = (GraphNode) convert2GraphCell(result.next());
            }
            return graphNode;
        });
    }

    private GraphCell convert2GraphCell(Record record) {
        List<Value> values = record.values();
        if (values.size() == 1) {
            Value value = values.get(0);
            if (Objects.isNull(value)) {
                return null;
            }
            Map<String, Object> valuMap = value.asMap();
            if (ValueUtil.isEmpty(valuMap)) {
                return null;
            }

            Map<String, Object> map = new HashMap<>(valuMap);
            GraphCell cell = null;
            if (value instanceof NodeValue) {
                GraphNode node = new GraphNode();
                cell = node;
                cell.setId(map.getOrDefault("id", "") + "");
            } else if (value instanceof RelationshipValue) {
                GraphEdge edge = new GraphEdge();
                cell = edge;
                edge.setSourceId(map.getOrDefault("sourceId", "") + "");
                edge.setTargetId(map.getOrDefault("targetId", "") + "");
                map.remove("sourceId");
                map.remove("targetId");
            } else {
                return null;
            }
            cell.setName(map.getOrDefault("name", "") + "");
            cell.setType(map.getOrDefault("type", "") + "");
            map.remove("id");
            map.remove("name");
            map.remove("type");
            cell.setProperties(map);
            return cell;
        }
        return null;
    }

    private <T> T doWithTransaction(Function<Transaction, T> function) {
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

    private <T> T run(String sql, Map<String, Object> params, Function<Result, T> function) {
        try (Session session = driver.session()) {
            Result result = session.run(sql, params);
            return function.apply(result);
        }
    }

    private Result run(Transaction transaction, String sql, Map<String, Object> params) {
        return transaction.run(sql, params);
    }

    private StringBuilder convertParams(Map<String, Object> params) {
        return convertParams(null, ":", params);
    }

    private StringBuilder convertParams(String prefix, String sp, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        if (ValueUtil.isEmpty(params)) {
            return builder;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (Objects.isNull(value)) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(",");
            }
            if (Objects.nonNull(prefix)) {
                builder.append(prefix);
            }
            builder.append(entry.getKey()).append(sp).append("$").append(entry.getKey());
        }
        return builder;
    }

    @Override
    public boolean addOne(GraphCell cell, boolean throwEx) {
        return addOne0(cell, throwEx, null);
    }

    public boolean addOne0(GraphCell cell, boolean throwEx, Transaction transaction) {
        Map<String, Object> params = new HashMap<>();
        if (Objects.nonNull(cell.getProperties())) {
            params.putAll(cell.getProperties());
        }
        if (ValueUtil.isNotEmpty(cell.getType())) {
            params.put("type", cell.getType());
        }
        if (ValueUtil.isNotEmpty(cell.getName())) {
            params.put("name", cell.getName());
        }

        if (cell instanceof GraphNode) {
            if (ValueUtil.isEmpty(cell.getId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
            }
            GraphNode node = findById(cell.getId());
            if (Objects.nonNull(node)) {
                if (!throwEx) {
                    return false;
                }
                throw new BusinessRuntimeException(CommonErrorCode.ALREADY_EXISTS);
            }
            params.put("id", cell.getId());
            StringBuilder paramBuilder = convertParams(params);
            String sql = "CREATE(n:Node{" + paramBuilder + "}) RETURN n";
            if (Objects.nonNull(transaction)) {
                Result result = transaction.run(sql, params);
                return result.hasNext();
            }
            return run(sql, params, result -> result.hasNext());
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            if (ValueUtil.isEmpty(cell.getType())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "type is required.");
            }
            if (ValueUtil.isEmpty(graphEdge.getTargetId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "targetId is required.");
            }
            if (ValueUtil.isEmpty(graphEdge.getSourceId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "sourceId is required.");
            }
            GraphCell graphCell = findOne(cell, false);
            if (Objects.nonNull(graphCell)) {
                if (throwEx) {
                    throw new BusinessRuntimeException(CommonErrorCode.ALREADY_EXISTS);
                } else {
                    return false;
                }
            }
            params.put("sourceId", graphEdge.getSourceId());
            params.put("targetId", graphEdge.getTargetId());
            StringBuilder paramBuilder = convertParams(params);
            String sql = "MATCH(n1:Node{id:$sourceId}),(n2:Node{id:$targetId}) WHERE NOT (n1)-[:r{type:$type}]->(n2) CREATE(n1)-[:r{" + paramBuilder + "}]->(n2) RETURN n1";
            if (Objects.nonNull(transaction)) {
                Result result = transaction.run(sql, params);
                return result.hasNext();
            }
            return run(sql, params, result -> result.hasNext());
        }
        return false;
    }

    @Override
    public boolean addBatch(List<GraphCell> cells, boolean throwEx) {
        return doWithTransaction(transaction -> {
            int count = 0;
            for (GraphCell cell : cells) {
                boolean result = addOne0(cell, false, transaction);
                if (result) {
                    count++;
                }
            }
            if (count != cells.size()) {
                throw new BusinessRuntimeException(CommonErrorCode.ERROR);
            }
            return true;
        });
    }

    @Override
    public boolean deleteById(String id, boolean throwEx) {
        String sql = "MATCH(n:Node{id:$id}) DETACH DELETE n RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return run(sql, params, result -> result.hasNext());
    }

    @Override
    public boolean deleteOne(GraphCell cell, boolean throwEx) {
        if (cell.isNode()) {
            if (ValueUtil.isNotEmpty(cell.getId())) {
                return deleteById(cell.getId(), throwEx);
            }
        }
        return false;
    }

    @Override
    public boolean delete(GraphCell cell, boolean throwEx) {
        return false;
    }

    @Override
    public boolean addOne(String sourceId, GraphNode node, boolean throwEx) {
        return false;
    }

    @Override
    public boolean addBatch(String sourceId, List<GraphNode> nodes, boolean throwEx) {
        return false;
    }

    @Override
    public boolean updateById(GraphCell cell, boolean throwEx) {
        Map<String, Object> params = new HashMap<>();
        if (Objects.nonNull(cell.getProperties())) {
            params.putAll(cell.getProperties());
        }
        if (cell instanceof GraphNode) {
            params.put("id", cell.getId());
            StringBuilder paramBuilder = convertParams("n.", "=", params);
            String sql = "MATCH(n:Node{id:$id}) SET " + paramBuilder + " RETURN n";
            doWithTransaction(session -> {
                Result run = run(session, sql, params);
                return null;
            });
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            params.put("type", cell.getType());
            params.put("sourceId", graphEdge.getSourceId());
            params.put("targetId", graphEdge.getTargetId());
            StringBuilder paramBuilder = convertParams("n.", "=", params);
            String sql = "MATCH(n:Node{id:$sourceId})-[r]->(n1:Node{id:$targetId}) WHERE r.type=$type SET " + paramBuilder + " RETURN r";
            doWithTransaction(session -> {
                Result run = run(session, sql, params);
                return null;
            });
        }
        return false;
    }

    @Override
    public GraphCell findOne(GraphCell cell, boolean throwEx) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", cell.getType());
        if (cell.isNode()) {
            if (Objects.nonNull(cell.getProperties())) {
                params.putAll(cell.getProperties());
            }
            params.put("name", cell.getName());
            GraphNode node = null;
            if (ValueUtil.isNotEmpty(cell.getId())) {
                node = findById(cell.getId());
            } else {
                String cql = "MATCH(n:Node{" + convertParams(params) + "}) LIMIT 2 return n";
                node = run(cql, params, result -> {
                    GraphNode graphNode = null;
                    if (result.hasNext()) {
                        Record next = result.next();
                        if (result.hasNext()) {
                            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "too many,expected:1,actual:2");
                        }
                        graphNode = (GraphNode) convert2GraphCell(next);
                    }
                    return graphNode;
                });
            }

            if (Objects.isNull(node)) {
                if (!throwEx) {
                    return null;
                }
                throw new BusinessRuntimeException(CommonErrorCode.NOT_FOUND);
            }
            return node;
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            if (ValueUtil.isEmpty(cell.getType())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "type is required.");
            }
            if (ValueUtil.isEmpty(graphEdge.getTargetId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "targetId is required.");
            }
            if (ValueUtil.isEmpty(graphEdge.getSourceId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "sourceId is required.");
            }
            params.put("type", cell.getType());
            params.put("sourceId", graphEdge.getSourceId());
            params.put("targetId", graphEdge.getTargetId());
            String sql = "MATCH(n:Node{id:$sourceId})-[r]->(n1:Node{id:$targetId}) WHERE r.type=$type LIMIT 1 RETURN r";
            GraphEdge edge = run(sql, params, result -> {
                GraphEdge graphNode = null;
                if (result.hasNext()) {
                    graphNode = (GraphEdge) convert2GraphCell(result.next());
                }
                return graphNode;
            });
            if (Objects.isNull(edge)) {
                if (throwEx) {
                    throw new BusinessRuntimeException(CommonErrorCode.NOT_FOUND);
                }
            }
            return edge;
        }
        return null;
    }
}
