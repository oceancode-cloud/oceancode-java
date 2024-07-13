package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.NotifierType;

public final class NotifierUtil {
    private NotifierUtil() {
    }

    public static boolean isModify(NotifierType notifierType) {
        return NotifierType.ADD.equals(notifierType) ||
                NotifierType.UPDATE.equals(notifierType) ||
                NotifierType.DELETE.equals(notifierType);
    }
}
