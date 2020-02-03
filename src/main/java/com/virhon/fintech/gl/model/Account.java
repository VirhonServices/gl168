package com.virhon.fintech.gl.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class Account {
    private UUID            accountUUID;
    private String          accountNumber;
    private AccountType     accountType;
    private BigDecimal      amount;
    private BigDecimal      reservedAmount;

    private CurrentPage     currentPage;

    public Account() {
    }

    /**
     * For reading from JSON
     *
     * @param accountUUID
     * @param accountNumber
     * @param accountType
     * @param amount
     * @param reservedAmount
     * @param currentPage
     */
    public Account(UUID         accountUUID,
                   String       accountNumber,
                   AccountType  accountType,
                   BigDecimal   amount,
                   BigDecimal   reservedAmount,
                   CurrentPage  currentPage) {
        this.accountUUID = accountUUID;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.amount = amount;
        this.reservedAmount = reservedAmount;
        this.currentPage = currentPage;
    }

    /**
     * To create new instance of account
     *
     * @param accountNumber
     * @param accountType
     * @return
     */
    public static Account createNew(String accountNumber, AccountType accountType) {
        Account result = new Account();
        result.accountNumber = accountNumber;
        result.accountType = accountType;
        result.accountUUID = UUID.randomUUID();
        result.amount = BigDecimal.ZERO;
        result.reservedAmount = BigDecimal.ZERO;
        result.currentPage = new CurrentPage(ZonedDateTime.now(), BigDecimal.ZERO);
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

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getReservedAmount() {
        return reservedAmount;
    }

    public CurrentPage getCurrentPage() {
        return currentPage;
    }
}
