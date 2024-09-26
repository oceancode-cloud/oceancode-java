package com.oceancode.cloud.api.crypto;

@FunctionalInterface
public interface EncryptCryptoFunction {
    Object encrypt(Object value);
}
