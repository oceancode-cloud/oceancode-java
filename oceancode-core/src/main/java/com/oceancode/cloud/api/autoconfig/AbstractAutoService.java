package com.oceancode.cloud.api.autoconfig;

import java.util.List;

public abstract class AbstractAutoService implements AutoConfigService {
    @Override
    public AutoConfigResult autoConfig(AutoConfigRequest request) {
        AutoConfigRequestContext context = createAutoConfigRequestContext(request);
        for (AutoConfigValue value : context.getValues()) {
            List<AutoConfigHandler> handlers = getHandlers(context, value);
            for (AutoConfigHandler handler : handlers) {
                doAutoConfig(context, handler, value);
            }
        }
        return null;
    }

    protected List<AutoConfigHandler> getHandlers(AutoConfigRequestContext context, AutoConfigValue value) {
        return AutoConfigRequestUtil.getHandlers(value.getGroup()).stream()
                .filter(it -> it.support(context, value)).toList();
    }

    protected AutoConfigRequestContext createAutoConfigRequestContext(AutoConfigRequest request) {
        return new AutoConfigRequestContext(request);
    }

    protected abstract AutoConfigResult doAutoConfig(AutoConfigRequestContext request, AutoConfigHandler handler, AutoConfigValue value);
}
