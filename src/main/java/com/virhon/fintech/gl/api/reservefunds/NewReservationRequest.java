package com.virhon.fintech.gl.api.reservefunds;

import com.virhon.fintech.gl.api.RequestValidator;

import java.math.BigDecimal;

public class NewReservationRequest extends RequestValidator {
    private String transferRef;
    private String creditAccountUuid;
    private BigDecimal amount;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
