package com.oceancode.cloud.api.security;

import java.util.List;
import java.util.Map;

public interface EncryptValue {
    List<Object> encryptValues();

    default Map<String, Object> rawValues() {
        return null;
    }
}
