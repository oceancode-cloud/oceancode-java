package com.oceancode.cloud.api.autoconfig;

import java.util.List;

public abstract class AbstractAutoService implements AutoConfigService {
    @Override
    public AutoConfigResult autoConfig(AutoConfigRequest request) {
        AutoConfigRequestContext context = createAutoConfigRequestContext(request);
        boolean hasError = false;
        for (AutoConfigValue value : context.getValues()) {
            List<AutoConfigHandler> handlers = getHandlers(context, value);
            for (AutoConfigHandler handler : handlers) {
                try {
                    doAutoConfig(context, handler, value);
                } catch (Exception e) {
                    context.getResult().setThrowable(e);
                    context.getResult().setSuccess(false);
                    hasError = true;
                    break;
                }
            }
            if (hasError) {
                break;
            }
        }
        return context.getResult();
    }

    protected List<AutoConfigHandler> getHandlers(AutoConfigRequestContext context, AutoConfigValue value) {
        return AutoConfigRequestUtil.getHandlers(value.getGroup()).stream()
                .filter(it -> it.support(context, value)).toList();
    }

    protected AutoConfigRequestContext createAutoConfigRequestContext(AutoConfigRequest request) {
        return new AutoConfigRequestContext(request);
    }

    protected abstract AutoConfigResult doAutoConfig(AutoConfigRequestContext context, AutoConfigHandler handler, AutoConfigValue value);
}
