package com.oceancode.cloud.api.crypto;

@FunctionalInterface
public interface DecryptCryptoFunction {
    Object decrypt(Object value);
}
