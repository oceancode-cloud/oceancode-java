package com.oceancode.cloud.common.plugin.ai;

import com.oceancode.cloud.api.ai.AiMessage;
import com.oceancode.cloud.api.ai.AiMessageContent;
import com.oceancode.cloud.api.ai.AiResponse;
import com.oceancode.cloud.api.ai.AiService;
import com.oceancode.cloud.chart.ChartMessageCallback;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Primary
@Component
public class DefaultAiServiceImpl implements AiService {
    private Map<String, AiService> serviceMap;

    @Resource
    private CommonConfig commonConfig;

    public DefaultAiServiceImpl(Set<AiService> services) {
        serviceMap = new HashMap<>(services.size());
        for (AiService service : services) {
            serviceMap.put(service.getType(), service);
        }
    }

    @Override
    public String getType() {
        return "default";
    }

    @Override
    public AiResponse chart(AiMessage message, ChartMessageCallback callback) {
        if (ValueUtil.isEmpty(message.getContents())) {
            return null;
        }
        AiService aiService = serviceMap.get(message.getId());
        if (Objects.nonNull(aiService)) {
            return aiService.chart(message, callback);
        }
        String id = message.getId();
        String baseUrl = commonConfig.getValue("oc.ai." + id + ".base-url");
        String authorization = "Bearer " + commonConfig.getValue("oc.ai." + id + ".api-key");

        Map<String, Object> params = new HashMap<>();
        params.put("model", message.getModel());
        String temperature = commonConfig.getValue("oc.ai.options.temperature");
        if (ValueUtil.isNotEmpty(temperature)) {
            params.put("temperature", temperature);
        }
        params.put("stream", true);

        List<Map<String, Object>> messages = new ArrayList<>();
        params.put("messages", messages);
        for (AiMessageContent content : message.getContents()) {
            Map<String, Object> msgItem = new HashMap<>();
            messages.add(msgItem);
            String role = content.getRole();
            if (ValueUtil.isEmpty(role)) {
                role = "user";
            }
            msgItem.put("role", role);
            List<Map<String, Object>> contents = new ArrayList<>();
            msgItem.put("content", contents);

            Map<String, Object> contentItem = new HashMap<>();
            String type = content.getType();
            if (ValueUtil.isEmpty(type)) {
                type = "text";
            }
            contentItem.put("type", type);
            contentItem.put("text", content.getContent());
            contents.add(contentItem);
        }

        String url = baseUrl;
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "chat/completions";
        Flux<String> flux = WebClient.create(url)
                .post()
                .header("Authorization", authorization)
                .bodyValue(params)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve().bodyToFlux(String.class);
        flux.subscribe(callback::call);
        return null;
    }
}
