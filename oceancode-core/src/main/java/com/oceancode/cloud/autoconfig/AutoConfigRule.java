package com.oceancode.cloud.autoconfig;

public interface AutoConfigRule {
    String getGroup();

    void apply(AutoConfigGroup group);
}
