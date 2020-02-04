package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class HistoricalPage extends AbstractPage {
    private ZonedDateTime   finishedAt;
    private BigDecimal      finishAmount;

    public HistoricalPage(ZonedDateTime startedAt,
                          BigDecimal startAmount,
                          ZonedDateTime finishedAt,
                          BigDecimal finishAmount) {
        super(startedAt, startAmount);
        this.finishedAt = finishedAt;
        this.finishAmount = finishAmount;
    }

    public ZonedDateTime getFinishedAt() {
        return finishedAt;
    }

    public BigDecimal getFinishAmount() {
        return finishAmount;
    }
}
