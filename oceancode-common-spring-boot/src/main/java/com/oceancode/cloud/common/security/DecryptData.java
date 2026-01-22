package com.oceancode.cloud.common.security;

import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;

import java.util.Map;

public class DecryptData {
    private String key;
    private String data;
    private Object decryptedData;
    private boolean success = false;
    private Map<String, Object> rawData;

    public <T> T decryptData(String privateKey, Class<T> returnClass) {
        if (success) {
            return returnClass.cast(this.decryptedData);
        }
        AesCryptoService aesCryptoService = ComponentUtil.getBean(AesCryptoService.class);
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class);
        String rawKey = rsa2CryptoService.decryptByPrivateKey(this.key, privateKey);
        String target = aesCryptoService.decrypt(this.data, rawKey);
        T targetData = JsonUtil.toBean(target, returnClass);
        success = true;
        return targetData;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, Object> getRawData() {
        return rawData;
    }

    public void setRawData(Map<String, Object> rawData) {
        this.rawData = rawData;
    }
}
