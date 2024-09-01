package com.oceancode.cloud.common.elasticsearch;

public class EsHitsTotal {
    private Long value;
    private String relation;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
