package com.virhon.fintech.gl.api;

public class LedgerError {
    private Integer code;
    private String  message;

    public LedgerError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
