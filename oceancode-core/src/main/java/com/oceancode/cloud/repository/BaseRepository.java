package com.oceancode.cloud.repository;

import java.util.List;
import java.util.Set;

public interface BaseRepository<T, PK> {
    T findById(PK id);

    List<T> findByIds(Set<PK> ids);

    boolean addOne(T entity);

    boolean addBatch(List<T> list);

    boolean deleteById(PK id);

    boolean deleteByIds(Set<PK> ids);

    boolean updateById(T entity);

    boolean updateBatchById(List<T> list);
}
