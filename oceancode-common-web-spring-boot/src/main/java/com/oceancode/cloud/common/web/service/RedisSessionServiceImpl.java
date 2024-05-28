package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.api.session.UserBaseInfo;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ExpressUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.web.util.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

public class RedisSessionServiceImpl implements SessionService {
    @Resource
    private RedisCacheService redisCacheService;
    @Resource
    private CommonConfig commonConfig;

    private String sessionKey() {
        return commonConfig.getValue(Config.Cache.SESSION_CACHE_KEY);
    }

    @Override
    public boolean isLogin(String token) {
        return Objects.nonNull(getUserInfo(token));
    }

    private CacheKey buildKey(String token) {
        TokenInfo tokenInfo = TokenUtil.parseToken(token);
        return KeyParam.of(sessionKey())
                .addParam("userId", tokenInfo.getUserId())
                .addParam("token", tokenInfo.getSessionId())
                .addParam("openid", tokenInfo.getOpenid());
    }

    @Override
    public UserBaseInfo getUserInfo(String token) {
        UserBaseInfo userBaseInfo = redisCacheService.getEntity(buildKey(token), UserBaseInfo.class);
        if (Objects.isNull(userBaseInfo)) {
            return null;
        }
        SessionUtil.setUserId(userBaseInfo.getUserId());
        return userBaseInfo;
    }


    private String getUserKeyPrefix(String key) {
        String tokenVar = ":#{#token}";
        if (key.endsWith(tokenVar)) {
            return key.substring(0, key.length() - tokenVar.length());
        } else if (key.contains(tokenVar + ":")) {
            return key.substring(0, key.indexOf(tokenVar + ":"));
        }
        throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "key must contain token variable,eg: uid:#{#token}");
    }


    @Override
    public void setUserInfo(String token, UserBaseInfo userInfo) {
        CacheKey keyParam = KeyParam.of(sessionKey())
                .addParam("userId", userInfo.getUserId())
                .addParam("token", token)
                .addParam("openid", userInfo.getOpenid());
        String userKey = ExpressUtil.parse(getUserKeyPrefix(keyParam.pattern()), keyParam.params(), String.class);
        redisCacheService.deleteByPrefix(KeyParam.of(keyParam.key())
                .express(userKey + ":")
                .addParams(keyParam.params()));
        redisCacheService.setEntity(keyParam, userInfo);
    }

    @Override
    public void logout(String token) {
        redisCacheService.delete(buildKey(token));
    }

    @Override
    public CacheKey getSessionKey(String token) {
        return buildKey(token);
    }
}
