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

    boolean isSensitive();

    boolean isUsername();

    boolean isPassword();

    boolean isProjectId();

    boolean isTenantId();

    boolean isUserId();

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
}
