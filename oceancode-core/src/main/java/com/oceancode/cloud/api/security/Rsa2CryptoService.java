package com.oceancode.cloud.api.security;

public interface Rsa2CryptoService extends CryptoService<String, String> {
    String encryptByPrivateKey(String input, String key);

    String encryptByPublicKey(String input, String key);

    String decryptByPublicKey(String input, String key);

    String decryptByPrivateKey(String input, String key);
}
