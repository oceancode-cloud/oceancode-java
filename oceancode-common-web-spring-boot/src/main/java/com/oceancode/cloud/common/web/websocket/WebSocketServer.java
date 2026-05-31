package com.oceancode.cloud.common.web.websocket;

import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.chat.ChatMessage;
import com.oceancode.cloud.chat.ChatMessageCallback;
import com.oceancode.cloud.chat.ChatMessageHandler;
import com.oceancode.cloud.chat.ChatMessageType;
import com.oceancode.cloud.chat.RTMessageService;
import com.oceancode.cloud.chat.UserType;
import com.oceancode.cloud.chat.ChatMessageResponse;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/chat/{userId}")
@ConditionalOnClass(ServerEndpointExporter.class)
public class WebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);
    public static final Map<Long, WsSession> SESSIONS = new ConcurrentHashMap<>();

    private static SessionService sessionService;

    private static RTMessageService RTMessageService;
    private static Map<String, ChatMessageHandler> handlers = new HashMap<>();
    private static ChatMessageHandler chatMessageHandler;

    @PostConstruct
    public void init() {
        sessionService = ComponentUtil.getBean(SessionService.class);
        RTMessageService = ComponentUtil.getBean(WebsocketRTMessageServiceImpl.class);
        JsonUtil.registerTypeEnum(ChatMessageType.class);
        JsonUtil.registerTypeEnum(UserType.class);
        chatMessageHandler = ComponentUtil.getBean(ChatMessageHandler.class, false);
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
            wsSession.reply(ChatMessage.notifier().errorCode(CommonErrorCode.USER_NOT_FOUND));
            wsSession.close();
            return;
        }

        if (!sessionService.isLogin(userId)) {
            wsSession.reply(ChatMessage.notifier().errorCode(CommonErrorCode.ACCESS_DENIED));
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

        ChatMessage chatMessage = JsonUtil.toBean(message, ChatMessage.class);
        if (Objects.isNull(chatMessage)) {
            return;
        }
        if (Objects.nonNull(chatMessage.getProjectId())) {
            SessionUtil.setProjectId(chatMessage.getProjectId());
        }

        if (Objects.nonNull(chatMessage.getTenantId())) {
            SessionUtil.setTenantId(chatMessage.getTenantId());
        }
        SessionUtil.setUserId(userId);

        chatMessage.setFromUser(userId);
        if (Objects.nonNull(chatMessageHandler)) {
            ChatMessageCallback callback = data -> {
                ChatMessage notifier = ChatMessage.notifier();
                notifier.setMsgId(chatMessage.getMsgId());
                notifier.setData(data);
                notifier.toUser(chatMessage.getFromUser());
                notifier.fromUser(chatMessage.getToUser());
                notifier.setFrom(chatMessage.getTo());
                notifier.setTo(chatMessage.getFrom());
                if (data instanceof ChatMessageResponse) {
                    notifier.setType(ChatMessageType.STREAM_MESSAGE);
                }
                wsSession.reply(notifier);
            };
            chatMessageHandler.onMessage(chatMessage, callback);
            return;
        }

        if (ValueUtil.isNotEmpty(chatMessage.getToUser())) {
            RTMessageService.sendTo(chatMessage.getToUser(), chatMessage);
            ChatMessage replyMessage = ChatMessage.notifier();
            replyMessage.setMsgId(chatMessage.getMsgId());
            replyMessage.setToUser(chatMessage.getToUser());
            replyMessage.setType(ChatMessageType.NOTIFIER_MESSAGE_PUSHED);
            wsSession.reply(replyMessage);
        }
    }

    private void processReplyMessage(WsSession wsSession, ChatMessage message, ChatMessage replyMessage) {
        if (Objects.isNull(replyMessage) || Objects.isNull(wsSession)) {
            return;
        }
        replyMessage.setMsgId(message.getMsgId());
        replyMessage.setType(ChatMessageType.NOTIFIER_MESSAGE_PUSHED);
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
