package com.oceancode.cloud.common.util;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Base64Util {
    private Base64Util() {
    }

    public static String encode(String rawData) {
        return Base64.encodeBase64String(rawData.getBytes(StandardCharsets.UTF_8));
    }

    public static String decode(String encodeData) {
        return new String(Base64.decodeBase64(encodeData));
    }

    public static Object encodeObject(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String) {
            return encode((String) value);
        } else if (value instanceof byte[]) {
            return Base64.encodeBase64String((byte[]) value);
        } else {
            return encode(String.valueOf(value));
        }
    }
}
