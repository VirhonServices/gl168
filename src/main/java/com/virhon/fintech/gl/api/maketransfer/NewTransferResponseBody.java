package com.virhon.fintech.gl.api.maketransfer;

import java.math.BigDecimal;

public class NewTransferResponseBody {
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

    public String getTransferRef() {
        return transferRef;
    }

    public void setTransferRef(String transferRef) {
        this.transferRef = transferRef;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public String getReportedOn() {
        return reportedOn;
    }

    public void setReportedOn(String reportedOn) {
        this.reportedOn = reportedOn;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getDebit() {
        return debit;
    }

    public void setDebit(Account debit) {
        this.debit = debit;
    }

    public Account getCredit() {
        return credit;
    }

    public void setCredit(Account credit) {
        this.credit = credit;
    }
}
