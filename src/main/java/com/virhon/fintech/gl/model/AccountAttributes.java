package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountAttributes {
    private AccountType     accountType;
    private String          accountUUID;
    private String          accountNumber;
    private String          iban;
    private BigDecimal      balance;
    private BigDecimal      reservedAmount;

    public AccountAttributes() {
    }

    /**
     * For reading from JSON
     *
     * @param accountType
     * @param accountUUID
     * @param accountNumber
     * @param balance
     * @param reservedAmount
     */
    public AccountAttributes(String       accountUUID,
                             String       accountNumber,
                             AccountType  accountType,
                             String       iban,
                             BigDecimal   balance,
                             BigDecimal   reservedAmount) {
        this.accountUUID = accountUUID;
        this.accountNumber = accountNumber;
        this.iban = iban;
        this.accountType = accountType;
        this.balance = balance;
        this.reservedAmount = reservedAmount;
    }

    /**
     * To create new instance of account
     *
     * @param accountNumber
     * @param accountType
     * @return
     */
    public static AccountAttributes createNew(String accountNumber, String iban, AccountType accountType) {
        AccountAttributes result = new AccountAttributes();
        result.accountNumber = accountNumber;
        result.accountType = accountType;
        result.accountUUID = UUID.randomUUID().toString();
        result.iban = iban;
        result.balance = BigDecimal.ZERO;
        result.reservedAmount = BigDecimal.ZERO;
        return result;
    }

    public String getAccountUUID() {
        return this.accountUUID;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public AccountType getAccountType() {
        return this.accountType;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public BigDecimal getReservedAmount() {
        return this.reservedAmount;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getIban() {
        return iban;
    }

    public void setReservedAmount(BigDecimal reservedAmount) throws LedgerException {
        if (reservedAmount.compareTo(balance) > 0) {
            throw LedgerException.wrongReservation(this.accountNumber);
        }
        this.reservedAmount = reservedAmount;
    }
}
