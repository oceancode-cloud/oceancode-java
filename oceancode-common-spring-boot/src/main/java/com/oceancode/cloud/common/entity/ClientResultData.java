package com.oceancode.cloud.common.entity;

import com.oceancode.cloud.api.ClientResult;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClientResultData<T> implements ClientResult<T> {
    private T results;

    private String code;

    private String message;

    private transient HttpHeaders headers;

    @Override
    public T getResults() {
        return results;
    }

    @Override
    public boolean isSuccess() {
        return CommonErrorCode.SUCCESS.getShortCode().equals(getCode());
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public List<String> getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public List<String> getCookies() {
        List<String> list = headers.get("Set-Cookie");
        return list == null ? Collections.emptyList() : list;
    }

    @Override
    public String getJSessionId() {
        String result = getCookies().stream().filter(e -> e.startsWith("JSESSIONID=")).findAny().orElse(null);
        return result != null ? result.substring(result.indexOf("=") + 1, result.indexOf(";")) : null;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
