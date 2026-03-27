package com.oceancode.cloud.api.ai;

import com.oceancode.cloud.chart.ChartMessageCallback;

public interface AiService {
    String getType();

    AiResponse chart(AiMessage message, ChartMessageCallback callback);
}
