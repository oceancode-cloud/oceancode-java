package com.oceancode.cloud.api.plugin;

import java.util.List;
import java.util.Map;

public class ArthasBody {
    private List<Map<String, Object>> results;

    public List<Map<String, Object>> getResults() {
        return results;
    }

    public void setResults(List<Map<String, Object>> results) {
        this.results = results;
    }
}
