package com.oceancode.cloud.common.web.mcp;

import com.oceancode.cloud.api.ErrorCode;
import com.oceancode.cloud.api.tool.ToolLoader;
import com.oceancode.cloud.api.tool.ToolManager;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@ConditionalOnBean(ToolManager.class)
public class McpProtocolService {
    @Value("${oc.mcp.name:idesign}")
    private String name;
    @Value("${oc.mcp.version:2.0}")
    private String version;
    @Value("${oc.mcp.protocol.version:2025-06-18}")
    private String protocolVersion;
    private ToolManager toolManager;
    private final static Logger LOGGER = LoggerFactory.getLogger(McpProtocolService.class);

    public McpProtocolService(ToolManager toolManager) {
        this.toolManager = toolManager;
        toolManager.setName(name);
    }

    public Object handler(JsonRpcRequest request) {
        try {
            if (Objects.isNull(request) || ValueUtil.isEmpty(request.method())) {
                return JsonRpcResponse.error(null, -32600, "Invalid request");
            }
            return switch (request.method()) {
                case "initialize" -> handleInitialize(request);
                case "tools/list" -> handleListTools(request);
                case "tools/call" -> handleCallTool(request);
                case "ping" -> JsonRpcResponse.success(request.id(), Map.of());
                case "notifications/initialized" -> null;
                default -> JsonRpcResponse.error(request.id(), -32600, "method(" + request.method() + ") not found");
            };
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (ex instanceof BusinessRuntimeException runtimeException) {
                ErrorCode code = runtimeException.getCode();
                message = code.getMessage();
            } else if (ex instanceof ErrorCodeRuntimeException e) {
                message = e.getErrorCode() + " - " + e.getMessage();
            }
            LOGGER.error("err", ex);
            return JsonRpcResponse.error(request.id(), -32600, message);
        }
    }


    // 1. 握手协议
    private JsonRpcResponse handleInitialize(JsonRpcRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", protocolVersion);
        result.put("capabilities", Map.of("tools", Map.of())); // 声明支持工具
        result.put("serverInfo", Map.of("name", name, "version", version));
        return JsonRpcResponse.success(request.id(), result);
    }

    // 2. 定义工具列表
    private JsonRpcResponse handleListTools(JsonRpcRequest request) {
        List<Map<String, Object>> list = toolManager.getTools();
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (Objects.nonNull(list)) {
            resultList.addAll(list);
        }
        try {
            Map<String, ToolLoader> beans = ComponentUtil.getBeans(ToolLoader.class);
            Collection<ToolLoader> values = beans.values();
            for (ToolLoader value : values) {
                List<Map<String, Object>> tools = value.getTools();
                if (Objects.nonNull(tools)) {
                    resultList.addAll(tools);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        LOGGER.info("tools/list count={}", resultList.size());
        return JsonRpcResponse.success(request.id(), Map.of("tools", resultList));
    }

    private Object handleCallTool(JsonRpcRequest request) {
        String name = request.params().get("name").asText();
        Map<String, Object> arguments = JsonUtil.toBean(request.params().get("arguments").toString(), Map.class);

        if (toolManager.isClientMethod(name)) {
            Map<String, Object> func = new HashMap<>();
            name = name.substring(name.indexOf(".") + 1);
            func.put("name", name);
            func.put("arguments", arguments);

            Map<String, Object> map = new HashMap<>();
            map.put("type", "text");
            map.put("text", "前端执行");
            map.put("functionCall", func);

            Map<String, Object> content = Map.of(
                    "content", List.of(map)
            );

            return JsonRpcResponse.success(request.id(), content);
        }
        Object result = toolManager.execute(name, arguments);
        Map<String, Object> data = new HashMap<>();
        data.put("code", 200);
        data.put("message", "");
        data.put("results", result);
        int total = 1;
        if (Objects.isNull(result)) {
            total = 0;
        } else if (result instanceof List list) {
            total = list.size();
        }
        data.put("total", total);
        Map<String, Object> content = new HashMap<>();


        String text = JsonUtil.toJson(result);

        List<Map<String, Object>> contentList = new ArrayList<>();
        if (Objects.nonNull(text)) {
            Map<String, Object> contentDataMap = new HashMap<>();
            contentList.add(contentDataMap);
            contentDataMap.put("type", "text");
            contentDataMap.put("text", text);
        }
        content.put("content", contentList);
        content.put("structuredContent", data);
        return JsonRpcResponse.success(request.id(),
                content);
    }
}
