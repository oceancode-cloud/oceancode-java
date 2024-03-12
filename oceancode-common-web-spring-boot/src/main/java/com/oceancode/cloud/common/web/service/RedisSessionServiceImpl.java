package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.RedisCacheService;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.api.session.UserInfo;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.web.util.ApiUtil;
import com.oceancode.cloud.common.web.util.TokenUtil;

import java.util.Map;
import java.util.Objects;

public class RedisSessionServiceImpl implements SessionService {
    private RedisCacheService redisCacheService;
    private CommonConfig commonConfig;
    private String sessionKey;

    public RedisSessionServiceImpl(CommonConfig commonConfig, RedisCacheService bean) {
        this.commonConfig = commonConfig;
        this.redisCacheService = bean;
        this.sessionKey = commonConfig.getValue(Config.Cache.SESSION_CACHE_KEY);
    }

    @Override
    public boolean isLogin() {
        return Objects.nonNull(getUserInfo());
    }

    @Override
    public Map<String, Object> getUserInfo() {
        TokenInfo tokenInfo = TokenUtil.parseToken(ApiUtil.getToken());
        CacheKey keyParam = KeyParam.of(sessionKey)
                .addParam("userId", tokenInfo.getUserId());
        return redisCacheService.getMap(keyParam);
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        CacheKey cacheKey = KeyParam.of(sessionKey)
                .addParam("userId", userInfo.getId());
        redisCacheService.setMap(cacheKey, userInfo.getDetail());
    }

    @Override
    public void logout() {
        String token = ApiUtil.getToken();
        TokenInfo tokenInfo = TokenUtil.parseToken(token);
        CacheKey keyParam = KeyParam.of(sessionKey)
                .addParam("userId", tokenInfo.getUserId());
        redisCacheService.delete(keyParam);
    }
}
