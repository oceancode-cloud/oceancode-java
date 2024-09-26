package com.oceancode.cloud.api.security;

public interface AesCryptoService extends CryptoService<String, String> {

    @Override
    default boolean isSupport(String type) {
        return "aes".equalsIgnoreCase(type);
    }
}
