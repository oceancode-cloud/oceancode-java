package com.oceancode.cloud.common.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EsHits<T> {
    private EsHitsTotal total;

    @JsonProperty("max_score")
    private Double maxScore;

    private List<EsHitItem<T>> hits;

    public EsHitsTotal getTotal() {
        return total;
    }

    public void setTotal(EsHitsTotal total) {
        this.total = total;
    }

    public Double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }

    public List<EsHitItem<T>> getHits() {
        return hits;
    }

    public void setHits(List<EsHitItem<T>> hits) {
        this.hits = hits;
    }
}
