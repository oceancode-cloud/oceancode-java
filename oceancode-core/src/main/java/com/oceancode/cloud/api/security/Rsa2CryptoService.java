package com.oceancode.cloud.api.security;

public interface Rsa2CryptoService extends CryptoService<String, String> {
    String encryptByPrivateKey(String input, String key);

    String encryptByPublicKey(String input, String key);

    String decryptByPublicKey(String input, String key);

    String decryptByPrivateKey(String input, String key);

    RsaKeyPair generatorKey();

    String getPublicKeyFromPem(String publicPemKey);

    @Override
    default boolean isSupport(String type) {
        return "rsa2".equalsIgnoreCase(type) || "rsa".equalsIgnoreCase(type);
    }
}
