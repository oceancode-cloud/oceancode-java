package com.oceancode.cloud.thread;

import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.SessionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThreadGroup {
    private List<Thread> threads = new ArrayList<>();
    private CountDownLatch countDownLatch;
    private boolean running;

    public void addTask(String name, int index, Runnable runnable) {
        process(name, index, runnable);
    }

    public void addTask(String name, Runnable runnable) {
        process(name, null, runnable);
    }

    public void start() {
        start(1 * 60 * 60 * 1000L);
    }

    public void start(long timeout) {
        if (running) {
            return;
        }
        running = true;
        countDownLatch = new CountDownLatch(threads.size());

        try {
            for (Thread thread : threads) {
                thread.run();
            }
        } finally {
            try {
                countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                countDownLatch = null;
                threads.clear();
            }
        }
    }

    private void process(String name, Integer index, Runnable runnable) {
        if (running) {
            throw new BusinessRuntimeException(CommonErrorCode.ERROR, "thread group is running");
        }
        List<Object> values = SessionUtil.getValues();
        Thread.Builder.OfVirtual builder = Objects.nonNull(index) ? Thread.ofVirtual().name(name, index) : Thread.ofVirtual().name(name);
        Thread start = builder.start(() -> {
            SessionUtil.setValues(values);
            try {
                runnable.run();
            } finally {
                SessionUtil.remove();
                countDownLatch.countDown();
            }
        });
        threads.add(start);
    }
}
