package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;

import java.util.HashSet;
import java.util.Set;

public class TypeGroup {
    private String type;
    private AutoConfigType configType;
    private Set<AutoConfig> items = new HashSet<>();

    public TypeGroup(String type) {
        this.type = type;
        try {
            configType = DefaultAutoConfigType.from(Integer.parseInt(type));
        } catch (Exception e) {
            configType = DefaultAutoConfigType.CUSTOM;
        }
    }

    public void add(AutoConfig item) {
        items.add(item);
    }

    public boolean isRemove() {
        return DefaultAutoConfigType.REMOVE.equals(configType) ||
                isRemoveMany();
    }

    public boolean isRemoveMany() {
        return DefaultAutoConfigType.REMOVE_MANY.equals(configType);
    }

    public boolean isUpdate() {
        return DefaultAutoConfigType.UPDATE.equals(configType) || isUpdateMany();
    }

    public boolean isUpdateMany() {
        return DefaultAutoConfigType.UPDATE_MANY.equals(configType);
    }

    public boolean isAdd() {
        return DefaultAutoConfigType.ADD.equals(configType) || isAddMany();
    }

    public boolean isAddMany() {
        return DefaultAutoConfigType.ADD_MANY.equals(configType);
    }

    public Set<AutoConfig> getItems() {
        return items;
    }


}
