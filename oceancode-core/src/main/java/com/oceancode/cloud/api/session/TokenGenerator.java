package com.oceancode.cloud.api.session;

public interface TokenGenerator {
    TokenInfo get(Long userId, String openid);

    TokenInfo parse(String token);
}
