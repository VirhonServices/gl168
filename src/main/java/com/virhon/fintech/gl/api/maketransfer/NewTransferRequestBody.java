package com.virhon.fintech.gl.api.maketransfer;

import com.virhon.fintech.gl.api.SeparatedDate;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class NewTransferRequestBody {
    @NotNull
    private String transferRef;
    @NotNull
    private String creditAccountUuid;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal repAmount;
    @NotNull
    private SeparatedDate reportedOn;
    @NotNull
    private String description;

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
