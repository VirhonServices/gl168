package com.virhon.fintech.gl.repo;

public class TransferPages {
    private String transferUuid;
    private String debitPageUuid;
    private String creditPageUuid;

    public String getTransferUuid() {
        return transferUuid;
    }

    public void setTransferUuid(String transferUuid) {
        this.transferUuid = transferUuid;
    }

    public String getDebitPageUuid() {
        return debitPageUuid;
    }

    public void setDebitPageUuid(String debitPageUuid) {
        this.debitPageUuid = debitPageUuid;
    }

    public String getCreditPageUuid() {
        return creditPageUuid;
    }

    public void setCreditPageUuid(String creditPageUuid) {
        this.creditPageUuid = creditPageUuid;
    }
}
