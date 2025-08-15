package com.oceancode.cloud.api.indexer;

import java.util.Set;

public interface ReferenceIndexer extends Indexer {

    void add(String id, Set<IndexerItem> items);

    void add(Set<IndexerItem> items, String id);

    void add(IndexerItem item, String id);

    void add(String id, IndexerItem item);

    void delete(IndexerItem item);
}
