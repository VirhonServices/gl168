package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class AccountAttributes {
    private String          clientUuid;
    private String          clientCustomerId;
    private AccountType     accountType;
    private String          accountUUID;
    private String          accountNumber;
    private String          iban;
    private BigDecimal      balance;
    private BigDecimal      localBalance;
    private BigDecimal      reservedDebit;
    private BigDecimal      reservedCredit;
    private ZonedDateTime   openedAt;
    private ZonedDateTime   closedAt;

    private AccountAttributes() {
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
        result.localBalance = BigDecimal.ZERO;
        result.reservedDebit = BigDecimal.ZERO;
        result.reservedCredit = BigDecimal.ZERO;
        result.openedAt = ZonedDateTime.now();
        return result;
    }


    /**
     * Returns type of the balance
     *
     * @return
     */
    public AmountType getBalanceType(BigDecimal bal) {
        if (bal.signum() == -1) {
            return AmountType.CREDIT;
        } else if (bal.signum() == 1) {
            return AmountType.DEBIT;
        } else {
            if (this.accountType == AccountType.ACTIVE) {
                return AmountType.DEBIT;
            } else {
                return AmountType.CREDIT;
            }
        }
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

    public AmountType getBalanceType() {
        return getBalanceType(this.balance);
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

    public BigDecimal getLocalBalance() {
        return localBalance;
    }

    public BigDecimal getReservedDebit() {
        return this.reservedDebit;
    }

    public BigDecimal getReservedCredit() {
        return reservedCredit;
    }

    public ZonedDateTime getOpenedAt() {
        return openedAt;
    }

    public ZonedDateTime getClosedAt() {
        return closedAt;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setLocalBalance(BigDecimal localBalance) {
        this.localBalance = localBalance;
    }

    public String getIban() {
        return iban;
    }

    public void setReservedDebit(BigDecimal reservedDebit) throws LedgerException {
        this.reservedDebit = reservedDebit;
    }

    public void setReservedCredit(BigDecimal reservedCredit) {
        this.reservedCredit = reservedCredit;
    }
}
