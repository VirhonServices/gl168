package com.virhon.fintech.gl.api.accountinformation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

public class AccountInformationResponseBody {
    private String uuid;
    private String clientUuid;
    private String clientCustomerId;
    private String accType;
    private String accNumber;
    private String iban;
    private String currency;
    private BigDecimal balance;
    private BigDecimal repBalance;
    private String balType;
    private BigDecimal available;
    private String openedAt;
    private String closedAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getRepBalance() {
        return repBalance;
    }

    public void setRepBalance(BigDecimal repBalance) {
        this.repBalance = repBalance;
    }

    public String getBalType() {
        return balType;
    }

    public void setBalType(String balType) {
        this.balType = balType;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public String getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(String openedAt) {
        this.openedAt = openedAt;
    }

    public String getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(String closedAt) {
        this.closedAt = closedAt;
    }
}
