package com.virhon.fintech.gl.api.maketransfer;

import java.math.BigDecimal;

public class TransferData {
    public static class Account {
        private String accUuid;
        private String accNumber;
        private String iban;
        private String accType;

        public String getAccUuid() {
            return accUuid;
        }

        public void setAccUuid(String accUuid) {
            this.accUuid = accUuid;
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

        public String getAccType() {
            return accType;
        }

        public void setAccType(String accType) {
            this.accType = accType;
        }
    }
    private String uuid;
    private String clientCustomerId;
    private String transferRef;
    private String postedAt;
    private String reportedOn;
    private BigDecimal amount;
    private BigDecimal repAmount;
    private String description;
    private Account debit;
    private Account credit;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getClientCustomerId() {
        return this.clientCustomerId;
    }

    public void setClientCustomerId(String clientCustomerId) {
        this.clientCustomerId = clientCustomerId;
    }

    public String getTransferRef() {
        return this.transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
    }

    public String getPostedAt() {
        return this.postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getReportedOn() {
        return this.reportedOn;
    }

    public void setReportedOn(String reportedOn) {
        this.reportedOn = reportedOn;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRepAmount() {
        return this.repAmount;
    }

    public void setRepAmount(BigDecimal repAmount) {
        this.repAmount = repAmount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getDebit() {
        return this.debit;
    }

    public void setDebit(Account debit) {
        this.debit = debit;
    }

    public Account getCredit() {
        return this.credit;
    }

    public void setCredit(Account credit) {
        this.credit = credit;
    }
}
