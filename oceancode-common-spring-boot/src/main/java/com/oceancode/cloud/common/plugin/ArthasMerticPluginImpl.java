package com.oceancode.cloud.common.plugin;

import com.oceancode.cloud.api.ApiClient;
import com.oceancode.cloud.api.ClientResult;
import com.oceancode.cloud.api.plugin.ApiMetricsPlugin;
import com.oceancode.cloud.api.plugin.Metrics;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
public class ArthasMerticPluginImpl implements ApiMetricsPlugin {
    @Resource
    private ApiClient apiClient;

    private String getApi(String url) {
        return ValueUtil.isEmpty(url) ? "http://localhost:8563/api" : url;
    }

    public ArthasResult init(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "init_session");
        ClientResult<ArthasResult> clientResult = apiClient.postFor(getApi(url), params, ArthasResult.class);
        if (clientResult instanceof ArthasResult) {
            return (ArthasResult) clientResult;
        }
        throw new ErrorCodeRuntimeException(clientResult.getCode(), clientResult.getMessage());
    }


    @Override
    public List<Metrics> getMetrics(String url, String api, Runnable runnable) {
        ArthasResult arthasResult = init(url);
        ClientResult<ArthasResult> clientResult = sendMethodCommond(arthasResult, url, api);
        if (!"SCHEDULED".equals(clientResult.getCode())) {
            return Collections.emptyList();
        }
        pullResult(arthasResult, url);
        List<Metrics> list = new ArrayList<>();
        try {
            processRequest(list, url, api, arthasResult, runnable);
        } finally {
            close(arthasResult, url);
        }

        return list;
    }

    private void close(ArthasResult arthasResult, String url) {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "close_session");
        params.put("consumerId", arthasResult.getConsumerId());
        params.put("sessionId", arthasResult.getSessionId());
        apiClient.postFor(getApi(url), params, ArthasResult.class);
    }

    private List<Metrics> processRequest(List<Metrics> list, String url, String api, ArthasResult arthasResult, Runnable runnable) {
        String methodName = api.substring(api.lastIndexOf(" ")).trim();
        processRun(runnable);
        list.addAll(processPullResult(arthasResult, url, methodName));

        return list.stream().filter(e -> methodName.equals(e.getName()))
                .filter(e -> ValueUtil.isNotEmpty(e.getPath()))
                .filter(e -> {
                    return !list.stream().filter(p -> p.getParentId().equals(e.getId())).findFirst().isPresent();
                }).toList();

    }

    private Collection<? extends Metrics> processPullResult(ArthasResult arthasResult, String url, String methodName) {
        ArthasResult result = null;
        int count = 0;
        while (count < 3) {
            ArthasResult temp = pullResult(arthasResult, url);

            if (Objects.nonNull(temp) && Objects.nonNull(temp.getBody()) && ValueUtil.isNotEmpty(temp.getBody().getResults())) {
                result = temp;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Throwable throwable) {

            }
            count++;
        }

        if (Objects.isNull(result)) {
            return Collections.emptyList();
        }

        List<Metrics> list = new ArrayList<>();
        for (Map<String, Object> map : result.getBody().getResults()) {
            Map<String, Object> rootMap = (Map<String, Object>) map.get("root");
            if (ValueUtil.isEmpty(rootMap)) {
                continue;
            }
            processData(list, Arrays.asList(rootMap), null);
        }

        List<Metrics> resultList = list.stream().filter(e -> methodName.equals(e.getName())).toList();
        Set<String> ids = new HashSet<>();
        List<Metrics> list2 = new ArrayList<>();
        list2.addAll(resultList);
        ids.addAll(resultList.stream().map(e -> e.getId()).toList());
        filterData(list, list2, resultList, ids, true);
        filterData(list, list2, resultList, ids, false);
        return list2;
    }

    private void filterData(List<Metrics> allList, List<Metrics> list, List<Metrics> methods, Set<String> ids, boolean processChild) {
        for (Metrics method : methods) {
            List<Metrics> results = allList.stream().filter(e -> {
                        if (processChild) {
                            return e.getParentId().equals(method.getId());
                        }
                        return e.getId().equals(method.getId());
                    }).filter(e -> !ids.contains(e.getId()))
                    .toList();
            list.addAll(results);
            ids.addAll(results.stream().map(e -> e.getId()).toList());
            filterData(allList, list, results, ids, processChild);
        }
    }

    private void processData(List<Metrics> list, List<Map<String, Object>> dataList, String parentId) {
        if (ValueUtil.isEmpty(dataList)) {
            return;
        }
        for (Map<String, Object> map : dataList) {
            String type = (String) map.get("type");
            String id = UUID.randomUUID().toString().replace("-", "");
            if ("method".equals(type)) {
                Metrics metrics = new Metrics();
                metrics.setName((String) map.get("methodName"));
                metrics.setPath((String) map.get("className"));
                metrics.setCodeLineNumber(Integer.parseInt(map.get("lineNumber") + ""));
                metrics.setTotalCost(Long.parseLong(map.get("totalCost") + ""));
                metrics.setId(id);
                metrics.setParentId(parentId);
                list.add(metrics);
            }
            processData(list, (List<Map<String, Object>>) map.get("children"), id);
        }
    }

    private void processRun(Runnable runnable) {
        try {
            Thread.sleep(1000);
            runnable.run();
        } catch (Throwable throwable) {

        }
    }

    private ArthasResult pullResult(ArthasResult arthasResult, String url) {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "pull_results");
        params.put("consumerId", arthasResult.getConsumerId());
        params.put("sessionId", arthasResult.getSessionId());
        return (ArthasResult) apiClient.postFor(getApi(url), params, ArthasResult.class);
    }

    private ClientResult<ArthasResult> sendMethodCommond(ArthasResult arthasResult, String url, String api) {
        Map<String, Object> params = new HashMap<>();
        params.put("action", "async_exec");
        params.put("command", "trace " + api + " -n 1");
        params.put("consumerId", arthasResult.getConsumerId());
        params.put("sessionId", arthasResult.getSessionId());
        return apiClient.postFor(getApi(url), params, ArthasResult.class);
    }
}
