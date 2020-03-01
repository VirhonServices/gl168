package com.virhon.fintech.gl.repo.mysql.transfer;

public class MySQLTransferRecord {
    private Long        id;
    private String      uuid;
    private String      clientUuid;
    private String      clientCustomerId;
    private String      debitPageUuid;
    private String      creditPageUuid;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return this.uuid;
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

    public String getDebitPageUuid() {
        return this.debitPageUuid;
    }

    public void setDebitPageUuid(String debitPageUuid) {
        this.debitPageUuid = debitPageUuid;
    }

    public String getCreditPageUuid() {
        return this.creditPageUuid;
    }

    public void setCreditPageUuid(String creditPageUuid) {
        this.creditPageUuid = creditPageUuid;
    }
}
