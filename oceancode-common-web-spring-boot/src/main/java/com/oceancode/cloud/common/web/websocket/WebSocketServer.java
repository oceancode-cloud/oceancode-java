package com.oceancode.cloud.common.web.websocket;

import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.api.session.UserBaseInfo;
import com.oceancode.cloud.chart.ChartMessage;
import com.oceancode.cloud.chart.ChartMessageHandler;
import com.oceancode.cloud.chart.ChartMessageType;
import com.oceancode.cloud.chart.Messenger;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
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

    private static Messenger messenger;

    @PostConstruct
    public void init() {
        sessionService = ComponentUtil.getBean(SessionService.class);
        messenger = ComponentUtil.getBean(WebsocketMessengerImpl.class);
        JsonUtil.registerTypeEnum(ChartMessageType.class);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
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
    public void onMessage(String message, Session session, @PathParam("userId") Long userId) {
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
            ChartMessageHandler messageHandler = ComponentUtil.getBean(ChartMessageHandler.class);
            ChartMessage replyMessage = messageHandler.onMessage(chartMessage);
            processReplyMessage(wsSession, replyMessage);
            return;
        }

        if (ValueUtil.isNotEmpty(chartMessage.getToUser())) {
            messenger.sendTo(chartMessage.getToUser(), chartMessage);
        }
    }

    private void processReplyMessage(WsSession wsSession, ChartMessage replyMessage) {
        if (Objects.isNull(replyMessage) || Objects.isNull(wsSession)) {
            return;
        }
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
