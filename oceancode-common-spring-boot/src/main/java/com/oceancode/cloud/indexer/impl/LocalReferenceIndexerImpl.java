package com.oceancode.cloud.indexer.impl;

import com.oceancode.cloud.api.indexer.IndexerItem;
import com.oceancode.cloud.api.indexer.ReferenceIndexer;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LocalReferenceIndexerImpl implements ReferenceIndexer {
    private static final Map<String, Map<String, Set<String>>> REFERENCE_MAP = new HashMap<>();
    private static final Map<String, Map<String, Set<String>>> REFERENCED_MAP = new HashMap<>();

    @Override
    public synchronized void add(String id, Set<IndexerItem> items) {
        for (IndexerItem item : items) {
            putElement(REFERENCE_MAP, id, item.getType(), item.getId());
            putElement(REFERENCED_MAP, item.getId(), item.getType(), id);
        }
    }

    @Override
    public synchronized void add(Set<IndexerItem> items, String id) {
        for (IndexerItem item : items) {
            putElement(REFERENCE_MAP, item.getId(), item.getType(), id);
            putElement(REFERENCED_MAP, id, item.getType(), item.getId());
        }
    }

    @Override
    public void add(IndexerItem items, String id) {
        add(Collections.singleton(items), id);
    }

    private void putElement(Map<String, Map<String, Set<String>>> map, String id, String type, String targetId) {
        if (!map.containsKey(id)) {
            map.put(id, new HashMap<>());
        }
        if (!map.get(id).containsKey(type)) {
            map.get(id).put(type, new HashSet<>());
        }
        map.get(id).get(type).add(targetId);
    }

    @Override
    public synchronized void add(String id, IndexerItem item) {
        add(id, Collections.singleton(item));
    }

    @Override
    public void delete(IndexerItem item) {
        if (Objects.isNull(item)) {
            return;
        }
        if (ValueUtil.isEmpty(item.getId())) {
            return;
        }
        if (!REFERENCE_MAP.containsKey(item.getId())) {
            return;
        }
        Map<String, Set<String>> map = REFERENCE_MAP.get(item.getId());
        if (ValueUtil.isEmpty(item.getType())) {
            REFERENCE_MAP.remove(item.getId());

            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                processRemoveReferenced(entry.getValue(), entry.getKey(), item.getId());
            }
            return;
        }

        processRemoveReferenced(map.get(item.getType()), item.getType(), item.getId());
        map.remove(item.getType());
    }

    private void processRemoveReferenced(Set<String> idList, String type, String id) {
        if (ValueUtil.isEmpty(idList)) {
            return;
        }
        for (String it : idList) {
            if (!REFERENCED_MAP.containsKey(it)) {
                continue;
            }
            Map<String, Set<String>> map = REFERENCED_MAP.get(type);
            if (!map.containsKey(type)) {
                continue;
            }
            map.get(type).remove(id);
        }
    }
}
