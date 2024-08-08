package com.oceancode.cloud.common.fetcher;

import com.oceancode.cloud.api.fetcher.DataFetcher;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import jakarta.annotation.Resource;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ApiDataFetcher implements DataFetcher {
    private CommonConfig commonConfig;

    private WebClient.Builder webClientBuilder;

    public ApiDataFetcher() {
        commonConfig = ComponentUtil.getBean(CommonConfig.class);
        webClientBuilder = ComponentUtil.getBean(WebClient.Builder.class);
    }

    @Override
    public <T> T get(String dataId, Object params, Class<T> returnTypeClass) {
        return (T) fetchData(dataId, params, returnTypeClass, false);
    }

    @Override
    public <T> List<T> getList(String dataId, Object params, Class<T> returnTypeClass) {
        return (List<T>) fetchData(dataId, params, returnTypeClass, true);
    }

    private <T> Object fetchData(String dataId, Object params, Class<T> returnTypeClass, boolean multiple) {
        String protocol = commonConfig.getValue("oc.data." + dataId + ".protocol", "http://");
        String type = commonConfig.getValue("oc.data." + dataId + ".request.type", "post");
        String url = commonConfig.getValue("oc.data." + dataId + ".request.url");
        String name = commonConfig.getValue("oc.data." + dataId + ".request.service.name");
        boolean isGet = "get".equalsIgnoreCase(type);
        boolean isDelete = "delete".equalsIgnoreCase(type);
        if (isGet || isDelete) {
            if (Objects.nonNull(params)) {
                if (params instanceof Map<?, ?>) {
                    Map<?, ?> p = (Map<?, ?>) params;
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<?, ?> entry : p.entrySet()) {
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                    if (!url.contains("?")) {
                        url = url + "?" + sb.toString();
                    } else {
                        if (url.endsWith("&") || url.endsWith("?")) {
                            url = url + sb.toString();
                        } else {
                            url = url + "&" + sb.toString();
                        }
                    }
                }
            }
        }
        WebClient webClient = webClientBuilder.build();
        WebClient.RequestHeadersSpec<?> spec = null;
        String uri = protocol + name + url;
        if ("post".equalsIgnoreCase(type)) {
            spec = webClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        } else if (isGet) {
            spec = webClient.get()
                    .uri(uri);
        } else if ("put".equalsIgnoreCase(type)) {
            spec = webClient.put().uri(uri).contentType(MediaType.APPLICATION_JSON).bodyValue(params);
        } else if (isDelete) {
            spec = webClient.delete().uri(uri);
        }
        if (Objects.nonNull(SessionUtil.userId())) {
            spec.header(CommonConst.X_USER_ID, SessionUtil.userId() + "");
        }
        if (Objects.nonNull(SessionUtil.projectId())) {
            spec.header(CommonConst.X_PROJECT_ID, SessionUtil.userId() + "");
        }
        if (Objects.nonNull(SessionUtil.tenantId())) {
            spec.header(CommonConst.X_TENANT_ID, SessionUtil.userId() + "");
        }
        spec.header(CommonConst.X_REQUEST_ID, MDC.get(CommonConst.REQUEST_ID));
        spec.header(CommonConst.TRACE_ID, MDC.get(CommonConst.TRACE_ID));
        WebClient.ResponseSpec responseSpec = spec
                .accept(MediaType.APPLICATION_JSON)
                .retrieve();
        if (multiple) {
            return responseSpec.bodyToFlux(returnTypeClass).collectList().block();
        }
        return responseSpec.bodyToMono(returnTypeClass).block();
    }
}
