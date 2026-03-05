package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResult;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AutoConfigContext {
    private AutoConfigRequest request;
    private Map<String, AutoConfigGroup> configGroupMap;
    private AutoConfigResponse result = new AutoConfigResponse();
    private Map<String, AutoConfigRule> ruleMap;

    public AutoConfigContext(AutoConfigRequest request) {
        this.request = request;
        result.setSuccess(false);
    }

    public void setRuleMap(Map<String, AutoConfigRule> ruleMap) {
        this.ruleMap = ruleMap;
    }

    private void init() {
        if (ValueUtil.isEmpty(request.getItems())) {
            result.setSuccess(true);
            return;
        }
        for (int index = 0; index < request.getItems().size(); index++) {
            AutoConfig item = request.getItems().get(index);
            if (ValueUtil.isEmpty(item.getGroup())) {
                result.setSuccess(false);
                result.setErrorMessage("The group of item[" + index + "] is empty.");
                return;
            }
            if (ValueUtil.isEmpty(item.getType())) {
                result.setSuccess(false);
                result.setErrorMessage("The type of item[" + index + "] is empty.");
                return;
            }
            AutoConfigRule autoConfigRule = ruleMap.get(item.getGroup());
            if (Objects.isNull(autoConfigRule)) {
                result.setSuccess(false);
                result.setErrorMessage("The group of item[" + index + "] is invalid.");
                return;
            }
            String notifier = item.getNotifier();
            if (ValueUtil.isEmpty(notifier)) {
                if (!isAdd(item.getType())) {
                    result.setSuccess(false);
                    result.setErrorMessage("The notifier of item[" + index + "] is empty.");
                    return;
                }
            }
            AutoConfigGroup autoConfigGroup = configGroupMap.computeIfAbsent(notifier, k -> new AutoConfigGroup(notifier, this));
            autoConfigGroup.add(item);

        }
    }

    private boolean isAdd(String type) {
        return DefaultAutoConfigType.ADD.getType().toString().equals(type) ||
                DefaultAutoConfigType.ADD_MANY.toString().equals(type);
    }

    public Collection<AutoConfigGroup> getConfigGroups() {
        if (Objects.nonNull(configGroupMap)) {
            return configGroupMap.values();
        }
        configGroupMap = new HashMap<>();
        init();
        return configGroupMap.values();
    }

    public AutoConfigResponse getResult() {
        return result;
    }

    public void apply(AutoConfigGroup group) {
        group.apply();
    }

    public AutoConfigRule getRule(String group) {
        return ruleMap.get(group);
    }

    public void toAdd(Object value) {
        AutoConfigResult configResult = new AutoConfigResult();
        configResult.setNewValue(value.toString());
        result.toAdd(configResult);
    }

    public void toUpdate(Object value) {
        AutoConfigResult configResult = new AutoConfigResult();
        configResult.setNewValue(value.toString());
        result.toUpdate(configResult);
    }

    public void toDelete(Object value) {
        AutoConfigResult configResult = new AutoConfigResult();
        configResult.setNewValue(value.toString());
        result.toDelete(configResult);
    }
}
