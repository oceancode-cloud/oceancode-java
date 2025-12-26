package com.oceancode.cloud.api.log;

import com.oceancode.cloud.common.util.SessionUtil;

import java.util.HashMap;
import java.util.Map;

public class RecordLog {
    private String dataId;
    private String category;
    private String message;
    private Map<String, Object> data = new HashMap<>();
    private String name;
    private String requestId;
    private boolean success;
    private String group;


    private RecordLog(boolean success) {
        this.success = success;
        this.requestId = SessionUtil.requestId();
    }

    public static RecordLog error(String message) {
        return new RecordLog(false).message(message);
    }

    public static RecordLog info(String message) {
        return new RecordLog(true).message(message);
    }

    public static RecordLog warning(String message) {
        return new RecordLog(true).message(message);
    }

    public RecordLog init() {
        return this.category("init");
    }

    public RecordLog start() {
        return this.category("start");
    }

    public RecordLog finish() {
        return this.category("finish");
    }

    public RecordLog waiting() {
        return this.category("waiting");
    }

    public RecordLog running() {
        return this.category("running");
    }

    public String dataId() {
        return this.dataId;
    }

    public RecordLog dataId(String dataId) {
        this.dataId = dataId;
        return this;
    }

    public String category() {
        return this.category;
    }

    public Map<String, Object> data() {
        return this.data;
    }

    public String name() {
        return this.name;
    }

    public RecordLog name(String name) {
        this.name = name;
        return this;
    }

    public RecordLog message(String message) {
        this.message = message;
        return this;
    }

    public RecordLog category(String category) {
        this.category = category;
        return this;
    }

    public RecordLog addData(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    public RecordLog setData(Map<String, Object> data) {
        this.data.clear();
        this.data.putAll(data);
        return this;
    }

    public RecordLog addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public String requestId() {
        return this.requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String message() {
        return this.message;
    }

    public String group() {
        return this.group;
    }

    public RecordLog group(String group) {
        this.group = group;
        return this;
    }

    public String getDataId() {
        return dataId;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getGroup() {
        return group;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
