package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class HistoricalPage extends AbstractPage {
    private ZonedDateTime   finishedAt;
    private BigDecimal      finishAmount;

    public HistoricalPage(ZonedDateTime startedAt,
                          LocalDate reportedAt,
                          BigDecimal startAmount,
                          ZonedDateTime finishedAt,
                          BigDecimal finishAmount) {
        super(startedAt, reportedAt, startAmount);
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
