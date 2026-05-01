package com.oceancode.cloud.common.web.mcp;

import com.oceancode.cloud.api.permission.Permission;
import com.oceancode.cloud.api.permission.PermissionConst;
import com.oceancode.cloud.api.tool.ToolManager;
import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.PermissionUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
public class McpController {
    private McpProtocolService mcpProtocolService;
    private ToolManager toolManager;
    private static final Map<String, SseEmitter> SESSION_MAP = new ConcurrentHashMap<>();

    public McpController(McpProtocolService mcpProtocolService, ToolManager toolManager) {
        this.mcpProtocolService = mcpProtocolService;
        this.toolManager = toolManager;
    }

    @CrossOrigin
    @GetMapping(value = "/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Permission(resourceId = "mcp_sse", authorities = {"login"}, resourceType = PermissionConst.RESOURCE_SSE)
    public SseEmitter sse(HttpServletRequest request) {
        String sessionId = SessionUtil.userId().toString();
        SseEmitter emitter = new SseEmitter(0L);
        SESSION_MAP.put(sessionId, emitter);
        Runnable runnable = () -> SESSION_MAP.remove(sessionId);
        emitter.onCompletion(runnable);
        emitter.onTimeout(runnable);
//        emitter.onError(e -> {
//            try {
//                emitter.send(SseEmitter.event().name("endpoint").data(CommonConst.API_PREFIX + "mcp/messages?sessionId=" + id, MediaType.TEXT_PLAIN));
//            } catch (Exception ex) {
//                SESSION_MAP.remove(id);
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
//            }
//        });
        try {
            // MCP 标准：连接建立后，服务端发送 endpoint 事件，告知客户端去哪里发 POST 消息
            // 注意：这里硬编码了本地地址，实际部署需改为配置的域名/IP
            String endpointUrl = CommonConst.API_PREFIX + "mcp/messages";
            emitter.send(SseEmitter.event().name("endpoint").data(endpointUrl));
            System.out.println("Client connected, session: " + sessionId);
        } catch (IOException e) {
            SESSION_MAP.remove(sessionId);
        }

        return emitter;
    }

    @CrossOrigin
    @PostMapping(value = "/mcp/messages", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Permission(resourceId = "mcp_message", authorities = {"login"}, resourceType = PermissionConst.RESOURCE_SSE)
    public ResponseEntity<Void> message(@RequestBody JsonRpcRequest request) {
        String sessionId = SessionUtil.userId().toString();
        SseEmitter out = SESSION_MAP.get(sessionId);
        if (Objects.isNull(out)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
            }
        }
        return ResponseEntity.accepted().build();
    }
}
