package com.virhon.fintech.gl.api.accounts;

import javax.validation.constraints.NotNull;

public class NewAccountRequestBody {
    @NotNull(message = "Account's type can't be null")
    private String accType;
    private String accNumber;
    private String iban;
    @NotNull(message = "Account's currency must be defined")

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
