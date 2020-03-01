package com.virhon.fintech.gl.api.reservefunds;

import java.math.BigDecimal;

public class NewReservationResponse {
    private String uuid;
    private String transferRef;
    private String clientUuid;
    private String clientCustomerId;
    private String debitAccountUuid;
    private String creditAccountUuid;
    private BigDecimal amount;
    private String description;
    private String expireAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTransferRef() {
        return transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
    }

    public String getClientUuid() {
        return clientUuid;
    }

    public void setClientUuid(String clientUuid) {
        this.clientUuid = clientUuid;
    }

    public String getClientCustomerId() {
        return clientCustomerId;
    }

    public void setClientCustomerId(String clientCustomerId) {
        this.clientCustomerId = clientCustomerId;
    }

    public String getDebitAccountUuid() {
        return debitAccountUuid;
    }

    public void setDebitAccountUuid(String debitAccountUuid) {
        this.debitAccountUuid = debitAccountUuid;
    }

    public String getCreditAccountUuid() {
        return creditAccountUuid;
    }

    public void setCreditAccountUuid(String creditAccountUuid) {
        this.creditAccountUuid = creditAccountUuid;
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

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }
}
