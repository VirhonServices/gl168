package com.virhon.fintech.gl.repo.mysql.historicalpage;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class MySQLHistoricalPageRecord {
    private Long            id;
    private Long            accountId;
    private ZonedDateTime   startedAt;
    private ZonedDateTime   finishedAt;
    private LocalDate       repStartedOn;
    private LocalDate       repFinishedOn;
    private String          data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public ZonedDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(ZonedDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public ZonedDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(ZonedDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public LocalDate getRepStartedOn() {
        return repStartedOn;
    }

    public void setRepStartedOn(LocalDate repStartedOn) {
        this.repStartedOn = repStartedOn;
    }

    public LocalDate getRepFinishedOn() {
        return repFinishedOn;
    }

    public void setRepFinishedOn(LocalDate repFinishedOn) {
        this.repFinishedOn = repFinishedOn;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
