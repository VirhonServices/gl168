package com.virhon.fintech.gl.api.accounts;

import java.time.ZonedDateTime;

public class NewAccountResponseBody {
    private String uuid;
    private String accType;
    private String accNumber;
    private String iban;
    private String currency;
    private String openedAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }
}
