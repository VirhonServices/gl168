package com.virhon.fintech.gl.exception;

import com.virhon.fintech.gl.model.Account;

import java.time.ZonedDateTime;

public class LedgerException extends Exception {
    private int code;

    public LedgerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static LedgerException redBalance(String accountNumber) {
        return new LedgerException(100, "Red balance on the account ".concat(accountNumber));
    }

    public static LedgerException invalidHistoricalData(Account account, ZonedDateTime at) {
        return new LedgerException(200, "The account ".concat(account.getAttributes().getEntity().getAccountNumber()
                .concat(" didn't exist at the date ").concat(at.toString())));
    }

    public static LedgerException invalidMode(Account account) {
        return new LedgerException(300, "Can't operate read-only account "
                .concat(account.getAttributes().getEntity().getAccountNumber()));
    }
}
