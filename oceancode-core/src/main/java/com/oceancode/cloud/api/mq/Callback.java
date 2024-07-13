/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.api.mq;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.Objects;

public interface Callback {

    void onSuccess(Object data);

    default void onException(Throwable e) {
        throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
    }
}
