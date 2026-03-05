package com.oceancode.cloud.common.autoconfig;

import com.oceancode.cloud.api.ObjectBuild;
import com.oceancode.cloud.api.autoconfig.AutoConfigType;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigContext;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResult;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRule;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.Util;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.repository.BaseRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractCrudAutoConfigRule<PK, ADD, UPDATE, INFO, MODEL> implements AutoConfigRule {
    @Override
    public String getProperty() {
        return "";
    }

    @Override
    public boolean support(AutoConfigContext context, AutoConfig item, List<AutoConfig> groups) {
        return true;
    }

    @Override
    public boolean isIgnore(AutoConfig item) {
        return false;
    }

    @Override
    public void apply(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups) {
        AutoConfig target = item;
        if (Objects.isNull(target)) {
            if (groups.size() == 1) {
                target = groups.getFirst();
            }
        }

        if (Objects.isNull(target)) {
            return;
        }
        try {
            String type = target.getType();
            result.setSuccess(true);
            result.setSourceId(target.getSourceId());
            result.setGroup(target.getGroup());
            result.setNotifier(target.getNotifier());
            if (AutoConfigType.ADD.getValue().equals(type)) {
                ADD model = JsonUtil.toBean(target.getNewValue(), getAddTypeClass());
                INFO info = onAdd(model, context, result, groups);
                result.setNewValue(JsonUtil.toJson(info));
            } else if (AutoConfigType.ADD_MANY.getValue().equals(type)) {
                List<ADD> model = JsonUtil.toList(target.getNewValue(), getAddTypeClass());
                List<INFO> info = onAddMany(model, context, result, groups);
                result.setNewValue(JsonUtil.toJson(info));
            } else if (AutoConfigType.UPDATE.getValue().equals(type)) {
                Map<String, Object> map = null;

                if (ValueUtil.isNotEmpty(target.getNewValue()) && target.getNewValue().startsWith("{")) {
                    map = JsonUtil.toBean(target.getNewValue(), Map.class);
                } else {
                    map = new HashMap<>();
                    map.put(target.getProperty(), target.getNewValue());
                }
                boolean hasVersion = false;
                if (ValueUtil.isNotEmpty(target.getVersionId())) {
                    hasVersion = true;
                    map.put("versionId", target.getVersionId());
                }
                Object pkValue = target.getNotifier();
                Class<PK> primaryKeyClass = getPrimaryKeyClass();
                if (Long.class.equals(primaryKeyClass)) {
                    pkValue = Long.parseLong(target.getNotifier());
                }
                MODEL temp = getBaseRepository().findById(primaryKeyClass.cast(pkValue));
                boolean ret = Util.assignWithVersion(temp, map, hasVersion ? "versionId" : null);
                if (!ret) {
                    result.setSuccess(false);
                    return;
                }
                INFO info = onUpdate(temp, context, result, groups);
                result.setNewValue(JsonUtil.toJson(info));
            } else if (AutoConfigType.UPDATE_MANY.getValue().equals(type)) {
                List<UPDATE> model = JsonUtil.toList(target.getNewValue(), getUpdateTypeClass());
                List<INFO> info = onUpdateMany(model, context, result, groups);
                result.setNewValue(JsonUtil.toJson(info));
            } else if (AutoConfigType.REMOVE.getValue().equals(type)) {
                String pk = target.getNotifier();
                Class<PK> primaryKeyClass = getPrimaryKeyClass();
                Object value = pk;
                if (Long.class.equals(primaryKeyClass)) {
                    value = Long.parseLong(pk);
                }
                INFO info = onDelete(primaryKeyClass.cast(value), context, result, groups);
                result.setOldValue(JsonUtil.toJson(info));
            } else if (AutoConfigType.REMOVE_MANY.getValue().equals(type)) {
                Class<PK> primaryKeyClass = getPrimaryKeyClass();
                List<PK> list = JsonUtil.toList(item.getOldValue(), primaryKeyClass);
                List<INFO> info = onDeleteMany(list, context, result, groups);
                result.setOldValue(JsonUtil.toJson(info));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }

    protected <T> T build(Object data) {
        if (data instanceof ObjectBuild objectBuild) {
            return (T) objectBuild.build();
        }
        return null;
    }

    protected Class<PK> getPrimaryKeyClass() {
        return (Class<PK>) getTypeClass(0);
    }

    protected Class<ADD> getAddTypeClass() {
        return (Class<ADD>) getTypeClass(1);
    }

    protected Class<UPDATE> getUpdateTypeClass() {
        return (Class<UPDATE>) getTypeClass(2);
    }

    protected Class<UPDATE> getInfoTypeClass() {
        return (Class<UPDATE>) getTypeClass(3);
    }

    protected Class<MODEL> getModelTypeClass() {
        return (Class<MODEL>) getTypeClass(4);
    }

    protected abstract INFO convert(MODEL data);

    private Class<?> getTypeClass(int index) {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) superClass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (index >= actualTypeArguments.length) {
                return null;
            }
            return (Class<?>) actualTypeArguments[index];
        }
        return null;
    }

    protected abstract BaseRepository<MODEL, PK> getBaseRepository();

    protected INFO onAdd(ADD value, AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        Object data = build(value);
        if (Objects.isNull(data)) {
            result.setSuccess(false);
        } else {
            result.setSuccess(getBaseRepository().addOne(getModelTypeClass().cast(data)));
            return convert((MODEL) data);
        }
        return null;
    }

    protected List<INFO> onAddMany(List<ADD> values, AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        List<Object> list = values.stream().map(this::build).toList();
        if (getBaseRepository().addBatch((List<MODEL>) list)) {
            return list.stream().map(it -> this.convert((MODEL) it)).toList();
        }
        return null;
    }

    protected INFO onUpdate(MODEL value, AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        if (Objects.isNull(value)) {
            result.setSuccess(false);
        } else {
            result.setSuccess(getBaseRepository().updateById(value));
            return convert(value);
        }
        return null;
    }

    protected List<INFO> onUpdateMany(List<UPDATE> values, AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        List<Object> list = values.stream().map(this::build).toList();
        if (getBaseRepository().updateBatchById((List<MODEL>) list)) {
            return list.stream().map(it -> this.convert((MODEL) it)).toList();
        }
        return null;
    }

    protected INFO onDelete(PK value, AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        MODEL model = getBaseRepository().findById(value);
        if (getBaseRepository().deleteById(value)) {
            return convert(model);
        }
        return null;
    }

    protected List<INFO> onDeleteMany(List<PK> values, AutoConfigContext context, AutoConfigResult result, List<AutoConfig> groups) {
        HashSet<PK> pks = new HashSet<>(values);
        List<MODEL> models = getBaseRepository().findByIds(pks);
        boolean ret = getBaseRepository().deleteByIds(pks);
        if (ret) {
            return models.stream().map(this::convert).toList();
        }
        return null;
    }
}
