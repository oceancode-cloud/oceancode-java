package com.oceancode.cloud.common.autoconfig;

import com.oceancode.cloud.api.autoconfig.AutoConfigType;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfig;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigContext;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRequest;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResponse;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigResult;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigRule;
import com.oceancode.cloud.api.autoconfig.v2.AutoConfigService;
import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AutoConfigServiceImpl implements AutoConfigService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AutoConfigServiceImpl.class);
    private Map<String, Map<String, AutoConfigRule>> ruleMap;

    public AutoConfigServiceImpl(Set<AutoConfigRule> autoConfigRules) {
        ruleMap = new HashMap<>();
        for (AutoConfigRule item : autoConfigRules) {
            if (ValueUtil.isEmpty(item.getGroup())) {
                LOGGER.error("group is empty.{}", item.getClass().getName());
                continue;
            }
            String property = item.getProperty();
            if (Objects.isNull(property)) {
                property = "";
            }
            property = property.trim();
            String group = item.getGroup().trim();
            if (!ruleMap.containsKey(group)) {
                ruleMap.put(group, new HashMap<>());
            }
            Map<String, AutoConfigRule> map = ruleMap.get(group);
            if (map.containsKey(property)) {
                LOGGER.error("property is already exists. {}", item.getClass().getName());
                continue;
            }
            map.put(property, item);
            LOGGER.info("register auto config,group:{},property:{} - {}", item.getGroup(), property, item.getClass().getName());
        }
    }

    @Override
    public AutoConfigResponse autoConfig(AutoConfigRequest autoConfigRequest) {
        AutoConfigResponse autoConfigResponse = new AutoConfigResponse();
        if (Objects.isNull(autoConfigRequest)) {
            return autoConfigResponse;
        }
        List<AutoConfig> items = autoConfigRequest.getItems();
        if (ValueUtil.isEmpty(items)) {
            return autoConfigResponse;
        }
        try {
            processAutoConfigRequest(autoConfigRequest, autoConfigResponse);
        } catch (Exception e) {
            autoConfigResponse.setSuccess(false);
            LOGGER.error("autoconfig error", e);
        }
        return autoConfigResponse;
    }

    private void processAutoConfigRequest(AutoConfigRequest autoConfigRequest, AutoConfigResponse autoConfigResponse) {
        Map<String, List<AutoConfig>> groupMap = autoConfigRequest.getItems()
                .stream()
                .collect(Collectors.groupingBy(AutoConfig::getGroup));
        AutoConfigContext context = new AutoConfigContext();
        for (Map.Entry<String, List<AutoConfig>> entry : groupMap.entrySet()) {
            Map<String, AutoConfigRule> map = ruleMap.get(entry.getKey());
            AutoConfigResult result = new AutoConfigResult();
            if (ValueUtil.isEmpty(map)) {
                result.setSuccess(false);
                autoConfigResponse.setSuccess(false);
                LOGGER.error("not found auto config handler,group:{}", entry.getKey());
                return;
            }
            List<AutoConfig> items = entry.getValue();

            try {
                if (map.size() == 1) {
                    for (AutoConfigRule value : map.values()) {
                        if (!ValueUtil.isEmpty(value.getProperty())) {
                            continue;
                        }
                        processAddResult(autoConfigResponse, result, items);
                        processItem(context, result, value, null, items);
                    }
                } else {
                    for (AutoConfig item : items) {
                        AutoConfigRule targetRule = map.get(item.getProperty());
                        if (Objects.nonNull(targetRule)) {
                            processAddResult(autoConfigResponse, result, item);
                            processItem(context, result, targetRule, item, items);
                        }
                    }
                }
                result.setSuccess(true);
            } catch (Exception e) {
                if (e instanceof ErrorCodeRuntimeException exception) {
                    result.setErrorCode(exception.getErrorCode());
                    result.setErrorMessage(exception.getMessage());
                }
                autoConfigResponse.setSuccess(false);
                result.setSuccess(false);
                throw e;
            }
        }
    }

    private void processAddResult(AutoConfigResponse autoConfigResponse, AutoConfigResult result, AutoConfig item) {
        if (AutoConfigType.ADD.getValue().equals(item.getType())) {
            autoConfigResponse.toAdd(result);
        } else if (AutoConfigType.ADD_MANY.getValue().equals(item.getType())) {
            autoConfigResponse.toAdd(result);
        } else if (AutoConfigType.UPDATE.getValue().equals(item.getType())) {
            autoConfigResponse.toUpdate(result);
        } else if (AutoConfigType.UPDATE_MANY.getValue().equals(item.getType())) {
            autoConfigResponse.toUpdate(result);
        } else if (AutoConfigType.REMOVE.getValue().equals(item.getType())) {
            autoConfigResponse.toDelete(result);
        }
    }

    private void processAddResult(AutoConfigResponse autoConfigResponse, AutoConfigResult result, List<AutoConfig> items) {
        for (AutoConfig item : items) {
            processAddResult(autoConfigResponse, result, item);
        }
    }

    private void processItem(AutoConfigContext context, AutoConfigResult result, AutoConfigRule targetRule, AutoConfig item, List<AutoConfig> items) {
        if (!targetRule.support(context, item, items)) {
            return;
        }

        result.setGroup(targetRule.getGroup());
        if (Objects.nonNull(item)) {
            result.setNotifier(item.getNotifier());
            result.setProperty(targetRule.getProperty());
            result.setSourceId(item.getSourceId());
        }
        targetRule.apply(context, result, item, items);
    }
}
