package com.virhon.fintech.gl.api.maketransfer;

import com.virhon.fintech.gl.api.RequestValidator;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.exception.LedgerException;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class NewTransferRequestBody extends RequestValidator {
    private String transferRef;
    private String clientCustomerId;
    private String creditAccountUuid;
    private BigDecimal amount;
    private BigDecimal repAmount;
    private SeparatedDate reportedOn;
    private String description;

    public String getClientCustomerId() {
        return clientCustomerId;
    }

    public void setClientCustomerId(String clientCustomerId) {
        this.clientCustomerId = clientCustomerId;
    }

    public String getTransferRef() {
        return transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
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

    public BigDecimal getRepAmount() {
        return repAmount;
    }

    public void setRepAmount(BigDecimal repAmount) {
        this.repAmount = repAmount;
    }

    public SeparatedDate getReportedOn() {
        return reportedOn;
    }

    public void setReportedOn(SeparatedDate reportedOn) {
        this.reportedOn = reportedOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
