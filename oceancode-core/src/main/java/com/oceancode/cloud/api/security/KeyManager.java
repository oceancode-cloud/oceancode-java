package com.oceancode.cloud.api.security;

import java.util.List;

public interface KeyManager {
    /**
     * get keys
     *
     * @param id
     * @param type
     */
    String getKey(String id, String type, KeyType keyType);

    /**
     * update keys
     *
     * @param id
     * @param type
     */
    void updateKeys(String id, String type, KeyType keyType, String key);

    void deleteKeys(String id, String type, KeyType keyType);
}
