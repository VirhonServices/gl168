package com.virhon.fintech.gl.exception;

import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.Post;

import java.time.ZonedDateTime;

public class LedgerException extends Exception {
    private int code;

    public LedgerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static LedgerException invalidPostedAt(Post post) {
        return new LedgerException(100, "Unable to add the post older than the page ".concat(post.getPostedAt().toString()));
    }

    public static LedgerException invalidReportedOn(Post post) {
        return new LedgerException(110, "Unable to add the post older than the page ".concat(post.getReportedOn().toString()));
    }

    public static LedgerException redBalance(String accountNumber) {
        return new LedgerException(200, "Red balance on the account ".concat(accountNumber));
    }

    public static LedgerException wrongReservation(String accountNumber) {
        return new LedgerException(210, "The balance of the account ".concat(accountNumber).concat(" is not enough to make a reservation"));
    }

    public static LedgerException invalidHistoricalData(Account account, ZonedDateTime at) {
        return new LedgerException(300, "The account ".concat(account.getAttributes().getEntity().getAccountNumber()
                .concat(" didn't exist at the date ").concat(at.toString())));
    }

    public static LedgerException invalidMode(Account account) {
        return new LedgerException(400, "Can't operate read-only account "
                .concat(account.getAttributes().getEntity().getAccountNumber()));
    }

}
