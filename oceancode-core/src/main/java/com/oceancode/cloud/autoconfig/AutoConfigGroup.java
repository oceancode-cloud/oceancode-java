package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AutoConfigGroup {
    private String notifier;
    private String group;
    private AutoConfigContext context;
    private AutoConfigRule rule;
    private Map<String, TypeGroup> typeGroupMap = new HashMap<>();

    public AutoConfigGroup(String notifier, String group, AutoConfigContext context) {
        this.notifier = notifier;
        this.group = group;
        this.context = context;
    }

    public void add(AutoConfig item) {
        TypeGroup typeGroup = typeGroupMap.computeIfAbsent(item.getType(), k -> new TypeGroup(item.getType()));
        typeGroup.add(item);
        rule = context.getRule(item.getGroup());
    }

    public String getNotifier() {
        return notifier;
    }

    public void apply() {
        rule.apply(this);
    }

    public String getGroup() {
        return group;
    }

    public TypeGroup getRemoveGroup() {
        return getGroup(DefaultAutoConfigType.REMOVE);
    }

    public TypeGroup getRemoveManyGroup() {
        return getGroup(DefaultAutoConfigType.REMOVE_MANY);
    }

    public TypeGroup getAddGroup() {
        return getGroup(DefaultAutoConfigType.ADD);
    }

    public TypeGroup getAddManyGroup() {
        return getGroup(DefaultAutoConfigType.ADD_MANY);
    }

    public TypeGroup getUpdateGroup() {
        return getGroup(DefaultAutoConfigType.UPDATE);
    }

    public TypeGroup getUpdateManyGroup() {
        return getGroup(DefaultAutoConfigType.UPDATE_MANY);
    }

    public TypeGroup getCustomGroup(String type) {
        return typeGroupMap.get(type);
    }

    private TypeGroup getGroup(AutoConfigType type) {
        if (Objects.isNull(type)) {
            return null;
        }
        if (Objects.isNull(type.getType())) {
            return null;
        }
        return typeGroupMap.get(type.getType().toString());
    }

    public AutoConfigContext getContext() {
        return context;
    }
}
