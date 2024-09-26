package com.oceancode.cloud.api.security;

import com.oceancode.cloud.api.TypeEnum;

public enum KeyType implements TypeEnum<String> {
    PUBLIC("publicKey", "Public Key", null),
    PRIVATE("privateKey", "PrivateKey", null),
    KEY("key", "key", null),
    ;

    private String value;
    private String name;
    private String desc;

    KeyType(String value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    public static KeyType from(String value) {
        return TypeEnum.from(value, KeyType.class);
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
