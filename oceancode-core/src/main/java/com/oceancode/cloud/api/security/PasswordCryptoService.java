package com.oceancode.cloud.api.security;

public interface PasswordCryptoService extends CryptoService<String, String> {

    @Override
    default String decode(String input) {
        return null;
    }
}
