package com.virhon.fintech.gl.api.reportingperiodbalances;

import java.math.BigDecimal;

public class ReportingPeriodBalancesResponse {
    public static class Balance {
        private BigDecimal balance;
        private BigDecimal repBalance;
        private String balType;

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
    }

    private String accType;
    private String accNumber;
    private String iban;
    private Balance open;
    private Balance closed;

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

    public Balance getOpen() {
        return open;
    }

    public void setOpen(Balance open) {
        this.open = open;
    }

    public Balance getClosed() {
        return closed;
    }

    public void setClosed(Balance closed) {
        this.closed = closed;
    }
}
