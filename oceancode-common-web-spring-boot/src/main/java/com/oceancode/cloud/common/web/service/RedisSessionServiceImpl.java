package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.TypeEnum;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.CacheService;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.api.session.UserBaseInfo;
import com.oceancode.cloud.api.session.UserType;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.CacheUtil;
import com.oceancode.cloud.common.util.ExpressUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.*;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RedisSessionServiceImpl implements SessionService {
    private CacheService redisCacheService;
    @Resource
    private CommonConfig commonConfig;

    public RedisSessionServiceImpl(CacheService cacheService) {
        this.redisCacheService = cacheService;
    }

    private String sessionKey() {
        return commonConfig.getValue(Config.Cache.SESSION_CACHE_KEY, KeyParam.DEFAULT_KEY);
    }

    @Override
    public boolean isLogin(String token) {
        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:" + token);
        String userId = redisCacheService.getString(cacheKey);
        if (ValueUtil.isEmpty(userId)) {
            return false;
        }
        SessionUtil.setUserId(Long.valueOf(userId));
        return true;
    }

    @Override
    public boolean isLogin(Long userId) {
        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:info:" + userId);
        Map<String, Object> map = redisCacheService.getMap(cacheKey);
        return ValueUtil.isNotEmpty(map);
    }

    @Override
    public UserBaseInfo getUserInfo(String token) {
        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:" + token);
        String userId = redisCacheService.getString(cacheKey);
        if (ValueUtil.isEmpty(userId)) {
            return null;
        }
        return getUserInfoById(Long.parseLong(userId));
    }

    @Override
    public UserBaseInfo getUserInfoById(Long userId) {
        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:info:" + userId);
        Map<String, Object> map = redisCacheService.getMap(cacheKey);
        if (Objects.isNull(map)) {
            map = new HashMap<>();
        }
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        userBaseInfo.setUserId(userId);
        userBaseInfo.setData(map);

        Object openid = map.get("openid");
        if (Objects.nonNull(openid) && openid instanceof String str) {
            if (ValueUtil.isNotEmpty(str)) {
                userBaseInfo.setOpenid(str.trim());
            }
        }
        Object userType = map.get("userType");
        userBaseInfo.setUserType(TypeEnum.from(userType, UserType.class));
        return userBaseInfo;
    }

    @Override
    public String getUsername(Long userId) {
        return (String) getUserProperty(userId, "username");
    }

    @Override
    public boolean setUserProperties(Long userId, Map<String, Object> map) {
        if (Objects.isNull(userId) || ValueUtil.isEmpty(map)) {
            return false;
        }
        UserBaseInfo userBaseInfo = getUserInfoById(userId);
        userBaseInfo.getData().putAll(map);

        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:info:" + userId);
        redisCacheService.setMap(cacheKey, userBaseInfo.getData());
        return true;
    }

    @Override
    public boolean setUserProperty(Long userId, String key, Object value) {
        if (Objects.isNull(userId) || ValueUtil.isEmpty(key)) {
            return false;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return setUserProperties(userId, map);
    }

    @Override
    public void setUserInfo(String token, UserBaseInfo userInfo) {
        CacheKey tokenKey = KeyParam.of(this.sessionKey()).express("_u:" + token);
        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:info:" + userInfo.getUserId());

        Map<String, Object> map = new HashMap<>();
        if (Objects.nonNull(userInfo.getData())) {
            map.putAll(userInfo.getData());
        }
        if (ValueUtil.isNotEmpty(userInfo.getOpenid())) {
            map.put("openid", userInfo.getOpenid());
        } else {
            map.remove("openid");
        }

        if (Objects.nonNull(userInfo.getUserType())) {
            map.put("userType", userInfo.getUserType().getValue());
        } else {
            map.remove("userType");
        }
        redisCacheService.setMap(cacheKey, map);
        redisCacheService.setString(tokenKey, String.valueOf(userInfo.getUserId()));
    }

    @Override
    public void logout(String token) {
        CacheKey tokenKey = KeyParam.of(this.sessionKey()).express("_u:" + token);
        String userId = redisCacheService.getString(tokenKey);
        if (ValueUtil.isNotEmpty(userId)) {
            CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:info:" + userId);
            redisCacheService.delete(cacheKey);
        }

        redisCacheService.delete(tokenKey);
    }

    @Override
    public Object getUserProperty(Long userId, String property) {
        if (Objects.isNull(userId) || ValueUtil.isEmpty(property)) {
            return null;
        }
        CacheKey cacheKey = KeyParam.of(this.sessionKey()).express("_u:info:" + userId);
        Map<String, Object> mapValues = redisCacheService.getMapValues(cacheKey, Arrays.asList(property));
        if (ValueUtil.isEmpty(mapValues)) {
            return null;
        }
        for (Object value : mapValues.values()) {
            return value;
        }
        return null;
    }
}
