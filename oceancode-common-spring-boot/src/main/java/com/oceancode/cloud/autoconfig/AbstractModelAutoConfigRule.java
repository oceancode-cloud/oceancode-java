package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.common.util.JsonUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractModelAutoConfigRule<NOTIFIER, ADD, UPDATE, INFO> implements AutoConfigRule {
    private static List<Class<?>> typeClass;

    @Override
    public void apply(AutoConfigGroup group) {
        Set<NOTIFIER> removeNotifiers = new HashSet<>();
        processRemoveGroup(group.getRemoveGroup(), removeNotifiers);
        processRemoveGroup(group.getRemoveManyGroup(), removeNotifiers);

        List<UPDATE> updatedList = new ArrayList<>();
        processUpdateGroup(group.getUpdateGroup(), updatedList);
        processUpdateGroup(group.getUpdateManyGroup(), updatedList);

        List<ADD> addedList = new ArrayList<>();
        processAddGroup(group.getAddGroup(), addedList);
        processAddGroup(group.getAddManyGroup(), addedList);

        AutoConfigResponse result = group.getContext().getResult();
        result.setSuccess(false);
        doWithTransaction(() -> {
            if (!removeNotifiers.isEmpty()) {
                delete(group.getContext(), removeNotifiers);
            }
            if (!updatedList.isEmpty()) {
                update(group.getContext(), updatedList);
            }
            if (!addedList.isEmpty()) {
                add(group.getContext(), addedList);
            }
            onCustom(group);
            result.setSuccess(true);
        });
    }

    protected void doWithTransaction(Runnable runnable) {
        runnable.run();
    }

    private void processAddGroup(TypeGroup group, List<ADD> addedList) {
        if (Objects.isNull(group)) {
            return;
        }
        boolean isMany = group.isAddMany();
        Class<ADD> addType = getAddType();
        for (AutoConfig item : group.getItems()) {
            List<ADD> add = convertAdd(item);
            if (Objects.nonNull(add)) {
                addedList.addAll(add);
                continue;
            }
            if (isMany) {
                addedList.addAll(JsonUtil.toList(item.getNewValue(), addType));
                continue;
            }
            addedList.add(JsonUtil.toBean(item.getNewValue(), addType));
        }
    }

    protected List<ADD> convertAdd(AutoConfig item) {
        return null;
    }

    protected Class<ADD> getAddType() {
        return getTypeClass(1);
    }

    private <T> Class<T> getTypeClass(int index) {
        if (Objects.isNull(typeClass)) {
            typeClass = getTypeClasses();
        }
        if (index >= typeClass.size()) {
            return null;
        }
        Class<?> cls = typeClass.get(index);
        if (Objects.isNull(cls)) {
            return null;
        }
        return (Class<T>) cls;
    }

    protected List<Class<?>> getTypeClasses() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return Arrays.stream(actualTypeArguments)
                    .map(it -> it instanceof Class<?> cls ? cls : (Class<?>) null)
                    .toList();
        }
        return Collections.emptyList();
    }

    private void processUpdateGroup(TypeGroup group, List<UPDATE> list) {
        if (Objects.isNull(group)) {
            return;
        }
        boolean isMany = group.isUpdateMany();
        Class<UPDATE> type = getUpdateType();
        for (AutoConfig item : group.getItems()) {
            List<UPDATE> target = convertUpdate(item);
            if (Objects.nonNull(target)) {
                list.addAll(target);
                continue;
            }
            if (isMany) {
                list.addAll(JsonUtil.toList(item.getNewValue(), type));
                continue;
            }
            Map<String, Object> map = null;
            if (item.getNewValue().startsWith("{")) {
                map = JsonUtil.toBean(item.getNewValue(), Map.class);
            } else {
                map = new HashMap<>();
                map.put(item.getProperty(), item.getNewValue());
            }
            map.put("updatedAt", item.getExtra());
            map.put("versionId", item.getVersionId());
            map.put("id", item.getNotifier());

            list.add(JsonUtil.mapToBean(map, type));
        }
    }

    protected List<UPDATE> convertUpdate(AutoConfig item) {
        return null;
    }

    protected Class<UPDATE> getUpdateType() {
        return getTypeClass(2);
    }

    private void processRemoveGroup(TypeGroup group, Set<NOTIFIER> list) {
        if (Objects.isNull(group)) {
            return;
        }
        boolean isMany = group.isRemoveMany();
        Class<NOTIFIER> type = getNotifierType();
        for (AutoConfig item : group.getItems()) {
            if (isMany) {
                list.addAll(JsonUtil.toList(item.getNewValue(), type));
                continue;
            }
            list.add(JsonUtil.toBean(item.getNotifier(), type));
        }
    }

    private Class<NOTIFIER> getNotifierType() {
        return getTypeClass(0);
    }

    protected void onCustom(AutoConfigGroup group) {
    }

    protected abstract void add(AutoConfigContext context, List<ADD> list);

    protected abstract void update(AutoConfigContext context, List<UPDATE> list);

    protected abstract void delete(AutoConfigContext context, Set<NOTIFIER> list);

    protected abstract List<INFO> list(Set<NOTIFIER> list);
}
