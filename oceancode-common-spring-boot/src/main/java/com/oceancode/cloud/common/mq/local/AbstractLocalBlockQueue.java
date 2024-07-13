/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.common.mq.local;

import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.api.mq.Message;
import com.oceancode.cloud.api.mq.MessageUtil;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.ValueUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractLocalBlockQueue {

    private Thread consumerThread;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLocalBlockQueue.class);

    @PostConstruct
    public void init() {
        if (Objects.isNull(getQueue())) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "queue should not null");
        }
        if (ValueUtil.isEmpty(getKey())) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "queue key should not be empty.");
        }
        if (Objects.isNull(getConsumer())) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "consumer should not be null");
        }
        GlobalLocalQueueManager.add(getKey(), new QueueEntry(getQueue(), getConsumer()));
        startConsumer();
    }

    @PreDestroy
    public void destory() {
        GlobalLocalQueueManager.close();
    }

    private void startConsumer() {
        consumerThread = new ConsumerThread();
        consumerThread.start();
    }

    private class ConsumerThread extends Thread {

        @Override
        public void run() {
            this.setName(getKey() + "-consumer-thread");
            QueueEntry queueEntry = GlobalLocalQueueManager.get(getKey());
            BlockingQueue<Message<?>> blockingQueue = queueEntry.getBlockingQueue();
            while (true) {
                try {
                    Message<?> message = blockingQueue.take();
                    MessageUtil.setSession(message);
                    if (setBachSize() > 1) {
                        List<Message<?>> list = new ArrayList<>();
                        list.add(message);
                        blockingQueue.drainTo(list, setBachSize() - 1);
                        queueEntry.getConsumer().poll(list);
                    } else {
                        queueEntry.getConsumer().poll(Arrays.asList(message));
                    }

                } catch (InterruptedException e) {
                    try {
                        queueEntry.getConsumer().onException(e);
                    } catch (Exception err) {
                        LOGGER.error(getKey() + " consumer error.", e);
                    }
                }
            }
        }
    }


    protected BlockingQueue<Message<?>> getQueue() {
        return new ArrayBlockingQueue<>(5000);
    }

    abstract protected String getKey();

    abstract protected Consumer getConsumer();

    protected int setBachSize() {
        return 1000;
    }
}
