package com.virhon.fintech.gl.api.balanceat;

import java.math.BigDecimal;

public class BalanceAtResponseBody {
    private String at;
    private String accType;
    private String accNumber;
    private String iban;
    private BigDecimal balance;
    private BigDecimal repBalance;
    private String balType;

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getBalType() {
        return balType;
    }

    public void setBalType(String balType) {
        this.balType = balType;
    }

    public BigDecimal getRepBalance() {
        return repBalance;
    }

    public void setRepBalance(BigDecimal repBalance) {
        this.repBalance = repBalance;
    }
}
