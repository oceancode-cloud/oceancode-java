package com.oceancode.cloud.model;

import com.oceancode.cloud.api.model.MFieldObject;

public interface ModelField extends BaseObject<MFieldObject> {
    String field();

    String type();

    String versionId();

    boolean isLong();

    boolean isInteger();

    boolean isNumber();

    boolean isBigDecimal();

    boolean isString();

    boolean isText();

    boolean isCollection();

    boolean isAny();

    boolean isMap();

    boolean isDateTime();

    boolean isDate();

    boolean isTime();

    boolean isTimestamp();

    Model ref();

    boolean isRef();

    boolean isRefList();

    Model refList();

    boolean isCharArray();

    String simpleType();

    boolean isDeleted();

    boolean isBusinesses();

    boolean isSimpleType();

    boolean isSensitive();

    boolean isUsername();

    boolean isPassword();

    boolean isProjectId();

    boolean isTenantId();

    boolean isUserId();

    boolean isVersion();

    boolean isParentId();

    boolean isSessionBusiness();

    boolean isCreatedAt();

    boolean isUpdatedAt();

    boolean isCreatedBy();

    boolean isUpdatedBy();

    boolean isToken();

    boolean isStringList();

    boolean isLongList();

    boolean isIntegerList();

    boolean isNumberList();

    boolean isFile();

    boolean isFileList();

    boolean isBoolean();

    boolean isPrimaryKey();

    boolean isRefEnum();

    boolean isList();

    boolean isSimpleList();

    boolean hasTag(String tag);

    boolean isMultiple();

    boolean isAutoIncrement();

    boolean isUpdateBy();

    boolean isCreateBy();

    boolean isUnique();

    String name();

    boolean isName();

    boolean isGroupId();

    String methodFieldName();
}
