package com.oceancode.cloud.api.security;

import com.oceancode.cloud.api.strategy.StrategyAdaptor;

public interface CryptoService<T,R> extends StrategyAdaptor<String> {

    R encrypt(T input,String key);

    R decrypt(T input,String key);
}
