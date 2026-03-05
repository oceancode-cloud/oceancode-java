package com.oceancode.cloud.api.autoconfig.v2;

import com.oceancode.cloud.common.util.ValueUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AutoConfigContext {
    private Set<String> visited = new HashSet<>();

    public void done(AutoConfig item) {
        String key = item.getGroup() + ":" + item.getProperty();
        visited.add(key);
    }

    public void done(Collection<AutoConfig> collection) {
        if (ValueUtil.isEmpty(collection)) {
            return;
        }
        for (AutoConfig item : collection) {
            done(item);
        }
    }

    public boolean isApplied(AutoConfig item) {
        return visited.contains(item.getGroup() + ":" + item.getProperty());
    }
}
