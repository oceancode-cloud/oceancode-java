package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.common.util.ValueUtil;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AutoConfigValue {
    public static final String VERSION_KEY = "versionId";
    private final Map<String, List<String>> addedValues;
    private final Map<String, Map<String, Object>> updatedValues;
    private final Set<String> deletedValues;
    private final Set<String> updatedDataIds;
    private AutoConfigGroupType type;
    private Long timestamp;
    private transient AutoConfigRequestContext context;

    private List<?> addObjects;
    private List<?> updateObjects;
    private List<?> deleteObjects;

    public AutoConfigValue(AutoConfigGroupType group, AutoConfigRequestContext context) {
        this.addedValues = new HashMap<>();
        updatedValues = new HashMap<>();
        deletedValues = new HashSet<>();
        updatedDataIds = new HashSet<>();
        this.type = group;
        this.context = context;
    }

    public void addRequest(AutoConfigRequestItem item) {
        if (Objects.isNull(item)) {
            return;
        }
        AutoConfigType type = AutoConfigType.from(item.getType());
        if (AutoConfigType.UPDATE.equals(type)) {
            updatedDataIds.add(item.getNotifier());
            updateTimestamp(item.getTimestamp());
            Map<String, Object> map = updatedValues.computeIfAbsent(item.getNotifier(), k -> new HashMap<>());
            map.put(item.getProperty(), item.getNewValue());
            if (ValueUtil.isNotEmpty(item.getVersionId())) {
                map.put(VERSION_KEY, item.getVersionId());
            }
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
            updateTimestamp(item.getTimestamp());
            if (ValueUtil.isNotEmpty(notifier)) {
                deletedValues.add(notifier);

            }
        }
    }

    private void updateTimestamp(Long currentTimestamp) {
        if (Objects.nonNull(currentTimestamp)) {
            if (Objects.isNull(this.timestamp)) {
                timestamp = currentTimestamp;
            } else {
                timestamp = Math.min(timestamp, currentTimestamp);
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
        return type.getGroup();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public <T> T getTimestampAs(Class<T> returnType) {
        if (Objects.isNull(this.timestamp)) {
            return null;
        }
        if (Long.class.equals(returnType)) {
            return returnType.cast(this.timestamp);
        }
        Object result = this.timestamp;
        if (Date.class.equals(returnType)) {
            result = new Date(this.timestamp);
        } else if (Instant.class.equals(returnType)) {
            result = Instant.ofEpochMilli(this.timestamp);
        } else if (Timestamp.class.equals(returnType)) {
            result = Timestamp.from(Instant.ofEpochMilli(this.timestamp));
        }
        return returnType.cast(result);
    }

    public AutoConfigRequestContext getContext() {
        return context;
    }

    public List<?> getAddObjects() {
        return addObjects;
    }

    public void setAddObjects(List<?> addObjects) {
        this.addObjects = addObjects;
    }

    public List<?> getUpdateObjects() {
        return updateObjects;
    }

    public void setUpdateObjects(List<?> updateObjects) {
        this.updateObjects = updateObjects;
    }

    public List<?> getDeleteObjects() {
        return deleteObjects;
    }

    public void setDeleteObjects(List<?> deleteObjects) {
        this.deleteObjects = deleteObjects;
    }

    public AutoConfigGroupType getType() {
        return type;
    }

    public List<String> collectAddValues(String key) {
        List<String> list = addedValues.get(key);
        if (Objects.isNull(list)) {
            return Collections.emptyList();
        }
        return list;
    }
}
