package com.oceancode.cloud.api.plugin;

public class Metrics {
    private String name;

    private String path;
    private String id;
    private String parentId;
    private Integer codeLineNumber;
    private Long totalCost;
    private Long startTime;
    private Long endTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getCodeLineNumber() {
        return codeLineNumber;
    }

    public void setCodeLineNumber(Integer codeLineNumber) {
        this.codeLineNumber = codeLineNumber;
    }

    public Long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Long totalCost) {
        this.totalCost = totalCost;
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

    @Override
    public String toString() {
        return "Metrics{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", codeLineNumber=" + codeLineNumber +
                ", totalCost=" + totalCost +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
