package com.oceancode.cloud.api.task;

public interface TaskHandler {
    void run(TaskContext context);

    String getId();
}
