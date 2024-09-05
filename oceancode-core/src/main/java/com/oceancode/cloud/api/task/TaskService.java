package com.oceancode.cloud.api.task;

import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.strategy.StrategyAdaptor;

public interface TaskService extends StrategyAdaptor<TaskType> {
    Result<TaskInfo> addTask(Task task);

    Result<TaskInfo> startTask(String id);

    Result<TaskInfo> stopTask(String id);

    Result<TaskInfo> removeTask(String id);

    Result<TaskInfo> getInfo(String id);
}
