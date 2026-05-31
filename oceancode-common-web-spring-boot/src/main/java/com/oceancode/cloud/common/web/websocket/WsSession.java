package com.oceancode.cloud.common.web.websocket;

import com.oceancode.cloud.chat.ChatMessage;
import com.oceancode.cloud.chat.ChatMessageType;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.JsonUtil;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WsSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsSession.class);
    private Session session;

    private Long userId;

    private long time;

    public WsSession(Long userId, Session session) {
        this.session = session;
        this.userId = userId;
        time = System.currentTimeMillis();
    }


    public void reply(ChatMessage chatMessage) {
        sendText(JsonUtil.toJson(chatMessage));
    }

    public void sendText(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.error("replay ws message to user({}) failed.", userId, e);
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR);
        } finally {
            update();
        }
    }

    public void pong() {
        sendText("pong");
    }

    public void update() {
        this.time = System.currentTimeMillis();
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() - time >= 10000;
    }

    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            LOGGER.error("close ws error,userId:{}", userId, e);
        }
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isActive() {
        return session.isOpen();
    }

    public void send(ChatMessage message) {
        if (ChatMessageType.NOTIFIER_MESSAGE.equals(message.getType())) {
//            message.setToUser(null);
//            message.setFromUser(null);
        } else if (ChatMessageType.MESSAGE.equals(message.getType())) {
//            message.setToUser(null);
//            message.setFromUser(null);
            message.setType(ChatMessageType.NOTIFIER_MESSAGE);
        }
        sendText(JsonUtil.toJson(message));
    }
}
