package com.oceancode.cloud.api;

import java.util.List;

public interface ClientResult<T> extends Result<T> {

    List<String> getHeader(String key);

    List<String> getCookies();

    String getJSessionId();
}
