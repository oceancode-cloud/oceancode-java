/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


/**
 * <B>Md5Util</B>
 *
 * <p>
 * This class is a md5 util.
 * </p>
 *
 * @author Dynamic Gen
 * @since 1.0
 */
public final class Md5Util {
    private Md5Util() {
    }

    /**
     * get string md5
     *
     * @param text raw string
     * @return md5 string
     */
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * get stream md5
     *
     * @param inputStream stream
     * @return md5 string
     */
    public static String md5(InputStream inputStream) {
        try {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }

    /**
     * get file md5
     *
     * @param file file
     * @return md5 string
     */
    public static String md5(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            return DigestUtils.md5DigestAsHex(in);
        } catch (Exception e) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, e);
        }
    }
}