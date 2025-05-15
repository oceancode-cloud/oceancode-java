package com.oceancode.cloud.common.plugin.graph.neo4j;

import com.oceancode.cloud.api.graph.GraphCell;
import com.oceancode.cloud.api.graph.GraphEdge;
import com.oceancode.cloud.api.graph.GraphNode;
import com.oceancode.cloud.api.graph.GraphService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Neo4jGraphServiceImpl implements GraphService {
    @Resource
    private GraphRunner graphRunner;

    private void checkGraphCell(GraphCell cell) {
        if (cell.isNode()) {
            if (ValueUtil.isEmpty(cell.getId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
            }
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            if (ValueUtil.isEmpty(graphEdge.getType())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "type is required.");
            }
            if (ValueUtil.isEmpty(graphEdge.getSourceId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "sourceId is required.");
            }
            if (ValueUtil.isEmpty(graphEdge.getTargetId())) {
                throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "targetId is required.");
            }
        }
    }

    @Override
    public GraphNode findById(GraphCell cell, boolean throwEx) {
        checkGraphCell(cell);
        String cql = "MATCH(n:Node) WHERE n.id=$id return n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", cell.getId());
        GraphNode ret = run(cql, params, result -> {
            GraphNode graphNode = null;
            if (result.hasNext()) {
                graphNode = (GraphNode) convert2GraphCell(result.next());
            }
            return graphNode;
        });
        if (Objects.isNull(ret) && throwEx) {
            throw new BusinessRuntimeException(CommonErrorCode.NOT_FOUND);
        }
        return ret;
    }

    private GraphCell convert2GraphCell(Record record) {
        List<GraphCell> list = convert2GraphCells(record);
        return list.isEmpty() ? null : list.get(0);
    }

    private GraphCell convertValue(Value value) {
        if (Objects.isNull(value)) {
            return null;
        }
        boolean ret = value instanceof NodeValue ||
                value instanceof RelationshipValue ||
                value instanceof MapValue;

        if (!ret) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "unsupported type:" + value.getClass());
        }
        Map<String, Object> valuMap = value.asMap();
        if (ValueUtil.isEmpty(valuMap)) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(valuMap);
        GraphCell cell = null;
        if (value instanceof NodeValue) {
            GraphNode node = GraphNode.of("");
            cell = node;
            cell.setId(map.getOrDefault("id", "") + "");
        } else if (value instanceof RelationshipValue) {
            GraphEdge edge = GraphEdge.of("");
            cell = edge;
            edge.setSourceId(map.getOrDefault("sourceId", "") + "");
            edge.setTargetId(map.getOrDefault("targetId", "") + "");
            map.remove("sourceId");
            map.remove("targetId");
        } else if (value instanceof MapValue) {
            cell = new GraphCell();
            cell.setProperties(map);
        }
        cell.setName(map.getOrDefault("name", "") + "");
        cell.setType(map.getOrDefault("type", "") + "");
        map.remove("id");
        map.remove("name");
        map.remove("type");
        cell.setProperties(map);
        return cell;
    }

    private List<GraphCell> convert2GraphCells(Record record) {
        List<Value> values = record.values();
        return values.stream().map(this::convertValue).filter(Objects::nonNull).toList();
    }

    private <T> T doWithTransaction(Function<Transaction, T> function) {
        return graphRunner.doWithTransaction(function);
    }

    private <T> T run(String sql, Map<String, Object> params, Function<Result, T> function) {
        return graphRunner.run(sql, params, function);
    }

    private Result run(Transaction transaction, String sql, Map<String, Object> params) {
        return graphRunner.run(transaction, sql, params);
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
            GraphNode node = findById(cell, false);
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
            return run(sql, params, Result::hasNext);
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
            return run(sql, params, Result::hasNext);
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
    public boolean deleteById(GraphCell cell, boolean throwEx) {
        String sql = "MATCH(n:Node{id:$id}) DETACH DELETE n RETURN n";
        Map<String, Object> params = new HashMap<>();
        params.put("id", cell.getId());
        return run(sql, params, result -> result.hasNext());
    }

    @Override
    public boolean deleteOne(GraphCell cell, boolean throwEx) {
        if (cell.isNode()) {
            if (ValueUtil.isNotEmpty(cell.getId())) {
                return deleteById(cell, throwEx);
            }
            Map<String, Object> params = new HashMap<>();
            if (Objects.nonNull(cell.getProperties())) {
                params.putAll(cell.getProperties());
            }
            params.put("type", cell.getType());
            params.put("name", cell.getName());
            StringBuilder stringBuilder = convertParams(params);
            if (stringBuilder.isEmpty()) {
                return false;
            }
            String sql = "MATCH(n:" + GraphCell.NODE_NAME + "{" + stringBuilder + "}) DELETE n RETURN n LIMIT 2";
            boolean ret = doWithTransaction(transaction -> {
                Result result = run(transaction, sql, params);
                if (result.hasNext()) {
                    result.next();

                    if (result.hasNext()) {
                        throw new BusinessRuntimeException(CommonErrorCode.ERROR);
                    }
                }
                return true;
            });
            if (!ret && throwEx) {
                throw new BusinessRuntimeException(CommonErrorCode.ERROR, "delete failed.");
            }
            return ret;
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            checkGraphCell(graphEdge);

            String sql = "MATCH(n:" + GraphCell.NODE_NAME + "{id:$sourceId})-[" + GraphCell.EDGE_NAME + "]->(n1:" + GraphCell.NODE_NAME + "{id:$targetId}) WHERE r.type=$type DELETE r RETURN r";
            Map<String, Object> params = new HashMap<>();
            params.put("sourceId", graphEdge.getSourceId());
            params.put("targetId", graphEdge.getTargetId());
            params.put("type", graphEdge.getType());
            boolean ret = run(sql, params, Result::hasNext);
            if (!ret && throwEx) {
                throw new BusinessRuntimeException(CommonErrorCode.ERROR, "delete failed");
            }
            return ret;
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
            return run(sql, params, Result::hasNext);
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            params.put("type", cell.getType());
            params.put("sourceId", graphEdge.getSourceId());
            params.put("targetId", graphEdge.getTargetId());
            StringBuilder paramBuilder = convertParams("n.", "=", params);
            String sql = "MATCH(n:Node{id:$sourceId})-[r]->(n1:Node{id:$targetId}) WHERE r.type=$type SET " + paramBuilder + " RETURN r";
            return run(sql, params, Result::hasNext);
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
            GraphNode node;
            if (ValueUtil.isNotEmpty(cell.getId())) {
                node = findById(cell, throwEx);
            } else {
                String cql = "MATCH(n:Node{" + convertParams(params) + "}) return n LIMIT 2";
                node = run(cql, params, result -> {
                    GraphNode graphNode = null;
                    if (result.hasNext()) {
                        Record next = result.next();
                        if (result.hasNext()) {
                            if (!throwEx) {
                                return null;
                            }
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
            String sql = "MATCH(n:Node{id:$sourceId})-[r]->(n1:Node{id:$targetId}) WHERE r.type=$type RETURN r LIMIT 1";
            GraphEdge edge = run(sql, params, result -> {
                GraphEdge graphNode = null;
                if (result.hasNext()) {
                    graphNode = (GraphEdge) convert2GraphCell(result.next());

                    if (result.hasNext()) {
                        if (throwEx) {
                            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "too many.");
                        } else {
                            return null;
                        }
                    }
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

    @Override
    public boolean save(GraphCell cell, boolean throwEx) {
        if (cell instanceof GraphNode) {
            GraphNode node = findById(cell, false);
            if (Objects.isNull(node)) {
                return addOne(cell, throwEx);
            }
            return updateById(cell, throwEx);
        } else if (cell instanceof GraphNode) {
            GraphCell edge = findOne(cell, false);
            if (Objects.isNull(edge)) {
                return addOne(cell, throwEx);
            }
            return updateById(cell, throwEx);
        }

        return false;
    }

    @Override
    public List<GraphNode> findAllById(GraphCell cell) {
        String sql = "MATCH(n1:" + GraphCell.NODE_NAME + "{id:$id})-[" + GraphCell.EDGE_NAME + "]->(n) RETURN n";
        Map<String, Object> params = new HashMap<>();
        if (ValueUtil.isEmpty(cell.getId())) {
            throw new BusinessRuntimeException(CommonErrorCode.PARAMETER_MISSING, "id is required.");
        }
        params.put("id", cell.getId());
        return run(sql, params, result -> {
            List<GraphNode> list = new ArrayList<>();
            while (result.hasNext()) {
                Record next = result.next();
                GraphCell graphCell = convert2GraphCell(next);
                if (Objects.nonNull(graphCell)) {
                    list.add((GraphNode) graphCell);
                }
            }
            return list;
        });
    }

    @Override
    public boolean updateProperty(GraphCell cell, Map<String, Object> property, boolean throwEx) {
        if (property.isEmpty()) {
            return false;
        }
        checkGraphCell(cell);
        StringBuilder removeFieldBuilder = new StringBuilder();
        StringBuilder updatedFieldBuilder = new StringBuilder();
        if (cell instanceof GraphNode) {
            property.put("id", cell.getId());
        } else if (cell instanceof GraphEdge) {
            GraphEdge graphEdge = (GraphEdge) cell;
            property.put("sourceId", graphEdge.getSourceId());
            property.put("targetId", graphEdge.getTargetId());
            property.put("type", graphEdge.getType());
        }
        for (Map.Entry<String, Object> entry : property.entrySet()) {
            Object value = entry.getValue();
            if (Objects.isNull(value)) {
                if (!removeFieldBuilder.isEmpty()) {
                    removeFieldBuilder.append(",");
                }
                removeFieldBuilder.append(cell.isNode() ? "n." : "r.").append(entry.getKey());
            } else {
                if (!updatedFieldBuilder.isEmpty()) {
                    updatedFieldBuilder.append(",");
                }
                updatedFieldBuilder.append(cell.isNode() ? "n." : "r.").append(entry.getKey()).append("=$").append(entry.getKey());
            }
        }

        String removeSql;
        String updateSql;
        if (cell.isNode()) {
            removeSql = "MATCH(n:Node{id:$id}) REMOVE " + removeFieldBuilder + " RETURN n";
            updateSql = "MATCH(n:Node{id:$id}) SET " + updatedFieldBuilder + " RETURN n";
        } else {
            removeSql = "MATCH(n:Node{id:$sourceId})-[r]->(n1:Node{id:$targetId}) WHERE r.type=$type REMOVE " + removeFieldBuilder + " RETURN r";
            updateSql = "MATCH(n:Node{id:$sourceId})-[r]->(n1:Node{id:$targetId}) WHERE r.type=$type SET " + updatedFieldBuilder + " RETURN r";
        }

        boolean ret = doWithTransaction(transaction -> {
            if (!removeFieldBuilder.isEmpty()) {
                Result run = run(transaction, removeSql, property);
                if (!run.hasNext()) {
                    if (updatedFieldBuilder.isEmpty()) {
                        return false;
                    }
                    throw new BusinessRuntimeException(CommonErrorCode.ERROR, "remove fields error.");
                }
            }
            if (!updatedFieldBuilder.isEmpty()) {
                Result run = run(transaction, updateSql, property);
                return run.hasNext();
            }
            return false;
        });

        if (!ret && throwEx) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "update property failed.");
        }
        return ret;
    }

    @Override
    public List<List<GraphCell>> query(String sql, Map<String, Object> params) {
        List<List<GraphCell>> list = new ArrayList<>();
        graphRunner.run(sql, params, result -> {
            while (result.hasNext()) {
                Record next = result.next();
                List<GraphCell> cells = convert2GraphCells(next);
                if (!cells.isEmpty()) {
                    list.add(cells);
                }
            }
            return null;
        });
        return list;
    }
}
