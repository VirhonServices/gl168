package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Transfer {
    private String          transferRef;
    private ZonedDateTime   postedAt;
    private BigDecimal      amount;
    private BigDecimal      localAmount;
    private LocalDate       reportedOn;
    private String          description;

    public String getTransferRef() {
        return transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLocalAmount() {
        return localAmount;
    }

    public void setLocalAmount(BigDecimal localAmount) {
        this.localAmount = localAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReportedOn() {
        return reportedOn;
    }

    public void setReportedOn(LocalDate reportedOn) {
        this.reportedOn = reportedOn;
    }

    public ZonedDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(ZonedDateTime postedAt) {
        this.postedAt = postedAt;
    }
}
