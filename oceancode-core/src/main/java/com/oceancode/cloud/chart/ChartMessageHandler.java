package com.oceancode.cloud.chart;

public interface ChartMessageHandler {
    void onMessage(ChartMessage message, ChartMessageCallback callback);
}
