package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.cache.CacheKey;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.UserBaseInfo;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

public final class WebSessionServiceImpl implements SessionService {
    private static final String SESSION_INFO_KEY = "_userInfo";

    @Override
    public boolean isLogin(String token) {
        return Objects.nonNull(getUserInfo(token));
    }

    @Override
    public UserBaseInfo getUserInfo(String token) {
        UserBaseInfo userBaseInfo = (UserBaseInfo) ApiUtil.getSession(SESSION_INFO_KEY);
        if (null == userBaseInfo) {
            return null;
        }
        SessionUtil.setUserId(userBaseInfo.getUserId());
        return userBaseInfo;
    }

    @Override
    public void setUserInfo(String token, UserBaseInfo userInfo) {
        ApiUtil.setSession(SESSION_INFO_KEY, userInfo);
    }

    @Override
    public void logout(String token) {
        ApiUtil.removeSession(SESSION_INFO_KEY);
    }

    @Override
    public CacheKey getSessionKey(String token) {
        return null;
    }
}
