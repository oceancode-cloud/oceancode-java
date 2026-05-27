package com.oceancode.cloud.api.cache;

import java.util.List;
import java.util.Map;

public interface DataCacheManager {

    void setString(String key, Object value);

    String getString(String key);

    void setObject(String key, Object value);

    Map<String, Object> getObject(String key);

    void setObjectList(String key, List<?> list);

    <T> List<T> getObjectList(String key, Class<T> dataType);

    String getSource();

    <T> T load(String key, Class<T> dataType, DataLoader<T> loader);

    <T> List<T> loadList(String key, Class<T> dataType, DataLoader<List<T>> loader);

    void deleteByPrefix(String key);
    void delete(String key);
}
