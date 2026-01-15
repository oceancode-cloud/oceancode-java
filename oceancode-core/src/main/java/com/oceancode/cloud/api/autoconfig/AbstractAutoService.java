package com.oceancode.cloud.api.autoconfig;

import com.oceancode.cloud.common.exception.ErrorCodeRuntimeException;

import java.util.List;

public abstract class AbstractAutoService implements AutoConfigService {
    @Override
    public AutoConfigResult autoConfig(AutoConfigRequest request) {
        AutoConfigRequestContext context = createAutoConfigRequestContext(request);
        boolean hasError = false;
        for (AutoConfigValue value : context.getValues()) {
            if (value.getType().isCustom()) {
                continue;
            }
            List<AutoConfigHandler> handlers = getHandlers(context, value);
            for (AutoConfigHandler handler : handlers) {
                try {
                    doAutoConfig(context, handler, value);
                } catch (Exception e) {
                    context.getResult().setThrowable(e);
                    context.getResult().setSuccess(false);
                    if (e instanceof ErrorCodeRuntimeException exception) {
                        context.getResult().setErrorCode(exception.getErrorCode());
                        context.getResult().setDetail(exception.getMessage());
                    }
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
