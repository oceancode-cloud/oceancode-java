package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.TypeEnum;

public interface AutoConfigType extends TypeEnum<Integer> {
    default Integer getType() {
        return getValue();
    }
}
