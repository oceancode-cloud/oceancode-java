package com.oceancode.cloud.common.web.websocket;

import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.api.mq.MessageType;
import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.chat.ChatMessage;
import com.oceancode.cloud.chat.ChatMessageType;
import com.oceancode.cloud.chat.RTMessageService;
import com.oceancode.cloud.common.cache.KeyParam;
import com.oceancode.cloud.common.config.CommonConfig;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
public class WebsocketRTMessageServiceImpl implements RTMessageService {
    @Resource
    private SessionService sessionService;

    private static String msgKey;

    public WebsocketRTMessageServiceImpl(CommonConfig commonConfig) {
        msgKey = commonConfig.getValue("oc.message.queue." + KeyParam.DEFAULT_KEY + ".name", ChatMessage.CHART_MESSAGE_KEY);
    }

    @Override
    public void sendToSelf(ChatMessage message) {
        Long userId = SessionUtil.userId();
        if (Objects.isNull(userId)) {
            return;
        }
        WsSession wsSession = WebSocketServer.SESSIONS.get(userId);
        if (Objects.isNull(wsSession)) {
            return;
        }
        if (Objects.isNull(message.getType())) {
            message.setType(ChatMessageType.NOTIFIER_MESSAGE);
        }

        wsSession.send(message);
    }

    @Override
    public void sendTo(ChatMessage message) {
        sendTo(message.getToUser(), message);
    }

    @Override
    public void sendTo(Long userId, ChatMessage message) {
        if (Objects.isNull(userId) || Objects.isNull(message)) {
            return;
        }
        message.setToUser(userId);
        boolean isRecordMessage = true;
        try {
            WsSession wsSession = WebSocketServer.SESSIONS.get(userId);
            WsSession fromSession = Objects.nonNull(message.getFromUser()) ?
                    WebSocketServer.SESSIONS.get(message.getFromUser()) : null;
            if (!isOnline(userId) && Objects.nonNull(fromSession)) {
                fromSession.reply(ChatMessage.notifier().msgId(message.getMsgId())
                        .errorCode(CommonErrorCode.USER_NOT_ONLINE));
            } else {
                if (Objects.nonNull(wsSession) && wsSession.isActive()) {
                    wsSession.send(message);
                    isRecordMessage = false;
                    fromSession.reply(ChatMessage.notifier().msgId(message.getMsgId()));
                }
            }

        } catch (Throwable throwable) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, throwable);
        } finally {
            if (!isRecordMessage) {
                return;
            }

            if (ChatMessageType.CHAT_MESSAGE.equals(message.getType())) {
                message.setToUser(userId);
                Producer producer = ComponentUtil.getBean(Producer.class);
                Message<ChatMessage> msg = new Message<>();
                msg.setData(message);
                msg.setId(UUID.randomUUID().toString().replace("-", ""));
                msg.setKey(msgKey);
                msg.setUserId(userId);
                msg.setMessageType(MessageType.CHAT_MESSAGE);
                producer.fillMessage(msg);
                producer.sendWithBusiness(msg);
            }
        }
    }

    @Override
    public void sendTo(Set<Long> userIds, ChatMessage message) {
        if (Objects.isNull(userIds)) {
            return;
        }

        for (Long userId : userIds) {
            sendTo(userId, message);
        }
    }

    @Override
    public boolean isOnline(Long userId) {
        if (Objects.isNull(userId)) {
            return false;
        }
        WsSession wsSession = WebSocketServer.SESSIONS.get(userId);
        if (Objects.isNull(wsSession)) {
            Object address = sessionService.getUserProperty(userId, "ws_address");
            if (Objects.nonNull(address) && address instanceof String str) {
                return ValueUtil.isNotEmpty(str);
            }
            ;
            return false;
        }

        return wsSession.isActive();
    }

    @Override
    public boolean isOnline(Set<Long> userIds) {
        if (ValueUtil.isEmpty(userIds)) {
            return false;
        }
        for (Long userId : userIds) {
            if (!isOnline(userId)) {
                return false;
            }
        }
        return true;
    }

    @PreDestroy
    public void destroy() {
        for (WsSession value : WebSocketServer.SESSIONS.values()) {
            value.close();
        }
        WebSocketServer.SESSIONS.clear();
    }
}
