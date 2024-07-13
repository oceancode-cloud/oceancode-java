/**
 * Copyright (C) Code Cloud Platform. 2024-2023 .All Rights Reserved.
 */

package com.oceancode.cloud.common.mq.local;

import com.oceancode.cloud.api.mq.Consumer;
import com.oceancode.cloud.api.mq.Message;

import java.util.concurrent.BlockingQueue;

public class QueueEntry {
    private BlockingQueue<Message<?>> blockingQueue;

    private Consumer consumer;

    public QueueEntry() {
    }

    public QueueEntry(BlockingQueue<Message<?>> blockingQueue, Consumer consumer) {
        this.blockingQueue = blockingQueue;
        this.consumer = consumer;
    }

    public BlockingQueue<Message<?>> getBlockingQueue() {
        return blockingQueue;
    }

    public void setBlockingQueue(BlockingQueue<Message<?>> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
}
