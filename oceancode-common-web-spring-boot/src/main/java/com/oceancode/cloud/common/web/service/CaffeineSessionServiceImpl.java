package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.MapTransfer;
import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.cache.LocalCacheService;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.api.session.UserInfo;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.cache.caffeine.CaffeineServiceImpl;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.config.Config;
import com.oceancode.cloud.common.util.CacheUtil;
import com.oceancode.cloud.common.util.ExpressUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import com.oceancode.cloud.common.web.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CaffeineSessionServiceImpl implements SessionService {
    private CommonConfig commonConfig;
    private String sessionKey;
    private LocalCacheService localCacheService;

    public CaffeineSessionServiceImpl(CommonConfig commonConfig, LocalCacheService localCacheService) {
        this.commonConfig = commonConfig;
        this.sessionKey = commonConfig.getValue(Config.Cache.SESSION_CACHE_KEY);
        this.localCacheService = localCacheService;
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
        return localCacheService.getMap(keyParam);
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        CacheKey cacheKey = KeyParam.of(sessionKey)
                .addParam("userId", userInfo.getId());
        userInfo.getDetail().put("_token", userInfo.getToken());
        localCacheService.setMap(cacheKey, userInfo.getDetail());
    }

    @Override
    public void logout() {
        String token = ApiUtil.getToken();
        TokenInfo tokenInfo = TokenUtil.parseToken(token);
        CacheKey keyParam = KeyParam.of(sessionKey)
                .addParam("userId", tokenInfo.getUserId());
        localCacheService.delete(keyParam);
    }

}
