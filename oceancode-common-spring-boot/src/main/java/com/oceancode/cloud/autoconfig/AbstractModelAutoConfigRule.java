package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResult;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractModelAutoConfigRule<NOTIFIER, ADD, UPDATE, INFO> implements AutoConfigRule {

    @Override
    public void apply(AutoConfigGroup group) {
        Set<NOTIFIER> removeNotifiers = new HashSet<>();
        processRemoveGroup(group.getRemoveGroup(), removeNotifiers);
        processRemoveGroup(group.getRemoveManyGroup(), removeNotifiers);

        List<UPDATE> updatedList = new ArrayList<>();
        List<AutoConfig> updatePropertyList = new ArrayList<>();
        processUpdateGroup(group.getUpdateGroup(), updatedList, updatePropertyList);
        processUpdateGroup(group.getUpdateManyGroup(), updatedList, updatePropertyList);

        List<ADD> addedList = new ArrayList<>();
        processAddGroup(group.getAddGroup(), addedList);
        processAddGroup(group.getAddManyGroup(), addedList);

        AutoConfigResponse result = group.getContext().getResult();
        result.setSuccess(false);
        String groupValue = group.getGroup();
        AutoConfigContext context = group.getContext();
        doWithTransaction(() -> {
            if (!removeNotifiers.isEmpty()) {
                delete(context, removeNotifiers);
            }
            if (!updatePropertyList.isEmpty()) {
                updateProperty(context, updatePropertyList);
            }
            if (!updatedList.isEmpty()) {
                update(context, updatedList);
            }
            if (!addedList.isEmpty()) {
                add(context, addedList);
            }
            onCustom(group);
            result.setSuccess(true);
        });
        TypeGroup addGroup = group.getAddGroup();
        fillGroup(groupValue, context.getResult().getToAdd(), addGroup);
        fillGroup(groupValue, context.getResult().getToUpdate());
        fillGroup(groupValue, context.getResult().getToDelete());
    }

    private void fillGroup(String groupValue, List<AutoConfigResult> list) {
        fillGroup(groupValue, list, null);
    }

    private void fillGroup(String groupValue, List<AutoConfigResult> list, TypeGroup typeGroup) {
        for (int index = 0; index < list.size(); index++) {
            AutoConfigResult result = list.get(index);
            result.setGroup(groupValue);
        }
        if (list.size() == 1 && Objects.nonNull(typeGroup)) {
            if (typeGroup.getItems().size() == 1) {
                for (AutoConfig item : typeGroup.getItems()) {
                    item.setSourceId(list.getFirst().getSourceId());
                }
            }
        }

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
        List<Class<?>> typeClasses = getTypeClasses();
        if (index >= typeClasses.size()) {
            return null;
        }
        Class<?> cls = typeClasses.get(index);
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

    private void processUpdateGroup(TypeGroup group, List<UPDATE> list, List<AutoConfig> updatePropertyList) {
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
            if (ValueUtil.isEmpty(item.getProperty()) && item.getNewValue().startsWith("{")) {
                map = JsonUtil.toBean(item.getNewValue(), Map.class);
            } else {
                updatePropertyList.add(item);
                continue;
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

    protected NOTIFIER convertNotifier(String notifier) {
        if (ValueUtil.isEmpty(notifier)) {
            return null;
        }
        Class<NOTIFIER> notifierType = getNotifierType();
        if (String.class.equals(notifierType)) {
            return notifierType.cast(notifier);
        }
        return JsonUtil.toBean(notifier, notifierType);
    }

    private Class<NOTIFIER> getNotifierType() {
        return getTypeClass(0);
    }

    protected void onCustom(AutoConfigGroup group) {
    }

    protected abstract void add(AutoConfigContext context, List<ADD> list);

    protected abstract void update(AutoConfigContext context, List<UPDATE> list);

    protected void updateProperty(AutoConfigContext context, List<AutoConfig> list) {

    }

    protected abstract void delete(AutoConfigContext context, Set<NOTIFIER> list);

    protected abstract List<INFO> list(Set<NOTIFIER> list);

    protected INFO getById(NOTIFIER id) {
        if (Objects.isNull(id)) {
            return null;
        }
        List<INFO> list = list(Set.of(id));
        if (ValueUtil.isEmpty(list)) {
            return null;
        }
        return list.getFirst();
    }
}
