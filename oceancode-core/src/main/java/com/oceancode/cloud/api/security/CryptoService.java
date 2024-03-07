package com.oceancode.cloud.api.security;

public interface CryptoService<T, R> {

    R encode(T input);

    T decode(R input);

    boolean matches(T raw, R encoded);
}
