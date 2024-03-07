/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.util;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.Md5Util;
import com.oceancode.cloud.common.util.ValueUtil;
import org.springframework.util.StringUtils;

import java.util.UUID;

public final class TokenUtil {

    private TokenUtil() {
    }

    public static String createUserToken(String param) {
        String deviceUid = getShortMd5Str(getDeviceUid());
        String userUid = getShortMd5Str(Md5Util.md5(ValueUtil.isEmpty(param) ? "empty" : param));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String lengthUid = deviceUid.length() + "." + userUid.length();
        String token = deviceUid + userUid + getShortMd5Str(Md5Util.md5(lengthUid));
        String tempToken = "ojy5" + getShortMd5Str(Md5Util.md5(token));
        return tempToken + "." + uuid + "." + getShortMd5Str(Md5Util.md5(token));
    }

    public static boolean checkUserToken(String token, String param) {
        if (ValueUtil.isEmpty(token) || !token.startsWith("ojy5")) {
            return false;
        }
        String[] tokens = StringUtils.tokenizeToStringArray(token, ".");
        if (tokens.length != 3) {
            return false;
        }
        String uuid = tokens[1];
        if (ValueUtil.isEmpty(uuid) || uuid.length() != 32) {
            return false;
        }
        String deviceUid = getShortMd5Str(getDeviceUid());
        String userUid = getShortMd5Str(Md5Util.md5(ValueUtil.isEmpty(param) ? "empty" : param));
        String lengthUid = deviceUid.length() + "." + userUid.length();
        String currentToken = deviceUid + userUid + getShortMd5Str(Md5Util.md5(lengthUid));
        String tempToken = "ojy5" + getShortMd5Str(Md5Util.md5(currentToken));
        if (!tempToken.equals(tokens[0])) {
            return false;
        }
        if (!getShortMd5Str(Md5Util.md5(currentToken)).equals(tokens[2])) {
            return false;
        }

        return true;
    }

    private static String getShortMd5Str(String str) {
        return str.substring(0, 3) + str.substring(5, 7) + str.substring(9, 12) + str.substring(str.length() - 2) + "9";
    }

    private static String getDeviceUid() {
        StringBuilder sb = new StringBuilder();
        sb.append(ApiUtil.getClientIp()).append(".");
        sb.append(ApiUtil.getHost()).append(".");
        sb.append(ApiUtil.getUserAgent()).append(".");
        sb.append(ApiUtil.getHeader("Sec-Ch-Ua-Platform")).append(".");
        sb.append(ApiUtil.getHeader("Sec-Ch-Ua-Mobile"));
        return sb.toString();
    }

    public static String parseToken(String authorization) {
        if (ValueUtil.isEmpty(authorization)) {
            return null;
        }
        String token = authorization.trim();
        if (token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length()).trim();
        }
        throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
    }
}
