package com.oceancode.cloud.common.security;

import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EncryptData {
    private transient List<Object> dataList;
    private String key;
    private String data;
    private Map<String, Object> rawData;

    public EncryptData add(Object... values) {
        if (Objects.isNull(values)) {
            return this;
        }
        if (Objects.isNull(dataList)) {
            dataList = new ArrayList<>();
        }
        for (Object value : values) {
            dataList.add(value);
        }
        return this;
    }

    public EncryptData addExtra(String key, Object value) {
        if (Objects.isNull(key) || key.trim().isEmpty()) {
            return this;
        }
        if (Objects.isNull(rawData)) {
            rawData = new HashMap<>();
        }
        rawData.put(key, value);
        return this;
    }

    public void encrypt(String publicKey) {
        if (ValueUtil.isEmpty(dataList)) {
            return;
        }
        AesCryptoService aesCryptoService = ComponentUtil.getBean(AesCryptoService.class);
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class);
        String rawKey = aesCryptoService.generatorKey();
        this.key = rsa2CryptoService.decryptByPublicKey(rawKey, publicKey);
        this.data = aesCryptoService.encrypt(JsonUtil.toJson(dataList), rawKey);
        this.dataList = null;
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
