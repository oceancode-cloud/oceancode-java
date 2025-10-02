package com.oceancode.cloud.common.event;

import com.oceancode.cloud.api.event.EventParam;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EventWaitObj {
    private CountDownLatch countDownLatch;
    private EventParam param;
    private long timeout;
    private boolean isValid = false;

    public EventWaitObj(long timeout) {
        this.timeout = timeout;
        countDownLatch = new CountDownLatch(1);
    }

    public EventParam getData() {
        try {
            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return param;
    }

    public void setData(EventParam data) {
        this.param = data;
        isValid = false;
        countDownLatch.countDown();
    }

    public boolean isValid() {
        return isValid;
    }
}
