/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <B>PasswordUtil</B>
 *
 * <p>
 * This class is a password util.
 * </p>
 *
 * @author Dynamic Gen
 * @since 1.0
 */
public final class PasswordUtil {
    private PasswordUtil() {
    }


    /**
     * encode str
     *
     * @param rawPassword password
     * @return encode str
     */
    public static String encode(CharSequence rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    /**
     * match old password and encode password
     *
     * @param rawPassword     old password
     * @param encodedPassword encode password
     * @return if match return true else return false
     */
    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return ComponentUtil.getBean(PasswordEncoder.class).matches(rawPassword, encodedPassword);
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword, boolean throwEx) {
        if (!matches(rawPassword, encodedPassword)) {
            if (throwEx) {
                throw new BusinessRuntimeException(CommonErrorCode.USERNAME_OR_PASSWORD_INVLAID);
            }
            return false;
        }
        return true;
    }
}
