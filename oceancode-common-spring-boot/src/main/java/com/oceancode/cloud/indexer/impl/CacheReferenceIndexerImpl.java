package com.oceancode.cloud.indexer.impl;

import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.indexer.IndexerItem;
import com.oceancode.cloud.api.indexer.ReferenceIndexer;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class CacheReferenceIndexerImpl implements ReferenceIndexer {
    @Resource
    private CacheService cacheService;

    @Override
    public void add(String id, Set<IndexerItem> items) {
        for (IndexerItem item : items) {
            CacheKey referenceKey = buildKey(id, item.getType(), true);
            cacheService.addSet(referenceKey, Collections.singleton(item.getId()));

            CacheKey referencedKey = buildKey(item.getId(), item.getType(), false);
            cacheService.addSet(referencedKey, Collections.singleton(id));
        }
    }

    @Override
    public void add(Set<IndexerItem> items, String id) {
        for (IndexerItem item : items) {
            CacheKey referenceKey = buildKey(item.getId(), item.getType(), true);
            cacheService.addSet(referenceKey, Collections.singleton(id));

            CacheKey referencedKey = buildKey(id, item.getType(), false);
            cacheService.addSet(referencedKey, Collections.singleton(item.getId()));
        }
    }

    @Override
    public void add(IndexerItem items, String id) {
        add(Collections.singleton(items), id);
    }

    @Override
    public void add(String id, IndexerItem item) {
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
        if (ValueUtil.isEmpty(item.getType())) {
            return;
        }
        CacheKey key = buildKey(item.getId(), item.getType(), true);
        Result<Set<Object>> result = cacheService.getSet(key, 10000);
        if (!result.isSuccess()) {
            return;
        }
        if (ValueUtil.isEmpty(result.getResults())) {
            return;
        }
        for (Object it : result.getResults()) {
            if (Objects.isNull(it)) {
                continue;
            }
            String id = it + "";

            CacheKey referencedKey = buildKey(id, item.getType(), false);
            cacheService.deleteSet(referencedKey, Collections.singleton(item.getId()));
        }
    }

    private CacheKey buildKey(String id, String type, boolean isReference) {
        return KeyParam.of("indexer-reference" + (isReference ? "" : "d"), true, -1L)
                .express("indexer:reference" + (isReference ? "" : "d") + ":" + id + ":" + type);
    }
}
