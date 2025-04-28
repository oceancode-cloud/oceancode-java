package com.oceancode.cloud.common.plugin;

import com.oceancode.cloud.api.ClientResult;
import com.oceancode.cloud.api.plugin.ArthasBody;
import com.oceancode.cloud.common.entity.ClientResultData;

public class ArthasResult extends ClientResultData implements ClientResult {

    private ArthasBody body;
    private String consumerId;
    private String sessionId;
    private String state;

    @Override
    public ArthasBody getResults() {
        return body;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ArthasBody getBody() {
        return body;
    }

    public void setBody(ArthasBody body) {
        this.body = body;
    }

    @Override
    public boolean isSuccess() {
        return "SUCCEEDED".equals(getCode());
    }

    @Override
    public String getCode() {
        return state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ArthasResult{" +
                "body=" + body +
                ", consumerId='" + consumerId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
