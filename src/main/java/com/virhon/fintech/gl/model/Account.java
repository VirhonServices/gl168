package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

public class Account {
    private UUID            accountUUID;
    private String          accountNumber;
    private AccountType     accountType;
    private BigDecimal      balance;
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
     * @param balance
     * @param reservedAmount
     * @param currentPage
     */
    public Account(UUID         accountUUID,
                   String       accountNumber,
                   AccountType  accountType,
                   BigDecimal balance,
                   BigDecimal   reservedAmount,
                   CurrentPage  currentPage) {
        this.accountUUID = accountUUID;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
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
        result.balance = BigDecimal.ZERO;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getReservedAmount() {
        return reservedAmount;
    }

    public CurrentPage getCurrentPage() {
        return currentPage;
    }

    /**
     * Checks if specified balance accords to account's type
     *
     * @param balance
     * @return
     */
    private boolean isValidBalance(BigDecimal balance) {
        return (accountType.equals(AccountType.ACTIVEPASSIVE)) ||
                (accountType.equals(AccountType.ACTIVE) && (balance.signum() ==   1 || balance.signum() == 0)) ||
                (accountType.equals(AccountType.PASSIVE) && (balance.signum() == -1 || balance.signum() == 0));
    }

    /**
     * Register the post into a current page
     *
     * @param post
     * @throws LedgerException
     */
    private void registerPost(Post post) throws LedgerException {
        final BigDecimal newBalance = this.balance.add(post.getAmount());
        if (!isValidBalance(newBalance)) {
            throw LedgerException.redBalance(this.accountNumber);
        } else {
            this.currentPage.addPost(post);
        }
    }

    /**
     * Credit account
     *
     * @param documentId
     * @param postedAt
     * @param reportedAt
     * @param amount        positive only!!!!
     * @return              resulted account balance
     */
    public BigDecimal credit(Long documentId, ZonedDateTime postedAt, Date reportedAt, BigDecimal amount)
            throws LedgerException {
        Post post = new Post(documentId, postedAt, reportedAt, amount);
        registerPost(post);
        return this.getBalance();
    }

    public BigDecimal debit(Long documentId, ZonedDateTime postedAt, Date reportedAt, BigDecimal amount)
            throws LedgerException {
        return credit(documentId, postedAt, reportedAt, amount.negate());
    }

}
