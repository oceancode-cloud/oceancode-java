package com.oceancode.cloud.common.util;

import com.oceancode.cloud.api.task.TaskService;
import com.oceancode.cloud.api.task.TaskType;

public final class TaskUtil {
    private TaskUtil() {
    }

    public static TaskService getTaskService(TaskType taskType) {
        return ComponentUtil.getStrategyBean(TaskService.class, taskType);
    }

    public static TaskService getTaskService() {
        return getTaskService(TaskType.LOCAL);
    }
}
