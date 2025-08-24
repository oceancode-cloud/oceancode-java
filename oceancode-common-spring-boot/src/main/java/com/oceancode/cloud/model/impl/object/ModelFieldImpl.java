package com.oceancode.cloud.model.impl.object;

import com.oceancode.cloud.api.model.MFieldObject;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.model.Model;
import com.oceancode.cloud.model.ModelField;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ModelFieldImpl extends AbstractBaseObject<MFieldObject> implements ModelField {

    public static final ModelFieldImpl NULL = new ModelFieldImpl();

    public ModelFieldImpl(MFieldObject object) {
        super(object);
    }

    public ModelFieldImpl(Map<String, Object> dataMap) {
        super(dataMap);
    }

    public ModelFieldImpl() {

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
        return "map".equalsIgnoreCase(type()) || "StringTypeMap".equalsIgnoreCase(type());
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

    @Override
    public Model ref() {
        if (Objects.isNull(object())) {
            return ModelImpl.NULL;
        }
        String modelId = object().refModelId();
        if (ValueUtil.isEmpty(modelId)) {
            return ModelImpl.NULL;
        }
        return MODEL_CACHE_SERVICE.findModelById(modelId);
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
    public Model refList() {
        if (isRefList()) {
            if (Objects.nonNull(object().refModelId())) {
                return ref();
            }
        }
        return ModelImpl.NULL;
    }

    @Override
    public boolean isCharArray() {
        if (isSensitive()) {
            return isString() || isText();
        }
        return false;
    }

    @Override
    public boolean isSimpleType() {
        return ValueUtil.isNotEmpty(simpleType());
    }

    @Override
    public boolean isDeleted() {
        return hasTag("deleted");
    }

    @Override
    public boolean isBusinesses() {
        return isProjectId() || isDeleted() || isTenantId() || isUserId();
    }

    @Override
    public String simpleType() {
        if (isCharArray()) {
            return "char[]";
        }
        if (isLong() || isInteger() || isString() || isBoolean() || isBigDecimal() || isMap()) {
            if (isMap()) {
                return "Map<String, Object>";
            }
            return type();
        }
        if (isText()) {
            return "String";
        }

        return null;
    }

    @Override
    public boolean isSensitive() {
        return hasTag("sensitive");
    }

    @Override
    public boolean isUsername() {
        return hasTag("username") && isString();
    }

    @Override
    public boolean isPassword() {
        return hasTag("password");
    }

    @Override
    public boolean isProjectId() {
        return hasTag("projectId") || hasTag("ProjectId");
    }

    @Override
    public boolean isTenantId() {
        return hasTag("tenantId");
    }

    @Override
    public boolean isUserId() {
        if (hasTag("UserId")) {
            return true;
        }
        return hasTag("userId");
    }

    @Override
    public boolean isVersion() {
        return hasTag("version");
    }

    @Override
    public boolean isParentId() {
        return hasTag("parentId");
    }

    @Override
    public boolean isSessionBusiness() {
        return isUserId() || isProjectId() || isTenantId();
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

    public boolean isList() {
        return "list".equalsIgnoreCase(type());
    }

    @Override
    public boolean isSimpleList() {
        return isList() && !isFileList();
    }

    @Override
    public boolean hasTag(String tag) {
        if (Objects.isNull(object())) {
            return false;
        }
        return object().tags().contains(tag);
    }

    @Override
    public boolean isMultiple() {
        return isList() || isStringList() || isLongList() || isIntegerList() || isMap() || isRefList();
    }

    @Override
    public boolean isAutoIncrement() {
        if (isLong() || isInteger()) {
            if (ValueUtil.isEmpty(object().tags())) {
                return true;
            }
            return hasTag("auto_increment");
        }
        return false;
    }

    @Override
    public boolean isUpdateBy() {
        return hasTag("updateBy");
    }

    @Override
    public boolean isCreateBy() {
        return hasTag("createBy");
    }

    @Override
    public boolean isUnique() {
        return hasTag("unique");
    }

    @Override
    public String name() {
        return object().name();
    }

    @Override
    public boolean isName() {
        return hasTag("name");
    }

    @Override
    public boolean isGroupId() {
        return hasTag("groupId");
    }

    @Override
    public String methodFieldName() {
        return object().field().substring(0, 1).toUpperCase(Locale.ROOT) + object().field().substring(1);
    }
}
