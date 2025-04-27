package com.oceancode.cloud.api.plugin;

import java.util.List;

public interface ApiMetricsPlugin {
    List<Metrics> getMetrics(String url, String api, Runnable runnable);
}
