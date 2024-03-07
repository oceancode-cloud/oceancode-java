/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.repository;

/**
 * <B>Rollback</B>
 *
 * <p>
 * Auto rollback when commit occurs exception.
 * </p>
 *
 * @param <T> return type
 * @author Dynamic Gen
 * @since 1.0
 */
public interface Rollback<T> {
    /**
     * rollback when commit occurs exception
     *
     * @param throwable occurs exception
     * @return return data type
     */
    T rollback(Throwable throwable);
}
