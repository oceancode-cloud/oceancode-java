package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AutoConfigRequestContext {
    private AutoConfigRequest autoConfigRequest;
    private Map<String, AutoConfigValue> autoConfigValueMap;
    private AutoConfigResult autoConfigResult;

    public AutoConfigRequestContext(AutoConfigRequest autoConfigRequest) {
        this.autoConfigRequest = autoConfigRequest;
        this.autoConfigResult = new AutoConfigResult();
        this.autoConfigResult.setSuccess(true);

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
            String group = getGroup(item).getGroup();
            AutoConfigValue autoConfigValue = autoConfigValueMap.computeIfAbsent(group, k -> createAutoConfigValue(item));
            autoConfigValue.addRequest(item);
        }
        Collection<AutoConfigValue> values = autoConfigValueMap.values();
        for (AutoConfigValue it : values) {
            List<AutoConfigRule> rules = AutoConfigRequestUtil.getRules(it);
            for (AutoConfigRule rule : rules) {
                try {
                    rule.apply(it);
                } catch (Exception e) {
                    AutoConfigResult result = this.getResult();
                    result.setThrowable(e);
                    result.setSuccess(false);
                    if (e instanceof ErrorCodeRuntimeException exception) {
                        result.setErrorCode(exception.getErrorCode());
                        result.setDetail(exception.getMessage());
                    }
                    return;
                }
            }
        }
    }

    public AutoConfigGroupType getGroup(AutoConfigRequestItem item) {
        return new AutoConfigGroupType() {

            @Override
            public String getGroup() {
                return item.getGroup();
            }

            @Override
            public String getRawGroup() {
                return item.getGroup();
            }

            @Override
            public boolean isCustom() {
                return true;
            }
        };
    }

    private AutoConfigValue createAutoConfigValue(AutoConfigRequestItem item) {
        return new AutoConfigValue(getGroup(item), this);
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

    public AutoConfigResult getResult() {
        return autoConfigResult;
    }


}
