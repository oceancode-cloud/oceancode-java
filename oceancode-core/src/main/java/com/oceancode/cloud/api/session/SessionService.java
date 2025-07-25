/**
 * Copyright (C) Oceancode Cloud Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.session;

import com.oceancode.cloud.api.cache.CacheKey;

import java.util.Map;

/**
 * @author qinjiawang
 */
public interface SessionService {
    /**
     * check user whether login
     *
     * @return true user is login else false
     */
    boolean isLogin(String token);

    /**
     * get login user data info
     *
     * @return user info
     */
    UserBaseInfo getUserInfo(String token);

    UserBaseInfo getUserInfoById(Long userId);

    boolean setUserProperties(Long userId, Map<String, Object> map);

    boolean setUserProperty(Long userId, String key,Object value);

    /**
     * set user session info
     *
     * @param userInfo user info
     */
    void setUserInfo(String token, UserBaseInfo userInfo);

    /**
     * logout
     */
    void logout(String token);

    Object getUserProperty(Long userId, String property);
}
