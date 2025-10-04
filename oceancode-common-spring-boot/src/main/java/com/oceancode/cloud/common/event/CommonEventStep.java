package com.oceancode.cloud.common.event;

import com.oceancode.cloud.api.event.EventParam;
import com.oceancode.cloud.api.event.EventStep;
import com.oceancode.cloud.common.util.EventUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CommonEventStep implements EventStep {
    private long timeout;
    private CountDownLatch countDownLatch;
    private EventParam param;

    public CommonEventStep(long timeout) {
        this.timeout = timeout;
        countDownLatch = new CountDownLatch(1);
    }

    @Override
    public EventParam getData() {
        try {
            countDownLatch.await(this.timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        EventUtil.offEvent(EventUtil.createEventId(param.getEventType()));
        return this.param;
    }

    @Override
    public void setData(EventParam param) {
        this.param = param;
        this.countDownLatch.countDown();
    }
}
