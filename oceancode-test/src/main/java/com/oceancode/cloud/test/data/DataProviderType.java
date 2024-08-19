package com.oceancode.cloud.test.data;

import com.oceancode.cloud.api.TypeEnum;

public enum DataProviderType implements TypeEnum<String> {
    JSON_FILE("json-file", null, null),
//    EXECLE("excle", null, null),
//    API("api", null, null),
    ;

    private String value;
    private String name;
    private String desc;

    DataProviderType(String value, String name, String desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
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
