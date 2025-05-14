package com.oceancode.cloud.api.graph;

import java.util.List;

public interface GraphService {
    GraphNode findById(String id);

    boolean addOne(GraphCell cell, boolean throwEx);

    boolean addBatch(List<GraphCell> cells, boolean throwEx);

    boolean deleteById(String id, boolean throwEx);

    boolean deleteOne(GraphCell cell, boolean throwEx);

    boolean delete(GraphCell cell, boolean throwEx);

    boolean addOne(String sourceId, GraphNode node, boolean throwEx);

    boolean addBatch(String sourceId, List<GraphNode> nodes, boolean throwEx);

    boolean updateById(GraphCell cell, boolean throwEx);

    GraphCell findOne(GraphCell cell, boolean throwEx);
}
