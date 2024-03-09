package com.oceancode.cloud.api.security;

public interface CryptoService<T,R> {

    R encrypt(T input,String key);

    R decrypt(T input,String key);
}
