package com.oceancode.cloud.api.account;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Account {
    private Map<String, Object> config;

    public Account(Map<String, Object> config) {
        this.config = config;
        if (Objects.isNull(config)) {
            this.config = new HashMap<>();
        }
    }

    public Account(Account account) {
        this(account.getConfig());
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public String getId() {
        return (String) config.get("id");
    }
}
