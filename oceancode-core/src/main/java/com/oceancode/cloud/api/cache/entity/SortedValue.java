/**
 * Copyright (C) NA Technologies Co., Ltd. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.api.cache.entity;

public class SortedValue<T> {
    private T value;
    private double score;

    public SortedValue() {
    }

    public SortedValue(T value, double score) {
        this.value = value;
        this.score = score;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
