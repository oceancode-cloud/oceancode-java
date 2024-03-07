package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.UserInfo;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

public final class WebSessionServiceImpl implements SessionService {

    @Override
    public boolean isLogin() {
        return Objects.nonNull(getUserInfo());
    }

    @Override
    public Object getUserInfo() {
        return ApiUtil.getSession(ApiUtil.getAuthorizationToken());
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        ApiUtil.setSession(userInfo.getToken(), userInfo.getDetail());
    }

    @Override
    public void logout() {
        String token = ApiUtil.getAuthorizationToken();
        if (ValueUtil.isEmpty(token)){
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_MISSING,"Authorization token is required");
        }
        ApiUtil.removeSession(token);
    }
}
