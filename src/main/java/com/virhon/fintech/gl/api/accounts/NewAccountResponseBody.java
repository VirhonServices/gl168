package com.virhon.fintech.gl.api.accounts;

public class NewAccountResponseBody {
    private String uuid;
    private String clientUuid;
    private String clientCustomerId;
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

    public String getClientUuid() {
        return this.clientUuid;
    }

    public void setClientUuid(String clientUuid) {
        this.clientUuid = clientUuid;
    }

    public String getClientCustomerId() {
        return this.clientCustomerId;
    }

    public void setClientCustomerId(String clientCustomerId) {
        this.clientCustomerId = clientCustomerId;
    }

    public String getAccType() {
        return this.accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getAccNumber() {
        return this.accNumber;
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
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOpenedAt() {
        return this.openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }
}
