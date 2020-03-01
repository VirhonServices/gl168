package com.virhon.fintech.gl.repo;

public class TransferPages {
    private String transferUuid;
    private String clientUuid;
    private String clientCustomerId;
    private String debitPageUuid;
    private String creditPageUuid;

    public String getTransferUuid() {
        return transferUuid;
    }

    public void setTransferUuid(String transferUuid) {
        this.transferUuid = transferUuid;
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
