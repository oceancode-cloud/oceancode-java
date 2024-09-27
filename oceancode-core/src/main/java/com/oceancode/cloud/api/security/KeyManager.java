package com.oceancode.cloud.api.security;

import java.util.List;

public interface KeyManager {
    /**
     * get keys
     *
     * @param id
     */
    String getKey(String id, KeyType keyType);

    /**
     * update keys
     *
     * @param id
     */
    void updateKeys(String id, KeyType keyType, String key);

    void deleteKeys(String id, KeyType keyType);
}
