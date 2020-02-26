package com.virhon.fintech.gl.model;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Account {
    private Long                accountId;
    private Ledger              ledger;

    final static Logger LOGGER = Logger.getLogger(Account.class);

    public Account(Ledger ledger) {
        this.ledger = ledger;
    }

    /**
     * Returns the balance of the account from current or historical pages
     *
     * @param at
     * @return
     * @throws LedgerException
     */
    public BigDecimal getAccountBalanceAt(ZonedDateTime at) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> attributes = getAttributes();
        final IdentifiedEntity<Page> currentPage = getCurrentPage();
        if (currentPage.getEntity().contains(at)) {
            return currentPage.getEntity().getBalanceAt(at);
        } else {
            final IdentifiedEntity<Page> historicalPage = this.ledger.getHistPageRepo()
                    .getByAccountId(attributes.getId(), at);
            if (historicalPage == null) {
                throw LedgerException.invalidHistoricalData(this, at);
            } else {
                return historicalPage.getEntity().getBalanceAt(at);
            }
        }
    }

    /**
     * Checks if specified balance accords to account's type
     *
     * @param pBalance
     * @return
     */
    private boolean isValidBalance(AccountAttributes attributes, BigDecimal pBalance) {
        final BigDecimal balance = pBalance.add(attributes.getReservedDebit());
        return (attributes.getAccountType().equals(AccountType.ACTIVEPASSIVE)) ||
                (attributes.getAccountType().equals(AccountType.ACTIVE) && (balance.signum() ==   1 ||
                        balance.signum() == 0)) ||
                (attributes.getAccountType().equals(AccountType.PASSIVE) && (balance.signum() == -1 ||
                        balance.signum() == 0));
    }

    /**
     * Register the tr into a current page
     *
     * @param tr
     * @throws LedgerException
     */
    private void registerTransfer(Transfer tr) throws LedgerException {
        final IdentifiedEntity<AccountAttributes> attributes = this.ledger.getAttrRepo()
                .getByIdExclusive(this.accountId);
        if (attributes == null) {
            throw LedgerException.invalidAccount(this.accountId.toString());
        }
        final IdentifiedEntity<Page> currentPage = this.ledger.getCurPageRepo().getByIdExclusive(this.accountId);
        final BigDecimal newBalance = attributes.getEntity().getBalance().add(tr.getAmount());
        final BigDecimal newLocalBalance = attributes.getEntity().getLocalBalance().add(tr.getLocalAmount());
        if (!isValidBalance(attributes.getEntity(), newBalance)) {
            throw LedgerException.redBalance(attributes.getEntity().getAccountNumber());
        } else {
            attributes.getEntity().setBalance(newBalance);
            attributes.getEntity().setLocalBalance(newLocalBalance);
            if (currentPage.getEntity().hasNext()) {
                currentPage.getEntity().addTransfer(tr);
                this.ledger.getCurPageRepo().put(currentPage);
            } else {
                final Page hPage = currentPage.getEntity();
                final Page cPage = Page.create(hPage.getFinishedAt(),
                        hPage.getRepFinishedOn(), hPage.getFinishBalance(), hPage.getFinishRepBalance());
                cPage.addTransfer(tr);
                final IdentifiedEntity<Page> cidPage = new IdentifiedEntity<>(this.accountId, cPage);
                this.ledger.getCurPageRepo().put(cidPage);
                this.ledger.getHistPageRepo().insert(this.accountId, hPage);
            }
            this.ledger.getAttrRepo().update(attributes);
        }
    }

    private BigDecimal operate(Transfer tr)
            throws LedgerException {
        registerTransfer(tr);
        return getAttributes().getEntity().getBalance();
    }

    public BigDecimal credit(Transfer tr)
            throws LedgerException {
        LOGGER.info("Crediting account id=".concat(accountId.toString())
                .concat(" for ".concat(tr.getAmount().toString())));
        final BigDecimal balance = operate(tr);
        LOGGER.info("OK Resulting balance = ".concat(balance.toString()));
        return balance;
    }

    public BigDecimal debit(Transfer tr)
            throws LedgerException {
        LOGGER.info("Debting account id=".concat(accountId.toString())
                .concat(" for ".concat(tr.getAmount().toString())));
        final BigDecimal balance = operate(tr);
        LOGGER.info("OK Resulting balance = ".concat(balance.toString()));
        return balance;
    }

    /**
     * Reserves amount for future debiting
     *
     * @param amount
     * @return
     * @throws LedgerException
     */
    public BigDecimal reserveDebit(BigDecimal amount) throws LedgerException {
        // 1. Calculate new reservedAmount
        final IdentifiedEntity<AccountAttributes> iAttributes = this.ledger.getAttrRepo()
                .getByIdExclusive(this.accountId);
        if (iAttributes==null) {
            throw LedgerException.invalidAccount(this.accountId.toString());
        }
        if (amount.signum() == -1) {
            throw LedgerException.invalidReservationAmount(amount);
        }
        final AccountAttributes attributes = iAttributes.getEntity();
        final BigDecimal balance = attributes.getBalance();
        final BigDecimal reserved = attributes.getReservedDebit();
        final BigDecimal newReserved = reserved.add(amount);
        // 2. Check the reservedAmount
        if (attributes.getAccountType() == AccountType.PASSIVE && balance.negate().compareTo(newReserved) < 0) {
            throw LedgerException.wrongReservation(attributes.getAccountNumber());
        }
        // 3. Change reservedAmount
        attributes.setReservedDebit(newReserved);
        final IdentifiedEntity<AccountAttributes> oAttr = new IdentifiedEntity<>(this.accountId, attributes);
        this.ledger.getAttrRepo().update(oAttr);
        return newReserved;
    }

    /**
     * Reserves amount for future crediting
     *
     * @param amount
     * @return
     * @throws LedgerException
     */
    public BigDecimal reserveCredit(BigDecimal amount) throws LedgerException {
        // 1. Calculate new reservedAmount
        final IdentifiedEntity<AccountAttributes> iAttributes = this.ledger.getAttrRepo()
                .getByIdExclusive(this.accountId);
        if (iAttributes==null) {
            throw LedgerException.invalidAccount(this.accountId.toString());
        }
        if (amount.signum() == -1) {
            throw LedgerException.invalidReservationAmount(amount);
        }
        final AccountAttributes attributes = iAttributes.getEntity();
        final BigDecimal balance = attributes.getBalance();
        final BigDecimal reserved = attributes.getReservedCredit();
        final BigDecimal newReserved = reserved.add(amount);
        // 2. Check the reservedAmount
        if (attributes.getAccountType() == AccountType.ACTIVE && balance.compareTo(newReserved) < 0) {
            throw LedgerException.wrongReservation(attributes.getAccountNumber());
        }
        // 3. Change reservedAmount
        attributes.setReservedCredit(newReserved);
        final IdentifiedEntity<AccountAttributes> oAttr = new IdentifiedEntity<>(this.accountId, attributes);
        this.ledger.getAttrRepo().update(oAttr);
        return newReserved;
    }

    /**
     * Reserved for checking if the account arrested or blocked
     * @return
     */
    public Boolean canBeOperated() {
        // TODO: 22.02.20 needs to be refactored
        return true;
    }

    public IdentifiedEntity<AccountAttributes> getAttributes() {
        return this.ledger.getAttrRepo().getById(this.accountId);
    }

    public IdentifiedEntity<Page> getCurrentPage() {
        return this.ledger.getCurPageRepo().getById(this.accountId);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}