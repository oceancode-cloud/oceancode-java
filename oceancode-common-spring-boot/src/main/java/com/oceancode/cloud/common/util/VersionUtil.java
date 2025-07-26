package com.oceancode.cloud.common.util;

import java.util.regex.Pattern;

public class VersionUtil {
    private static final String PATTERN_STR = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)$";

    private static final Pattern VERSION_PATTERN = Pattern.compile(PATTERN_STR);

    private VersionUtil() {
    }


    /**
     * 是否是有效的版本号
     *
     * @param input 字符串
     * @return 是否是有效的版本号
     */
    public static boolean isValidVersion(String input) {
        if (input == null) {
            return false;
        }
        return VERSION_PATTERN.matcher(input).matches();
    }

    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }
}
