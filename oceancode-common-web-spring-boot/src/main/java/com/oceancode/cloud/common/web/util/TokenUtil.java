/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.util;

import com.oceancode.cloud.api.session.TokenGenerator;
import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.Md5Util;
import com.oceancode.cloud.common.util.ValueUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

public final class TokenUtil {

    private static final CommonConfig commonConfig;

    private TokenUtil() {
    }

    static {
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
    }

    private static TokenGenerator tokenGenerator() {
        try {
            return ComponentUtil.getBean(TokenGenerator.class);
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    public static TokenInfo createToken(Long userId) {
        return createToken(userId, null);
    }

    public static TokenInfo createToken(Long userId, String openid) {
        TokenGenerator generator = tokenGenerator();
        if (Objects.nonNull(generator)) {
            return generator.get(userId, openid);
        }
        if (userId == null) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "userId is required.");
        }
        String deviceUid = getShortMd5Str(getDeviceUid());
        String token = null;
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        if (isToken()) {
            token = deviceUid + "." + sessionId;
        } else {
            Map<String, Object> claims = new HashMap<>();
            claims.put("deviceId", deviceUid);
            claims.put("sessionId", sessionId);
            claims.put("userId", userId + "");
            claims.put("openid", openid);
            token = createJWT(getSecret(), commonConfig.getValueAsLong(Config.Cache.SESSION_TOKEN_EXPIRE, 600000L), claims);
        }

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(userId);
        tokenInfo.setOpenid(openid);
        tokenInfo.setSessionId(sessionId);
        tokenInfo.setToken(token);
        return tokenInfo;
    }

    private static String getTokenType() {
        return commonConfig.getValue(Config.Cache.SESSION_TOKEN_TYPE, "token");
    }

    private static boolean isToken() {
        String type = getTokenType();
        return ValueUtil.isEmpty(type) || "token".equals(type);
    }

    private static String getSecret() {
        String secret = commonConfig
                .getValue(Config.Cache.SESSION_TOKEN_SECRET);
        if (ValueUtil.isEmpty(secret)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "secret is required,please set property " + Config.Cache.SESSION_TOKEN_SECRET);
        }
        return secret;
    }


    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return
     */
    private static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        final SecretKey key = Jwts.SIG.HS256.key()
                .random(new SecureRandom(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build();
        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        JwtBuilder builder = Jwts.builder()
                .issuer("oceancode")
                .claims(claims)
                .issuedAt(new Date())
                .expiration(exp)
                .signWith(generalKey(secretKey), SignatureAlgorithm.HS512);
        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return
     */
    private static Claims parseJWT(String secretKey, String token) {
        try {
            // 得到DefaultJwtParser
            Claims claims = Jwts.parser()
                    .verifyWith(generalKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims;
        } catch (Exception e) {
            if (e instanceof ExpiredJwtException) {
                throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
            }
            if (e instanceof JwtException) {
                throw new BusinessRuntimeException(CommonErrorCode.NOT_LOGIN);
            }
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID, "token invalid.");
        }
    }

    public static SecretKey generalKey(String key) {
        //调用base64中的getDecoder方法获取解码器，调用解码器中的decode方法将明文密钥进行编码
        byte[] decodeKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = Keys.hmacShaKeyFor(decodeKey);
        //返回加密后的密钥
        return secretKey;
    }


    private static String getShortMd5Str(String str) {
        return Md5Util.md5(str);
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

    public static TokenInfo parseToken(String token) {
        if (ValueUtil.isEmpty(token)) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }

        TokenGenerator generator = tokenGenerator();
        if (Objects.nonNull(generator)) {
            return generator.parse(token);
        }

        if (isToken()) {
            String[] strs = token.split("[.]");
            if (strs.length != 2) {
                throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
            }
            String deviceUid = getShortMd5Str(getDeviceUid());
            if (!strs[0].equals(deviceUid)) {
                throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
            }
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setUserId(null);
            tokenInfo.setToken(strs[1]);
            tokenInfo.setSessionId(strs[1]);
            return tokenInfo;
        }

        String secret = getSecret();
        Map<String, Object> claims = parseJWT(secret, token);
        if (!claims.containsKey("userId") || ValueUtil.isEmpty((String) claims.get("sessionId"))) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }
        Long userId = Long.parseLong(claims.get("userId") + "");
        if (Objects.isNull(userId) || userId <= 0) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }
        String deviceUid = getShortMd5Str(getDeviceUid());
        if (!deviceUid.equals(claims.get("deviceId"))) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(userId);
        tokenInfo.setOpenid((String) claims.get("openid"));
        tokenInfo.setToken(token);
        tokenInfo.setSessionId((String) claims.get("sessionId"));
        return tokenInfo;
    }
}
