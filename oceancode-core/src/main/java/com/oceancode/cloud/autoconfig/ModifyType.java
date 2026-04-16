package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.TypeEnum;

public interface ModifyType extends TypeEnum<Integer> {
    default Integer getType() {
        return getValue();
    }
}
