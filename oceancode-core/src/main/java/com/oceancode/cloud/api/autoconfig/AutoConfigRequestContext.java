package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AutoConfigRequestContext {
    private AutoConfigRequest autoConfigRequest;
    private Map<String, AutoConfigValue> autoConfigValueMap;

    public AutoConfigRequestContext(AutoConfigRequest autoConfigRequest) {
        this.autoConfigRequest = autoConfigRequest;

        init();
    }

    protected void init() {
        this.autoConfigValueMap = new HashMap<>();
        if (Objects.isNull(autoConfigRequest)) {
            return;
        }
        if (Objects.isNull(autoConfigRequest.getExtra())) {
            autoConfigRequest.setExtra(Collections.emptyMap());
        }
        if (ValueUtil.isEmpty(autoConfigRequest.getItems())) {
            return;
        }
        for (int index = 0; index < autoConfigRequest.getItems().size(); index++) {
            AutoConfigRequestItem item = autoConfigRequest.getItems().get(index);
            String group = getGroup(item);
            AutoConfigValue autoConfigValue = autoConfigValueMap.computeIfAbsent(group, k -> createAutoConfigValue(item));
            autoConfigValue.addRequest(item);
        }
    }

    public String getGroup(AutoConfigRequestItem item) {
        return item.getGroup();
    }

    protected AutoConfigValue createAutoConfigValue(AutoConfigRequestItem item) {
        return new AutoConfigValue(getGroup(item));
    }

    public AutoConfigValue getValue(String group) {
        return autoConfigValueMap.get(group);
    }

    public boolean hasChangeValue(String group) {
        AutoConfigValue value = getValue(group);
        if (Objects.isNull(value)) {
            return false;
        }
        return value.hasChangeValue();
    }

    public Collection<AutoConfigValue> getValues() {
        return autoConfigValueMap.values();
    }
}
