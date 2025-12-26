package com.oceancode.cloud.api.autoconfig;

import java.util.List;
import java.util.Map;

public class AutoConfigRequest {
    private List<AutoConfigRequestItem> items;
    private Map<String, Object> extra;

    public List<AutoConfigRequestItem> getItems() {
        return items;
    }

    public void setItems(List<AutoConfigRequestItem> items) {
        this.items = items;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "AutoConfigRequest{" +
                "items=" + items +
                '}';
    }
}
