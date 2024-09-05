package com.oceancode.cloud.common.task.local;

import com.oceancode.cloud.api.Result;
import com.oceancode.cloud.api.task.ExpressType;
import com.oceancode.cloud.api.task.Task;
import com.oceancode.cloud.api.task.TaskContext;
import com.oceancode.cloud.api.task.TaskHandler;
import com.oceancode.cloud.api.task.TaskInfo;
import com.oceancode.cloud.api.task.TaskService;
import com.oceancode.cloud.api.task.TaskType;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.util.ComponentUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LocalTaskServiceImpl implements TaskService, DisposableBean {
    private final Map<String, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Override
    public Result<TaskInfo> addTask(Task task) {
        TaskHandler taskHandler = ComponentUtil.getBean(TaskHandler.class, handler -> task.getHandler().equals(handler.getId()));
        if (Objects.isNull(taskHandler)) {
            return ResultData.isFail(CommonErrorCode.SERVER_ERROR, task.getHandler() + " not found.");
        }
        CronTask cronTask = null;
        if (ExpressType.CORN.equals(task.getExpressType())) {
            cronTask = new CronTask(() -> {
                TaskContext taskContext = new TaskContext();
                taskContext.setId(task.getId());
                taskContext.setParam(task.getParam());
                taskHandler.run(taskContext);
            }, task.getExpress());
            if (cronTask != null) {
                if (this.scheduledTasks.containsKey(task.getId())) {
                    removeTask(task.getId());
                }

                ScheduledTask scheduledTask = new ScheduledTask(task);
                scheduledTask.future = taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
                this.scheduledTasks.put(task.getId(), scheduledTask);
            }
        }
//        else if (ExpressType.FIXED.equals(task.getExpressType())){
//            cronTask = new FixedDelayTask(()->{
//
//            },Dur);
//        }

        return ResultData.isOk();
    }

    @Override
    public Result<TaskInfo> startTask(String id) {
        return null;
    }

    @Override
    public Result<TaskInfo> stopTask(String id) {
        scheduledTasks.get(id).cancel();
        return ResultData.isOk();
    }

    @Override
    public Result<TaskInfo> removeTask(String id) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(id);
        if (scheduledTask != null)
            scheduledTask.cancel();
        return ResultData.isOk();
    }

    @Override
    public Result<TaskInfo> getInfo(String id) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(id);
        taskInfo.setProperties(scheduledTasks.get(id).getTask().getProperties());
        return ResultData.isOk(taskInfo);
    }

    @Override
    public boolean isSupport(TaskType type) {
        return TaskType.LOCAL.equals(type);
    }

    @Override
    public void destroy() throws Exception {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }
}
