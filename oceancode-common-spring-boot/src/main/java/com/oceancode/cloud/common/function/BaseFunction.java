/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */


package com.oceancode.cloud.common.function;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;

import java.util.Objects;

public abstract class BaseFunction<T> {
    private T targetService_;

    private T remoteService_;
    private Boolean inited_;

    protected T getService() {
        if (Objects.nonNull(targetService_)) {
            return targetService_;
        }
        if (Objects.nonNull(inited_)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, getInterfaceClass().getName() + " Not Found");
        }
        targetService_ = doGetService(getInterfaceClass());
        if (Objects.isNull(targetService_)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, getInterfaceClass().getName() + " Not Found");
        }
        return targetService_;
    }

    protected T getService(boolean remoteFunction) {
        if (remoteFunction) {
            if (Objects.isNull(remoteService_)) {
                getService();
            }
            return remoteService_;
        }
        return targetService_;
    }

    private T doGetService(Class<T> interfaceClass) {
        T remoteFunction = null;
        T clientFunction = null;
        T localFunction = null;
        inited_ = true;
        for (T value : ComponentUtil.getBeans(interfaceClass).values()) {
            if (value instanceof RemoteFunction) {
                remoteFunction = value;
            } else if (value instanceof ClientFunction) {
                clientFunction = value;
            } else if (!(value instanceof BaseFunction<?>)) {
                if (Objects.nonNull(localFunction)) {
                    localFunction = value;
                }
            }
        }

        if (Objects.isNull(remoteFunction) && Objects.nonNull(clientFunction)) {
            remoteService_ = clientFunction;
        }

        return localFunction;
    }

    protected abstract Class<T> getInterfaceClass();
}
