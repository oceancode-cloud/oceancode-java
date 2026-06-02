package com.oceancode.cloud.agent;

import com.oceancode.cloud.api.agent.Agent;
import com.oceancode.cloud.common.util.ValueUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class AgentManager {
    private Map<String, Agent> map;
    private Map<String, Consumer<Object>> clientSender = new ConcurrentHashMap<>();

    public AgentManager(Set<Agent> agents) {
        if (agents.isEmpty()) {
            map = Collections.emptyMap();
            return;
        }
        map = new HashMap<>(agents.size());

        for (Agent agent : agents) {
            if (ValueUtil.isEmpty(agent.getId())) {
                continue;
            }
            if (map.containsKey(agent.getId())) {
                continue;
            }
            map.put(agent.getId(), agent);
        }
    }

    public Agent getAgent(String id) {
        return map.get(id);
    }

    public void setAgentSender(String id, Consumer<Object> consumer) {
        clientSender.put(id, consumer);
    }

    public void sendToAgent(String id, Object data) {
        Consumer<Object> consumer = clientSender.get(id);
        if (Objects.isNull(consumer)) {
            return;
        }
        consumer.accept(data);
    }
}
