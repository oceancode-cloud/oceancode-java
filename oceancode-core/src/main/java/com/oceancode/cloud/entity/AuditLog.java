package com.oceancode.cloud.entity;

import com.oceancode.cloud.api.ErrorCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuditLog {
    private String errorCode;
    private String message;
    private String content;
    private Map<String, Object> input = new HashMap<>();
    private Map<String, Object> result = new HashMap<>();
    private boolean success = true;
    private String sessionId;
    private String logPath;
    private String sourceId;
    private String category;
    private Map<String, Object> extra = new HashMap<>();
    private String id;
    private Long startTime;
    private Long endTime;
    private String name;

    public AuditLog startTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public AuditLog startTime() {
        return startTime(System.nanoTime());
    }

    public AuditLog endTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    public AuditLog endTime() {
        return endTime(System.nanoTime());
    }

    public long timeout() {
        if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
            return 0;
        }
        return endTime - startTime;
    }


    public AuditLog name(String name) {
        this.name = name;
        return this;
    }

    public AuditLog content(String content) {
        this.content = content;
        return this;
    }

    public AuditLog logPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public AuditLog category(String category) {
        this.category = category;
        return this;
    }

    public AuditLog id(String id) {
        this.id = id;
        return this;
    }

    public AuditLog addExtra(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }

    public AuditLog errorCode(String errorCode) {
        this.errorCode = errorCode;
        this.success = false;
        return this;
    }

    public AuditLog errorCode(ErrorCode errorCode) {
        this.errorCode(errorCode.getCode());
        this.message = errorCode.getMessage();
        this.success = false;
        return this;
    }

    public AuditLog message(String message) {
        this.message = message;
        return this;
    }

    public AuditLog info(String message) {
        return message(message);
    }

    public AuditLog error(String message) {
        this.message = message;
        this.success = false;
        return this;
    }

    public AuditLog sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public AuditLog addInput(String key, Object value) {
        this.input.put(key, value);
        return this;
    }

    public AuditLog addOutput(String key, Object value) {
        this.result.put(key, value);
        return this;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getInput() {
        return input;
    }

    public void setInput(Map<String, Object> input) {
        this.input = input;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
