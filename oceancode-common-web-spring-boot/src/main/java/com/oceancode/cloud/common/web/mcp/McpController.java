package com.oceancode.cloud.common.web.mcp;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP HTTP transport:
 * <ul>
 *   <li>Streamable HTTP (2025+): single endpoint {@code POST/GET/DELETE /mcp}</li>
 *   <li>Legacy HTTP+SSE (2024-11): {@code GET /mcp/sse} + {@code POST /mcp/messages}</li>
 * </ul>
 */
@RestController
@RequestMapping(CommonConst.API_PREFIX)
@ConditionalOnBean(ToolManager.class)
public class McpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(McpController.class);

    /** Streamable HTTP session header (MCP spec). */
    public static final String MCP_SESSION_ID_HEADER = "Mcp-Session-Id";

    private static final String METHOD_INITIALIZE = "initialize";

    /** Legacy SSE: sessionId -> (emitter, userId). */
    private static final Map<String, Tuple2<SseEmitter, Long>> LEGACY_SESSION_MAP = new ConcurrentHashMap<>();

    /** Streamable HTTP: sessionId -> (optional GET stream emitter, userId). */
    private static final Map<String, Tuple2<SseEmitter, Long>> STREAMABLE_SESSION_MAP = new ConcurrentHashMap<>();

    private final McpProtocolService mcpProtocolService;
    private final ToolManager toolManager;

    public McpController(McpProtocolService mcpProtocolService, ToolManager toolManager) {
        this.mcpProtocolService = mcpProtocolService;
        this.toolManager = toolManager;
    }

    // -------------------------------------------------------------------------
    // Streamable HTTP (Cursor / MCP 2025-03+)
    // -------------------------------------------------------------------------

    @CrossOrigin
    @PostMapping(
            value = "/mcp",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE}
    )
    public Object streamableHttp(
            @RequestBody Object body,
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String accept,
            @RequestHeader(value = MCP_SESSION_ID_HEADER, required = false) String mcpSessionId,
            HttpServletRequest servletRequest) {

        if (!checkMcpToken("mcp")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        SessionUtil.setSource(CommonConst.MCP_SOURCE);
        List<JsonRpcRequest> messages = parseJsonRpcBody(body);
        if (messages.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        boolean hasRequest = messages.stream().anyMatch(this::isJsonRpcRequest);
        if (!hasRequest) {
            for (JsonRpcRequest msg : messages) {
                mcpProtocolService.handler(msg);
            }
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        String sessionId = resolveStreamableSession(messages, mcpSessionId);
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean preferSse = acceptsEventStream(accept);
        if (preferSse && messages.size() == 1 && isJsonRpcRequest(messages.get(0))) {
            return streamablePostAsSse(messages.get(0), sessionId);
        }

        List<Object> results = new ArrayList<>();
        for (JsonRpcRequest msg : messages) {
            if (!isJsonRpcRequest(msg)) {
                mcpProtocolService.handler(msg);
                continue;
            }
            Object res = mcpProtocolService.handler(msg);
            if (res != null) {
                results.add(res);
            }
        }

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
        builder.header(MCP_SESSION_ID_HEADER, sessionId);
        if (results.isEmpty()) {
            return builder.build();
        }
        if (results.size() == 1) {
            return builder.body(results.get(0));
        }
        return builder.body(results);
    }

    @CrossOrigin
    @GetMapping(value = "/mcp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Object streamableHttpGet(
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String accept,
            @RequestHeader(value = MCP_SESSION_ID_HEADER, required = false) String mcpSessionId,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

        if (!acceptsEventStream(accept)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
        if (!checkMcpToken("mcp")) {
            SseEmitter denied = new SseEmitter(0L);
            denied.complete();
            return denied;
        }

        SessionUtil.setSource(CommonConst.MCP_SOURCE);
        Long userId = SessionUtil.userId();
        if (userId == null) {
            LOGGER.error("not login");
            SseEmitter denied = new SseEmitter(0L);
            denied.complete();
            return denied;
        }

        if (!StringUtils.hasText(mcpSessionId) || !isValidStreamableSession(mcpSessionId, userId)) {
            LOGGER.error("invalid or missing {}", MCP_SESSION_ID_HEADER);
            SseEmitter denied = new SseEmitter(0L);
            denied.complete();
            return denied;
        }

        SseEmitter emitter = new SseEmitter(0L);
        STREAMABLE_SESSION_MAP.put(mcpSessionId, new Tuple2<>(emitter, userId));
        Runnable cleanup = () -> removeStreamableEmitter(mcpSessionId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            if (StringUtils.hasText(lastEventId)) {
                emitter.send(SseEmitter.event().id(lastEventId).comment("resume"));
            } else {
                emitter.send(SseEmitter.event().comment("open"));
            }
        } catch (IOException e) {
            cleanup.run();
        }
        return emitter;
    }

    @CrossOrigin
    @DeleteMapping("/mcp")
    public ResponseEntity<Void> streamableHttpDelete(
            @RequestHeader(value = MCP_SESSION_ID_HEADER, required = false) String mcpSessionId) {

        if (!StringUtils.hasText(mcpSessionId)) {
            return ResponseEntity.badRequest().build();
        }
        Tuple2<SseEmitter, Long> removed = STREAMABLE_SESSION_MAP.remove(mcpSessionId);
        if (removed != null && removed.getFirst() != null) {
            removed.getFirst().complete();
        }
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Legacy HTTP+SSE (MCP 2024-11, backwards compatibility)
    // -------------------------------------------------------------------------

    @CrossOrigin
    @GetMapping(value = "/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter legacySse() {
        SseEmitter emitter = new SseEmitter(0L);
        try {
            if (!checkMcpToken("mcp_message")) {
                LOGGER.error("sessionId invalid");
                emitter.complete();
                return emitter;
            }
        } catch (Exception e) {
            LOGGER.error("sessionId invalid", e);
            emitter.complete();
            return emitter;
        }

        Long id = SessionUtil.userId();
        SessionUtil.setSource(CommonConst.MCP_SOURCE);
        if (Objects.isNull(id)) {
            LOGGER.error("not login");
            emitter.complete();
            return emitter;
        }

        String sessionId = UUID.randomUUID().toString();
        LEGACY_SESSION_MAP.put(sessionId, new Tuple2<>(emitter, id));
        Runnable runnable = () -> LEGACY_SESSION_MAP.remove(sessionId);
        emitter.onCompletion(runnable);
        emitter.onTimeout(runnable);
        emitter.onError(e -> runnable.run());

        try {
            String endpointUrl = legacyMessagesEndpoint(sessionId);
            emitter.send(SseEmitter.event().name("endpoint").data(endpointUrl));
        } catch (IOException e) {
            LEGACY_SESSION_MAP.remove(sessionId);
            emitter.complete();
        }
        return emitter;
    }

    @CrossOrigin
    @PostMapping(value = "/mcp/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> legacyMessage(
            HttpServletRequest httpServletRequest,
            @RequestBody JsonRpcRequest request) {

        String sessionId = httpServletRequest.getParameter("sessionId");
        try {
            if (!checkMcpToken("mcp_message")) {
                LOGGER.error("sessionId invalid");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            LOGGER.error("sessionId invalid", e);
            return ResponseEntity.badRequest().build();
        }

        SessionUtil.setSource(CommonConst.MCP_SOURCE);
        Tuple2<SseEmitter, Long> item = LEGACY_SESSION_MAP.get(sessionId);
        if (Objects.isNull(item) || !Objects.equals(item.getSecond(), SessionUtil.userId())) {
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
                LEGACY_SESSION_MAP.remove(sessionId);
                LOGGER.error("error.", e);
            }
        }
        return ResponseEntity.accepted().build();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private SseEmitter streamablePostAsSse(JsonRpcRequest request, String sessionId) {
        SseEmitter emitter = new SseEmitter(0L);
        Runnable cleanup = () -> { /* POST stream is short-lived; no map entry */ };
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        try {
            Object res = mcpProtocolService.handler(request);
            if (res != null) {
                String line = JsonUtil.toJson(res);
                emitter.send(SseEmitter.event().data(line, MediaType.APPLICATION_JSON));
            }
            emitter.complete();
        } catch (Exception e) {
            LOGGER.error("streamable POST SSE error", e);
            emitter.completeWithError(e);
        }
        return emitter;
    }

    private String resolveStreamableSession(List<JsonRpcRequest> messages, String headerSessionId) {
        boolean initializing = messages.stream()
                .anyMatch(m -> METHOD_INITIALIZE.equals(m.method()));

        Long userId = SessionUtil.userId();
        if (userId == null) {
            LOGGER.error("not login");
            return null;
        }

        if (initializing) {
            String sessionId = UUID.randomUUID().toString();
            STREAMABLE_SESSION_MAP.put(sessionId, new Tuple2<>(null, userId));
            return sessionId;
        }

        if (!StringUtils.hasText(headerSessionId)) {
            LOGGER.error("missing {} for non-initialize request", MCP_SESSION_ID_HEADER);
            return null;
        }
        if (!isValidStreamableSession(headerSessionId, userId)) {
            LOGGER.error("invalid {}", MCP_SESSION_ID_HEADER);
            return null;
        }
        return headerSessionId;
    }

    private boolean isValidStreamableSession(String sessionId, Long userId) {
        Tuple2<SseEmitter, Long> item = STREAMABLE_SESSION_MAP.get(sessionId);
        return item != null && Objects.equals(item.getSecond(), userId);
    }

    private void removeStreamableEmitter(String sessionId, SseEmitter emitter) {
        Tuple2<SseEmitter, Long> item = STREAMABLE_SESSION_MAP.get(sessionId);
        if (item != null && item.getFirst() == emitter) {
            STREAMABLE_SESSION_MAP.put(sessionId, new Tuple2<>(null, item.getSecond()));
        }
    }

    private boolean checkMcpToken(String resource) {
        boolean ret = PermissionUtil.checkPrivateToken(resource, PermissionConst.RESOURCE_SSE);
        if (!ret) {
            LOGGER.error("permission denied for {}", resource);
        }
        return ret;
    }

    private static boolean acceptsEventStream(String accept) {
        if (!StringUtils.hasText(accept)) {
            return true;
        }
        return accept.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
    }

    /** JSON-RPC request: has method and id; notification has method but no id. */
    private boolean isJsonRpcRequest(JsonRpcRequest msg) {
        if (msg == null || !StringUtils.hasText(msg.method())) {
            return false;
        }
        return msg.id() != null;
    }

    @SuppressWarnings("unchecked")
    private List<JsonRpcRequest> parseJsonRpcBody(Object body) {
        List<JsonRpcRequest> list = new ArrayList<>();
        if (body == null) {
            return list;
        }
        if (body instanceof List<?> rawList) {
            for (Object item : rawList) {
                JsonRpcRequest req = toJsonRpcRequest(item);
                if (req != null) {
                    list.add(req);
                }
            }
            return list;
        }
        JsonRpcRequest single = toJsonRpcRequest(body);
        if (single != null) {
            list.add(single);
        }
        return list;
    }

    private JsonRpcRequest toJsonRpcRequest(Object item) {
        if (item instanceof JsonRpcRequest req) {
            return req;
        }
        String json = JsonUtil.toJson(item);
        return JsonUtil.toBean(json, JsonRpcRequest.class);
    }

    private static String legacyMessagesEndpoint(String sessionId) {
        String prefix = CommonConst.API_PREFIX;
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + "mcp/messages?sessionId=" + sessionId;
    }
}
