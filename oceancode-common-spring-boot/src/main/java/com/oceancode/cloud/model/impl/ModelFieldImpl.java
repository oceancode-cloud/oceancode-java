package com.oceancode.cloud.model.impl;

import com.oceancode.cloud.api.MFieldObject;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.Model;
import com.oceancode.cloud.model.ModelField;
import com.oceancode.cloud.model.ModelService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModelFieldImpl implements ModelField {
    private transient MFieldObject object;
    private transient Map<String, Object> dataMap;

    private static final BaseModelServiceImpl modelService;

    static {
        modelService = (BaseModelServiceImpl) ComponentUtil.getBean(ModelService.class);
    }


    @Override
    public MFieldObject object() {
        if (Objects.isNull(object)) {
            if (ValueUtil.isEmpty(dataMap)) {
                return null;
            }
            synchronized (this) {
                if (ValueUtil.isEmpty(dataMap)) {
                    return null;
                }
                if (Objects.isNull(object)) {
                    object = (MFieldObject) modelService.toBean(dataMap);
                }
            }
        }
        return object;
    }

    @Override
    public String id() {
        return Objects.nonNull(object()) ? object().id() : null;
    }

    @Override
    public String field() {
        return Objects.nonNull(object()) ? object().field() : null;
    }

    @Override
    public String type() {
        return Objects.nonNull(object()) ? object().type() : null;
    }

    @Override
    public String versionId() {
        return Objects.nonNull(object()) ? object().versionId() : null;
    }

    @Override
    public boolean isLong() {
        return "long".equalsIgnoreCase(type());
    }

    @Override
    public boolean isInteger() {
        return "integer".equalsIgnoreCase(type()) || "int".equalsIgnoreCase(type());
    }

    @Override
    public boolean isNumber() {
        return isLong() || isInteger();
    }

    @Override
    public boolean isBigDecimal() {
        return "bigdecimal".equalsIgnoreCase(type());
    }

    @Override
    public boolean isString() {
        return "string".equalsIgnoreCase(type());
    }

    @Override
    public boolean isText() {
        return "text".equalsIgnoreCase(type());
    }

    @Override
    public boolean isCollection() {
        return "list".equalsIgnoreCase(type()) || "set".equalsIgnoreCase(type());
    }

    @Override
    public boolean isAny() {
        return "any".equalsIgnoreCase(type());
    }

    @Override
    public boolean isMap() {
        return "map".equalsIgnoreCase(type()) || "stringMap".equalsIgnoreCase(type());
    }

    @Override
    public boolean isDateTime() {
        return "datetime".equalsIgnoreCase(type());
    }

    @Override
    public boolean isDate() {
        return "date".equalsIgnoreCase(type());
    }

    @Override
    public boolean isTime() {
        return "time".equalsIgnoreCase(type());
    }

    @Override
    public boolean isTimestamp() {
        return "timestamp".equalsIgnoreCase(type());
    }

    public void setObject(MFieldObject object) {
        this.object = object;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    @Override
    public Model ref() {
        if (Objects.isNull(object())) {
            return null;
        }
        String modelId = object().refModelId();
        if (ValueUtil.isEmpty(modelId)) {
            return null;
        }
        return modelService.findByModelId(modelId);
    }

    @Override
    public boolean isRef() {
        if (Objects.isNull(object())) {
            return false;
        }
        if (ValueUtil.isEmpty(object().refModelId())) {
            return false;
        }
        return "model".equalsIgnoreCase(type()) || "ref".equalsIgnoreCase(type());
    }

    @Override
    public boolean isRefList() {
        if (Objects.isNull(object())) {
            return false;
        }
        if (ValueUtil.isEmpty(object().refModelId())) {
            return false;
        }

        return "ref<list>".equalsIgnoreCase(type()) ||
                "list".equalsIgnoreCase(type()) ||
                "set".equalsIgnoreCase(type()) ||
                "collection".equalsIgnoreCase(type());
    }

    @Override
    public boolean isSensitive() {
        return hasTag("sensitive");
    }

    @Override
    public boolean isUsername() {
        return hasTag("username");
    }

    @Override
    public boolean isPassword() {
        return hasTag("password");
    }

    @Override
    public boolean isProjectId() {
        return hasTag("projectId");
    }

    @Override
    public boolean isTenantId() {
        return hasTag("tenantId");
    }

    @Override
    public boolean isUserId() {
        return hasTag("userId");
    }

    @Override
    public boolean isCreatedAt() {
        return hasTag("createdAt");
    }

    @Override
    public boolean isUpdatedAt() {
        return hasTag("updatedAt");
    }

    @Override
    public boolean isCreatedBy() {
        return hasTag("createdBy");
    }

    @Override
    public boolean isUpdatedBy() {
        return hasTag("updatedBy");
    }

    @Override
    public boolean isToken() {
        return hasTag("token");
    }

    @Override
    public boolean isStringList() {
        return "list<string>".equalsIgnoreCase(type());
    }

    @Override
    public boolean isLongList() {
        return "list<long>".equalsIgnoreCase(type());
    }

    @Override
    public boolean isIntegerList() {
        return "list<integer>".equalsIgnoreCase(type());
    }

    @Override
    public boolean isNumberList() {
        return isIntegerList() || isLongList();
    }

    @Override
    public boolean isFile() {
        return "file".equalsIgnoreCase(type());
    }

    @Override
    public boolean isFileList() {
        return "list<file>".equalsIgnoreCase(type());
    }

    @Override
    public boolean isBoolean() {
        return "boolean".equalsIgnoreCase(type());
    }

    @Override
    public boolean isPrimaryKey() {
        return hasTag("primaryKey");
    }

    @Override
    public boolean isRefEnum() {
        Model ref = ref();
        if (Objects.isNull(ref)) {
            return false;
        }
        return ref.isEnum();
    }

    private boolean hasTag(String tag) {
        if (Objects.isNull(object())) {
            return false;
        }
        return object().tags().contains(tag);
    }
}
