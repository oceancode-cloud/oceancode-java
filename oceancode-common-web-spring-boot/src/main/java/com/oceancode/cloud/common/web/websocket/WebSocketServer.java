package com.oceancode.cloud.common.web.websocket;

import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.chart.ChartMessage;
import com.oceancode.cloud.chart.ChartMessageHandler;
import com.oceancode.cloud.chart.ChartMessageType;
import com.oceancode.cloud.chart.RTMessageService;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.JsonUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/chart/{userId}")
@ConditionalOnClass(ServerEndpointExporter.class)
public class WebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);
    public static final Map<Long, WsSession> SESSIONS = new ConcurrentHashMap<>();

    private static SessionService sessionService;

    private static RTMessageService RTMessageService;
    private static Map<String, ChartMessageHandler> handlers = new HashMap<>();

    @PostConstruct
    public void init() {
        sessionService = ComponentUtil.getBean(SessionService.class);
        RTMessageService = ComponentUtil.getBean(WebsocketRTMessageServiceImpl.class);
        JsonUtil.registerTypeEnum(ChartMessageType.class);

        Collection<ChartMessageHandler> values = ComponentUtil.getBeans(ChartMessageHandler.class).values();
        for (ChartMessageHandler value : values) {
            if (ValueUtil.isEmpty(value.getCategory())) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "category is required." + value);
            }
            if (handlers.containsKey(value.getCategory())) {
                throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "category already exists." + value);
            }
            handlers.put(value.getCategory(), value);
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String uid) {
        Long userId = null;
        if (ValueUtil.isNotEmpty(uid)) {
            try {
                userId = Long.parseLong(uid);
            } catch (Exception e) {
                LOGGER.error("userId invalid.", e);
            }
        }
        if (Objects.isNull(userId)) {
            try {
                session.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        processConnect(userId, session);
    }

    private synchronized void processConnect(Long userId, Session session) {
        WsSession wsSession = new WsSession(userId, session);
        if (SESSIONS.containsKey(userId)) {
            SESSIONS.remove(userId);
        }
        if (Objects.isNull(userId)) {
            wsSession.reply(ChartMessage.notifier().errorCode(CommonErrorCode.USER_NOT_FOUND));
            wsSession.close();
            return;
        }

        if (!sessionService.isLogin(userId)) {
            wsSession.reply(ChartMessage.notifier().errorCode(CommonErrorCode.ACCESS_DENIED));
            wsSession.close();
            return;
        }
        SESSIONS.put(userId, wsSession);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSIONS.remove(userId);
        SessionUtil.remove();
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        try {
            processMessage(message, userId);
        } finally {
            SessionUtil.remove();
        }
    }

    private void processMessage(String message, Long userId) {
        WsSession wsSession = SESSIONS.get(userId);
        if (ValueUtil.isEmpty(message)) {
            return;
        }
        SessionUtil.setUserId(userId);
        wsSession.update();
        if ("ping".equalsIgnoreCase(message)) {
            wsSession.pong();
            return;
        }

        ChartMessage chartMessage = JsonUtil.toBean(message, ChartMessage.class);
        if (Objects.isNull(chartMessage)) {
            return;
        }
        if (Objects.nonNull(chartMessage.getProjectId())) {
            SessionUtil.setProjectId(chartMessage.getProjectId());
        }

        if (Objects.nonNull(chartMessage.getTenantId())) {
            SessionUtil.setTenantId(chartMessage.getTenantId());
        }
        SessionUtil.setUserId(userId);

        chartMessage.setFromUser(userId);
        if (ChartMessageType.MESSAGE.equals(chartMessage.getType())) {
            ChartMessageHandler messageHandler = handlers.get(chartMessage.getCategory());
            ChartMessage replyMessage = messageHandler.onMessage(chartMessage);
            processReplyMessage(wsSession, chartMessage, replyMessage);
            return;
        }

        if (ValueUtil.isNotEmpty(chartMessage.getToUser())) {
            RTMessageService.sendTo(chartMessage.getToUser(), chartMessage);
            ChartMessage replyMessage = ChartMessage.notifier();
            replyMessage.setMsgId(chartMessage.getMsgId());
            replyMessage.setToUser(chartMessage.getToUser());
            replyMessage.setType(ChartMessageType.NOTIFIER_MESSAGE_PUSHED);
            wsSession.reply(replyMessage);
        }
    }

    private void processReplyMessage(WsSession wsSession, ChartMessage message, ChartMessage replyMessage) {
        if (Objects.isNull(replyMessage) || Objects.isNull(wsSession)) {
            return;
        }
        replyMessage.setMsgId(message.getMsgId());
        replyMessage.setType(ChartMessageType.NOTIFIER_MESSAGE_PUSHED);
        wsSession.send(replyMessage);
    }

    @OnError
    public void onError(Throwable error, @PathParam("userId") Long userId) {
        LOGGER.error("error,userId:{}", userId, error);
    }

    // 10s
    @Scheduled(fixedRate = 10000)
    public void sendHeartbeat() {
        Set<Long> userIds = new HashSet<>();
        for (WsSession value : SESSIONS.values()) {
            if (value.isTimeout()) {
                value.close();
                userIds.add(value.getUserId());
            }
        }

        for (Long userId : userIds) {
            SESSIONS.remove(userId);
        }
    }
}
