/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.repository;

/**
 * <B>ActionCallback</B>
 *
 * <p>
 * Before commit transaction callback.
 * </p>
 *
 * @param <T> return type
 * @author Dynamic Gen
 * @since 1.0
 */
public interface ActionCallback<T> {
    /**
     * before commit transaction callback method
     *
     * @return return data type.
     */
    T action();
}
