package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.NotifierType;

public final class NotifierUtil {
    private NotifierUtil() {
    }

    public static boolean isModify(NotifierType notifierType) {
        return NotifierType.ADD.equals(notifierType) ||
                NotifierType.ADD_MANY.equals(notifierType) ||
                NotifierType.UPDATE.equals(notifierType) ||
                NotifierType.UPDATE_MANEY.equals(notifierType) ||
                NotifierType.DELETE.equals(notifierType) ||
                NotifierType.DELETE_MANY.equals(notifierType);
    }
}
