/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.api.mq;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;

import java.util.List;

public interface Consumer<T> {
    void poll(List<Message<T>> list);

    default void onException(Throwable throwable) {
        throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, throwable);
    }
}
