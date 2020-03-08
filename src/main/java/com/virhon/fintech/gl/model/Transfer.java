package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.AccessDenied;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Transfer {
    private String          transferUuid;
    private String          transferRef;
    private String          clientUuid;
    private String          clientCustomerId;
    private ZonedDateTime   postedAt;
    private BigDecimal      amount;
    private BigDecimal      localAmount;
    private LocalDate       reportedOn;
    private String          description;
    private String          debitPageUuid;
    private String          creditPageUuid;

    public Transfer getNegate() {
        final Transfer result = new Transfer();
        result.setTransferUuid(this.transferUuid);
        result.setTransferRef(this.getTransferRef());
        result.setClientUuid(this.getClientUuid());
        result.setClientCustomerId(this.getClientCustomerId());
        result.setPostedAt(this.getPostedAt());
        result.setAmount(this.getAmount().negate());
        result.setLocalAmount(this.getLocalAmount().negate());
        result.setReportedOn(this.getReportedOn());
        result.setDescription(this.getDescription());
        result.setDebitPageUuid(this.getDebitPageUuid());
        result.setCreditPageUuid(this.getCreditPageUuid());
        return result;
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

    public String getTransferUuid() {
        return transferUuid;
    }

    public void setTransferUuid(String transferUuid) {
        this.transferUuid = transferUuid;
    }

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

    public String getDebitPageUuid() {
        return debitPageUuid;
    }

    public void setDebitPageUuid(String debitPageUuid) {
        this.debitPageUuid = debitPageUuid;
    }

    public String getCreditPageUuid() {
        return creditPageUuid;
    }

    public void setCreditPageUuid(String creditPageUuid) {
        this.creditPageUuid = creditPageUuid;
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
