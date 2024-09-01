/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.errorcode;

import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.common.constant.CommonConst;

/**
 * CommonErrorCode
 *
 * <p>Useful for Api errors when occurs some exception at runtime.</p>
 *
 * @author qinjiawang
 * @since 1.0
 */
public enum CommonErrorCode implements ErrorCode {
    /**
     * SUCCESS
     */
    SUCCESS("000000", "SUCCESS"),

    /**
     * some other error not defined.
     */
    ERROR(CommonConst.CLIENT_ERROR_CODE_PREFIX + "error", "ERROR"),

    /**
     * some error only be log and can't return to client.
     */
    SERVER_ERROR(CommonConst.SERVER_ERROR_CODE_PREFIX + "error", "SERVER ERROR"),
    SERVER_UNKNOWN_ERROR(CommonConst.SERVER_ERROR_CODE_PREFIX + "unknown.error", "SERVER UNKNOWN ERROR"),

    /**
     * too many results
     */
    TOO_MANY_RESULTS(CommonConst.SERVER_ERROR_CODE_PREFIX + "result.many", "too many result"),

    /**
     * some resource not exists.
     */
    NOT_FOUND(CommonConst.CLIENT_ERROR_CODE_PREFIX + "not_found", "not found."),

    /**
     * some resource already exists.
     */
    ALREADY_EXISTS(CommonConst.CLIENT_ERROR_CODE_PREFIX + "already.exists", "already exists"),

    /**
     * some resource must be some authorization but user not login.
     */
    NOT_LOGIN(CommonConst.CLIENT_ERROR_CODE_PREFIX + "not_login", "Denied access"),

    /**
     * username or password invalid when user login.
     */
    USERNAME_OR_PASSWORD_INVLAID(CommonConst.CLIENT_ERROR_CODE_PREFIX + "username_or_password.invalid", "username or password invalid."),

    /**
     * some parameters must need but missing.
     */
    PARAMETER_MISSING(CommonConst.CLIENT_ERROR_CODE_PREFIX + "parameter.missing", "parameter missing"),

    /**
     * some parameters not expected.
     */
    PARAMETER_INVALID(CommonConst.CLIENT_ERROR_CODE_PREFIX + "parameter.invalid", "parameter invalid"),

    /**
     * register user error when user already exists.
     */
    USER_ALREADY_EXISTS(CommonConst.CLIENT_ERROR_CODE_PREFIX + "user.already.exists", "user already exists."),

    /**
     * user not exists.
     */
    USER_NOT_FOUND(CommonConst.CLIENT_ERROR_CODE_PREFIX + "user.not_found", "user not found"),

    /**
     * delete some resource error.
     */
    DELETE_RESOURCE_FAILED(CommonConst.CLIENT_ERROR_CODE_PREFIX + "delete.resource.failed", "delete resource failed"),

    /**
     * restfull api timeout.
     */
    HTTP_API_TIMEOUT(CommonConst.CLIENT_ERROR_CODE_PREFIX + "timeout", "api timeout"),

    /**
     * projectId missing
     */
    PROJECT_ID_MISSING(CommonConst.CLIENT_ERROR_CODE_PREFIX + "project.id.missing", "projectId missing"),

    /**
     * tenantId missing
     */
    TENANT_ID_MISSING(CommonConst.CLIENT_ERROR_CODE_PREFIX + "tenant.id.missing", "tenantId missing"),

    /**
     * Authorization Bearer invalid
     */
    AUTHORIZATION_INVALID(CommonConst.CLIENT_ERROR_CODE_PREFIX + "authorization.invalid", "Authorization invalid"),
    AUTHORIZATION_MISSING(CommonConst.CLIENT_ERROR_CODE_PREFIX + "authorization.missing", "Authorization missing"),

    /**
     * Access Denied
     */
    ACCESS_DENIED(CommonConst.CLIENT_ERROR_CODE_PREFIX + "access.denied", "Access Denied"),
    PERMISSION_DENIED(CommonConst.CLIENT_ERROR_CODE_PREFIX + "permission.denied", "Permission Denied"),
    GROUP_CONTENT_NOT_EMPTY(CommonConst.CLIENT_ERROR_CODE_PREFIX + "group.content.not_empty", "Group Not Empty"),
    ;
    /**
     * errorCode
     */
    private String code;

    /**
     * error message
     */
    private String message;

    CommonErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isSuccess() {
        return SUCCESS.equals(this);
    }

    @Override
    public boolean isClientError() {
        return getCode().startsWith(CommonConst.CLIENT_ERROR_CODE_PREFIX);
    }

    @Override
    public boolean isServerError() {
        return getCode().startsWith(CommonConst.SERVER_ERROR_CODE_PREFIX);
    }
}
