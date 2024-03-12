/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.util;

import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.Md5Util;
import com.oceancode.cloud.common.util.ValueUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class TokenUtil {

    private TokenUtil() {
    }

    public static String createUserToken(TokenInfo tokenInfo) {
        if (tokenInfo == null || tokenInfo.getUserId() == null) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "userId is required.");
        }
        String deviceUid = getShortMd5Str(getDeviceUid());
        Map<String, Object> claims = new HashMap<>();
        claims.put("deviceUid", deviceUid);
        claims.put("token", ValueUtil.isEmpty(tokenInfo.getToken()) ? UUID.randomUUID().toString().replace("-", "") : tokenInfo.getToken());
        claims.put("userId", tokenInfo.getUserId());
        if (Objects.nonNull(tokenInfo.getBody())) {
            claims.put("body", tokenInfo.getBody());
        }
        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        String secret = commonConfig
                .getValue(Config.Cache.SESSION_TOKEN_SECRET);

        return createJWT(secret, commonConfig.getValueAsLong(Config.Cache.SESSION_TOKEN_EXPIRE, 600000L), claims);
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
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);
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
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
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

        CommonConfig commonConfig = ComponentUtil.getBean(CommonConfig.class);
        String secret = commonConfig
                .getValue(Config.Cache.SESSION_TOKEN_SECRET);
        Map<String, Object> claims = parseJWT(secret, token);
        if (!claims.containsKey("userId") || ValueUtil.isEmpty((String) claims.get("token"))) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }
        Long userId = (Long) claims.get("userId");
        if (Objects.isNull(userId) || userId <= 0) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }
        String deviceUid = getShortMd5Str(getDeviceUid());
        if (!deviceUid.equals(claims.get("deviceUid"))) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
        }

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId(userId);
        tokenInfo.setToken((String) claims.get("token"));
        tokenInfo.setBody((Map<String, Object>) claims.get("body"));
        return tokenInfo;
    }
}
