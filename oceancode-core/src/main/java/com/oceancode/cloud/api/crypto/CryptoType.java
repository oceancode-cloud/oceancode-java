package com.oceancode.cloud.api.crypto;

import com.oceancode.cloud.api.TypeEnum;

public enum CryptoType implements TypeEnum<String> {
    RSA_AES("rsa-aes", "Rsa AES", null),
    OTHER("other", "Other", null),
    ;

    private String value;
    private String name;
    private String desc;

    CryptoType(String value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    public static CryptoType from(String value) {
        return TypeEnum.from(value, CryptoType.class);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
