package com.oceancode.cloud.chart;

public interface ChartMessageHandler {
    String getCategory();

    ChartMessage onMessage(ChartMessage message);
}
