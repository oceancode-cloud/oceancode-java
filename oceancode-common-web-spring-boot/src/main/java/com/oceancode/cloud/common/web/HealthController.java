package com.oceancode.cloud.common.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnExpression(value = "'${oc.health.enabled}'=='true'")
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        return "PONG";
    }
}
