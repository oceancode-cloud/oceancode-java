package com.oceancode.cloud.common.web.websocket;

import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.api.mq.Producer;
import com.oceancode.cloud.api.session.SessionService;
import com.oceancode.cloud.chart.ChartMessage;
import com.oceancode.cloud.chart.ChartMessageType;
import com.oceancode.cloud.chart.Messenger;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ComponentUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
public class WebsocketMessengerImpl implements Messenger {
    @Resource
    private SessionService sessionService;

    @Override
    public void sendTo(Long userId, ChartMessage message) {
        if (Objects.isNull(userId) || Objects.isNull(message)) {
            return;
        }
        message.setToUser(null);
        boolean isRecordMessage = true;
        try {
            WsSession wsSession = WebSocketServer.SESSIONS.get(userId);
            if (!isOnline(userId) && Objects.nonNull(wsSession)) {
                wsSession.reply(ChartMessage.notifier().msgId(message.getMsgId())
                        .errorCode(CommonErrorCode.USER_NOT_ONLINE));
            } else {
                if (Objects.nonNull(wsSession) && wsSession.isActive()) {
                    wsSession.send(message);
                    isRecordMessage = false;
                }
            }

        } catch (Throwable throwable) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, throwable);
        } finally {
            if (!isRecordMessage) {
                return;
            }

            if (ChartMessageType.CHART_MESSAGE.equals(message.getType())) {
                message.setToUser(userId);
                Producer producer = ComponentUtil.getBean(Producer.class);
                Message<ChartMessage> msg = new Message<>();
                msg.setData(message);
                msg.setId(UUID.randomUUID().toString().replace("-", ""));
                msg.setKey(ChartMessage.CHART_MESSAGE_KEY);
                msg.setUserId(userId);
                producer.sendWithBusiness(msg);
            }
        }
    }

    @Override
    public void sendTo(Set<Long> userIds, ChartMessage message) {
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
        return false;
    }

    @PreDestroy
    public void destroy() {
        for (WsSession value : WebSocketServer.SESSIONS.values()) {
            value.close();
        }
        WebSocketServer.SESSIONS.clear();
    }
}
