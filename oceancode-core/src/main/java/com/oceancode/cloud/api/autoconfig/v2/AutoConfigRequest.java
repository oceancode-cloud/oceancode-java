package com.oceancode.cloud.api.autoconfig.v2;

import java.util.List;

public class AutoConfigRequest {
    private List<AutoConfig> items;

    public List<AutoConfig> getItems() {
        return items;
    }

    public void setItems(List<AutoConfig> items) {
        this.items = items;
    }
}
