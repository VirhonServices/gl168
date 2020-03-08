package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.AccessDenied;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Reservation {
    private String          uuid;
    private String          transferRef;
    private String          clientUuid;
    private String          clientCustomerId;
    private Long            debitId;
    private Long            creditId;
    private BigDecimal      amount;
    private String          description;
    private ZonedDateTime   expireAt;

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

    public boolean isOwnedBy(final String clientUuid) {
        return this.clientUuid.toLowerCase().equals(clientUuid.toLowerCase());
    }

    public void checkAccess(final String clientUuid) throws AccessDenied {
        if (!isOwnedBy(clientUuid)) {
            throw new AccessDenied();
        }
    }

}
