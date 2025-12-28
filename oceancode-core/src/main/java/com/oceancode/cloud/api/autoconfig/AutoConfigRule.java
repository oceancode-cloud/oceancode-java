package com.oceancode.cloud.api.autoconfig;

public interface AutoConfigRule {
    boolean support(AutoConfigValue value);

    void apply(AutoConfigValue value);
}
