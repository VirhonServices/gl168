package com.virhon.fintech.gl.exception;

public class LedgerException extends Exception {
    private int code;

    public LedgerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static LedgerException redBalance(String accountNumber) {
        return new LedgerException(100, "Red balance on the account ".concat(accountNumber));
    }
}
