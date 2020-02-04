package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountAttributes {
    private UUID            accountUUID;
    private String          accountNumber;
    private AccountType     accountType;
    private BigDecimal      balance;
    private BigDecimal      reservedAmount;

    public AccountAttributes() {
    }

    /**
     * For reading from JSON
     *
     * @param accountUUID
     * @param accountNumber
     * @param accountType
     * @param balance
     * @param reservedAmount
     */
    public AccountAttributes(UUID         accountUUID,
                             String       accountNumber,
                             AccountType  accountType,
                             BigDecimal   balance,
                             BigDecimal   reservedAmount) {
        this.accountUUID = accountUUID;
        this.accountNumber = accountNumber;
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
    public static AccountAttributes createNew(String accountNumber, AccountType accountType) {
        AccountAttributes result = new AccountAttributes();
        result.accountNumber = accountNumber;
        result.accountType = accountType;
        result.accountUUID = UUID.randomUUID();
        result.balance = BigDecimal.ZERO;
        result.reservedAmount = BigDecimal.ZERO;
        return result;
    }

    public UUID getAccountUUID() {
        return accountUUID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getReservedAmount() {
        return reservedAmount;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setReservedAmount(BigDecimal reservedAmount) {
        this.reservedAmount = reservedAmount;
    }
}
