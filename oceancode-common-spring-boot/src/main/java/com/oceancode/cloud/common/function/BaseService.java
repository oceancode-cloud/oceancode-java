/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */


package com.oceancode.cloud.common.function;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;

import java.util.Objects;

public class BaseService<T> {
    private T targetService;

    public T getService() {
        if (Objects.nonNull(targetService)) {
            return targetService;
        }
        targetService = doGetService(getInterfaceClass());
        if (Objects.isNull(targetService)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, getInterfaceClass().getName() + " Not Found");
        }
        return targetService;
    }

    private <T> T doGetService(Class<T> interfaceClass) {
        return ComponentUtil.getBean(interfaceClass, service -> !(service instanceof BaseService));
    }

    public Class<T> getInterfaceClass() {
        return null;
    }
}
