package com.oceancode.cloud.common.web.service;

import com.oceancode.cloud.api.MapTransfer;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.TokenInfo;
import com.oceancode.cloud.api.session.UserInfo;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import com.oceancode.cloud.common.web.util.ApiUtil;
import com.oceancode.cloud.common.web.util.TokenUtil;

import java.util.Map;
import java.util.Objects;

public final class WebSessionServiceImpl implements SessionService {

    @Override
    public boolean isLogin() {
        return Objects.nonNull(getUserInfo());
    }

    @Override
    public Map<String, Object> getUserInfo() {
        TokenInfo tokenInfo = TokenUtil.parseToken(ApiUtil.getToken());
        return (Map<String, Object>) ApiUtil.getSession(tokenInfo.getUserId() + "");
    }

    @Override
    public void setUserInfo(UserInfo userInfo) {
        ApiUtil.setSession(userInfo.getId() + "", userInfo.getDetail());
    }

    @Override
    public void logout() {
        String token = ApiUtil.getToken();
        TokenInfo tokenInfo = TokenUtil.parseToken(token);
        if (ValueUtil.isEmpty(token)) {
            throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_MISSING, "Authorization token is required");
        }
        ApiUtil.removeSession(tokenInfo.getUserId() + "");
    }
}
