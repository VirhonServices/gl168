package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Reservation {
    private String          transferRef;
    private Long            debitId;
    private Long            creditId;
    private BigDecimal      amount;
    private String          description;
    private ZonedDateTime   expireAt;

    public String getTransferRef() {
        return transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
    }

    public Long getDebitId() {
        return debitId;
    }

    public void setDebitId(Long debitId) {
        this.debitId = debitId;
    }

    public Long getCreditId() {
        return creditId;
    }

    public void setCreditId(Long creditId) {
        this.creditId = creditId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(ZonedDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
