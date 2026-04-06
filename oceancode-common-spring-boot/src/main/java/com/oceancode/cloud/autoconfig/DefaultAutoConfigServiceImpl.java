package com.oceancode.cloud.autoconfig;

import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.Util;
import com.oceancode.cloud.common.util.ValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Primary
@Component
public class DefaultAutoConfigServiceImpl implements AutoConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAutoConfigServiceImpl.class);

    private Map<String, AutoConfigRule> ruleMap = new HashMap<>();
    private AutoConfigService autoConfigService;

    public DefaultAutoConfigServiceImpl(Set<AutoConfigRule> rules, AutoConfigService autoConfigService) {
        this.autoConfigService = autoConfigService;
        for (AutoConfigRule rule : rules) {
            if (ValueUtil.isEmpty(rule.getGroup())) {
                LOGGER.error("group is empty.{}", rule.getClass().getName());
                continue;
            }
            String group = ValueUtil.camelToUnderline(rule.getGroup());
            if (ruleMap.containsKey(group)) {
                LOGGER.error("group[{}] is already exists.{}", group, rule.getClass().getName());
                continue;
            }
            ruleMap.put(group, rule);
            LOGGER.info("register auto config rule success.group[{}] {}", group, rule.getClass().getName());
        }
    }

    @Override
    public AutoConfigResponse autoConfig(AutoConfigRequest autoConfigRequest) {
        try {
            if (Objects.nonNull(autoConfigService)) {
                return autoConfigService.autoConfig(autoConfigRequest);
            }
        } catch (Exception e) {
            LOGGER.warn("user custom autoConfig service", e);
        }
        AutoConfigContext context = new DefaultAutoConfigContext(autoConfigRequest);
        context.setRuleMap(ruleMap);
        Collection<AutoConfigGroup> items = context.getConfigGroups();
        AutoConfigResponse result = context.getResult();
        try {
            for (AutoConfigGroup item : items) {
                context.apply(item);
            }
        } catch (Exception e) {
            LOGGER.error("auto config error.", e);
            result.setSuccess(false);
            if (ValueUtil.isEmpty(result.getErrorMessage())) {
                if (e instanceof ErrorCodeRuntimeException exception) {
                    result.setErrorMessage(exception.getMessage());
                    result.setErrorCode(exception.getErrorCode());
                }
            }
        }
        return result;
    }
}
