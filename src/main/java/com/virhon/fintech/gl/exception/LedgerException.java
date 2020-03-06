package com.virhon.fintech.gl.exception;

import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.Transfer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class LedgerException extends Exception {
    private int code;

    public LedgerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static LedgerException invalidPostedAt(Transfer post) {
        return new LedgerException(100, "Unable to add the post older than the page ".concat(post.getPostedAt().toString()));
    }

    public static LedgerException invalidReportedOn(Transfer post) {
        return new LedgerException(110, "Unable to add the post older than the page ".concat(post.getReportedOn().toString()));
    }

    public static LedgerException invalidReservationAmount(BigDecimal amount) {
        return new LedgerException(120, "The negative amount ".concat(amount.toString()).concat(" can't be reserved"));
    }

    public static LedgerException invalidTransferAmount(BigDecimal amount) {
        return new LedgerException(130, "The negative amount ".concat(amount.toString()).concat(" can't be transferred"));
    }

    public static LedgerException invalidTypeOfAccount(String accountType) {
        return new LedgerException(140, "Invalid type of account ".concat(accountType));
    }

    public static LedgerException notSupportedCurrency(String currency) {
        return new LedgerException(150, "Currency ".concat(currency).concat(" not supported"));
    }

    public static LedgerException notNullValue(String valueName) {
        return new LedgerException(160, "The value ".concat(valueName).concat(" can't be null"));
    }

    public static LedgerException redBalance(String accountNumber) {
        return new LedgerException(200, "Red balance on the account ".concat(accountNumber));
    }

    public static LedgerException wrongReservation(String accountNumber) {
        return new LedgerException(210, "The balance of the account ".concat(accountNumber).concat(" is not enough to make a reservation"));
    }

    public static LedgerException invalidAccount(String account) {
        return new LedgerException(300, "The account identified by ".concat(account).concat(" doesn't exist"));
    }

    public static LedgerException invalidHistoricalData(Account account, ZonedDateTime at) {
        return new LedgerException(310, "The account ".concat(account.getAttributes().getEntity().getAccountNumber()
                .concat(" didn't exist on the date ").concat(at.toString())));
    }

    public static LedgerException accountCantBeOperated(Long accountId) {
        return new LedgerException(320, "The account id=".concat(accountId.toString()).concat(" can't be operated"));
    }

    public static LedgerException transferNotExist(String uuid) {
        return new LedgerException(330, "The transfer uuid=".concat(uuid).concat(" doesn't exist"));
    }

    public static LedgerException pageNotExist(String uuid) {
        return new LedgerException(340, "The page uuid=".concat(uuid).concat(" doesn't exist"));
    }

    public static LedgerException invalidMode(Account account) {
        return new LedgerException(600, "Can't operate read-only account "
                .concat(account.getAttributes().getEntity().getAccountNumber()));
    }

    public int getCode() {
        return code;
    }
}
