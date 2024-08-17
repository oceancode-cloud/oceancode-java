package com.oceancode.cloud.common.function;

import com.oceancode.cloud.api.TypeEnum;

public enum FunctionType implements TypeEnum<String> {
    CUSTOM("custom", 0, null, null),
    DUBBO("dubbo", 1, null, null),
    GRPC("grpc", 2, null, null),
    API("api", 3, null, null),
    OTHER("other", 100, null, null),
    ;

    private String value;
    private int order;
    private String name;
    private String desc;

    FunctionType(String value, int order, String name, String desc) {
        this.value = value;
        this.order = order;
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

    public int getOrder() {
        return order;
    }
}
