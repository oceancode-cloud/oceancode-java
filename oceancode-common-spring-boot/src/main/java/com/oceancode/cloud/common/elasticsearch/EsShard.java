package com.oceancode.cloud.common.elasticsearch;

public class EsShard {
    private Long total;
    private Long successful;
    private Long skipped;
    private Long failed;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSuccessful() {
        return successful;
    }

    public void setSuccessful(Long successful) {
        this.successful = successful;
    }

    public Long getSkipped() {
        return skipped;
    }

    public void setSkipped(Long skipped) {
        this.skipped = skipped;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }
}
