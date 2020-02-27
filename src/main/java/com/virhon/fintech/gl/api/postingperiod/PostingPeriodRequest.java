package com.virhon.fintech.gl.api.postingperiod;

import com.virhon.fintech.gl.api.SeparatedDateTime;

public class PostingPeriodRequest {
    private SeparatedDateTime startedAt;
    private SeparatedDateTime finishedAt;

    public SeparatedDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(SeparatedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public SeparatedDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(SeparatedDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
