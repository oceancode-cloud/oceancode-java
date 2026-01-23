package com.oceancode.cloud.common.security;

import com.oceancode.cloud.api.security.AesCryptoService;
import com.oceancode.cloud.api.security.EncryptValue;
import com.oceancode.cloud.api.security.Rsa2CryptoService;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.Util;
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

    public static EncryptData of(EncryptValue value) {
        EncryptData encryptData = new EncryptData();
        encryptData.add(value.encryptValues());
        encryptData.addExtra(value.rawValues());
        return encryptData;
    }

    public EncryptData add(Object... values) {
        if (Objects.isNull(values)) {
            return this;
        }
        if (values.length == 0) {
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

    public EncryptData addExtra(Map<String, Object> value) {
        if (Util.isEmpty(value)) {
            return this;
        }
        if (Objects.isNull(rawData)) {
            rawData = new HashMap<>();
        }
        rawData.putAll(value);
        return this;
    }

    public void encrypt(String publicKey) {
        if (ValueUtil.isEmpty(dataList)) {
            return;
        }
        AesCryptoService aesCryptoService = ComponentUtil.getBean(AesCryptoService.class);
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class);
        String rawKey = aesCryptoService.generatorKey();
        this.key = rsa2CryptoService.encryptByPublicKey(rawKey, publicKey);
        this.data = aesCryptoService.encrypt(JsonUtil.toJson(dataList), rawKey);
        this.dataList = null;
    }

    public void encrypt() {
        if (ValueUtil.isEmpty(dataList)) {
            return;
        }
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        encrypt(commonConfig.getPublicKey());
    }

    public void encryptByKey(String privateKey, String key) {
        if (ValueUtil.isEmpty(dataList)) {
            return;
        }
        AesCryptoService aesCryptoService = ComponentUtil.getBean(AesCryptoService.class);
        Rsa2CryptoService rsa2CryptoService = ComponentUtil.getBean(Rsa2CryptoService.class);
        String rawKey = rsa2CryptoService.decryptByPrivateKey(key, privateKey);
        this.data = aesCryptoService.encrypt(JsonUtil.toJson(dataList), rawKey);
        this.dataList = null;
    }

    public void encryptByKey(String key) {
        if (ValueUtil.isEmpty(dataList)) {
            return;
        }
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        encryptByKey(commonConfig.getPrivateKey(), key);
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
