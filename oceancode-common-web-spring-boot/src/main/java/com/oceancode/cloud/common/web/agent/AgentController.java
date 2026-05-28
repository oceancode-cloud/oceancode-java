package com.oceancode.cloud.common.web.agent;

import com.oceancode.cloud.agent.AgentManager;
import com.oceancode.cloud.api.agent.Agent;
import com.oceancode.cloud.api.agent.AgentInfo;
import com.oceancode.cloud.api.agent.AgentMessage;
import com.oceancode.cloud.common.constant.CommonConst;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConst.API_PREFIX)
@ConditionalOnBean(Agent.class)
public class AgentController {
    @Resource
    private AgentManager agentManager;

//    @PostMapping("/agent")
//    public Mono<AgentInfo> createAgent(@RequestBody AgentMessage agentMessage) {
//        return agentManager.getAgent(agentMessage.getId()).create(agentMessage);
//    }
//
//    @Post
}
