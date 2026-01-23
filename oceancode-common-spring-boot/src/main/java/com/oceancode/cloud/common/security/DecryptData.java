package com.oceancode.cloud.common.security;

import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DecryptData {
    private String key;
    private String data;
    private Object decryptedData;
    private boolean success = false;
    private Map<String, Object> rawData;

    public <T> T decrypt(String privateKey, Class<T> returnClass) {
        if (success) {
            return returnClass.cast(this.decryptedData);
        }
        success = true;
        AesCryptoService aesCryptoService = ComponentUtil.getBean(AesCryptoService.class);
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class);
        String rawKey = rsa2CryptoService.decryptByPrivateKey(this.key, privateKey);
        String target = aesCryptoService.decrypt(this.data, rawKey);
        if (Util.isEmpty(target)) {
            return null;
        }
        T targetData = null;
        boolean hasConverted = false;
        if (Util.isNotEmpty(this.rawData)) {
            boolean isArray = target.startsWith("[") && target.endsWith("]");
            if (!isArray) {
                Map<String, Object> map = new HashMap<>(this.rawData);
                map.putAll(JsonUtil.toBean(target, Map.class));
                targetData = JsonUtil.mapToBean(map, returnClass);
                hasConverted = true;
            }
        }
        if (!hasConverted) {
            targetData = JsonUtil.toBean(target, returnClass);
        }
        return targetData;
    }

    public <T> T decrypt(Class<T> returnClass) {
        if (success) {
            return returnClass.cast(this.decryptedData);
        }
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        return decrypt(commonConfig.getPrivateKey(), returnClass);
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
