package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoConfigValue {
    private final Map<String, List<String>> addedValues;
    private final Map<String, Map<String, Object>> updatedValues;
    private final Set<String> deletedValues;
    private final Set<String> updatedDataIds;
    private String group;

    public AutoConfigValue(String group) {
        this.addedValues = new HashMap<>();
        updatedValues = new HashMap<>();
        deletedValues = new HashSet<>();
        updatedDataIds = new HashSet<>();
        this.group = group;
    }

    public void addRequest(AutoConfigRequestItem item) {
        if (Objects.isNull(item)) {
            return;
        }
        AutoConfigType type = AutoConfigType.from(item.getType());
        if (AutoConfigType.UPDATE.equals(type)) {
            updatedDataIds.add(item.getNotifier());
            Map<String, Object> map = updatedValues.computeIfAbsent(item.getNotifier(), k -> new HashMap<>());
            map.put(item.getProperty(), item.getNewValue());
        } else if (AutoConfigType.ADD.equals(type)) {
            String newValue = item.getNewValue();
            List<String> list = addedValues.computeIfAbsent(item.getNotifier(), k -> new ArrayList<>());
            if (ValueUtil.isNotEmpty(newValue)) {
                if (!list.contains(newValue)) {
                    list.add(newValue);
                }
            }
        } else if (AutoConfigType.REMOVE.equals(type)) {
            String notifier = item.getNotifier();
            if (ValueUtil.isNotEmpty(notifier)) {
                deletedValues.add(notifier);
            }
        }
    }

    public boolean hasChangeValue() {
        return !addedValues.isEmpty() || !updatedValues.isEmpty() || !deletedValues.isEmpty();
    }

    public void setNewValue(AutoConfigType type, String notifier, String property, String value) {
        if (ValueUtil.isEmpty(notifier)) {
            return;
        }
        if (Objects.isNull(type)) {
            return;
        }
        if (AutoConfigType.UPDATE.equals(type)) {
            Map<String, Object> map = updatedValues.computeIfAbsent(notifier, k -> new HashMap<>());
            map.put(property, value);
            updatedDataIds.add(notifier);
        } else if (AutoConfigType.ADD.equals(type)) {
            if (ValueUtil.isEmpty(value)) {
                return;
            }
            List<String> list = addedValues.computeIfAbsent(notifier, k -> new ArrayList<>());
            list.add(value);
        } else if (AutoConfigType.REMOVE.equals(type)) {
            deletedValues.add(notifier);
        }
    }

    public Map<String, List<String>> collectAddValues() {
        return addedValues;
    }

    public Map<String, Map<String, Object>> collectUpdatedValue() {
        return updatedValues;
    }

    public Set<String> collectDeletedDataId() {
        return deletedValues;
    }

    public Set<String> collectUpdatedDataId() {
        return updatedDataIds;
    }

    public Set<Long> collectUpdatedDataIdAsLong() {
        return collectUpdatedDataId()
                .stream().map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    public String getGroup() {
        return group;
    }
}
