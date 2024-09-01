package com.oceancode.cloud.common.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.validation.Validator;
import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EsResult<T> implements Result<List<T>> {
    private Integer took;

    @JsonProperty("timed_out")
    private boolean timeout;


    @JsonProperty("_shards")
    private EsShard shard;

    private EsHits<T> hits;

    private String code;
    private String message;
    private boolean success;

    @JsonIgnore
    @Override
    public List<T> getResults() {
        if (Objects.nonNull(hits)) {
            if (ValueUtil.isEmpty(hits.getHits())) {
                return Collections.emptyList();
            }
            return hits.getHits().stream().map(e -> e.getSource()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @JsonIgnore
    @Override
    public boolean isSuccess() {
        return success;
    }

    @JsonIgnore
    @Override
    public String getMessage() {
        return message;
    }

    @JsonIgnore
    @Override
    public String getCode() {
        return code;
    }

    public Integer getTook() {
        return took;
    }

    public void setTook(Integer took) {
        this.took = took;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public EsShard getShard() {
        return shard;
    }

    public void setShard(EsShard shard) {
        this.shard = shard;
    }

    public EsHits<T> getHits() {
        return hits;
    }

    public void setHits(EsHits<T> hits) {
        this.hits = hits;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
