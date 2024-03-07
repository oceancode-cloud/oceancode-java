/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.validation;


import com.oceancode.cloud.common.entity.ResultData;

public interface BusinessValidator {
    <T> ResultData<T> validate();
}
