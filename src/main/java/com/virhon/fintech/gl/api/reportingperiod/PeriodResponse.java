package com.virhon.fintech.gl.api.reportingperiod;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public class PeriodResponse {
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

    public static class TransferResponse {
        private String          transferUuid;
        private String          transferRef;
        private String          postedAt;
        private String          transferType;
        private BigDecimal      amount;
        private BigDecimal      repAmount;
        private String          reportedOn;
        private String          description;
        private String          debitUuid;
        private String          creditUuid;

        public static TransferResponse createFrom(Transfer tr) {
            final Gson gson = GsonConverter.create();
            final TransferResponse tres = new TransferResponse();
            tres.setTransferUuid(tr.getTransferUuid());
            tres.setTransferRef(tr.getTransferRef());
            tres.setPostedAt(tr.getPostedAt().toString());
            tres.setAmount(tr.getAmount().abs());
            tres.setRepAmount(tr.getLocalAmount().abs());
            tres.setReportedOn(tr.getReportedOn().toString());
            tres.setDescription(tr.getDescription());
            tres.setDebitUuid(tr.getDebitUuid());
            tres.setCreditUuid(tr.getCreditUuid());
            return tres;
        }

        public String getTransferType() {
            return transferType;
        }

        public void setTransferType(String transferType) {
            this.transferType = transferType;
        }

        public String getTransferUuid() {
            return transferUuid;
        }

        public void setTransferUuid(String transferUuid) {
            this.transferUuid = transferUuid;
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

        public String getReportedOn() {
            return reportedOn;
        }

        public void setReportedOn(String reportedOn) {
            this.reportedOn = reportedOn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDebitUuid() {
            return debitUuid;
        }

        public void setDebitUuid(String debitUuid) {
            this.debitUuid = debitUuid;
        }

        public String getCreditUuid() {
            return creditUuid;
        }

        public void setCreditUuid(String creditUuid) {
            this.creditUuid = creditUuid;
        }
    }

    private String accType;
    private String accNumber;
    private String iban;
    private Balance open;
    private List<TransferResponse> transfers;
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

    public List<TransferResponse> getTransfers() {
        return transfers;
    }

    public void setTransfers(List<TransferResponse> transfers) {
        this.transfers = transfers;
    }
}