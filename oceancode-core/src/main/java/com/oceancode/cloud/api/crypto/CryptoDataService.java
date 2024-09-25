package com.oceancode.cloud.api.crypto;

import com.oceancode.cloud.api.strategy.StrategyAdaptor;

public interface CryptoDataService extends StrategyAdaptor<CryptoType> {
    <T extends Encrypt> void encode(T data, CryptoType cryptoType);

    <T extends Decrypt> void decode(T data, CryptoType cryptoType);
}
