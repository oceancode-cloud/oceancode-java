package com.oceancode.cloud.api.graph;

import java.util.List;
import java.util.Map;

public interface GraphService {
    GraphNode findById(GraphCell cell, boolean throwEx);

    default GraphNode findById(GraphCell cell) {
        return findById(cell, true);
    }

    boolean addOne(GraphCell cell, boolean throwEx);

    default boolean addOne(GraphCell cell) {
        return addOne(cell, true);
    }

    boolean addBatch(List<GraphCell> cells, boolean throwEx);

    default boolean addBatch(List<GraphCell> cells) {
        return addBatch(cells, true);
    }

    boolean deleteById(GraphCell cell, boolean throwEx);

    default boolean deleteById(GraphCell cell) {
        return deleteById(cell, true);
    }

    boolean deleteOne(GraphCell cell, boolean throwEx);

    default boolean deleteOne(GraphCell cell) {
        return deleteOne(cell, true);
    }

    boolean delete(GraphCell cell, boolean throwEx);

    default boolean delete(GraphCell cell) {
        return delete(cell, true);
    }

    boolean addOne(String sourceId, GraphNode node, boolean throwEx);

    default boolean addOne(String sourceId, GraphNode node) {
        return addOne(sourceId, node, true);
    }

    boolean addBatch(String sourceId, List<GraphNode> nodes, boolean throwEx);

    default boolean addBatch(String sourceId, List<GraphNode> nodes) {
        return addBatch(sourceId, nodes, true);
    }

    boolean updateById(GraphCell cell, boolean throwEx);

    default boolean updateById(GraphCell cell) {
        return updateById(cell, true);
    }

    GraphCell findOne(GraphCell cell, boolean throwEx);

    default GraphCell findOne(GraphCell cell) {
        return findOne(cell, true);
    }

    boolean save(GraphCell cell, boolean throwEx);

    default boolean save(GraphCell cell) {
        return save(cell, true);
    }

    List<GraphNode> findAllById(GraphCell cell);

    boolean updateProperty(GraphCell cell, Map<String, Object> property, boolean throwEx);

    default boolean updateProperty(GraphCell cell, Map<String, Object> property) {
        return updateProperty(cell, property, true);
    }

    List<List<GraphCell>> query(String sql, Map<String, Object> params);
}
