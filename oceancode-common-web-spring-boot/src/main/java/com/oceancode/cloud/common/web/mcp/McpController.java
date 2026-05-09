package com.oceancode.cloud.common.web.mcp;

import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.tool.ToolManager;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.PermissionUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.entity.Tuple2;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
public class McpController {
    private final static Logger LOGGER = LoggerFactory.getLogger(McpController.class);
    private McpProtocolService mcpProtocolService;
    private ToolManager toolManager;
    private static final Map<String, Tuple2<SseEmitter, Long>> SESSION_MAP = new ConcurrentHashMap<>();

    public McpController(McpProtocolService mcpProtocolService, ToolManager toolManager) {
        this.mcpProtocolService = mcpProtocolService;
        this.toolManager = toolManager;
    }

    @CrossOrigin
    @GetMapping(value = "/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @Permission(resourceId = "mcp_sse", authorities = {"login"}, resourceType = PermissionConst.RESOURCE_SSE)
    public SseEmitter sse(HttpServletRequest request) {
        SseEmitter emitter = new SseEmitter(0L);
        try {
            boolean ret = PermissionUtil.checkPrivateToken("mcp_message", PermissionConst.RESOURCE_SSE);
            if (!ret) {
                LOGGER.error("sessionId invalid");
                return emitter;
            }
        } catch (Exception e) {
            LOGGER.error("sessionId invalid", e);
            return emitter;
        }
        Long id = SessionUtil.userId();
        SessionUtil.setSource(CommonConst.MCP_SOURCE);
        if (Objects.isNull(id)) {
            LOGGER.error("not login");
            return emitter;
        }
        String sessionId = UUID.randomUUID().toString();
        SESSION_MAP.put(sessionId, new Tuple2<>(emitter, SessionUtil.userId()));
        Runnable runnable = () -> SESSION_MAP.remove(sessionId);
        emitter.onCompletion(runnable);
        emitter.onTimeout(runnable);
        try {
            String endpointUrl = CommonConst.API_PREFIX + "mcp/messages?sessionId=" + sessionId;
            emitter.send(SseEmitter.event().name("endpoint").data(endpointUrl));
        } catch (IOException e) {
            SESSION_MAP.remove(sessionId);
        }

        return emitter;
    }

    @CrossOrigin
    @PostMapping(value = "/mcp/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @Permission(resourceId = "mcp_message", authorities = {"login"}, resourceType = PermissionConst.RESOURCE_SSE)
    public ResponseEntity<Void> message(HttpServletRequest httpServletRequest, @RequestBody JsonRpcRequest request) {
        String sessionId = httpServletRequest.getParameter("sessionId");
        try {
            boolean ret = PermissionUtil.checkPrivateToken("mcp_message", PermissionConst.RESOURCE_SSE);
            if (!ret) {
                LOGGER.error("sessionId invalid");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            LOGGER.error("sessionId invalid", e);
            return ResponseEntity.badRequest().build();
        }
        SessionUtil.setSource(CommonConst.MCP_SOURCE);
        Tuple2<SseEmitter, Long> item = SESSION_MAP.get(sessionId);
        if (Objects.isNull(item) || !Objects.equals(item.getSecond(), SessionUtil.userId())) {
            LOGGER.error("sessionId invalid");
            return ResponseEntity.badRequest().build();
        }
        Long id = item.getSecond();
        if (Objects.isNull(id)) {
            LOGGER.error("sessionId invalid");
            return ResponseEntity.badRequest().build();
        }
        SseEmitter out = item.getFirst();
        if (Objects.isNull(out)) {
            LOGGER.error("sessionId invalid");
            return ResponseEntity.badRequest().build();
        }

        Object res = mcpProtocolService.handler(request);
        if (Objects.nonNull(res)) {
            try {
                String line = JsonUtil.toJson(res);
                synchronized (out) {
                    SseEmitter.SseEventBuilder builder = SseEmitter.event();
                    builder.name(toolManager.getEventName(request.method()));
                    out.send(builder.data(line, MediaType.APPLICATION_JSON));
                }
            } catch (Exception e) {
                SESSION_MAP.remove(sessionId);
                LOGGER.error("error.", e);
            }
        }
        return ResponseEntity.accepted().build();
    }
}
