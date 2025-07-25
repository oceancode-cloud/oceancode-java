package com.oceancode.cloud.common.mq.local;

import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.common.util.ComponentUtil;

public class LocalConsumer extends AbstractLocalBlockQueue {
    @Override
    protected String getKey() {
        return "default";
    }

    @Override
    protected Consumer getConsumer() {
        return ComponentUtil.getBean(Consumer.class);
    }
}
