package com.oceancode.cloud.api.autoconfig.v2;

import java.util.List;

public interface AutoConfigRule {
    String getGroup();

    String getProperty();

    void apply(AutoConfigContext context, AutoConfigResult result, AutoConfig item, List<AutoConfig> groups);

    boolean support(AutoConfigContext context, AutoConfig item, List<AutoConfig> groups);

    boolean isIgnore(AutoConfig item);

}
